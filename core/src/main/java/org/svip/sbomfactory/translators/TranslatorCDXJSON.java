package org.svip.sbomfactory.translators;

import org.svip.sbom.model.*;

import org.cyclonedx.exception.ParseException;
import org.cyclonedx.model.*;
import org.cyclonedx.parsers.JsonParser;
import org.svip.sbom.model.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * file: TranslatorCDXJSON.java
 * Coverts SPDX SBOMs into internal SBOM objects.
 * Compatible with CDX 1.4 JSON SBOMs
 *
 * @author Tyler Drake
 */
public class TranslatorCDXJSON {
    /**
     * Converts a file into an internal SBOM object
     *
     * @param fileContents String of file contents
     * @param file_path Path to file
     * @return internal SBOM object
     * @throws ParseException If file is not valid JSON
     */
    public static SBOM translatorCDXJSONContents(String fileContents, String file_path) throws ParseException {
        // Internal SBOM Object
        SBOM sbom;

        // Top level component for SBOM's dependencyTree
        Component top_component = null;

        Set<String> components_left = new HashSet<>();

        // Initialize JSON Parser
        JsonParser parser = new JsonParser();

        // Use JSON Parser to parse cdx.json file and store into cyclonedx Bom Object
        Bom json_sbom = parser.parse(fileContents.getBytes());

        // Attempt to create the SBOM object. If information isn't found, cancel process and return a null object,
        try {
            // TODO: Add signature's later. CycloneDX BOM does a good job at dealing with them.
            // Create new SBOM object with require data
            sbom = new SBOM(
                    json_sbom.getBomFormat(),
                    json_sbom.getSpecVersion(),
                    String.valueOf(json_sbom.getVersion()),
                    json_sbom.getMetadata().getAuthors() == null
                            ? json_sbom.getMetadata().getTools().toString()
                            : json_sbom.getMetadata().getAuthors().toString(),
                    json_sbom.getSerialNumber(),
                    json_sbom.getMetadata().getTimestamp().toString(),
                    null
            );
        } catch (Exception e) {
            System.err.println("Error in creating internal SBOM for: " + file_path );
            return null;
        }

        // Attempt to create the top component. If unable to create the component, print an error and continue.
        try {
            // Get top component information from MetaData
            org.cyclonedx.model.Component top_component_meta = json_sbom.getMetadata().getComponent();

            // Create top component and add it to SBOM object
            top_component = new Component(
                    top_component_meta.getName(),
                    top_component_meta.getPublisher(),
                    top_component_meta.getVersion(),
                    top_component_meta.getBomRef().replaceAll("@", "")
            );
        } catch(Exception e) {
            System.err.println("Could not create top-level component from MetaData.\n " +
                    "If this is not expected please check SBOM file: " + file_path);

        }

        // Create new collection of components
        HashMap<String, Component> components = new HashMap<>();

        // Loop through all components in cyclonedx component list
        for(org.cyclonedx.model.Component cdx_component : json_sbom.getComponents()) {

            if( cdx_component != null ) {

                // Initialize ID collections
                Set<String> cpe_set = new HashSet<>();
                Set<PURL> purl_set = new HashSet<>();
                Set<String> swid_set = new HashSet<>();

                // Get CPE, PURL, and SWIDs
                if(cdx_component.getCpe() != null) { cpe_set.add(cdx_component.getCpe()) ; }
                if(cdx_component.getPurl() != null) { purl_set.add(new PURL(cdx_component.getPurl())) ; }
                if(cdx_component.getSwid() != null) { swid_set.add(String.valueOf(cdx_component.getSwid())) ; }

                // Create new component with a name, publisher, version along with CPEs/PURLs/SWIDs
                Component new_component = new Component(
                        cdx_component.getName(),
                        cdx_component.getPublisher(),
                        cdx_component.getVersion(),
                        cpe_set, purl_set, swid_set
                );

                // Attempt to get licenses. If no licenses found put out error message and continue.
                try {
                    new_component.setLicenses(new HashSet<>(Arrays.asList(cdx_component.getLicenseChoice().getExpression())));
                } catch (NullPointerException e) {
                    // Getting a NullPointerException on licenses is fine. It just means the component had none.
                } catch (Exception e) {
                    // This may be an actual error
                    System.err.println("An error occurred while getting licenses: \n");
                    e.printStackTrace();
                }

                // Set the component's unique ID
                try {
                    new_component.setUniqueID(cdx_component.getBomRef().replaceAll("@", ""));
                } catch (NullPointerException nullPointerException) {
                    System.err.println("Could not find component Unique ID (bom-ref), defaulting to component name.");
                    new_component.setUniqueID(cdx_component.getName());
                } catch (Exception e) {
                    System.err.println("Unexpected error trying to get Unique ID for component");
                }

                // Add component to component list
                components.put(new_component.getUniqueID().replaceAll("@", ""), new_component);
                components_left.add(new_component.getUniqueID().replaceAll("@", ""));

                // If a top component doesn't exist, make this new component the top component
                top_component = top_component == null ? new_component : top_component;

            }

        }

        // Add the top component to the sbom
        sbom.addComponent(null, top_component);

        // Create dependency collection
        Map<String, List<Dependency>> dependencies;
        try {
            // Attempt to get all dependencies from CycloneDX Object
            dependencies = json_sbom.getDependencies()
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    x -> {
                                        return x.getRef().replaceAll("@", "");
                                    },
                                    Dependency::getDependencies,
                                    (x, y) -> x
                            )
                    );
        } catch (NullPointerException nullPointerException) {
            // It failed, output error message and default dependencies to null
            System.err.println("Could not find dependencies from CycloneDX Object. " +
                    "Defaulting all components to point to head component. File: " + file_path);
            dependencies = null;
        } catch (IllegalStateException illegalStateException) {
            System.err.println("Error: duplicate keys found in dependencies and could not be handled in: " + file_path);
            dependencies = null;
        } catch (Exception exception) {
            System.err.println("Error: unexpected error when attempting to get dependencies: " + file_path);
            dependencies = null;
        }

        // If the dependency list isn't empty, call dependencyBuilder to construct dependencyTree
        // Otherwise, default the dependencyTree by adding all subcomponents as children to the top component
        if( dependencies != null ) {
            try {
                dependencyBuilder(dependencies, components, components_left, top_component, sbom, null);
            } catch (Exception e) {
                System.out.println("Error building dependency tree. Dependency tree may be incomplete for: " + file_path);
            }
        } else {
            try {
                for (Map.Entry<String, Component> comp : components.entrySet()) {
                    if (comp != null) { sbom.addComponent(top_component.getUUID(), comp.getValue()); }
                }
            } catch (Exception exception) {
                System.out.println("Could not default default the dependency tree. Dependency tree may be empty.");
                exception.printStackTrace();
            }
        }

        // This will take all the components that were not added in the dependencyTree through
        // dependencyBuilder and will tack each remaining component to the top component by default
        for(String remaining_component : components_left) {
            sbom.addComponent(top_component.getUUID(), components.get(remaining_component));
        }


        // Return SBOM object
        return sbom;
    }

    /**
     * Coverts CDX JSON SBOMs into internal SBOM objects
     *
     * @param file_path Path to CDX JSON SBOM
     * @return internal SBOM object
     * @throws ParseException
     */
    public static SBOM translatorCDXJSON(String file_path) throws ParseException {
        // Read the file at file_path into a string
        String contents = null;
        try {
            contents = new String(Files.readAllBytes(Paths.get(file_path)));
        }
        catch (IOException e) {
            System.err.println("Could not read file: " + file_path);
            return null;
        }

        return translatorCDXJSONContents(contents, file_path);
    }

    /**
     * A simple recursive function to build a dependency tree out of the CDX JSON SBOM
     *
     * @param dependencies  A map containing packaged components with their CDX bom-refs, pointing to dependencies
     * @param components    A map containing each Component with their bom-ref ID as a key
     * @param parent        Parent component to have dependencies connected to
     * @param sbom          The SBOM object
     */
    public static void dependencyBuilder(
            Map dependencies,
            HashMap components,
            Collection components_left,
            Component parent,
            SBOM sbom,
            Set<String> visited)
    {

        // If top component is null, return. There is nothing to process.
        if (parent == null) { return; }

        if (visited != null) {
            // Add this parent to the visited set
            visited.add(parent.getUniqueID().replaceAll("@", ""));
        }

        // Get the parent's dependencies as a list
        String parent_id = parent.getUniqueID().replaceAll("@", "");
        List<Dependency> children_ref = (List<Dependency>) dependencies.get(parent_id.replaceAll("@", ""));

        // If there are no
        if( children_ref == null ) { return; }

        // Cycle through each dependency the parent component has
        for (Dependency child_ref: children_ref) {
            // Retrieve the component the parent has a dependency for
            Component child = (Component) components.get(child_ref.getRef().replaceAll("@", ""));

            // If component is already in the dependency tree, add it as a child to the parent
            // Else, add it to the dependency tree while setting the parent
            if(sbom.hasComponent(child.getUUID())) {
                parent.addChild(child.getUUID());
            } else {
                sbom.addComponent(parent.getUUID(), child);
            }

            // If this is the first time this component is being added/referenced on dependencyTree
            // Remove the component from remaining component list
            if(components_left.contains(child_ref)) {
                components_left.remove(child_ref);
            }

            if (visited == null) {
                // This means we are in the top level component
                // Pass in a new hashset instead of the visited set
                visited = new HashSet<>();
                dependencyBuilder(dependencies, components, components_left, child, sbom, new HashSet<>());
            }
            else {
                // Only explore if we haven't already visited this component
                if (!visited.contains(child.getUniqueID())) {
                    // Pass the child component as the new parent into dependencyBuilder
                    dependencyBuilder(dependencies, components, components_left, child, sbom, visited);
                }
            }
        }
    }
}
