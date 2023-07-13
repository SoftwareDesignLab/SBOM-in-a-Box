package org.svip.sbomfactory.serializers.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.builderfactory.SPDX23SBOMBuilderFactory;
import org.svip.builders.component.SPDX23PackageBuilder;
import org.svip.componentfactory.SPDX23PackageBuilderFactory;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.utils.Debug;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    public static final String TAG = "###";

    public static final String SEPARATOR = ": ";

    public static final String UNPACKAGED_TAG = "### Unpackaged files";

    public static final String PACKAGE_TAG = "### Package";

    public static final String RELATIONSHIP_TAG = "### Relationships";

    public static final String EXTRACTED_LICENSE_TAG = "### Extracted"; // starts with

    public static final String EXTRACTED_LICENSE_ID = "LicenseID";

    public static final String EXTRACTED_LICENSE_NAME = "LicenseName";

    public static final String EXTRACTED_LICENSE_TEXT = "ExtractedText";

    public static final String EXTRACTED_LICENSE_CROSSREF = "LicenseCrossReference";

    public static final String RELATIONSHIP_KEY = "Relationship";

    public static final String SPEC_VERSION_TAG = "SPDXVersion";

    public static final String ID_TAG = "SPDXID";

    public static final String TIMESTAMP_TAG = "Created";

    public static final String DOCUMENT_NAME_TAG = "DocumentName";

    public static final String DOCUMENT_NAMESPACE_TAG = "DocumentNamespace";

    public static final String DATA_LICENSE_TAG = "DataLicense";

    public static final String LICENSE_LIST_VERSION_TAG = "LicenseListVersion";

    public static final String CREATOR_TAG = "Creator";

    // Used as an identifier for main SBOM information. Sometimes used as reference in relationships to show header contains main component.
    public static final String DOCUMENT_REFERENCE_TAG = "SPDXRef-DOCUMENT";
    public static final String EXTERNAL_REFERENCE_TAG = "ExternalRef";

    private static final Pattern TAG_VALUE_PATTERN = Pattern.compile("(\\S+)" + SEPARATOR + "(.+)");
    private static final Pattern EXTERNAL_REF_PATTERN = Pattern.compile(EXTERNAL_REFERENCE_TAG + SEPARATOR +
            "(\\S*) (\\S*) (\\S*)");
    private static final Pattern RELATIONSHIP_PATTERN = Pattern.compile(RELATIONSHIP_KEY + SEPARATOR +
            "(\\S*) (\\S*) (\\S*)");
    // https://regex101.com/r/0jMwAU/1
    private static final Pattern CREATOR_PATTERN = Pattern.compile(
            "^(?:(Person|Organization): )(.+?)(?:$| (?:\\((.*)\\))?$)");
    private static final Pattern TOOL_PATTERN = Pattern.compile("^Tool: (?:(.*)-)(.*)$", Pattern.CASE_INSENSITIVE);

    //#endregion

    /**
     * Deserializes an SPDX 2.3 tag-value SBOM from a string.
     *
     * @param fileContents The file contents of the SPDX 2.3 tag-value SBOM to deserialize.
     * @return The deserialized SPDX 2.3 SBOM object.
     */
    @Override
    public SPDX23SBOM readFromString(String fileContents) {
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
                // NAME
                case DOCUMENT_NAME_TAG -> sbomBuilder.setName(mHeader.group(2));
                // UID
                case DOCUMENT_NAMESPACE_TAG -> sbomBuilder.setUID(mHeader.group(2));
                // SPEC VERSION
                case SPEC_VERSION_TAG -> sbomBuilder.setSpecVersion(mHeader.group(2).substring(mHeader.group(2).lastIndexOf('-') + 1)); // Get text after "SPDX-"
                // LICENSE
                case DATA_LICENSE_TAG -> sbomBuilder.addLicense(mHeader.group(2));
                // LICENSE LIST VERSION
                case LICENSE_LIST_VERSION_TAG -> sbomBuilder.setSPDXLicenseListVersion(mHeader.group(2));
                // AUTHORS
                case CREATOR_TAG -> {
                    Matcher toolMatcher = TOOL_PATTERN.matcher(mHeader.group(2));
                    while (toolMatcher.find()) {
                        CreationTool tool = new CreationTool();
                        tool.setName(toolMatcher.group(1));
                        tool.setVersion(toolMatcher.group(2));
                        creationData.addCreationTool(tool);
                    }

                    Contact creator = parseCreator(mHeader.group(2));
                    if (creator == null) continue;

                    // If we find an organization, set it to the supplier if there isn't already one. Otherwise,
                    // add another author with the contact info
                    if (mHeader.group(2).toLowerCase().startsWith("organization") &&
                            creationData.getSupplier() != null &&
                            !creationData.getSupplier().getName().isEmpty()) {

                        Organization supplier = new Organization(creator.getName(), null);
                        supplier.addContact(creator);
                        creationData.setSupplier(supplier);
                    } else {
                        creationData.addAuthor(creator);
                    }
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
            Relationship r = new Relationship(relationship.group(3), relationship.group(2));
            sbomBuilder.addRelationship(relationship.group(1), r);
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

            // TODO complete missing fields

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
                Matcher externalRefMatcher = EXTERNAL_REF_PATTERN.matcher(mPackages.group());
                if (!externalRefMatcher.find()) continue;
                switch(externalRefMatcher.group(2).toLowerCase()) {
                    case "cpe23type" -> {
                        // CPE
                        packageBuilder.addCPE(externalRefMatcher.group(3));
                    }
                    case "purl" -> {
                        // PURL
                        packageBuilder.addPURL(externalRefMatcher.group(3));
                    }
                    // EXTERNAL REFERENCES - DEFAULT CASE
                    default -> {
                        ExternalReference externalRef = new ExternalReference(externalRefMatcher.group(1),
                                externalRefMatcher.group(3), externalRefMatcher.group(2));
                        packageBuilder.addExternalReference(externalRef);
                    }
                }
            }
            // SUPPLIER
            // Cleanup package originator
            if (componentMaterials.get("PackageSupplier") != null) {
                Contact supplier = parseCreator(componentMaterials.get("PackageSupplier"));
                if (supplier != null) {
                    Organization org = new Organization(supplier.getName(), null);
                    org.addContact(supplier);
                    packageBuilder.setSupplier(org);
                }
            }

            // AUTHOR
            if (componentMaterials.get("PackageOriginator") != null) {
                packageBuilder.setAuthor(componentMaterials.get("PackageOriginator"));
            }

            // Set required information
            packageBuilder.setName(componentMaterials.get("PackageName"));
            packageBuilder.setVersion(componentMaterials.get("PackageVersion"));
            packageBuilder.setUID(componentMaterials.get("SPDXID"));

            // LICENSE EXPRESSION
            LicenseCollection licenseCollection = new LicenseCollection();
            if (componentMaterials.get("PackageLicenseConcluded") != null) {
                licenseCollection.addConcludedLicenseString(componentMaterials.get("PackageLicenseConcluded"));
            }
            if (componentMaterials.get("PackageLicenseDeclared") != null) {
                licenseCollection.addDeclaredLicense(componentMaterials.get("PackageLicenseDeclared"));
            }
            if (componentMaterials.get("PackageLicenseInfoFromFiles") != null) {
                licenseCollection.addLicenseInfoFromFile(componentMaterials.get("PackageLicenseInfoFromFiles"));
            }
            packageBuilder.setLicenses(licenseCollection);

            // HASHES
            // Packages hashing info
            if (componentMaterials.get("PackageChecksum") != null){
                String[] packageChecksum = componentMaterials.get("PackageChecksum").split(" ");
                packageBuilder.addHash(packageChecksum[0].substring(0,packageChecksum[0].length()-1), packageChecksum[1]);
            }

            // Other package info
            // DOWNLOAD LOCATION
            packageBuilder.setDownloadLocation(componentMaterials.get("PackageDownloadLocation"));
            // FILES ANALYZED
            packageBuilder.setFilesAnalyzed(Objects.equals(componentMaterials.get("FilesAnalyzed"), "true"));
            // PACKAGE VERIFICATION CODE
            packageBuilder.setVerificationCode(componentMaterials.get("PackageVerificationCode"));
            // HOMEPAGE
            packageBuilder.setHomePage(componentMaterials.get("PackageHomePage"));
            // SOURCE INFO
            packageBuilder.setSourceInfo(componentMaterials.get("PackageSourceInfo"));
            // COMMENT
            packageBuilder.setComment(componentMaterials.get("PackageComment"));
            // COPYRIGHT
            packageBuilder.setCopyright(componentMaterials.get("PackageCopyrightText"));
            // ATTRIBUTION TEXT
            packageBuilder.setAttributionText(componentMaterials.get("PackageAttributionText"));
            // TYPE
            packageBuilder.setType(componentMaterials.get("PrimaryPackagePurpose"));
            // RELEASE DATE
            packageBuilder.setReleaseDate(componentMaterials.get("ReleaseDate"));
            // BUILT DATE
            packageBuilder.setBuildDate(componentMaterials.get("BuiltDate"));
            // VALID UNTIL DATE
            packageBuilder.setValidUntilDate(componentMaterials.get("ValidUntilDate"));

            // TODO check for/complete missing fields

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

    private Contact parseCreator(String creator) {
        Matcher creatorMatcher = CREATOR_PATTERN.matcher(creator);
        if (!creatorMatcher.find()) return null;

        return new Contact(creatorMatcher.group(2), creatorMatcher.group(3), null);
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
