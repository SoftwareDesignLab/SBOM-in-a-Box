package org.svip.sbomfactory.translators;

import org.svip.sbom.model.Component;
import org.svip.sbom.model.SBOM;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbom.model.uids.PURL;
import org.svip.sbomfactory.generators.utils.Debug;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * file: TranslatorSPDX.java
 * Coverts SPDX SBOMs into internal SBOM objects.
 * Compatible with SPDX 2.2 and SPDX 2.3
 *
 * @author Tyler Drake
 * @author Matt London
 * @author Ian Dunn
 * @author Ethan Numan
 */
public class TranslatorSPDX extends TranslatorCore {

    //#region Constants

    public static final String TAG = "#####";

    public static final String UNPACKAGED_TAG = "##### Unpackaged files";

    public static final String PACKAGE_TAG = "##### Package";

    public static final String RELATIONSHIP_TAG = "##### Relationships";

    public static final String EXTRACTED_LICENSE_TAG = "##### Extracted"; // starts with

    public static final String EXTRACTED_LICENSE_ID = "LicenseID";

    public static final String EXTRACTED_LICENSE_NAME = "LicenseName";

    public static final String EXTRACTED_LICENSE_TEXT = "ExtractedText";

    public static final String EXTRACTED_LICENSE_CROSSREF = "LicenseCrossReference";

    public static final String RELATIONSHIP_KEY = "Relationship";

    public static final String SPEC_VERSION_TAG = "SPDXVersion";

    public static final String ID_TAG = "SPDXID";

    public static final String TIMESTAMP_TAG = "Created";

    public static final String DOCUMENT_NAMESPACE_TAG = "DocumentNamespace";

    public static final String AUTHOR_TAG = "Creator";

    // Used as an identifier for main SBOM information. Sometimes used as reference in relationships to show header contains main component.
    public static final String DOCUMENT_REFERENCE_TAG = "SPDXRef-DOCUMENT";
    public static final String EXTERNAL_REFERENCE_TAG = "ExternalRef";

    private static final Pattern TAG_VALUE_PATTERN = Pattern.compile("(\\S+): (.+)");
    private static final Pattern EXTERNAL_REF_PATTERN = Pattern.compile(EXTERNAL_REFERENCE_TAG + ": (\\S*) (\\S*) (\\S*)");
    private static final Pattern RELATIONSHIP_PATTERN = Pattern.compile(RELATIONSHIP_KEY + ": (\\S*) (\\S*) (\\S*)");

    //#endregion

    //#region Constructors

    public TranslatorSPDX() {
        super("spdx");
    }

    //#endregion

    //#region Abstract Method Overrides

