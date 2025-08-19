/*
 * Copyright 2021 Rochester Institute of Technology (RIT). Developed with
 *  government support under contract 70RCSA22C00000008 awarded by the United
 * States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
 *  <p>
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the “Software”), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  <p>
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *  <p>
 *  THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package org.svip.serializers.deserializer.v2;

import com.fasterxml.jackson.core.type.TypeReference;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23FileBuilder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23PackageBuilder;
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
import org.svip.serializers.FileFormat;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>File:</b> SPDX23Deserializer.java
 * <p>
 * <b>Description:</b> Deserialize SPDX 2.3 JSON and Tag-Value SBOMs into SBOM Objects
 *
 * @author Ian Dunn
 * @author Tyler Drake
 * @author Matt London
 * @author Ethan Numan
 * @author Thomas Roman
 * @author Derek Garcia
 */
public class SPDX23Deserializer extends Deserializer {

    private static final String TAG = "####";
    private static final String SEPARATOR = ": ";
    private static final String SPEC_VERSION_TAG = "SPDXVersion";
    private static final String TIMESTAMP_TAG = "Created";
    private static final String DOCUMENT_NAME_TAG = "DocumentName";
    private static final String DOCUMENT_NAMESPACE_TAG = "DocumentNamespace";
    private static final String DATA_LICENSE_TAG = "DataLicense";
    private static final String LICENSE_LIST_VERSION_TAG = "LicenseListVersion";
    private static final String CREATOR_TAG = "Creator";
    private static final String EXTERNAL_REFERENCE_TAG = "ExternalRef";

    /// Patterns

    private static final Pattern EXTRACTED_LICENSE_PATTERN = Pattern.compile("(^LicenseID:[\\w\\W]*?)\n{2}", Pattern.MULTILINE);
    private static final Pattern UNPACKAGED_PATTERN = Pattern.compile("(^FileName:[\\w\\W]*?)\\n{2}", Pattern.MULTILINE);
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("^#{5} Package: .*\n{2}([\\w\\W]*?)\n$", Pattern.MULTILINE);
    private static final Pattern TAG_VALUE_PATTERN = Pattern.compile("(\\S+)" + SEPARATOR + "(.+)");
    private static final Pattern EXTERNAL_REF_PATTERN = Pattern.compile(EXTERNAL_REFERENCE_TAG + SEPARATOR +
            "(\\S*) (\\S*) (\\S*)");
    private static final Pattern RELATIONSHIP_PATTERN = Pattern.compile("^Relationship: (.*?) (.*?) (.*)\n(?:RelationshipComment: (.*)|)", Pattern.MULTILINE);
    private static final Pattern CREATOR_PATTERN = Pattern.compile(
            "^(?:(Person|Organization): )(.+?)(?:$| (?:\\((.*)\\))?$)");
    private static final Pattern TOOL_PATTERN = Pattern.compile("^Tool: (?:(.*)-)(.*)$", Pattern.CASE_INSENSITIVE);


    /**
     * Create new deserializer
     *
     * @param fileFormat Type of deserializer
     */
    public SPDX23Deserializer(FileFormat fileFormat) {
        super(fileFormat);
    }


