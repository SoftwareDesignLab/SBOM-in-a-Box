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
import org.svip.serializers.Schema;
import org.svip.serializers.exceptions.DeserializerException;
import org.svip.serializers.exceptions.UnsupportedFileFormatException;

import java.io.File;
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

    /// Patterns

    private static final Pattern CREATOR_PATTERN = Pattern.compile("^(Person|Organization): (.+?)(?:$| (?:\\((.*)\\))?$)");
    private static final Pattern TOOL_PATTERN = Pattern.compile("^Tool: (.*)-(.*)$", Pattern.CASE_INSENSITIVE);


    /**
     * Create new deserializer
     *
     * @param fileFormat Type of deserializer
     * @throws UnsupportedFileFormatException if attempt to deserialize from an unsupported format
     */
    public SPDX23Deserializer(FileFormat fileFormat) {
        super(fileFormat);
        // check for unsupported file formats
        if (fileFormat != FileFormat.JSON && fileFormat != FileFormat.TAG_VALUE) {
            throw new UnsupportedFileFormatException(Schema.SPDX_23, fileFormat);
        }
    }

    /**
     * Resolve licenses in an SPDX Object
     *
     * @param spdxObject Tag-Value SPDX Object to parse licenses from
     * @return Collection of Licenses, null if no licenses found
     */
    private LicenseCollection resolveLicenses(Map<String, Object> spdxObject) {
        LicenseCollection objectLicenses = new LicenseCollection();

        String licenseConcluded = mapper.convertValue(spdxObject.get("licenseConcluded"), new TypeReference<>() {
        });
        if (licenseConcluded != null)
            objectLicenses.addConcludedLicenseString(licenseConcluded);
        String licenseDeclared = mapper.convertValue(spdxObject.get("licenseDeclared"), new TypeReference<>() {
        });

        if (licenseDeclared != null)
            objectLicenses.addDeclaredLicense(licenseDeclared);
        List<String> licenseInfoFromFiles = mapper.convertValue(spdxObject.get("licenseInfoFromFiles"), new TypeReference<>() {
        });

        if (licenseInfoFromFiles != null)
            licenseInfoFromFiles.forEach(objectLicenses::addLicenseInfoFromFile);
        objectLicenses.setComment((String) spdxObject.get("licenseComments"));

        // return null if no license
        if (!(objectLicenses.getConcluded().isEmpty() || objectLicenses.getDeclared().isEmpty() || objectLicenses.getInfoFromFiles().isEmpty()))
            return null;
        // else return licenses
        return objectLicenses;
    }

    /**
     * Parse SPDX style creator string into a Contact
     *
     * @param creator SPDX style creator string
     * @return Contact
     */
    private Contact resolveContact(String creator) {
        // skip if no data
        if (creator == null) return null;
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
     * Resolve Package details
     *
     * @param spdxObject Map with package details
     * @return Object with package details
     */
    private SPDX23PackageObject resolvePackage(Map<String, Object> spdxObject) {
        SPDX23PackageBuilder packageBuilder = new SPDX23PackageBuilder();
        // set basic fields
        packageBuilder.setType((String) spdxObject.get("primaryPackagePurpose"))
                .setUID((String) spdxObject.get("SPDXID"))
                .setAuthor((String) spdxObject.get("originator"))
                .setName((String) spdxObject.get("name"))
                .setVersion((String) spdxObject.get("version"))
                .setDownloadLocation((String) spdxObject.get("downloadLocation"))
                .setComment((String) spdxObject.get("comment"))
                .setFilesAnalyzed(Boolean.parseBoolean((String) spdxObject.get("filesAnalyzed")))   // ensure bool
                .setHomePage((String) spdxObject.get("homepage"))
                .setSourceInfo((String) spdxObject.get("sourceInfo"))
                .setAttributionText((String) spdxObject.get("attributionText"))
                .setCopyright((String) spdxObject.get("copyright"))
                .setBuiltDate((String) spdxObject.get("builtDate"))
                .setReleaseDate((String) spdxObject.get("releaseDate"))
                .setValidUntilDate((String) spdxObject.get("validUntilDate"))
                .setVerificationCode((String) spdxObject.get("packageVerificationCode"));

        // set supplier
        Contact supplierContact = resolveContact((String) spdxObject.get("supplier"));
        if (supplierContact != null) {
            Organization supplier = new Organization(supplierContact.getName(), null);
            supplier.addContact(supplierContact);
            packageBuilder.setSupplier(supplier);
        }

        // set description
        String summary = (String) spdxObject.get("summary");
        if (summary != null) {
            Description description = new Description(summary);
            description.setDescription((String) spdxObject.get("description"));
            packageBuilder.setDescription(description);
        }

        // set hashes
        List<Map<String, Object>> hashes = mapper.convertValue(spdxObject.get("checksums"), new TypeReference<>() {
        });
        if (hashes != null)
            hashes.forEach(h -> packageBuilder.addHash((String) h.get("algorithm"), (String) h.get("checksumValue")));


        // set licenses
        LicenseCollection packageLicenses = resolveLicenses(spdxObject);
        if (packageLicenses != null)
            packageBuilder.setLicenses(packageLicenses);


        // set external references and cpes / purls
        List<Map<String, Object>> externalReferences = mapper.convertValue(spdxObject.get("externalRefs"), new TypeReference<>() {
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

        var foo = packageBuilder.build();
        return foo;
    }

    /**
     * Resolve File details
     *
     * @param spdxObject Map with file details
     * @return Object with file details
     */
    private SPDX23FileObject resolveFile(Map<String, Object> spdxObject) {
        SPDX23FileBuilder fileBuilder = new SPDX23FileBuilder();
        // set basic fields
        fileBuilder.setUID((String) spdxObject.get("SPDXID"))
                .setName((String) spdxObject.get("fileName"))
                .setType((String) spdxObject.get("type"))
                .setCopyright((String) spdxObject.get("copyrightText"))
                .setComment((String) spdxObject.get("comment"))
                .setFileNotice((String) spdxObject.get("noticeText"))
                .setAttributionText((String) spdxObject.get("attributionText"));

        // TYPE
        List<String> fileTypes = mapper.convertValue(spdxObject.get("fileTypes"), new TypeReference<>() {
        });
        if (fileTypes != null && !fileTypes.isEmpty())
            // TODO set more filetypes, sbom only supports 1
            fileBuilder.setType(fileTypes.get(0));

        // AUTHOR
        List<String> authors = mapper.convertValue(spdxObject.get("fileContributors"), new TypeReference<>() {
        });
        if (authors != null && !authors.isEmpty())
            // TODO store more than 1 author
            fileBuilder.setAuthor(authors.get(0));


        // set licenses
        LicenseCollection fileLicenses = resolveLicenses(spdxObject);
        if (fileLicenses != null)
            fileBuilder.setLicenses(fileLicenses);

        // set hashes
        List<Map<String, Object>> hashes = mapper.convertValue(spdxObject.get("checksums"), new TypeReference<>() {
        });
        if (hashes != null)
            hashes.forEach(h -> fileBuilder.addHash((String) h.get("algorithm"), (String) h.get("checksumValue")));


        return fileBuilder.build();
    }

    /**
     * Set the metadata for this SBOM object
     *
     * @param sbomBuilder Builder used to make SBOM
     * @param spdxObject  Map with SBOM details
     */
    private void setMetadata(SPDX23Builder sbomBuilder, Map<String, Object> spdxObject) {
        // set basic details
        sbomBuilder.setName((String) spdxObject.get("name"))
                .setUID((String) spdxObject.get("documentNamespace"))
                .setSpecVersion((String) spdxObject.get("spdxVersion"))
                .addLicense((String) spdxObject.get("dataLicense"))
                .setDocumentComment((String) spdxObject.get("comment"));

        // get the metadata object
        Map<String, Object> metadata = mapper.convertValue(spdxObject.get("creationInfo"), new TypeReference<>() {
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