    /**
     * Coverts SPDX SBOMs into internal SBOM objects by its contents
     *
     * @param fileContents Contents of the SBOM to translate
     * @param file_path Original path to SPDX SBOM
     * @return internal SBOM object from contents
     */
    @Override
    protected SBOM translateContents(String fileContents, String file_path) throws TranslatorException {
        // Top level component information
        String product_id = "";
        // Collection for components, packaged and unpackaged
        // Key = SPDXID , Value = Component
        HashMap<String, Component> components = new HashMap<>();
        // Collection of package IDs used for adding head components to top component if in the header SPDXRef-DOCUMENT
        ArrayList<String> packages = new ArrayList<>();
        // Map of external licenses to mirror Component.externalLicenses attribute
        Map<String, Map<String, String>> externalLicenses = new HashMap<>();

        /*
            Top level SBOM data (metadata, etc.)
        */

        fileContents = fileContents.replaceAll("\r", ""); // Remove carriage return characters if windows
        int firstIndex = fileContents.indexOf(TAG); // Find first index of next "section"
        String header;

        // If no tags found, assume the header is the only part of the file
        if (firstIndex == -1) header = fileContents;
        else {
            header = fileContents.substring(0, firstIndex - 2); // Remove newlines as well
            fileContents = fileContents.substring(firstIndex); // Remove all header info from fileContents
        }

        this.parseHeader(header);
        bom_data.put("sbomVersion", "1");
        bom_data.put("format", "spdx");

        /*
            Relationships
         */
        List<String> lines = new ArrayList<>(List.of(fileContents.split("\n")));
        // Find all relationships in the file contents regardless of where they are
        Matcher relationship = RELATIONSHIP_PATTERN.matcher(fileContents);
        while(relationship.find()) {
            switch(relationship.group(2)) {
                case "DEPENDS_ON" -> addDependency( // TODO verify
                        relationship.group(1),
                        relationship.group(3)
                );
                case "DEPENDENCY_OF" -> addDependency( // TODO verify
                        relationship.group(3),
                        relationship.group(1)
                );
                case "DESCRIBES" -> product_id = relationship.group(3);
            }
            lines.remove(relationship.group()); // Remove parsed relationship from contents
        }
        fileContents = String.join("\n", lines); // Remove all relationships from fileContents

        /*
            Extracted Licensing Info
         */

        String extractedLicenseContent = getTagContents(fileContents, EXTRACTED_LICENSE_TAG);
        List<String> extractedLicenses = List.of(extractedLicenseContent.split("\n\n"));

        for (String extractedLicenseBlock : extractedLicenses) {
            if (extractedLicenseBlock.equals("")) continue;
            this.parseExternalLicense(extractedLicenseBlock, externalLicenses);
        }

        /*
            Files
         */

        String unpackagedFilesContents = getTagContents(fileContents, UNPACKAGED_TAG);
        List<String> files = List.of(unpackagedFilesContents.split("\n\n")); // Split over newline

        for(String fileBlock : files) {
            if (fileBlock.equals("")) continue;
            Component file = this.buildFile(fileBlock);

            // Add unpackaged file to components
            this.components.put(file.getUniqueID(), file);
        }

        /*
            Packages
         */

        String packageContents = getTagContents(fileContents, PACKAGE_TAG);
        List<String> packageList = Stream.of(packageContents.split("\n\n")).filter(pkg -> !pkg.contains(TAG)).toList();

        for (String pkg : packageList) {
            if (pkg.equals("")) continue;
            Component component = buildComponent(pkg, externalLicenses);

            // Add packaged component to components list
            this.loadComponent(component);

            // Add packaged component to packages list as well
            packages.add(component.getUniqueID());
        }

        // Build the top component of the SBOM (containing metadata)
        if (product_id.contains(DOCUMENT_REFERENCE_TAG)) {
            product_data.put("name", bom_data.get("DocumentName"));
            product_data.put("publisher", bom_data.get("Unknown"));
            product_data.put("version", bom_data.get("N/A"));
            product_data.put("id", bom_data.get("id"));
            defaultTopComponent(product_data.get("id"), packages);
        } else {
            topComponent = components.get(product_id);
        }

        // Create the new SBOM Object with top level data
        this.createSBOM();

        // Create the top level component
        // Build the dependency tree using dependencyBuilder
        try {
            this.dependencyBuilder(components, this.topComponent, null);
        } catch (Exception e) {
            Debug.log(Debug.LOG_TYPE.ERROR, "Error processing dependency tree.");
            Debug.log(Debug.LOG_TYPE.EXCEPTION, e.getMessage());
        }

        this.defaultDependencies(this.topComponent);

        // Return the final SBOM object
        return sbom;
    }

    //#endregion

    //#region Helper Methods

    /**
     * Private helper method to get all tag-value pairs categorized under the specified tag (ex. ##### Unpackaged
     * Files). The tags will be located anywhere in the file; the order of tags does not impact translation of the SBOM.
     *
     * @param fileContents The file contents to get the tag from.
     * @param tag The tag to get all tag-value pairs of.
     * @return An "excerpt" from the {@code fileContents} string containing all tag-value pairs categorized under {@code
     * tag}.
     */
    private String getTagContents(String fileContents, String tag) {
        String tagContents = "";
        int firstIndex;
        int lastIndex;

        while (fileContents.contains(tag)) {
            // Get boundaries of this tag
            firstIndex = fileContents.indexOf(tag);
            lastIndex = fileContents.indexOf(TAG, firstIndex + 1);

            // If another tag is not found, last index goes to end of file
            if (lastIndex == -1) lastIndex = fileContents.length();

            // Use this data to update tagContents with the found tag
            tagContents += fileContents.substring(firstIndex, lastIndex); // Remove newline
            fileContents = fileContents.substring(0, firstIndex) + fileContents.substring(lastIndex);
        }

        return tagContents;
    }

