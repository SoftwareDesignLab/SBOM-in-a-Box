package org.svip.sbomfactory.translators;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.svip.sbom.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * file: TranslatorSPDX.java
 * Coverts SPDX SBOMs into internal SBOM objects.
 * Compatible with SPDX 2.2 and SPDX 2.3
 *
 * @author Tyler Drake
 * @author Matt London
 */
public class TranslatorSPDX {

    /**
     * Constants
     */
    private static final String TAG = "#####";

    private static final String UNPACKAGED_TAG = "##### Unpackaged files";

    private static final String PACKAGE_TAG = "##### Package";

    private static final String RELATIONSHIP_TAG = "##### Relationships";

    private static final String RELATIONSHIP_KEY = "Relationship: ";

    private static final String DOCUMENT_NAMESPACE_TAG = "DocumentNamespace: ";

    private static final String AUTHOR_TAG = "Creator: ";


    // Used as an identifier for main SBOM information. Sometimes used as reference in relationships to show header contains main component.
    private static final String DOCUMENT_REFERENCE_TAG = "SPDXRef-DOCUMENT";

    //Regex expression provided from: https://stackoverflow.com/questions/37615731/java-regex-for-uuid
    private static final Pattern uuid_pattern = Pattern.compile("[a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8}");


    /**
     * Coverts SPDX SBOMs into internal SBOM objects by its contents
     *
     * @param fileContents Contents of the SBOM to translate
     * @param file_path Original path to SPDX SBOM
     * @return internal SBOM object from contents
     */
    public static SBOM translatorSPDXContents(String fileContents, String file_path) throws IOException {
        // Create a new SBOM object
        SBOM sbom;

        // Top level component information
        Component top_component = null;
        String sbom_serial_number;

        // Collection for sbom materials
        // Key = Name , Value = Value
        HashMap<String, String> sbom_materials = new HashMap<>();

        // Creator information
        // Key = Name , Value = Value
        String author = "";

        // Collection for components, packaged and unpackaged
        // Key = SPDXID , Value = Component
        HashMap<String, Component> components = new HashMap<>();

        // Collection for dependencies, contains every single component, and what it relies on, if any
        // Key (SPDX_ID) = Component, Values (SPDX_ID) = Components it needs
        ArrayListMultimap<String, String> dependencies = ArrayListMultimap.create();

        // Collection of packages, used  for adding head components to top component if in the header (SPDXRef-DOCUMENT)
        // Value (SPDX_ID)
        List<String> packages = new ArrayList<>();

        // Get SPDX file
        // Initialize BufferedReader along with current line
        BufferedReader br = new BufferedReader(new StringReader(fileContents));
        String current_line;

        /**
         * Parse through top level SBOM data
         */
        // Get next line until end of file is found or un-packaged tag not found
        while ( (current_line = br.readLine()) != null
                && !current_line.contains(UNPACKAGED_TAG)
                && !current_line.contains(PACKAGE_TAG)
                && !current_line.contains(RELATIONSHIP_TAG)
                && !current_line.contains(RELATIONSHIP_KEY)
        ) {

            // If the document namespace (serial number) tag is found, attempt to extract the UUID
            // Else, if line with separator is found, split it and store into sbom materials as key:value
            if (current_line.contains(DOCUMENT_NAMESPACE_TAG)) {

                // Attempt to get UUID from current line using regex
                // replaceAll regex provided by:
                // https://stackoverflow.com/questions/25852961/how-to-remove-brackets-character-in-string-java
                sbom_serial_number = Arrays.toString(
                        uuid_pattern
                                .matcher(current_line)
                                .results()
                                .map(MatchResult::group)
                                .toArray(String[]::new)
                ).replaceAll("[\\[\\](){}]", "");

                // If a UUID is found, set it as the SBOM serial number, otherwise default to DocumentNamespace value
                sbom_serial_number = (sbom_serial_number == null)
                        ? sbom_materials.get("DocumentNamespace")
                        : sbom_serial_number;

                // Add DocumentNamespace value to sbom materials collection
                sbom_materials.put(current_line.split(": ", 2)[0], sbom_serial_number);

            } else if (current_line.contains(AUTHOR_TAG)) {

                String author_item = current_line.split(AUTHOR_TAG)[1];

                if(author_item.contains(": ")) {
                    if(author != "") { author += " ~ "; }
                    author += author_item.split(": ", 2)[1];
                }

            } else if (current_line.contains(": ")) {

                // Split current line by key:value, store into sbom materials collection
                sbom_materials.put(current_line.split(": ", 2)[0], current_line.split(": ", 2)[1]);
            }

        }


        /**
         * Parse through unpackaged files, add them to components HashSet
         */
        // While line isn't null, package tag, relationship tag, or a relationship key
        while ( current_line != null
                && !current_line.contains(PACKAGE_TAG)
                && !current_line.contains(RELATIONSHIP_TAG)
                && !current_line.contains(RELATIONSHIP_KEY)
        ) {
            if (current_line.contains(PACKAGE_TAG) || current_line.contains(RELATIONSHIP_TAG)) break;

            // Information for un-packaged component
            HashMap<String, String> file_materials = new HashMap<>();

            // If current line is empty (new un-packaged component)
            if (current_line.isEmpty()) {

                // Loop through the contents until the next empty line or tag
                while (!(current_line = br.readLine()).contains(TAG) && !current_line.isEmpty()) {

                    // If line contains separator, split line into Key:Value then store it into component materials map
                    if ( current_line.contains(": ")) {
                        file_materials.put(current_line.split(": ", 2)[0], current_line.split(": ", 2)[1]);
                    }
                }

                // Create new component from materials
                Component unpackaged_component = new Component(
                        file_materials.get("FileName"),
                        "Unknown",
                        file_materials.get("PackageVersion"),
                        file_materials.get("SPDXID")
                );
                unpackaged_component.setUnpackaged(true);

                // Add unpackaged file to components
                components.put(unpackaged_component.getUniqueID(), unpackaged_component);

            } else {
                current_line = br.readLine();
            }
        }


        /**
         * Parse through components, add them to components HashSet
         */
        // Loop through every Package until Relationships or end of file
        while ( current_line != null
                && !current_line.contains(RELATIONSHIP_TAG)
                && !current_line.contains(RELATIONSHIP_KEY)
        ) {
            if (current_line.contains(RELATIONSHIP_TAG)) break;

            // Temporary component collection of materials
            HashMap<String, String> component_materials = new HashMap<>();
            Set<String> cpes = new HashSet<>();
            Set<PURL> purls = new HashSet<>();
            Set<String> swids = new HashSet<>();

            // If new package/component is found
            if (current_line.contains(PACKAGE_TAG)) {

                // While in the same package/component
                while ( (current_line = br.readLine()) != null
                        && !current_line.contains(TAG)
                        && !current_line.contains(RELATIONSHIP_TAG)
                        && !current_line.contains(RELATIONSHIP_KEY))
                {
                    // Special case for CPEs, PURLs, and SWIDs
                    if (current_line.contains("ExternalRef: SECURITY")) {
                        // We have a CPE
                        String[] lineSplit = current_line.split(" ");
                        // Last element is the CPE
                        String cpe = lineSplit[lineSplit.length - 1];

                        cpes.add(cpe);

                        // Don't continue parsing after we add the special cases
                        continue;
                    }
                    else if (current_line.contains("ExternalRef: PACKAGE-MANAGER purl")) {
                        // We have a PURL
                        String[] lineSplit = current_line.split(" ");
                        // Last element is the PURL
                        String purl = lineSplit[lineSplit.length - 1];

                        purls.add(new PURL(purl));

                        // Don't continue parsing after we add the special cases
                        continue;
                    }
                    // TODO find examples of how SPDX represents SWID and implement that here

                    // If line isn't blank split it on separator and store into component collect as key:value
                    if (current_line.contains(": ")) {
                        component_materials.put(current_line.split(": ", 2)[0], current_line.split(": ", 2)[1]);
                    }
                }

                // Cleanup package originator
                String supplier = null;
                if(component_materials.get("PackageSupplier") != null) {
                    supplier = component_materials.get("PackageSupplier");
                } else if(component_materials.get("PackageOriginator") != null) {
                    supplier = component_materials.get("PackageOriginator");
                }

                if (supplier != null) {
                    supplier = supplier.contains("Person: ") && supplier.contains("<")
                            ? supplier.substring(8)
                            : supplier;
                }
                // Create new component from required information
                Component component = new Component(
                        component_materials.get("PackageName"),
                        supplier,
                        component_materials.get("PackageVersion"),
                        component_materials.get("SPDXID")
                );

                // Append CPEs and Purls
                component.setCpes(cpes);
                component.setPurls(purls);

                // License materials map
                HashSet<String> licenses = new HashSet<>();

                // Get licenses from component materials and split them by 'AND' tag, store them into HashSet and add them to component object
                if (component_materials.get("PackageLicenseConcluded") != null) {
                    licenses.addAll(Arrays.asList(component_materials.get("PackageLicenseConcluded").split(" AND ")));
                }
                if (component_materials.get("PackageLicenseDeclared") != null) {
                    licenses.addAll(Arrays.asList(component_materials.get("PackageLicenseDeclared").split(" AND ")));
                }

                // Remove any NONE licenses
                licenses.remove("NONE");
                component.setLicenses(licenses);

                // Add packaged component to components list
                components.put(component.getUniqueID(), component);

                // Add packaged component to packages list as well
                packages.add(component.getUniqueID());

            } else {
                // if no package/component is found, get next line
                current_line = br.readLine();
            }
        }

        // Parse through what is left (relationships, if there are any)
        while(current_line != null) {

            // If relationship key is found
            if(current_line.contains(RELATIONSHIP_KEY)) {

                // Split and get and value of the line
                String relationship = current_line.split(RELATIONSHIP_KEY, 2)[1];

                // Split dependency relationship and store into relationships map depends on relationship type
                if(current_line.contains("CONTAINS")) {

                    dependencies.put(
                            relationship.split(" CONTAINS ")[0],
                            relationship.split(" CONTAINS ")[1]
                    );

                } else if (current_line.contains("DEPENDS_ON")) {

                    dependencies.put(
                            relationship.split(" DEPENDS_ON ")[0],
                            relationship.split(" DEPENDS_ON ")[1]
                    );

                } else if (current_line.contains("DEPENDENCY_OF")) {

                    dependencies.put(
                            relationship.split(" DEPENDENCY_OF ")[1],
                            relationship.split(" DEPENDENCY_OF ")[0]
                    );

                } else if (current_line.contains("OTHER")) {

                    dependencies.put(
                            relationship.split(" OTHER ")[1],
                            relationship.split(" OTHER ")[0]
                    );

                } else if (current_line.contains("DESCRIBES")) {

                    // If document references itself as top component, make it the top component using sbom head information
                    // Otherwise, get the SPDXID for the component it references and make that the top component
                    top_component = relationship.split(" DESCRIBES ")[1].contains(DOCUMENT_REFERENCE_TAG)
                            ? new Component(sbom_materials.get("DocumentName"), "N/A", "N/A", sbom_materials.get("SPDXID") )
                            : components.get(relationship.split(" DESCRIBES ")[1]);

                    // If top component exists, and if it is SPDXID: SPDXRef-DOCUMENT, add top level components as its dependencies
                    // Then, add it as the top level component of the dependency tree
                    if( top_component != null && top_component.getUniqueID().contains(DOCUMENT_REFERENCE_TAG) ) {
                        dependencies.putAll(top_component.getUniqueID(), packages);
                        dependencies.remove(top_component.getUniqueID(), top_component.getUniqueID());
                    }
                }
            }

            current_line = br.readLine();

        }

        // Create the new SBOM Object with top level data
        try {
            sbom = new SBOM(
                    "spdx",
                    sbom_materials.get("SPDXVersion"),
                    "1",
                    author == "" ? "Unknown" : author,
                    sbom_materials.get("DocumentNamespace"),
                    sbom_materials.get("Created"),
                    null);
        } catch (Exception e) {
            System.err.println("Could not create SBOM object. File: " + file_path);
            e.printStackTrace();
            br.close();
            return null;
        }

        if(top_component!=null) { sbom.addComponent(null, top_component); }

        // Create the top level component
        // Build the dependency tree using dependencyBuilder
        try {
            dependencyBuilder(dependencies, components, top_component, sbom, null);
        } catch (Exception e) {
            System.err.println("Error processing dependency tree.");
        }

        br.close();

        // Return SBOM object
        return sbom;
    }

