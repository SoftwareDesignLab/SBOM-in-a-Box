package org.svip.sbomfactory.serializers.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.internal.matchers.Or;
import org.svip.builderfactory.SPDX23SBOMBuilderFactory;
import org.svip.builders.component.SPDX23PackageBuilder;
import org.svip.componentfactory.SPDX23PackageBuilderFactory;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.old.Component;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbom.model.uids.PURL;
import org.svip.sbomfactory.generators.utils.Debug;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * File: SPDX23TagValueDeserializer.java
 * This class implements the Deserializer interface and the Jackson StdDeserializer to provide all functionality to
 * read an SPDX 2.3 SBOM object from an SPDX 2.3 tag-value file string.
 *
 * @author Ian Dunn
 * @author Tyler Drake
 * @author Matt London
 * @author Ethan Numan
 * @author Thomas Roman
 */
public class SPDX23TagValueDeserializer implements Deserializer {

    //#region Constants

    public static final String TAG = "#####";

    public static final String SEPARATOR = ": ";

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

    private static final Pattern TAG_VALUE_PATTERN = Pattern.compile("(\\S+)" + SEPARATOR + "(.+)");
    private static final Pattern EXTERNAL_REF_PATTERN = Pattern.compile(EXTERNAL_REFERENCE_TAG + SEPARATOR +
            "(\\S*) (\\S*) (\\S*)");
    private static final Pattern RELATIONSHIP_PATTERN = Pattern.compile(RELATIONSHIP_KEY + SEPARATOR +
            "(\\S*) (\\S*) (\\S*)");

    //#endregion