    /**
     * Private helper method to process the header/metadata of an SPDX document and put all relevant data into the
     * {@code bom_data} map in {@code TranslatorCore}.
     *
     * @param header The header data of the SPDX document.
     */
    private void parseHeader(String header) {
        // Process header TODO throw error if required fields are not found. Create enum with all tags?
        Matcher m = TAG_VALUE_PATTERN.matcher(header);
        while(m.find()) {
            switch (m.group(1)) {
                case DOCUMENT_NAMESPACE_TAG -> bom_data.put("serialNumber", m.group(2));
                case SPEC_VERSION_TAG -> bom_data.put("specVersion", m.group(2));
                case AUTHOR_TAG -> {
                    if (!bom_data.containsKey("author")) bom_data.put("author", m.group(2));
                    else bom_data.put("author", bom_data.get("author") + " " + m.group(2));
                }
                case ID_TAG -> bom_data.put("id", m.group(1));
                case TIMESTAMP_TAG -> bom_data.put("timestamp", m.group(2));
                default -> bom_data.put(m.group(1), m.group(2));
            }
        }
    }

    /**
     * Private helper method to process an external license in an SPDX document and append all relevant data into
     * the {@code externalLicenses} map with each entry having an ID (key) and name (value).
     *
     * @param extractedLicenseBlock An extracted licensing information "block" in the document.
     * @param externalLicenses The map of external licenses to append to.
     */
    private void parseExternalLicense(String extractedLicenseBlock, Map<String, Map<String, String>> externalLicenses) {
        // Required fields
        String id = null;
        String name = null;

        // Optional attributes
        Map<String, String> attributes = new HashMap<>();

        Matcher m = TAG_VALUE_PATTERN.matcher(extractedLicenseBlock);
        while(m.find()) {
            switch (m.group(1)) {
                case EXTRACTED_LICENSE_ID -> id = m.group(2);
                case EXTRACTED_LICENSE_NAME -> name = m.group(2);
                case EXTRACTED_LICENSE_TEXT -> {
                    // Find a multiline block. If one does not exist, just put the one line of text.
                    Matcher extractedText = Pattern.compile("<text>(\\X*)</text>").matcher(extractedLicenseBlock);
                    String text = m.group(2); // The first line of text.
                    if (extractedText.find()) text = extractedText.group(1); // Multiline text, if any.
                    attributes.put("text", text); // Add the attribute
                }
                case EXTRACTED_LICENSE_CROSSREF -> attributes.put("crossRef", m.group(2));
                default -> {} // TODO more fields?
            }
        }

        if (id != null && name != null) {
            attributes.put("name", name);
            externalLicenses.put(id, attributes);
            Debug.log(Debug.LOG_TYPE.DEBUG, "External license found with ID " + id + " and name " + name);
        } else {
            Debug.log(Debug.LOG_TYPE.WARN, String.format("External license skipped due to one or more of the " +
                    "following fields not existing:\nID: %s\nName: %s", id, name));
        }
    }

    /**
     * Private helper method to process an unpackaged file in an SPDX document and use its data to build a
     * {@code Component} representation.
     *
     * @param fileBlock An unpackaged file "block" in the document.
     * @return A {@code Component} with the data of the file "block".
     */
    private Component buildFile(String fileBlock) {
        Matcher m = TAG_VALUE_PATTERN.matcher(fileBlock);
        HashMap<String, String> file_materials = new HashMap<>();
        while(m.find()) file_materials.put(m.group(1), m.group(2));

        // Create new component from materials
        Component unpackaged_component = new Component(
                file_materials.get("FileName"),
                "Unknown",
                file_materials.get("PackageVersion"),
                file_materials.get("SPDXID")
        );
        unpackaged_component.setUnpackaged(true);

        return unpackaged_component;
    }

