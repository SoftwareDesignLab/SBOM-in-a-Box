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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14Builder;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14PackageBuilder;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
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
import java.util.*;

/**
 * <b>File:</b> CDX14Serializer.java
 * <p>
 * <b>Description:</b> Deserialize CycloneDX 1.4 JSON and XML SBOMs into SBOM Objects
 *
 * @author Ian Dunn
 * @author Thomas Roman
 * @author Derek Garcia
 */
public class CDX14Deserializer extends Deserializer {

    /**
     * Create new CycloneDX 1.4 deserializer
     *
     * @param fileFormat Type of deserializer
     */
    public CDX14Deserializer(FileFormat fileFormat) {
        super(fileFormat);
    }

    /**
     * Normalize a snippet of JSON or XML.
     * XML use headers which skew file parsing
     *
     * @param collection Collection to normalize
     * @return List of values in a section
     */
    private List<Map<String, Object>> normalizeExcerpt(Object collection) {
        // skip empty data
        if (collection == null) return null;

        // no extra headers if json
        if (fileFormat == FileFormat.JSON)
            return mapper.convertValue(collection, new TypeReference<>() {
            });

        // else flatten xml object
        Map<String, Object> tmp = mapper.convertValue(collection, new TypeReference<>() {
        });
        if (!tmp.isEmpty()) {
            // get the first (and only) value
            Object o = tmp.values().iterator().next();
            // only 1 object, wrap in list
            if (o instanceof Map) {
                Map<String, Object> r = mapper.convertValue(o, new TypeReference<>() {
                });
                return List.of(r);
            }
            // else return list in its entirety
            return mapper.convertValue(o, new TypeReference<>() {
            });
        }
        // no data
        return Collections.emptyList();
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
        creationData.setCreationTime((String) metadata.get("timestamp"));

        // CREATION TOOLS
        List<Map<String, Object>> tools = normalizeExcerpt(metadata.get("tools"));
        for (Map<String, Object> tool : tools) {
            CreationTool creationTool = new CreationTool();
            // set details
            creationTool.setName((String) tool.get("name"));
            creationTool.setVendor((String) tool.get("vendor"));
            creationTool.setVersion((String) tool.get("version"));
            // add tool hashes
            List<Map<String, Object>> hashes = normalizeExcerpt(tool.get("hashes"));
            if (hashes != null)
                hashes.forEach(h -> creationTool.addHash((String) h.get("alg"), (String) h.get("content")));
            // add tool external references
            List<Map<String, Object>> externalReferences = normalizeExcerpt(tool.get("externalReferences"));
            if (externalReferences != null)
                externalReferences.forEach(e -> creationTool.addExternalReference(resolveExternalReference(e)));
            // add the tool
            creationData.addCreationTool(creationTool);
        }

        // add authors
        List<Map<String, Object>> authors = normalizeExcerpt(metadata.get("authors"));
        if (authors != null)
            authors.forEach(a -> creationData.addAuthor(resolveContact(a)));

        // set manufacture
        Map<String, Object> manufacture = mapper.convertValue(metadata.get("manufacture"), new TypeReference<>() {
        });
        creationData.setManufacture(resolveOrganization(manufacture));

        // set supplier
        Map<String, Object> supplier = mapper.convertValue(metadata.get("supplier"), new TypeReference<>() {
        });
        creationData.setSupplier(resolveOrganization(supplier));

        // add properties
        List<Map<String, Object>> properties = normalizeExcerpt(metadata.get("properties"));
        if (properties != null)
            properties.forEach(p -> {
                String name = (String) p.get("name");
                String value = (String) p.get("value");
                // set creator comment if provided, else just save property
                if (name.equals("creatorComment")) creationData.setCreatorComment(value);
                else creationData.addProperty(name, value);
            });

        // add licenses
        List<Map<String, Object>> licenses = normalizeExcerpt(metadata.get("licenses"));
        if (licenses != null) {
            licenses.forEach(l -> {
                String lID = (String) l.get("id");
                String lName = (String) l.get("name");
                if (lID != null) {
                    creationData.addLicense(lID);
                } else if (lName != null) {
                    creationData.addLicense(lName);
                }
            });

        }

        // done
        return creationData;
    }

