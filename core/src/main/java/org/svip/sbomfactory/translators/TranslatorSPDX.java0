package org.svip.sbomfactory.translators;

import org.cyclonedx.exception.ParseException;
import org.svip.sbom.model.Component;
import org.svip.sbom.model.PURL;
import org.svip.sbom.model.SBOM;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
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
public class TranslatorSPDX extends TranslatorCore {

    /**
     * Constants
     */
    public static final String TAG = "#####";

    public static final String UNPACKAGED_TAG = "##### Unpackaged files";

    public static final String PACKAGE_TAG = "##### Package";

    public static final String RELATIONSHIP_TAG = "##### Relationships";

    public static final String RELATIONSHIP_KEY = "Relationship: ";

    public static final String SPEC_VERSION_TAG = "SPDXVersion: ";

    public static final String ID_TAG = "SPDXID: ";

    public static final String TIMESTAMP_TAG = "Created: ";

    public static final String DOCUMENT_NAMESPACE_TAG = "DocumentNamespace: ";

    public static final String AUTHOR_TAG = "Creator: ";


    // Used as an identifier for main SBOM information. Sometimes used as reference in relationships to show header contains main component.
    public static final String DOCUMENT_REFERENCE_TAG = "SPDXRef-DOCUMENT";

    //Regex expression provided from: https://stackoverflow.com/questions/37615731/java-regex-for-uuid
    private static final Pattern uuid_pattern = Pattern.compile("urn:uuid:[a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8}");

    public TranslatorSPDX() {
        super("spdx");
    }


    /**
     * Coverts SPDX SBOMs into internal SBOM objects by its contents
     *
     * @param fileContents Contents of the SBOM to translate
     * @param file_path Original path to SPDX SBOM
     * @return internal SBOM object from contents
     */
    // TODO: Break into sub-methods
    @Override
    public SBOM translateContents(String fileContents, String file_path) throws IOException, ParseException {

        // Top level component information
        String sbom_serial_number;

        String product_id = "";

        // Collection for components, packaged and unpackaged
        // Key = SPDXID , Value = Component
        HashMap<String, Component> components = new HashMap<>();

        // Collection of packages, used  for adding head components to top component if in the header (SPDXRef-DOCUMENT)
        // Value (SPDX_ID)
        ArrayList<String> packages = new ArrayList<>();

        // Get SPDX file
        // Initialize BufferedReader along with current line
        BufferedReader br = new BufferedReader(new StringReader(fileContents));
        String current_line;

        bom_data.put("sbomVersion", "1");
        bom_data.put("format", "spdx");

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
            if (current_line.contains(": ")) {

                switch (current_line.split(": ", 2)[0] + ": ") {

                    case DOCUMENT_NAMESPACE_TAG:
                        // Attempt to get UUID from current line using regex
                        // replaceAll regex provided by:
                        // https://stackoverflow.com/questions/25852961/how-to-remove-brackets-character-in-string-java
//                        sbom_serial_number = Arrays.toString(
//                                uuid_pattern
//                                        .matcher(current_line)
//                                        .results()
//                                        .map(MatchResult::group)
//                                        .toArray(String[]::new)
//                        ).replaceAll("[\\[\\](){}]", "");

                        // If a UUID is found, set it as the SBOM serial number, otherwise default to DocumentNamespace value
//                        sbom_serial_number = (sbom_serial_number.isBlank()) // TODO: Verify change (sbom_serial_number was never null, only possibly empty)
//                                ? bom_data.get("DocumentNamespace")
//                                : sbom_serial_number;
                        sbom_serial_number = current_line.split(": ", 2)[1];

                        // Add DocumentNamespace value to sbom materials collection
                        bom_data.put("serialNumber", sbom_serial_number);
                        break;

                    case SPEC_VERSION_TAG:
                        bom_data.put("specVersion", current_line.split(": SPDX-", 2)[1]);
                        break;

                    case AUTHOR_TAG:
                        if(!bom_data.containsKey("author")) {
                            bom_data.put("author", current_line.split(": ", 2)[1]);
                        } else {
                            bom_data.put("author", bom_data.get("author") + " " + current_line.split(":", 2)[1]);
                        }
                        break;

                    case ID_TAG:
                        bom_data.put("id", current_line.split(": ", 2)[1]);
                        break;

                    case TIMESTAMP_TAG:
                        bom_data.put("timestamp", current_line.split(": ", 2)[1]);
                        break;

                    default:
                        bom_data.put(current_line.split(": ", 2)[0], current_line.split(": ", 2)[1]);

                }

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
                    if ( current_line.contains(": ")) { // TODO last unpackaged file -> first package results in a null component
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
        while ( current_line != null ) {

            // If new package/component is found
            if (current_line.contains(PACKAGE_TAG)) {

                // Temporary component collection of materials
                HashMap<String, String> component_materials = new HashMap<>();
                Set<String> cpes = new HashSet<>();
                Set<PURL> purls = new HashSet<>();
                Set<String> swids = new HashSet<>();


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

            }
            // If relationship key is found
            else if(current_line.contains(RELATIONSHIP_KEY)) {

                // Split and get and value of the line
                String relationship = current_line.split(RELATIONSHIP_KEY, 2)[1];

                // Split dependency relationship and store into relationships map depends on relationship type
                if (current_line.contains("DEPENDS_ON")) {

                    addDependency(
                            relationship.split(" DEPENDS_ON ")[0],
                            relationship.split(" DEPENDS_ON ")[1]
                    );

                } else if (current_line.contains("DEPENDENCY_OF")) {

                    addDependency(
                            relationship.split(" DEPENDENCY_OF ")[1],
                            relationship.split(" DEPENDENCY_OF ")[0]
                    );

                }  else if (current_line.contains("DESCRIBES")) {

                    product_id = relationship.split(" DESCRIBES ")[1];

                }

                current_line = br.readLine();

            }
            else {
                // if no package/component is found, get next line
                current_line = br.readLine();
            }
        }

        br.close();

        if (product_id.contains(DOCUMENT_REFERENCE_TAG)) {
            product_data.put("name", bom_data.get("DocumentName"));
            product_data.put("publisher", bom_data.get("N/A"));
            product_data.put("version", bom_data.get("N/A"));
            product_data.put("id", bom_data.get("id"));
            defaultTopComponent(product_data.get("id"), packages);
        } else {
            product = components.get(product_id);
        }

        // Create the new SBOM Object with top level data
        this.createSBOM();

        // Create the top level component
        // Build the dependency tree using dependencyBuilder
        try {
            this.dependencyBuilder(components, this.product, null);
        } catch (Exception e) {
            System.err.println("Error processing dependency tree.");
        }

        this.defaultDependencies(components, this.product);

        // Return SBOM object
        return this.sbom;

    }

}