    /**
     * Private helper method to process a package in an SPDX document and use its data to build a {@code Component}
     * representation.
     *
     * @param packageBlock A package "block" in the document.
     * @return A {@code Component} with the data of the package "block".
     */
    private Component buildComponent(String packageBlock, Map<String, Map<String, String>> externalLicenses) {
        Map<String, String> componentMaterials = new HashMap<>();
        Set<String> cpes = new HashSet<>();
        Set<String> purls = new HashSet<>();
        Set<String> swids = new HashSet<>();

        Matcher m = TAG_VALUE_PATTERN.matcher(packageBlock);

        while (m.find()) {
            if (!m.group(1).equals(EXTERNAL_REFERENCE_TAG)) {
                componentMaterials.put(m.group(1), m.group(2));
                continue;
            }

            // Special case for external references
            Matcher externalRef = EXTERNAL_REF_PATTERN.matcher(m.group());
            if (!externalRef.find()) continue;

            switch(externalRef.group(2).toLowerCase()) {
                case "cpe23type" -> { if(!cpes.contains(externalRef)) cpes.add(externalRef.group(3)); }
                case "purl" -> { if(!purls.contains(externalRef)) purls.add(externalRef.group(3)); }
                case "swid" -> { if(!swids.contains(externalRef)) swids.add(externalRef.group(3)); }
            }
        }

        // Cleanup package originator
        String supplier = "Unknown"; // Default value of unknown
        if (componentMaterials.get("PackageSupplier") != null) {
            supplier = componentMaterials.get("PackageSupplier");
        } else if (componentMaterials.get("PackageOriginator") != null) {
            supplier = componentMaterials.get("PackageOriginator");
        }

        // Create new component from required information
        Component component = new Component(
                componentMaterials.get("PackageName"),
                supplier,
                componentMaterials.get("PackageVersion"),
                componentMaterials.get("SPDXID"));

        // Append CPEs and Purls
        component.setCpes(cpes);
        component.setPurls(purls);
        if (component.getPurls().size() > 0) {
            try {
                PURL purl = new PURL(component.getPurls().stream().toList().get(0));
                component.setGroup(purl.getType());
            } catch (Exception ignored) {}
        }
        component.setSwids(swids);

        // License materials map
        HashSet<String> licenses = new HashSet<>();

        // Get licenses from component materials and split them by 'AND' tag, store them into HashSet and add them to component object
        if (componentMaterials.get("PackageLicenseConcluded") != null)
            licenses.addAll(Arrays.asList(componentMaterials.get("PackageLicenseConcluded").split(" AND ")));
        if (componentMaterials.get("PackageLicenseDeclared") != null)
            licenses.addAll(Arrays.asList(componentMaterials.get("PackageLicenseDeclared").split(" AND ")));

        // Add external licenses found
        List<String> externalLicensesToRemove = new ArrayList<>();
        for(String license : licenses) {
            Map<String, String> attributes = externalLicenses.get(license);
            if (attributes != null) {
                component.addExtractedLicense(
                        license,
                        attributes.get("name"),
                        attributes.get("text"),
                        attributes.get("crossRef"));

                externalLicensesToRemove.add(license);
            }
        }
        externalLicensesToRemove.forEach(licenses::remove); // Remove all found external licenses

        // Clean up all other licenses
        licenses = // Remove NONE/NOASSERTION as well as any extracted licenses (IDs containing LicenseRef).
                (HashSet<String>) licenses.stream().filter(l ->
                                !l.equals("NONE") && !l.equals("NOASSERTION"))
                        .collect(Collectors.toSet());

        component.setLicenses(licenses);



        // Packages hashing info
        Hash packageHash;
        if (componentMaterials.get("PackageChecksum") != null){
            String[] packageChecksum = componentMaterials.get("PackageChecksum").split(" ");
            packageHash = new Hash(packageChecksum[0].substring(0,packageChecksum[0].length()-1), packageChecksum[1]);
            component.addHash(packageHash);
        }



        // Other package info TODO tests
        String packageDownloadLocation = componentMaterials.get("PackageDownloadLocation");
        String filesAnalyzed = componentMaterials.get("FilesAnalyzed"); // true or false
        String packageVerificationCode = componentMaterials.get("PackageVerificationCode");





        // PackageDownloadLocation
        if (packageDownloadLocation != null
                && !packageDownloadLocation.equals("NONE") && !packageDownloadLocation.equals("NOASSERTION")) {
            component.setDownloadLocation(packageDownloadLocation);
        }

        // FilesAnalyzed
        if (filesAnalyzed != null) {
            component.setFilesAnalyzed(filesAnalyzed.equalsIgnoreCase("true"));
        }

        // PackageVerificationCode
        if (packageVerificationCode != null
                && !packageVerificationCode.equals("NONE") && !packageVerificationCode.equals("NOASSERTION")) {
            component.setVerificationCode(packageVerificationCode);
        }


        return component;
    }
}