    /**
     * Resolve Package details
     *
     * @param component Map with package details
     * @return Object with package details
     */
    private CDX14ComponentObject resolvePackage(Map<String, Object> component) {
        // skip if no data
        if (component == null) return null;
        CDX14PackageBuilder packageBuilder = new CDX14PackageBuilder();
        // set simple fields
        packageBuilder.setType((String) component.get("type"))
                .setMimeType((String) component.get("mime-type"))
                .setUID((String) component.get("bom-ref"))
                .setAuthor((String) component.get("author"))
                .setPublisher((String) component.get("publisher"))
                .setGroup((String) component.get("group"))
                .setName((String) component.get("name"))
                .setVersion((String) component.get("version"))
                .setScope((String) component.get("scope"))
                .setCopyright((String) component.get("copyright"))
                .addCPE((String) component.get("cpe"))
                .addPURL((String) component.get("purl"));

        // set description
        String description = (String) component.get("description");
        if (description != null)
            packageBuilder.setDescription(new Description(description));

        // set supplier
        Map<String, Object> supplier = mapper.convertValue(component.get("supplier"), new TypeReference<>() {
        });
        packageBuilder.setSupplier(resolveOrganization(supplier));

        // add hashes
        List<Map<String, Object>> hashes = normalizeExcerpt(component.get("hashes"));
        if (hashes != null)
            hashes.forEach(h -> packageBuilder.addHash((String) h.get("alg"), (String) h.get("content")));

        // add licenses
        List<Map<String, Object>> licenses = normalizeExcerpt(component.get("licenses"));
        if (licenses != null) {
            if (!licenses.isEmpty()) {
                LicenseCollection componentLicenses = new LicenseCollection();
                licenses.forEach(l -> {
                    // todo - cleanup
                    String lID;
                    String lName;
                    if (fileFormat == FileFormat.JSON) {
                        Map<String, Map<String, String>> licenseObj = mapper.convertValue(l, new TypeReference<>() {
                        });
                        lID = licenseObj.get("license").get("id");
                        lName = licenseObj.get("license").get("name");
                    } else {
                        Map<String, String> licenseObj = mapper.convertValue(l, new TypeReference<>() {
                        });
                        lID = licenseObj.get("id");
                        lName = licenseObj.get("name");
                    }
                    // set license
                    if (lID != null) {
                        componentLicenses.addLicenseInfoFromFile(lID);
                    } else if (lName != null) {
                        componentLicenses.addLicenseInfoFromFile(lName);
                    }
                });
                packageBuilder.setLicenses(componentLicenses);
            }
        }

        // add external references
        List<Map<String, Object>> externalReferences = normalizeExcerpt(component.get("externalReferences"));
        if (externalReferences != null)
            externalReferences.forEach(e -> packageBuilder.addExternalReference(resolveExternalReference(e)));

        // add properties
        List<Map<String, Object>> properties = normalizeExcerpt(component.get("properties"));
        if (properties != null)
            properties.forEach(p -> packageBuilder.addProperty((String) p.get("name"), (String) p.get("value")));

        // done
        return packageBuilder.build();
    }

    /**
     * Resolve External Reference details
     *
     * @param externalReference map with details
     * @return External Reference
     */
    private ExternalReference resolveExternalReference(Map<String, Object> externalReference) {
        // skip if no data
        if (externalReference == null) return null;
        ExternalReference externalReferenceObj = new ExternalReference(
                (String) externalReference.get("url"),
                (String) externalReference.get("type")
        );

        // TODO do we want to store comments?
        // add hashes
        List<Map<String, Object>> hashes = normalizeExcerpt(externalReference.get("hashes"));
        if (hashes != null)
            hashes.forEach(h -> externalReferenceObj.addHash((String) h.get("alg"), (String) h.get("content")));

        return externalReferenceObj;
    }