    /**
     * Resolve Package details
     *
     * @param component Map with package details
     * @return Object with package details
     */
    private SPDX23PackageObject resolvePackage(Map<String, Object> component) {
        SPDX23PackageBuilder packageBuilder = new SPDX23PackageBuilder();
        // set basic fields
        packageBuilder.setType((String) component.get("primaryPackagePurpose"))
                .setUID((String) component.get("SPDXID"))
                .setAuthor((String) component.get("originator"))
                .setName((String) component.get("name"))
                .setVersion((String) component.get("versionInfo"))
                .setDownloadLocation((String) component.get("downloadLocation"))
                .setComment((String) component.get("comment"))
                .setFileName((String) component.get("packageFileName"))
                .setFilesAnalyzed((Boolean) component.get("filesAnalyzed"))
                .setHomePage((String) component.get("homepage"))
                .setSourceInfo((String) component.get("sourceInfo"))
                .setAttributionText((String) component.get("attributionText"))
                .setCopyright((String) component.get("copyright"))
                .setBuiltDate((String) component.get("builtDate"))
                .setReleaseDate((String) component.get("releaseDate"))
                .setValidUntilDate((String) component.get("validUntilDate"))
                .setVerificationCode((String) component.get("packageVerificationCode"));

        // set supplier
        Contact supplierContact = resolveContact((String) component.get("supplier"));
        if (supplierContact != null) {
            Organization supplier = new Organization(supplierContact.getName(), null);
            supplier.addContact(supplierContact);
            packageBuilder.setSupplier(supplier);
        }

        // set description
        String summary = (String) component.get("summary");
        if (summary != null) {
            Description description = new Description(summary);
            description.setDescription((String) component.get("description"));
            packageBuilder.setDescription(description);
        }

        // set hashes
        List<Map<String, Object>> hashes = mapper.convertValue(component.get("checksums"), new TypeReference<>() {
        });
        if (hashes != null)
            hashes.forEach(h -> packageBuilder.addHash((String) h.get("algorithm"), (String) h.get("checksumValue")));


        // set licenses
        LicenseCollection componentLicenses = new LicenseCollection();
        List<String> licenseConcluded = mapper.convertValue(component.get("licenseConcluded"), new TypeReference<>() {
        });
        if (licenseConcluded != null)
            licenseConcluded.forEach(componentLicenses::addConcludedLicenseString);
        List<String> licenseDeclared = mapper.convertValue(component.get("licenseDeclared"), new TypeReference<>() {
        });
        if (licenseDeclared != null)
            licenseDeclared.forEach(componentLicenses::addDeclaredLicense);
        List<String> licenseInfoFromFiles = mapper.convertValue(component.get("licenseInfoFromFiles"), new TypeReference<>() {
        });
        if (licenseInfoFromFiles != null)
            licenseInfoFromFiles.forEach(componentLicenses::addLicenseInfoFromFile);
        componentLicenses.setComment((String) component.get("licenseComments"));
        // only set if licenses
        if (!(componentLicenses.getConcluded().isEmpty() || componentLicenses.getDeclared().isEmpty() || componentLicenses.getInfoFromFiles().isEmpty()))
            packageBuilder.setLicenses(componentLicenses);

        // set external references and cpes / purls
        List<Map<String, Object>> externalReferences = mapper.convertValue(component.get("externalRefs"), new TypeReference<>() {
        });
        if (externalReferences != null)
            externalReferences.stream()
                    // skip if missing data
                    .filter(ref -> ref.get("referenceCategory") != null && ref.get("referenceLocator") != null && ref.get("referenceType") != null)
                    // else add data if all values present
                    .forEach(ref -> {
                        String category = (String) ref.get("referenceCategory");
                        String url = (String) ref.get("referenceLocator");
                        String type = (String) ref.get("referenceType");
                        // set ref accordingly
                        if (category.equalsIgnoreCase("security") && type.equalsIgnoreCase("cpe23type"))
                            packageBuilder.addCPE(url);
                        else if (category.equalsIgnoreCase("package-manager") && type.equalsIgnoreCase("purl"))
                            packageBuilder.addPURL(url);
                        else
                            packageBuilder.addExternalReference(new ExternalReference(category, url, type));
                    });

        return packageBuilder.build();
    }

    /**
     * Resolve File details
     *
     * @param file Map with file details
     * @return Object with file details
     */
    private SPDX23FileObject resolveFile(Map<String, Object> file) {
        SPDX23FileBuilder fileBuilder = new SPDX23FileBuilder();
        // set basic fields
        fileBuilder.setUID((String) file.get("SPDXID"))
                .setName((String) file.get("fileName"))
                .setType((String) file.get("type"))
                .setCopyright((String) file.get("copyrightText"))
                .setComment((String) file.get("comment"))
                .setFileNotice((String) file.get("noticeText"))
                .setAttributionText((String) file.get("attributionText"));

        // TYPE
        List<String> fileTypes = mapper.convertValue(file.get("fileTypes"), new TypeReference<>() {
        });
        if (fileTypes != null && !fileTypes.isEmpty())
            // TODO set more filetypes, sbom only supports 1
            fileBuilder.setType(fileTypes.get(0));

        // AUTHOR
        List<String> authors = mapper.convertValue(file.get("fileContributors"), new TypeReference<>() {
        });
        if (authors != null && !authors.isEmpty())
            // TODO store more than 1 author
            fileBuilder.setAuthor(authors.get(0));


        // set licenses
        LicenseCollection fileLicenses = new LicenseCollection();
        List<String> licenseConcluded = mapper.convertValue(file.get("licenseConcluded"), new TypeReference<>() {
        });
        if (licenseConcluded != null)
            licenseConcluded.forEach(fileLicenses::addConcludedLicenseString);
        List<String> licenseInfoFromFiles = mapper.convertValue(file.get("licenseInfoInFiles"), new TypeReference<>() {
        });
        if (licenseInfoFromFiles != null)
            licenseInfoFromFiles.forEach(fileLicenses::addLicenseInfoFromFile);
        fileLicenses.setComment((String) file.get("licenseComments"));
        // only set if licenses
        if (!(fileLicenses.getConcluded().isEmpty() || fileLicenses.getInfoFromFiles().isEmpty()))
            fileBuilder.setLicenses(fileLicenses);

        // set hashes
        List<Map<String, Object>> hashes = mapper.convertValue(file.get("checksums"), new TypeReference<>() {
        });
        if (hashes != null)
            hashes.forEach(h -> fileBuilder.addHash((String) h.get("algorithm"), (String) h.get("checksumValue")));


        return fileBuilder.build();
    }


