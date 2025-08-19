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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <b>File:</b> CDX14Serializer.java
 * <p>
 * <b>Description:</b> Serialize CycloneDX 1.4 JSON and XML SBOMs into SBOM Objects
 *
 * @author Derek Garcia
 */
public class CDX14Deserializer extends Deserializer {
    private final ObjectMapper mapper = new ObjectMapper();

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
        List<HashMap<String, Object>> tools = mapper.convertValue(metadata.get("tools"), new TypeReference<>() {
        });
        for (HashMap<String, Object> tool : tools) {
            CreationTool creationTool = new CreationTool();
            // set details
            creationTool.setName((String) tool.get("name"));
            creationTool.setVendor((String) tool.get("vendor"));
            creationTool.setVersion((String) tool.get("version"));
            // add tool hashes
            List<HashMap<String, String>> hashes = mapper.convertValue(tool.get("hashes"), new TypeReference<>() {
            });
            if (hashes != null)
                hashes.forEach(h -> creationTool.addHash(h.get("alg"), h.get("content")));
            // add tool external references
            List<HashMap<String, Object>> externalReferences = mapper.convertValue(tool.get("externalReferences"), new TypeReference<>() {
            });
            if (externalReferences != null)
                externalReferences.forEach(e -> creationTool.addExternalReference(resolveExternalReference(e)));
            // add the tool
            creationData.addCreationTool(creationTool);
        }

        // add authors
        List<HashMap<String, Object>> authors = mapper.convertValue(metadata.get("hashes"), new TypeReference<>() {
        });
        if (authors != null)
            authors.forEach(a -> creationData.addAuthor(resolveContact(a)));

        // set manufacture
        HashMap<String, Object> manufacturer = mapper.convertValue(metadata.get("manufacturer"), new TypeReference<>() {
        });
        creationData.setManufacture(resolveOrganization(manufacturer));

        // set supplier
        HashMap<String, Object> supplier = mapper.convertValue(metadata.get("supplier"), new TypeReference<>() {
        });
        creationData.setSupplier(resolveOrganization(supplier));

        // add properties
        List<HashMap<String, String>> properties = mapper.convertValue(metadata.get("properties"), new TypeReference<>() {
        });
        if (properties != null)
            properties.forEach(p -> {
                String name = p.get("name");
                String value = p.get("value");
                // set creator comment if provided, else just save property
                if (name.equals("creatorComment")) creationData.setCreatorComment(value);
                else creationData.addProperty(name, value);
            });

        // add licenses
        List<HashMap<String, String>> licenses = mapper.convertValue(metadata.get("licenses"), new TypeReference<>() {
        });
        if (licenses != null)
            licenses.forEach(l -> creationData.addLicense(l.get("name")));

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
        packageBuilder.setName((String) component.get("name"))
                .setMimeType((String) component.get("mimeType"))
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
        HashMap<String, Object> supplier = mapper.convertValue(component.get("supplier"), new TypeReference<>() {
        });
        packageBuilder.setSupplier(resolveOrganization(supplier));

        // add hashes
        List<HashMap<String, String>> hashes = mapper.convertValue(component.get("hashes"), new TypeReference<>() {
        });
        if (hashes != null)
            hashes.forEach(h -> packageBuilder.addHash(h.get("alg"), h.get("content")));

        // add licenses
        List<HashMap<String, String>> licenses = mapper.convertValue(component.get("licenses"), new TypeReference<>() {
        });
        if (licenses != null) {
            if (!licenses.isEmpty()) {
                LicenseCollection componentLicenses = new LicenseCollection();
                licenses.forEach(l -> {
                    String lID = l.get("id");
                    String lName = l.get("name");
                    if (lID != null) {
                        componentLicenses.addLicenseInfoFromFile(l.get("id"));
                    } else if (lName != null) {
                        componentLicenses.addLicenseInfoFromFile(l.get("name"));
                    }
                });
                packageBuilder.setLicenses(componentLicenses);
            }
        }

