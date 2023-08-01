package org.svip.serializers.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23FileBuilder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23PackageBuilder;
import org.svip.sbom.factory.objects.SPDX23.SPDX23FileBuilderFactory;
import org.svip.sbom.factory.objects.SPDX23.SPDX23PackageBuilderFactory;
import org.svip.sbom.factory.objects.SPDX23.SPDX23SBOMBuilderFactory;
import org.svip.sbom.model.objects.SPDX23.SPDX23FileObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
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

    public static final String UNPACKAGED_TAG = "### Unpackaged Files";

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
        SPDX23FileBuilderFactory fileFactory = new SPDX23FileBuilderFactory();
        SPDX23FileBuilder fileBuilder = fileFactory.createBuilder();

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

        sbomBuilder.setFormat("SPDX");

        // Process header TODO throw error if required fields are not found. Create enum with all tags?
        Matcher mHeader = TAG_VALUE_PATTERN.matcher(header);
        CreationData creationData = new CreationData();
        List<String> creators = new ArrayList<>();
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
                case CREATOR_TAG -> creators.add(mHeader.group(2));
                // TIMESTAMP
                case TIMESTAMP_TAG -> creationData.setCreationTime(mHeader.group(2));
                // CREATOR COMMENT
                case "CreatorComment" -> creationData.setCreatorComment(mHeader.group(2));
                // DOCUMENT COMMENT
                case "DocumentComment" -> sbomBuilder.setDocumentComment(mHeader.group(2));
            }
        }
        parseSPDXCreatorInfo(creationData, creators);
        // CREATION DATA
        sbomBuilder.setCreationData(creationData);

        // RELATIONSHIPS
        List<String> lines = new ArrayList<>(List.of(fileContents.split("\n")));
        // Find all relationships in the file contents regardless of where they are
        Matcher relationship = RELATIONSHIP_PATTERN.matcher(fileContents);
        while(relationship.find()) {
            Relationship r = new Relationship(relationship.group(3), relationship.group(2));

            String nextLine;
            try{
                nextLine = lines.get(lines.indexOf(relationship.group()) + 1);
            }
            catch (IndexOutOfBoundsException e){
                break;
            }
            if (nextLine.startsWith("RelationshipComment: ")) {
                r.setComment(nextLine.substring(nextLine.indexOf(" ") + 1));
                lines.remove(nextLine);
            }

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
        List<String> files = new ArrayList<>(List.of(unpackagedFilesContents.split("\n\n"))); // Split over newline
        files.remove(UNPACKAGED_TAG);

        for(String fileBlock : files) {
            if (fileBlock.strip().equals("")) continue;
            sbomBuilder.addSPDX23Component(buildFile(fileBuilder, fileBlock));
        }

        // PACKAGES
        String packageContents = getTagContents(fileContents, PACKAGE_TAG);
        List<String> packageList = Stream.of(packageContents.split("\n\n")).filter(pkg -> !pkg.contains(TAG)).toList();

        for (String packageBlock : packageList) {
            if (packageBlock.strip().equals("")) continue;
            sbomBuilder.addSPDX23Component(buildPackage(packageBuilder, packageBlock));
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

    protected static Contact parseSPDXCreator(String creator) {
        Matcher creatorMatcher = CREATOR_PATTERN.matcher(creator);
        if (!creatorMatcher.find()) return null;

        return new Contact(creatorMatcher.group(2), creatorMatcher.group(3), null);
    }

    protected static void parseSPDXCreatorInfo(CreationData data, List<String> creatorInfo) {
        for (String creator : creatorInfo) {
            Matcher toolMatcher = SPDX23TagValueDeserializer.TOOL_PATTERN.matcher(creator);
            while (toolMatcher.find()) {
                CreationTool tool = new CreationTool();
                tool.setName(toolMatcher.group(1));
                tool.setVersion(toolMatcher.group(2));
                data.addCreationTool(tool);
            }

            Contact contact = SPDX23TagValueDeserializer.parseSPDXCreator(creator);
            if (contact == null) continue;

            // If we find an organization, set it to the supplier if there isn't already one. Otherwise,
            // add another author with the contact info
            if (creator.toLowerCase().startsWith("organization") &&
                    (data.getSupplier() == null || data.getSupplier().getName().isEmpty())) {

                Organization supplier = new Organization(contact.getName(), null);
                supplier.addContact(contact);
                data.setSupplier(supplier);
            } else {
                data.addAuthor(contact);
            }
        }
    }

    private SPDX23PackageObject buildPackage(SPDX23PackageBuilder builder, String contents) {
        Map<String, String> componentMaterials = new HashMap<>();
        Matcher mPackages = TAG_VALUE_PATTERN.matcher(contents);

        while (mPackages.find()) {
            if (mPackages.group(1).equals(EXTERNAL_REFERENCE_TAG)) {
                Matcher externalRefMatcher = EXTERNAL_REF_PATTERN.matcher(mPackages.group());
                if (!externalRefMatcher.find()) continue;

                switch (externalRefMatcher.group(2).toLowerCase()) {
                    case "cpe23type" -> builder.addCPE(externalRefMatcher.group(3));
                    case "purl" -> builder.addPURL(externalRefMatcher.group(3));
                    default ->
                            builder.addExternalReference(new ExternalReference(externalRefMatcher.group(1), externalRefMatcher.group(3), externalRefMatcher.group(2)));
                }
            }
            else componentMaterials.put(mPackages.group(1), mPackages.group(2));
        }

        builder.setName(componentMaterials.get("PackageName"));
        builder.setVersion(componentMaterials.get("PackageVersion"));
        builder.setUID(componentMaterials.get("SPDXID"));

        // SUPPLIER
        if (componentMaterials.get("PackageSupplier") != null) {
            Contact supplier = parseSPDXCreator(componentMaterials.get("PackageSupplier"));
            if (supplier != null) {
                Organization org = new Organization(supplier.getName(), null);
                org.addContact(supplier);
                builder.setSupplier(org);
            }
        }

        // AUTHOR
        if (componentMaterials.get("PackageOriginator") != null) builder.setAuthor(componentMaterials.get("PackageOriginator"));

        // LICENSE EXPRESSION
        LicenseCollection licenseCollection = new LicenseCollection();
        if (componentMaterials.get("PackageLicenseConcluded") != null)
            licenseCollection.addConcludedLicenseString(componentMaterials.get("PackageLicenseConcluded"));
        if (componentMaterials.get("PackageLicenseDeclared") != null)
            licenseCollection.addDeclaredLicense(componentMaterials.get("PackageLicenseDeclared"));
        if (componentMaterials.get("PackageLicenseInfoFromFiles") != null)
            licenseCollection.addLicenseInfoFromFile(componentMaterials.get("PackageLicenseInfoFromFiles"));
        if (componentMaterials.get("PackageLicenseComments") != null)
            licenseCollection.setComment(componentMaterials.get("PackageLicenseComments"));

        builder.setLicenses(licenseCollection);

        // HASHES
        // Packages hashing info
        if (componentMaterials.get("PackageChecksum") != null) {
            Matcher mChecksum = TAG_VALUE_PATTERN.matcher(componentMaterials.get("PackageChecksum"));
            if (mChecksum.find())
                builder.addHash(mChecksum.group(1), mChecksum.group(2));
        }

        if (componentMaterials.get("PackageSummary") != null) {
            Description description = new Description(componentMaterials.get("PackageSummary"));
            if (componentMaterials.get("PackageDescription") != null)
                description.setDescription(componentMaterials.get("PackageDescription"));

            builder.setDescription(description);
        }

        // Other package info
        // DOWNLOAD LOCATION
        builder.setDownloadLocation(componentMaterials.get("PackageDownloadLocation"));
        // FILES ANALYZED
        builder.setFilesAnalyzed(Objects.equals(componentMaterials.get("FilesAnalyzed"), "true"));
        // PACKAGE VERIFICATION CODE
        builder.setVerificationCode(componentMaterials.get("PackageVerificationCode"));
        // HOMEPAGE
        builder.setHomePage(componentMaterials.get("PackageHomePage"));
        // SOURCE INFO
        builder.setSourceInfo(componentMaterials.get("PackageSourceInfo"));
        // COMMENT
        builder.setComment(componentMaterials.get("PackageComment"));
        // COPYRIGHT
        builder.setCopyright(componentMaterials.get("PackageCopyrightText"));
        // ATTRIBUTION TEXT
        builder.setAttributionText(componentMaterials.get("PackageAttributionText"));
        // TYPE
        builder.setType(componentMaterials.get("PrimaryPackagePurpose"));
        // RELEASE DATE
        builder.setReleaseDate(componentMaterials.get("ReleaseDate"));
        // BUILT DATE
        builder.setBuildDate(componentMaterials.get("BuiltDate"));
        // VALID UNTIL DATE
        builder.setValidUntilDate(componentMaterials.get("ValidUntilDate"));
        // FILE NAME
        builder.setFileName(componentMaterials.get("PackageFileName"));

        // build package
        return builder.buildAndFlush();
    }

    private SPDX23FileObject buildFile(SPDX23FileBuilder builder, String contents) {
        Matcher mFiles = TAG_VALUE_PATTERN.matcher(contents);
        HashMap<String, String> fileMaterials = new HashMap<>();
        while(mFiles.find()) fileMaterials.put(mFiles.group(1), mFiles.group(2));

        // Create new component from materials
        // FILE NAME
        builder.setName(fileMaterials.get("FileName"));
        // FILE UID
        builder.setUID(fileMaterials.get("SPDXID"));
        builder.setType(fileMaterials.get("FileType"));
        builder.setFileNotice(fileMaterials.get("FileNotice"));
        builder.setComment(fileMaterials.get("FileComment"));
        builder.setAuthor(fileMaterials.get("FileContributor"));
        builder.setCopyright(fileMaterials.get("FileCopyrightText"));
        builder.setAttributionText(fileMaterials.get("FileAttributionText"));

        // LICENSE EXPRESSION
        LicenseCollection licenseCollection = new LicenseCollection();
        if (fileMaterials.get("LicenseConcluded") != null)
            licenseCollection.addConcludedLicenseString(fileMaterials.get("LicenseConcluded"));
        if (fileMaterials.get("LicenseDeclared") != null)
            licenseCollection.addDeclaredLicense(fileMaterials.get("LicenseDeclared"));
        if (fileMaterials.get("LicenseInfoInFile") != null)
            licenseCollection.addLicenseInfoFromFile(fileMaterials.get("LicenseInfoInFile"));
        if (fileMaterials.get("LicenseComments") != null)
            licenseCollection.setComment(fileMaterials.get("LicenseComments"));

        builder.setLicenses(licenseCollection);

        if (fileMaterials.get("PackageChecksum") != null) {
            Matcher mChecksum = TAG_VALUE_PATTERN.matcher(fileMaterials.get("PackageChecksum"));
            if (mChecksum.find())
                builder.addHash(mChecksum.group(1), mChecksum.group(2));
        }

        // add component
        return builder.buildAndFlush();
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
