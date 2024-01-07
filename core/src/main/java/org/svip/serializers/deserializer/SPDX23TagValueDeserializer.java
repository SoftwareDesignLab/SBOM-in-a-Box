/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
 */

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
 * @author Derek Garcia
 */
public class SPDX23TagValueDeserializer implements Deserializer {

    //#region Constants

    public static final String TAG = "####";
    public static final String SEPARATOR = ": ";
    public static final String SPEC_VERSION_TAG = "SPDXVersion";
    public static final String TIMESTAMP_TAG = "Created";
    public static final String DOCUMENT_NAME_TAG = "DocumentName";
    public static final String DOCUMENT_NAMESPACE_TAG = "DocumentNamespace";
    public static final String DATA_LICENSE_TAG = "DataLicense";
    public static final String LICENSE_LIST_VERSION_TAG = "LicenseListVersion";
    public static final String CREATOR_TAG = "Creator";
    public static final String EXTERNAL_REFERENCE_TAG = "ExternalRef";

    ///
    /// Patterns
    ///

    public static final Pattern EXTRACTED_LICENSE_PATTERN = Pattern.compile("(^LicenseID:[\\w\\W]*?)\n{2}", Pattern.MULTILINE);
    public static final Pattern UNPACKAGED_PATTERN = Pattern.compile("(^FileName:[\\w\\W]*?)\\n{2}", Pattern.MULTILINE);
    public static final Pattern PACKAGE_PATTERN = Pattern.compile("^#{5} Package: .*\n{2}([\\w\\W]*?)\n$", Pattern.MULTILINE);
    private static final Pattern TAG_VALUE_PATTERN = Pattern.compile("(\\S+)" + SEPARATOR + "(.+)");
    private static final Pattern EXTERNAL_REF_PATTERN = Pattern.compile(EXTERNAL_REFERENCE_TAG + SEPARATOR +
            "(\\S*) (\\S*) (\\S*)");
    private static final Pattern RELATIONSHIP_PATTERN = Pattern.compile("^Relationship: (.*?) (.*?) (.*)\n(?:RelationshipComment: (.*)|)", Pattern.MULTILINE);
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
        while (mHeader.find()) {
            switch (mHeader.group(1)) {
                // NAME
                case DOCUMENT_NAME_TAG -> sbomBuilder.setName(mHeader.group(2));
                // UID
                case DOCUMENT_NAMESPACE_TAG -> sbomBuilder.setUID(mHeader.group(2));
                // SPEC VERSION
                case SPEC_VERSION_TAG ->
                        sbomBuilder.setSpecVersion(mHeader.group(2).substring(mHeader.group(2).lastIndexOf('-') + 1)); // Get text after "SPDX-"
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

        // Parse and Add Packages
        Matcher packageMatcher = PACKAGE_PATTERN.matcher(fileContents);
        while(packageMatcher.find())
            sbomBuilder.addSPDX23Component(buildPackage(packageBuilder, packageMatcher.group(1)));

        // Parse and Add unpackaged files
        Matcher fileMatcher = UNPACKAGED_PATTERN.matcher(fileContents);
        while(fileMatcher.find())
            sbomBuilder.addSPDX23Component(buildFile(fileBuilder, fileMatcher.group(1)));

        // Parse and Add external license
        Matcher licenseMatcher = EXTRACTED_LICENSE_PATTERN.matcher(fileContents);
        while(licenseMatcher.find())
            sbomBuilder.addLicense(buildExternalLicense(licenseMatcher.group(1)));

        // Parse and Add relationships
        Matcher relationshipMatcher = RELATIONSHIP_PATTERN.matcher(fileContents);
        while(relationshipMatcher.find())
            sbomBuilder.addRelationship(relationshipMatcher.group(1), buildRelationship(relationshipMatcher));

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
     * Parse SPDX style creator string into a Contact
     *
     * @param creator SPDX style creator string
     * @return Contact
     */
    protected static Contact parseSPDXCreator(String creator) {
        Matcher creatorMatcher = CREATOR_PATTERN.matcher(creator);
        if (!creatorMatcher.find()) return null;

        return new Contact(creatorMatcher.group(2), creatorMatcher.group(3), null);
    }

    /**
     * Update CreationData with info from SPDX
     *
     * @param data CreationData object
     * @param creatorInfo Creation info from SPDX
     */
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

    /**
     * Build a SPDX23 Package
     *
     * @param builder Package Builder
     * @param contents String to extract details from
     * @return SPDX23 Package Object
     */
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
            } else componentMaterials.put(mPackages.group(1), mPackages.group(2));
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
        if (componentMaterials.get("PackageOriginator") != null)
            builder.setAuthor(componentMaterials.get("PackageOriginator"));

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

    /**
     * Build a SPDX23 File
     *
     * @param builder File Builder
     * @param contents String to extract details from
     * @return SPDX23 File Object
     */
    private SPDX23FileObject buildFile(SPDX23FileBuilder builder, String contents) {
        Matcher mFiles = TAG_VALUE_PATTERN.matcher(contents);
        HashMap<String, String> fileMaterials = new HashMap<>();
        while (mFiles.find()) fileMaterials.put(mFiles.group(1), mFiles.group(2));

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
     * Parse External License and build a license
     * TODO currently only parses License ID, looses all other info. Keeping ID since this is what is used when
     * referenced by other elements
     *
     * @param licenseBlock String of license details
     * @return extracted license ID
     */
    private String buildExternalLicense(String licenseBlock){
        Pattern licenseNamePattern = Pattern.compile("^LicenseID: (.*)");
        Matcher licenseIDMatcher = licenseNamePattern.matcher(licenseBlock);

        // return just ID because this will be referenced by other
        // TODO more complex license so we don't loose the extra details
        return licenseIDMatcher.find()
                ? licenseIDMatcher.group(1)
                : "";
    }

    /**
     * Extract data from match to build a Relationship
     *
     * @param match Regex match of relationship details
     * @return Relationships
     */
    private Relationship buildRelationship(Matcher match){
        Relationship r = new Relationship(match.group(3), match.group(2));
        // add comment if present
        if(match.group(4) != null)
            r.setComment(match.group(4));

        return r;
    }


}
