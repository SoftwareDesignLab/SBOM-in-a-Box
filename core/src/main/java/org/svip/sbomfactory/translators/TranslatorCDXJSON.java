package org.svip.sbomfactory.translators;

import org.cyclonedx.exception.ParseException;
import org.cyclonedx.model.Bom;
import org.cyclonedx.model.BomReference;
import org.cyclonedx.model.Dependency;
import org.cyclonedx.parsers.JsonParser;
import org.svip.sbom.model.Component;
import org.svip.sbom.model.PURL;
import org.svip.sbom.model.SBOM;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * file: TranslatorCDXJSON.java
 * Coverts SPDX SBOMs into internal SBOM objects.
 * Compatible with CDX 1.4 JSON SBOMs
 *
 * @author Tyler Drake
 */
public class TranslatorCDXJSON extends TranslatorCore {
    public TranslatorCDXJSON() {
        super("json");
    }

    /**
     * Converts a file into an internal SBOM object
     *
     * @param fileContents String of file contents
     * @param file_path Path to file
     * @return internal SBOM object
     * @throws ParseException If file is not valid JSON
     */
    @Override
    protected SBOM translateContents(String fileContents, String file_path) throws IOException, ParseException {
        // Internal SBOM Object
        SBOM sbom;

        // Top level component for SBOM's dependencyTree
        Component top_component = null;

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
                    top_component_meta.getBomRef()
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

                // Get CPE, PURL, and SWIDs
                String cpe = cdx_component.getCpe() == null ? null : cdx_component.getCpe();
                PURL purl = cdx_component.getPurl() == null ? null : new PURL(cdx_component.getPurl());
                String swid = cdx_component.getSwid() == null ? null : String.valueOf(cdx_component.getSwid());

                // Create new component with a name, publisher, version along with CPEs/PURLs/SWIDs
                Component new_component = new Component(
                        cdx_component.getName(),
                        cdx_component.getPublisher(),
                        cdx_component.getVersion(),
                        Collections.singleton(cpe),
                        Collections.singleton(purl),
                        Collections.singleton(swid)
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
                new_component.setUniqueID(cdx_component.getBomRef());

                // Add component to component list
                components.put(new_component.getUniqueID(), new_component);

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
                                    Dependency::getRef,
                                    Dependency::getDependencies
                            )
                    );
        } catch (NullPointerException nullPointerException) {
            // I failed, ourput error message and default dependencies to null
            System.err.println("Could not find dependencies from CycloneDX Object. " +
                    "Defaulting all components to point to head component. File: " + file_path);
            dependencies = null;
        }


        // If the dependency list isn't empty, call dependencyBuilder to construct dependencyTree
        // Otherwise, default the dependencyTree by adding all subcomponents as children to the top component
        if( dependencies != null ) {
            try {
                this.dependencyBuilder(dependencies, components, top_component, sbom, null);
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


        // Return SBOM object
        return sbom;
    }

    /**
     * A simple recursive function to build a dependency tree out of the CDX JSON SBOM
     *
     * @param dependencies  A map containing packaged components with their CDX bom-refs, pointing to dependencies
     * @param components    A map containing each Component with their bom-ref ID as a key
     * @param parent        Parent component to have dependencies connected to
     * @param sbom          The SBOM object
     */
    @Override
    protected void dependencyBuilder(Object dependencies, HashMap<String, Component> components, Component parent, SBOM sbom, Set<String> visited) {
        // If top component is null, return. There is nothing to process.
        if (parent == null) return;

        // Get the parent's dependencies as a list
        List<String> childRefs = ((Map<String, List<Dependency>>) dependencies).get(parent.getUniqueID())
                .stream().map(BomReference::getRef).toList();

        if (visited != null) {
            // Add this parent to the visited set
            visited.add(parent.getUniqueID());
        }

        // Return if there are no children found
        if(childRefs.size() == 0) return;

        // Cycle through each dependency the parent component has
        for (String childRef : childRefs) {
            // Retrieve the component the parent has a dependency for
            Component child = components.get(childRef);

            addDependency(dependencies, components, parent, child, sbom, visited);
        }
    }
}