    /**
     * Coverts SPDX SBOMs into internal SBOM objects
     *
     * @param file_path Path to SPDX SBOM
     * @return internal SBOM object
     * @throws IOException
     */
    public static SBOM translatorSPDX(String file_path) throws IOException {
        // Get file_path contents and save it into a string
        String file_contents = "";
        try {
            file_contents = new String(Files.readAllBytes(Paths.get(file_path)));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: Unable to read file: " + file_path);
            return null;
        }

        return translatorSPDXContents(file_contents, file_path);
    }

    /**
     * A simple recursive function to build a dependency tree out of the SPDX SBOM
     *
     * @param dependencies  A map containing packaged components with their SPDX IDs, pointing to dependencies
     * @param components    A map containing each Component with their SPDX ID as a key
     * @param parent        Parent component to have dependencies connected to
     * @param sbom          The SBOM object
     */
    public static void dependencyBuilder(Multimap dependencies, HashMap components, Component parent, SBOM sbom, Set<String> visited) {

        // If top component is null, return. There is nothing to process.
        if (parent == null) { return; }

        if (visited != null) {
            // Add this parent to the visited set
            visited.add(parent.getUniqueID());
        }

        // Get the parent's dependencies as a list
        String parent_id = parent.getUniqueID();
        Collection<Object> children_SPDX = dependencies.get(parent_id);

        // Cycle through each dependency the parent component has
        for (Object child_SPDX : children_SPDX) {
            // Retrieve the component the parent has a dependency for
            Component child = (Component) components.get(child_SPDX);

            // If component is already in the dependency tree, add it as a child to the parent
            // Else, add it to the dependency tree while setting the parent
            if(sbom.hasComponent(child.getUUID())) {
                parent.addChild(child.getUUID());
            } else {
                sbom.addComponent(parent.getUUID(), child);
            }

            if (visited == null) {
                // This means we are in the top level component
                // Pass in a new hashset instead of the visited set
                visited = new HashSet<>();
                dependencyBuilder(dependencies, components, child, sbom, new HashSet<>());
            }
            else {
                // Only explore if we haven't already visited this component
                if (!visited.contains(child.getUniqueID())) {
                    // Pass the child component as the new parent into dependencyBuilder
                    dependencyBuilder(dependencies, components, child, sbom, visited);
                }
            }
        }
    }
}
