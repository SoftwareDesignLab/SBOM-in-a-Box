package org.svip.sbomfactory.generators.parsers.packagemanagers;

import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import org.svip.sbom.model.CPE;
import org.svip.sbom.model.PURL;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbomfactory.generators.utils.QueryWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * file: NugetParser.java
 * Description: Package-manager specific implementation of the PackageManagerParser (.nuspec/.json)
 *
 * @author Juan Francisco Patino
 */
public class NugetParser extends PackageManagerParser{

    /**
     * Protected Constructor meant for use by parser implementations
     * to store their package-manager-specific static values in their respective
     * attributes.
     *
     */
    protected NugetParser() {
        super(
                "https://www.nuget.org/api/v2/",
                new XmlFactory(),
                "\\$\\{([^}]*)\\}" // TODO: Token regex
        );
    }

    /**
     * Parses a given set of raw data into Components and adds them to the given list
     * of Components. This method is abstract and should be implemented to parse each specific
     * dependency file differently, as needed.
     *
     * @param components list of Components to add to
     * @param data       map of data to be parsed
     */
    @Override
    protected void parseData(ArrayList<ParserComponent> components, HashMap<String, Object> data) {

        /*
                <?xml version="1.0" encoding="utf-8"?>
        <package xmlns="http://schemas.microsoft.com/packaging/2010/07/nuspec.xsd">
            <metadata>
                <!-- Required elements-->
                <id></id>
                <version></version>
                <description></description>
                <authors></authors>

                <!-- Optional elements -->
                <dependencies></dependencies>
                <frameworkAssemblies></frameworkAssemblies>
                <!-- ... -->
            </metadata>
            <!-- Optional 'files' node -->
        </package>
         */

        this.dependencies = new HashMap<>();
        HashMap<String, String> metadata = new HashMap((LinkedHashMap<String, ArrayList<HashMap<String, String>>>) data.get("metadata"));




       // this.resolveProperties(this.dependencies, new HashMap((metadata data.get("dependencies")).get("dependency"));

        int i = 0;

        for (Object o: metadata.values()
             ) {

            String s = o.toString();

            if(s.contains("frameworkAssembly")){ // treat C# framework assemblies as dependencies

                this.resolveProperties(
                        this.dependencies,


                        new HashMap(((ArrayList<LinkedHashMap<String,String>>) (((LinkedHashMap<?, ?>)o).get("frameworkAssembly")))
                                .stream().collect(
                                        Collectors.toMap(
                                                d -> d.get("assemblyName"),
                                                d -> d,
                                                (d1, d2) -> {
                                                    log(Debug.LOG_TYPE.WARN, String.format("Duplicate key found: %s", d2.get("assemblyName")));
                                                    return d2;
                                                }
                                        )
                                )
                        )
                );

            } else if (s.contains("dependency")) {

                this.resolveProperties(this.dependencies,

                new HashMap(((ArrayList<LinkedHashMap<String,String>>) (((LinkedHashMap<?, ?>)o).get("dependency")))
                        .stream().collect(
                                Collectors.toMap(
                                        d -> d.get("id"),
                                        d -> d,
                                        (d1, d2) -> {
                                            log(Debug.LOG_TYPE.WARN, String.format("Duplicate key found: %s", d2.get("id")));
                                            return d2;
                                        }
                                )
                        )

                ));

            }


        }

        // Iterate and build URLs
        for (final String id : this.dependencies.keySet()) { //todo this shares a lot of code with POMParser. Maybe make static method
            // Get value from map
            final HashMap<String, String> d = this.dependencies.get(id);

            // Format all property keys -> values
            String groupId = d.get("id");
            if(groupId == null)
                groupId = d.get("assemblyName"); // framework assembly name

            String version = d.get("version");
            if(version == null)
                version = d.get("targetFramework"); //todo this isn't entirely synonymous with version?

            final ParserComponent c = new ParserComponent(id);

            //framework assemblies use assemblyName + targetFramework
            if (groupId != null) c.setGroup(groupId);
            if (version != null) c.setVersion(version);

            // TODO: Find this PURL regex a home (Translator?): https://regex101.com/r/sbFd7Z/2
            //  "^pkg:([^/]+)/([^#\n@]+)(?:@([^\?\n]+))?(?:\?([^#\n]+))?(?:#([^\n]+))?"

            // is this a .NET assembly
            boolean frameworkAssembly =
                    groupId != null && (groupId.toLowerCase().contains("system") || groupId.toLowerCase().contains("microsoft"));

            // Build PURL String
            final HashMap<String, String> PURLData = new HashMap<>();
            PURLData.put("type", "nuget");
            PURLData.put("name", id);
            if (groupId != null) PURLData.put("namespace", groupId);
            if (version != null) PURLData.put("version", version);

            if (frameworkAssembly)
                c.setPublisher("Microsoft");

            String PURLString = PackageManagerParser.buildPURL(PURLData);

            // Add built PURL
            c.addPURL(new PURL(PURLString));
            log(Debug.LOG_TYPE.DEBUG, String.format("Dependency Found with PURL: %s", PURLString));

            // Build CPE
            CPE cpe = new CPE("nuget", id, version);
            String cpeFormatString = cpe.bindToFS();
            c.addCPE(cpeFormatString);
            log(Debug.LOG_TYPE.DEBUG, String.format("Dependency Found with CPE: %s", cpeFormatString));

            // Build URL and worker object
            if (groupId != null) {
                String url = this.STD_LIB_URL;
                url += groupId;
                url += "/" + id;
                if (version != null) url += "/" + version;
                // Create and add QueryWorker with Component reference and URL
                this.queryWorkers.add(new QueryWorker(c, url) {
                    @Override
                    public void run() {
                        // Get page contents
                        final String contents = getUrlContents(queryURL(this.url, false));

                        // Parse license(s)
                        // Regex101: https://regex101.com/r/FUOPSK/1
                        final Matcher m = Pattern.compile("<li data-test=\\\"license\\\">(.*?)</li>",
                                Pattern.MULTILINE).matcher(contents); //todo look into this more for nuget

                        // Add all found licenses
                        while (m.find()) {
                            this.component.addLicense(m.group(1).trim());
                        }
                    }
                });
            }

            // Add ParserComponent to components
            components.add(c);
        }


    }


}
