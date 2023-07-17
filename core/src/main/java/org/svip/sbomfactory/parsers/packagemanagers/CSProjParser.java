package org.svip.sbomfactory.parsers.packagemanagers;

import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import org.svip.builders.component.SVIPComponentBuilder;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.utils.QueryWorker;

import java.util.*;

/**
 * file: CSProjParser.java
 * Description: Package-manager specific implementation of the PackageManagerParser (.csproj)
 *
 * @author Henry Orsagh
 * @author Dylan Mulligan
 */
public class CSProjParser extends PackageManagerParser {

    public CSProjParser() {
        super(
                "https://learn.microsoft.com/en-us/dotnet/api/",
                new XmlFactory(),
                "" // TODO: Token regex
        );
    }

    @Override
    protected void parseData(List<SVIPComponentObject> components, HashMap<String, Object> data) {

        /*
        xml structure

        {type} = Compile, Reference, etc

        <Project>
            <ItemGroup>
                <{Type} Include="{Dependency}">
                    <Version>1.0.0</Version>
                </{Type}>
                <{Type} Include="{Dependency}" />
            </ItemGroup>
        </Project>
         */

        // Ensure ItemGroup is not null
        if(data.get("ItemGroup") == null) return;

        // List of all Item Groups
        final ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> itemgroups = (ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>)data.get("ItemGroup");

        // Iterate over itemgroups
        for(HashMap<String, ArrayList<HashMap<String, String>>> itemgroup : itemgroups) {

            // Get all types in this itemgroup
            final Iterator types = itemgroup.entrySet().iterator();

            // Iterate over types
            while (types.hasNext()) {
                // Get type (as Map.Entry)
                final Map.Entry<String, Object> type = (Map.Entry<String, Object>) types.next();

                // If there is only one type, it will be a hashmap instead of an arraylist
                if (type.getValue() instanceof HashMap) {
                    // Add component with type as HashMap
                    addComponent(components, type.getKey(), (HashMap<String, String>) type.getValue());
                }
                else { // Otherwise, there will be a list of hashmaps
                    for (HashMap<String, String> item : (ArrayList<HashMap<String, String>>) type.getValue()) {
                        // Add component with type as an ArrayList of HashMaps
                        addComponent(components, type.getKey(), item);
                    }
                }
            }

            // Query all found URLs and store any relevant data
            queryURLs(this.queryWorkers);
        }

//        props(components, data);
    }

    /**
     * Reformats to ParserComponent and adds the component to the list.
     * @param components list to add component to
     * @param type item type (Reference, Compile, etc)
     * @param component component properties table
     */
    private void addComponent(List<SVIPComponentObject> components, String type, HashMap<String, String> component) {

        // Internal components will have Include = {filepath}, whereas external and system will be Include = {group.name}
        String include = component.get("Include");

        // Ensure include was found
        if(include == null) return;

        SVIPComponentBuilder builder = new SVIPComponentBuilder();
        builder.setType("EXTERNAL"); // Default to EXTERNAL
        builder.setName(component.get("Include")); // TODO is this a correct default name?

        // Convert hashmap to parser component
        switch(type) {
            // These types are external or language
            case "Reference", "PackageReference" -> {
                // Split string on "."
                final String[] split = include.split("\\.");

                // Set name to last element of split
                builder.setName(split[split.length - 1]);

                // If more than one element, rejoin elements on "/"
                if (split.length > 1) builder.setGroup(String.join("/", Arrays.copyOfRange(split, 0, split.length - 1)));

                // Build url and worker object
                this.queryWorkers.add(new QueryWorker(builder, this.buildURL(builder.build())) {
                    @Override
                    public void run() {
                        try {
                            // If query is sucessful, set type to LANGUAGE and return
                            if(queryURL(url, true).getResponseCode() == 200) {
                                builder.setType("LANGUAGE");
                                return;
                            }
                        } catch (Exception ignored) { } // If an error is thrown, ignore it

                        // If this is reached, queryURL returned something other than
                        // 200, or an exception was thrown
                        builder.setType("EXTERNAL");
                    }
                });
            }
            // These types are filepaths
            case "Compile", "None", "Content", "EmbeddedResource" -> {
                // Set type to INTERNAL
                builder.setType("INTERNAL");

                // Replace back-slashes with forward-slashes
                include = include.replace('\\', '/');

                // Split at / to get each directory/filename
                final String[] fileSplit = include.split("/");

                // Last element is the filename
                final String filename = fileSplit[fileSplit.length - 1];

                // Strip file extension
                builder.setName(filename.substring(0, filename.lastIndexOf('.')));

                // No slashes (only one element) = no group
                if(fileSplit.length == 1) break;

                // Include without the filename is the group
                builder.setGroup(include.substring(0, include.length() - 1 - filename.length()));
            }
        }
        /*
        some items have metadata which holds other information like version. I couldn't find a reference for
        what metadata properties there could be, other than Version which was in some of my testing projects.
         */
        if(component.get("Version") != null) builder.setVersion(component.get("Version"));

        // Add ParserComponent to components
        components.add(builder.build());
    }

    // TODO: Docstring
    private String buildURL(SVIPComponentObject component) {
        // Build URL to query
        String endpoint =
                component.getGroup() == null ?
                        component.getName() :
                        component.getGroup().replace('/', '.') + "." + component.getName();

        // If component is typed, count types and go to correct URL
        // "System.Func<string, string>" -> "System.Func-2"
        final int index = endpoint.indexOf('<');
        if(index != -1) {
            // Rebuild endpoint
            final int params = endpoint.substring(index).split(",").length;
            endpoint = endpoint.substring(0, index) + "-" + params;

            // Strip typing from component name
            final String name = component.getName();
            set(component, b -> b.setName(name.substring(0, name.indexOf('<'))));
        }

        // Return built URL
        return this.STD_LIB_URL + endpoint;
    }

//    private void props(ArrayList<ParserComponent> components, HashMap<String, Object> data) {
//        // Ensure ItemGroup is not null
//        if (data.get("PropertyGroup") == null) return;
//
//        // List of all Item Groups
//        final ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> propgroups = (ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>) data.get("PropertyGroup");
//
//        // Iterate over itemgroups
//        for (HashMap<String, ArrayList<HashMap<String, String>>> propgroup : propgroups) {
//
//            // Get all types in this itemgroup
//            final Iterator types = propgroup.entrySet().iterator();
//
//            // Iterate over types
//            while (types.hasNext()) {
//                // Get type (as Map.Entry)
//                final Map.Entry<String, Object> type = (Map.Entry<String, Object>) types.next();
//
//                // If there is only one type, it will be a hashmap instead of an arraylist
//                if (type.getValue() instanceof HashMap) {
//                    // Add component with type as HashMap
//                    addComponent(components, type.getKey(), (HashMap<String, String>) type.getValue());
//                } else { // Otherwise, there will be a list of hashmaps
//                    for (HashMap<String, String> item : (ArrayList<HashMap<String, String>>) type.getValue()) {
//                        // Add component with type as an ArrayList of HashMaps
//                        addComponent(components, type.getKey(), item);
//                    }
//                }
//            }
//        }
//    }
}