    /**
     * Parse SPDX style creator string into a Contact
     *
     * @param creator SPDX style creator string
     * @return Contact
     */
    private Contact resolveContact(String creator) {
        Matcher creatorMatcher = CREATOR_PATTERN.matcher(creator);
        // nothing found
        if (!creatorMatcher.find()) return null;
        // else make new contact
        return new Contact(creatorMatcher.group(2), creatorMatcher.group(3), null);
    }


    /**
     * Resolve the SBOM creation details
     *
     * @param metadata Metadata map with details
     * @return Creation Data
     */
    private CreationData resolveCreationData(Map<String, Object> metadata) {
        // skip if no data
        if (metadata == null) return null;

        CreationData creationData = new CreationData();

        // CREATED
        creationData.setCreationTime((String) metadata.get("created"));
        // COMMENT
        creationData.setCreatorComment((String) metadata.get("comment"));
        // CREATION TOOL
        List<String> creators = mapper.convertValue(metadata.get("creators"), new TypeReference<>() {
        });
        if (creators != null) {
            /*
             SPDX keep details on same line, so several details could be present
             */
            for (String creator : creators) {
                Matcher toolMatcher = TOOL_PATTERN.matcher(creator);
                // add all tools
                while (toolMatcher.find()) {
                    CreationTool tool = new CreationTool();
                    tool.setName(toolMatcher.group(1));
                    tool.setVersion(toolMatcher.group(2));
                    creationData.addCreationTool(tool);
                }
                // attempt to get contact, if not continue
                Contact contact = resolveContact(creator);
                if (contact == null) continue;

                // If we find an organization, set it to the supplier if there isn't already one. Otherwise,
                // add another author with the contact info
                if (creator.toLowerCase().startsWith("organization") && (creationData.getSupplier() == null || creationData.getSupplier().getName().isEmpty())) {
                    Organization supplier = new Organization(contact.getName(), null);
                    supplier.addContact(contact);
                    creationData.setSupplier(supplier);
                } else {
                    creationData.addAuthor(contact);
                }
            }
        }

        // done
        return creationData;
    }

    /**
     * Set the metadata for this SBOM object
     *
     * @param sbomBuilder Builder used to make SBOM
     * @param content     Map with SBOM details
     */
    private void setMetadata(SPDX23Builder sbomBuilder, Map<String, Object> content) {
        // set basic details
        sbomBuilder.setName((String) content.get("name"))
                .setUID((String) content.get("documentNamespace"))
                .setSpecVersion((String) content.get("specVersion"))
                .addLicense((String) content.get("dataLicense"))
                .setDocumentComment((String) content.get("comment"));

        // get the metadata object
        Map<String, Object> metadata = mapper.convertValue(content.get("creationInfo"), new TypeReference<>() {
        });

        // set creation data
        sbomBuilder.setSPDXLicenseListVersion((String) metadata.get("licenseListVersion"));
        sbomBuilder.setCreationData(resolveCreationData(metadata));

    }


    /**
     * Deserialize the file into an SBOM object
     *
     * @param file File to deserialize
     * @return SBOM object
     */
    @Override
    public SPDX23SBOM deserialize(File file) throws DeserializerException {
        // load into map
        Map<String, Object> content = super.loadFile(file);

        // initialize builders
        SPDX23Builder sbomBuilder = new SPDX23Builder();

        // set metadata
        setMetadata(sbomBuilder, content);

        // add packages
        List<Map<String, Object>> packages = mapper.convertValue(content.get("packages"), new TypeReference<>() {
        });
        if (packages != null)
            packages.forEach(p -> sbomBuilder.addSPDX23Component(resolvePackage(p)));

        // add files
        List<Map<String, Object>> files = mapper.convertValue(content.get("files"), new TypeReference<>() {
        });
        if (files != null)
            files.forEach(f -> sbomBuilder.addSPDX23Component(resolveFile(f)));

        // add relationships
        List<Map<String, Object>> relationships = mapper.convertValue(content.get("relationships"), new TypeReference<>() {
        });
        if (relationships != null) {
            relationships.forEach(r -> {
                // build relationship
                Relationship relationship = new Relationship(
                        (String) r.get("relatedSpdxElement"),
                        (String) r.get("relationshipType"));
                relationship.setComment((String) r.get("comment"));
                // add to sbom
                sbomBuilder.addRelationship((String) r.get("spdxElementId"), relationship);
            });
        }

        // Build the SBOM
        return sbomBuilder.buildSPDX23SBOM();

    }
}