        // add external references
        List<HashMap<String, Object>> externalReferences = mapper.convertValue(component.get("externalReferences"), new TypeReference<>() {
        });
        if (externalReferences != null)
            externalReferences.forEach(e -> packageBuilder.addExternalReference(resolveExternalReference(e)));

        // add properties
        List<HashMap<String, String>> properties = mapper.convertValue(component.get("properties"), new TypeReference<>() {
        });
        if (properties != null)
            properties.forEach(p -> packageBuilder.addProperty(p.get("name"), p.get("value")));

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
        List<HashMap<String, String>> hashes = mapper.convertValue(externalReference.get("hashes"), new TypeReference<>() {
        });
        if (hashes != null)
            hashes.forEach(h -> externalReferenceObj.addHash(h.get("alg"), h.get("content")));

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
        List<HashMap<String, Object>> contacts = mapper.convertValue(organization.get("contact"), new TypeReference<>() {
        });
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
        String depBomRef = (String) dependency.get("ref");
        // add all deps
        List<String> bomRefs = mapper.convertValue(dependency.get("dependsOn"), new TypeReference<>() {
        });
        if (bomRefs != null)
            bomRefs.forEach(br -> relationships.add(new Relationship(depBomRef, "DEPENDS_ON")));    //todo replace with SPDX enums

        return relationships;
    }


    /**
     * Set the metadata for this SBOM object
     *
     * @param sbomBuilder Builder used to make SBOM
     * @param content     Map with SBOM details
     */
    private void setMetadata(CDX14Builder sbomBuilder, HashMap<String, Object> content) {
        // set basic details
        sbomBuilder.setFormat((String) content.get("bomFormat"))
                .setUID((String) content.get("serialNumber"))
                .setVersion(Integer.toString((Integer) content.get("version")))
                .setSpecVersion((String) content.get("specVersion"));

        // get the metadata object
        Map<String, Object> metadata = mapper.convertValue(
                content.get("metadata"),
                new TypeReference<>() {
                }
        );

        // add licenses
        List<String> licenses = mapper.convertValue(metadata.get("licenses"), new TypeReference<>() {
        });
        if (licenses != null)
            licenses.forEach(sbomBuilder::addLicense);

        // set root component
        HashMap<String, Object> rootComponent = mapper.convertValue(metadata.get("component"), new TypeReference<>() {
        });

        sbomBuilder.setRootComponent(resolvePackage(rootComponent));

        // set creation data
        sbomBuilder.setCreationData(resolveCreationData(metadata));
    }

    /**
     * Load CycloneDX SBOM file into memory
     *
     * @param file   File to deserialize
     * @param format Format of file
     * @return CycloneDX 1.4 SBOM
     * @throws DeserializerException Failed to load file
     */
    @Override
    public CDX14SBOM deserialize(File file, FileFormat format) throws DeserializerException {
        // cdx doesn't support tag-value
        if (format == FileFormat.TAG_VALUE)
            throw new DeserializerException("CycloneDX 1.4 does not support Tag-Value", file, format);
        // load into map
        HashMap<String, Object> content = super.loadFile(file, format);

        // initialize builders
        CDX14Builder sbomBuilder = new CDX14Builder();

        // set fields
        setMetadata(sbomBuilder, content);

        // components
        List<HashMap<String, Object>> packages = mapper.convertValue(content.get("components"), new TypeReference<>() {
        });
        if (packages != null)
            packages.forEach(p -> sbomBuilder.addCDX14Package(resolvePackage(p)));

        // external references
        List<HashMap<String, Object>> externalReferences = mapper.convertValue(content.get("externalReferences"), new TypeReference<>() {
        });
        if (externalReferences != null)
            externalReferences.forEach(er -> sbomBuilder.addExternalReference(resolveExternalReference(er)));

        // Dependencies
        List<HashMap<String, Object>> dependencies = mapper.convertValue(content.get("dependencies"), new TypeReference<>() {
        });
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