    /**
     * Resolve Contact details
     *
     * @param contact map with details
     * @return Contact
     */
    private Contact resolveContact(Map<String, Object> contact) {
        // skip if no data
        if (contact == null) return null;
        return new Contact((String) contact.get("name"), (String) contact.get("email"), (String) contact.get("phone"));
    }

    /**
     * Resolve Organization details
     *
     * @param organization map with details
     * @return Organization
     */
    private Organization resolveOrganization(Map<String, Object> organization) {
        // skip if no data
        if (organization == null) return null;
        Organization organizationObj = new Organization(
                (String) organization.get("name"),
                (String) organization.get("url"));
        // add contacts
        List<Map<String, Object>> contacts = normalizeExcerpt(organization.get("contact"));
        if (contacts != null)
            contacts.forEach(c -> organizationObj.addContact(resolveContact(c)));

        return organizationObj;
    }

    /**
     * Resolve dependency details
     *
     * @param dependency map with details
     * @return List of relationships of this object
     */
    private List<Relationship> resolveDependency(Map<String, Object> dependency) {
        // skip if no data
        if (dependency == null) return null;
        List<Relationship> relationships = new ArrayList<>();
        // add all deps
        List<String> bomRefs = mapper.convertValue(dependency.get("dependsOn"), new TypeReference<>() {
        });
        if (bomRefs != null)
            bomRefs.forEach(br -> relationships.add(new Relationship(br, "DEPENDS_ON")));    //todo replace with SPDX enums

        return relationships;
    }


    /**
     * Set the metadata for this SBOM object
     *
     * @param sbomBuilder Builder used to make SBOM
     * @param content     Map with SBOM details
     */
    private void setMetadata(CDX14Builder sbomBuilder, Map<String, Object> content) {
        // set basic details
        sbomBuilder.setFormat((String) content.get("bomFormat"))
                .setUID((String) content.get("serialNumber"))
                .setSpecVersion((String) content.get("specVersion"));

        // handle converting version to string
        if (content.get("version") instanceof Integer) {
            sbomBuilder.setVersion(Integer.toString((Integer) content.get("version")));
        } else {
            sbomBuilder.setVersion((String) content.get("version"));
        }


        // get the metadata object
        Map<String, Object> metadata = mapper.convertValue(
                content.get("metadata"),
                new TypeReference<>() {
                }
        );

        // set root component
        Map<String, Object> rootComponent = mapper.convertValue(metadata.get("component"), new TypeReference<>() {
        });

        sbomBuilder.setRootComponent(resolvePackage(rootComponent));

        // set creation data
        sbomBuilder.setCreationData(resolveCreationData(metadata));
    }

    /**
     * Load CycloneDX SBOM file into memory
     *
     * @param file File to deserialize
     * @return CycloneDX 1.4 SBOM
     * @throws DeserializerException Failed to load file
     */
    @Override
    public CDX14SBOM deserialize(File file) throws DeserializerException {
        // cdx doesn't support tag-value
        if (fileFormat == FileFormat.TAG_VALUE)
            throw new DeserializerException("CycloneDX 1.4 does not support Tag-Value", file, fileFormat);
        // load into map
        Map<String, Object> content = super.loadFile(file);

        // initialize builders
        CDX14Builder sbomBuilder = new CDX14Builder();

        // set fields
        setMetadata(sbomBuilder, content);

        // components
        List<Map<String, Object>> packages = normalizeExcerpt(content.get("components"));
        if (packages != null)
            packages.forEach(p -> sbomBuilder.addCDX14Package(resolvePackage(p)));

        // external references
        List<Map<String, Object>> externalReferences = normalizeExcerpt(content.get("externalReferences"));
        if (externalReferences != null)
            externalReferences.forEach(er -> sbomBuilder.addExternalReference(resolveExternalReference(er)));

        // Dependencies
        List<Map<String, Object>> dependencies = normalizeExcerpt(content.get("dependencies"));
        if (dependencies != null) {
            dependencies.forEach(d -> {
                // add to sbom
                String ref = (String) d.get("ref");
                resolveDependency(d).forEach(r -> sbomBuilder.addRelationship(ref, r));
            });
        }

        return sbomBuilder.buildCDX14SBOM();
    }
}