    /**
     * Deserializes an SPDX 2.3 tag-value SBOM from a string.
     *
     * @param fileContents The file contents of the SPDX 2.3 tag-value SBOM to deserialize.
     * @return The deserialized SPDX 2.3 SBOM object.
     */
    @Override
    public SBOM readFromString(String fileContents) {
        // Map of external licenses to mirror Component.externalLicenses attribute
        Map<String, Map<String, String>> externalLicenses = new HashMap<>();

        // initialize builders
        SPDX23SBOMBuilderFactory sbomFactory = new SPDX23SBOMBuilderFactory();
        SPDX23Builder sbomBuilder = sbomFactory.createBuilder();
        SPDX23PackageBuilderFactory packageFactory = new SPDX23PackageBuilderFactory();
        SPDX23PackageBuilder packageBuilder = packageFactory.createBuilder();
        SPDX23PackageBuilderFactory fileFactory = new SPDX23PackageBuilderFactory();
        SPDX23PackageBuilder fileBuilder = fileFactory.createBuilder();

        // Metadata
        fileContents = fileContents.replaceAll("\r", ""); // Remove carriage return characters if windows
        int firstIndex = fileContents.indexOf(TAG); // Find first index of next "section"
        String header;

        // If no tags found, assume the header is the only part of the file
        if (firstIndex == -1) header = fileContents;
        else {
            header = fileContents.substring(0, firstIndex - 2); // Remove newlines as well
            fileContents = fileContents.substring(firstIndex); // Remove all header info from fileContents
        }

        // Process header TODO throw error if required fields are not found. Create enum with all tags?
        Matcher mHeader = TAG_VALUE_PATTERN.matcher(header);
        CreationData creationData = new CreationData();
        while(mHeader.find()) {
            switch (mHeader.group(1)) {
                // UID
                case DOCUMENT_NAMESPACE_TAG -> sbomBuilder.setUID(mHeader.group(2));
                // SPEC VERSION
                case SPEC_VERSION_TAG -> sbomBuilder.setSpecVersion(mHeader.group(2).substring(mHeader.group(2).lastIndexOf('-') + 1)); // Get text after "SPDX-"
                // AUTHORS
                case AUTHOR_TAG -> {
                    Contact author = new Contact(mHeader.group(2), "", "");
                    creationData.addAuthor(author);
                }
                // TIMESTAMP
                case TIMESTAMP_TAG -> creationData.setCreationTime(mHeader.group(2));
            }
        }
        // CREATION DATA
        sbomBuilder.setCreationData(creationData);

        // RELATIONSHIPS
        List<String> lines = new ArrayList<>(List.of(fileContents.split("\n")));
        // Find all relationships in the file contents regardless of where they are
        Matcher relationship = RELATIONSHIP_PATTERN.matcher(fileContents);
        while(relationship.find()) {
            switch(relationship.group(2)) {
                case "DEPENDS_ON" -> {
                    Relationship dependsOn = new Relationship(relationship.group(3), "DEPENDS_ON");
                    sbomBuilder.addRelationship(relationship.group(1), dependsOn);
                }
                case "DEPENDENCY_OF" -> {
                    Relationship dependencyOf = new Relationship(relationship.group(3), "DEPENDENCY_OF");
                    sbomBuilder.addRelationship(relationship.group(1), dependencyOf);
                }
                case "DESCRIBES" -> {
                    Relationship describes = new Relationship(relationship.group(3), "DESCRIBES");
                    sbomBuilder.addRelationship(relationship.group(1), describes);
                }
            }
            lines.remove(relationship.group()); // Remove parsed relationship from contents
        }
        fileContents = String.join("\n", lines); // Remove all relationships from fileContents

        // Licenses
        String extractedLicenseContent = getTagContents(fileContents, EXTRACTED_LICENSE_TAG);
        List<String> extractedLicenses = List.of(extractedLicenseContent.split("\n\n"));

        for (String extractedLicenseBlock : extractedLicenses) {
            if (extractedLicenseBlock.equals("")) continue;
            this.parseExternalLicense(extractedLicenseBlock, externalLicenses);
        }

        // FILES
        String unpackagedFilesContents = getTagContents(fileContents, UNPACKAGED_TAG);
        List<String> files = List.of(unpackagedFilesContents.split("\n\n")); // Split over newline
        for(String fileBlock : files) {
            if (fileBlock.equals("")) continue;
            Matcher mFiles = TAG_VALUE_PATTERN.matcher(fileBlock);
            HashMap<String, String> file_materials = new HashMap<>();
            while(mFiles.find()) file_materials.put(mFiles.group(1), mFiles.group(2));

            // Create new component from materials
            // FILE NAME
            fileBuilder.setName(file_materials.get("FileName"));
            // FILE VERSION
            fileBuilder.setVersion(file_materials.get("PackageVersion"));
            // FILE UID
            fileBuilder.setUID(file_materials.get("SPDXID"));

            // add component
            sbomBuilder.addSPDX23Component(fileBuilder.buildAndFlush());
        }

        // PACKAGES
        String packageContents = getTagContents(fileContents, PACKAGE_TAG);
        List<String> packageList = Stream.of(packageContents.split("\n\n")).filter(pkg -> !pkg.contains(TAG)).toList();

        for (String packageBlock : packageList) {
            if (packageBlock.equals("")) continue;
            Map<String, String> componentMaterials = new HashMap<>();
            Matcher mPackages = TAG_VALUE_PATTERN.matcher(packageBlock);
            while (mPackages.find()) {
                if (!mPackages.group(1).equals(EXTERNAL_REFERENCE_TAG)) {
                    componentMaterials.put(mPackages.group(1), mPackages.group(2));
                    continue;
                }

                // Special case for external references
                Matcher externalRef = EXTERNAL_REF_PATTERN.matcher(mPackages.group());
                if (!externalRef.find()) continue;

                switch(externalRef.group(2).toLowerCase()) {
                    case "cpe23type" -> {
                        // CPE
                        packageBuilder.addCPE(externalRef.group(3));
                    }
                    case "purl" -> {
                        // PURL
                        packageBuilder.addPURL(externalRef.group(3));
                    }
                }
            }
            // SUPPLIER
            // Cleanup package originator
            if (componentMaterials.get("PackageSupplier") != null) {
                Organization supplier = new Organization(componentMaterials.get("PackageSupplier"), "");
                packageBuilder.setSupplier(supplier);
            } else if (componentMaterials.get("PackageOriginator") != null) {
                Organization supplier = new Organization(componentMaterials.get("PackageOriginator"), "");
                packageBuilder.setSupplier(supplier);
            }

            // Set required information
            packageBuilder.setName(componentMaterials.get("PackageName"));
            packageBuilder.setVersion(componentMaterials.get("PackageVersion"));
            packageBuilder.setUID(componentMaterials.get("SPDXID"));

            // License materials map
            HashSet<String> licenses = new HashSet<>();

            // Get licenses from component materials and split them by 'AND' tag, store them into HashSet and add them to component object
            if (componentMaterials.get("PackageLicenseConcluded") != null)
                licenses.addAll(Arrays.asList(componentMaterials.get("PackageLicenseConcluded").split(" AND ")));
            if (componentMaterials.get("PackageLicenseDeclared") != null)
                licenses.addAll(Arrays.asList(componentMaterials.get("PackageLicenseDeclared").split(" AND ")));

            // LICENSES
            // Add external licenses found
            LicenseCollection licenseCollection = new LicenseCollection();
            List<String> externalLicensesToRemove = new ArrayList<>();
            for(String license : licenses) {
                Map<String, String> attributes = externalLicenses.get(license);
                if (attributes != null) {
                    licenseCollection.addLicenseInfoFromFile(license);
                    externalLicensesToRemove.add(license);
                }
            }
            externalLicensesToRemove.forEach(licenses::remove); // Remove all found external licenses
            packageBuilder.setLicenses(licenseCollection);

            // HASHES
            // Packages hashing info
            if (componentMaterials.get("PackageChecksum") != null){
                String[] packageChecksum = componentMaterials.get("PackageChecksum").split(" ");
                packageBuilder.addHash(packageChecksum[0].substring(0,packageChecksum[0].length()-1), packageChecksum[1]);
            }

            // Other package info TODO tests
            // DOWNLOAD LOCATION
            packageBuilder.setDownloadLocation(componentMaterials.get("PackageDownloadLocation"));
            // FILES ANALYZED
            packageBuilder.setFilesAnalyzed(Objects.equals(componentMaterials.get("FilesAnalyzed"), "true"));
            // PACKAGE VERIFICATION CODE
            packageBuilder.setVerificationCode(componentMaterials.get("PackageVerificationCode"));

            // build package
            sbomBuilder.addSPDX23Component(packageBuilder.buildAndFlush());
        }


        return sbomBuilder.buildSPDX23SBOM();
    }

    /**
     * Gets the ObjectMapper of the serializer to expose configuration.
     *
     * @return A reference to the ObjectMapper of the serializer.
     */
    @Override
    public ObjectMapper getObjectMapper() {
        // We don't need an objectmapper for tag value but removing this breaks tests
        return new ObjectMapper();
    }

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
}
