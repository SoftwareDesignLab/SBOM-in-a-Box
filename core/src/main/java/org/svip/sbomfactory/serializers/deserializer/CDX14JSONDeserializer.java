package org.svip.sbomfactory.serializers.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.svip.builderfactory.CDX14SBOMBuilderFactory;
import org.svip.builders.component.CDX14PackageBuilder;
import org.svip.componentfactory.CDX14PackageBuilderFactory;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14Builder;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import javax.management.relation.Relation;
import java.io.IOException;

/**
 * File: CDX14JSONDeserializer.java
 * This class implements the Deserializer interface and the Jackson StdDeserializer to provide all functionality to
 * read a CDX1.4 SBOM object from a CDX 1.4 JSON file string.
 *
 * @author Ian Dunn
 * @author Thomas Roman
 */
public class CDX14JSONDeserializer extends StdDeserializer<CDX14SBOM> implements Deserializer {
    public CDX14JSONDeserializer() {
        super(CDX14SBOM.class);
    }

    protected CDX14JSONDeserializer(Class<CDX14SBOM> t) {
        super(t);
    }

    /**
     * Deserializes a CDX 1.4 JSON SBOM from a string.
     *
     * @param fileContents The file contents of the CDX 1.4 JSON SBOM to deserialize.
     * @return The deserialized CDX 1.4 SBOM object.
     */
    @Override
    public SBOM readFromString(String fileContents) throws JsonProcessingException {
        return getObjectMapper().readValue(fileContents, CDX14SBOM.class);
    }

    /**
     * Gets the ObjectMapper of the serializer to expose configuration.
     *
     * @return A reference to the ObjectMapper of the serializer.
     */
    @Override
    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(CDX14SBOM.class, this);
        mapper.registerModule(module);

        return mapper;
    }

    @Override
    public CDX14SBOM deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException,
            JacksonException {
        // get JSON node
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        // initialize builders
        CDX14SBOMBuilderFactory sbomFactory = new CDX14SBOMBuilderFactory();
        CDX14Builder sbomBuilder = sbomFactory.createBuilder();
        CDX14PackageBuilderFactory componentFactory = new CDX14PackageBuilderFactory();
        CDX14PackageBuilder componentBuilder = componentFactory.createBuilder();

        // FORMAT
        if (node.get("bomFormat") != null) {
            sbomBuilder.setFormat(node.get("bomFormat").asText());
        }
        // UID
        if (node.get("serialNumber") != null) {
            sbomBuilder.setUID(node.get("serialNumber").asText());
        }
        // VERSION
        if (node.get("version") != null) {
            sbomBuilder.setVersion(node.get("version").asText());
        }
        // SPEC VERSION
        if (node.get("specVersion") != null) {
            sbomBuilder.setSpecVersion(node.get("specVersion").asText());
        }
        // add MetaData to the builder
        if (node.get("metadata") != null) {
            // LICENSES
            if (node.get("metadata").get("licenses") != null) {
                for (int i = 0; i < node.get("metadata").get("licenses").size(); i++) {
                    sbomBuilder.addLicense(node.get("metadata").get("licenses").asText());
                }
            }
            // CREATION DATA
            CreationData creationData = new CreationData();
            // CREATED
            if (node.get("metadata").get("timestamp") != null) {
                creationData.setCreationTime(node.get("metadata").get("timestamp").asText());
            }
            // CREATION TOOL
            if (node.get("metadata").get("tools") != null) {
                for (int i = 0; i < node.get("metadata").get("tools").size(); i++) {
                    CreationTool creationTool = new CreationTool();
                    // TOOL VENDOR
                    if (node.get("metadata").get("tools").get(i).get("vendor") != null) {
                        creationTool.setVendor(node.get("metadata").get("tools").get(i).get("vendor").asText());
                    }
                    // TOOL NAME
                    if (node.get("metadata").get("tools").get(i).get("name") != null) {
                        creationTool.setName(node.get("metadata").get("tools").get(i).get("name").asText());
                    }
                    // TOOL VERSION
                    if (node.get("metadata").get("tools").get(i).get("version") != null) {
                        creationTool.setVersion(node.get("metadata").get("tools").get(i).get("version").asText());
                    }
                    // TOOL HASHES
                    if (node.get("metadata").get("tools").get(i).get("hashes") != null) {
                        for (int j = 0; j < node.get("metadata").get("tools").get(i).get("hashes").size(); j++) {
                            creationTool.addHash(node.get("metadata").get("tools").get(i).get("hashes").get(j).get("alg").asText(),
                                    node.get("metadata").get("tools").get(i).get("hashes").get(j).get("content").asText());
                        }
                    }
                    // add the creation tool to the creation data
                    creationData.addCreationTool(creationTool);
                }
            }
            // add the creation data to the builder
            sbomBuilder.setCreationData(creationData);
        }

        // ROOT COMPONENT
        if (node.get("metadata").get("component") != null) {
            // ROOT COMPONENT TYPE
            if (node.get("metadata").get("component").get("type") != null) {
                componentBuilder.setType(node.get("metadata").get("component").get("type").asText());
            }
            // ROOT COMPONENT MIME TYPE
            if (node.get("metadata").get("component").get("mime-type") != null) {
                componentBuilder.setMimeType(node.get("metadata").get("component").get("mime-type").asText());
            }
            // ROOT COMPONENT UID
            if (node.get("metadata").get("component").get("bom-ref") != null) {
                componentBuilder.setUID(node.get("metadata").get("component").get("bom-ref").asText());
            }
            // ROOT COMPONENT SUPPLIER
            if (node.get("metadata").get("component").get("supplier") != null) {
                if (node.get("metadata").get("component").get("supplier").get("name") != null && node.get("metadata").get("component").get("supplier").get("url") != null) {
                    Organization supplier = new Organization(node.get("metadata").get("component").get("supplier").get("name").asText(),
                            node.get("metadata").get("component").get("supplier").get("url").asText());
                    if (node.get("metadata").get("component").get("supplier").get("contact") != null) {
                        // Contact requires all fields in its constructor, it might be more usable with an empty constructor and "add" functions
                        if (node.get("metadata").get("component").get("supplier").get("contact").get("name") != null &&
                                node.get("metadata").get("component").get("supplier").get("contact").get("email") != null &&
                                node.get("metadata").get("component").get("supplier").get("contact").get("phone") != null) {
                            Contact contact = new Contact(node.get("metadata").get("component").get("supplier").get("contact").get("name").asText(),
                                    node.get("metadata").get("component").get("supplier").get("contact").get("email").asText(),
                                    node.get("metadata").get("component").get("supplier").get("contact").get("phone").asText());
                            supplier.addContact(contact);
                        }
                    }
                    componentBuilder.setSupplier(supplier);
                }
            }
            // ROOT COMPONENT AUTHOR
            if (node.get("metadata").get("component").get("author") != null) {
                componentBuilder.setAuthor(node.get("metadata").get("component").get("author").asText());
            }
            // ROOT COMPONENT PUBLISHER
            if (node.get("metadata").get("component").get("publisher") != null) {
                componentBuilder.setPublisher(node.get("metadata").get("component").get("publisher").asText());
            }
            // ROOT COMPONENT GROUP
            if (node.get("metadata").get("component").get("group") != null) {
                componentBuilder.setGroup(node.get("metadata").get("component").get("group").asText());
            }
            // ROOT COMPONENT NAME
            if (node.get("metadata").get("component").get("name") != null) {
                componentBuilder.setName(node.get("metadata").get("component").get("name").asText());
            }
            // ROOT COMPONENT VERSION
            if (node.get("metadata").get("component").get("version") != null) {
                componentBuilder.setVersion(node.get("metadata").get("component").get("version").asText());
            }
            // ROOT COMPONENT DESCRIPTION
            if (node.get("metadata").get("component").get("description") != null) {
                Description description = new Description(node.get("metadata").get("component").get("description").asText());
                componentBuilder.setDescription(description);
            }
            // ROOT COMPONENT SCOPE
            if (node.get("metadata").get("component").get("scope") != null) {
                componentBuilder.setScope(node.get("metadata").get("component").get("scope").asText());
            }
            // ROOT COMPONENT HASHES
            if (node.get("metadata").get("component").get("hashes") != null) {
                for (int i = 0; i < node.get("metadata").get("component").get("hashes").size(); i++) {
                    componentBuilder.addHash(node.get("metadata").get("component").get("hashes").get(i).get("alg").asText(),
                            node.get("metadata").get("component").get("hashes").get(i).get("content").asText());
                }
            }
            // ROOT COMPONENT Licenses
            if (node.get("metadata").get("component").get("licenses") != null) {
                LicenseCollection componentLicenses = new LicenseCollection();
                for (int i = 0; i < node.get("metadata").get("component").get("licenses").size(); i++) {
                    componentLicenses.addLicenseInfoFromFile(node.get("metadata").get("component").get("licenses").get(i).asText());
                }
                componentBuilder.setLicenses(componentLicenses);
            }
            // ROOT COMPONENT COPYRIGHT
            if (node.get("metadata").get("component").get("copyright") != null) {
                componentBuilder.setCopyright(node.get("metadata").get("component").get("copyright").asText());
            }
            // ROOT COMPONENT CPE
            if (node.get("metadata").get("component").get("cpe") != null) {
                componentBuilder.addCPE(node.get("metadata").get("component").get("cpe").asText());
            }
            // ROOT COMPONENT PURL
            if (node.get("metadata").get("component").get("purl") != null) {
                componentBuilder.addPURL(node.get("metadata").get("component").get("purl").asText());
            }
            // ROOT COMPONENT EXTERNAL REFERENCES
            if (node.get("metadata").get("component").get("externalReferences") != null) {
                for (int i = 0; i < node.get("metadata").get("component").get("externalReferences").size(); i++) {
                    if (node.get("metadata").get("component").get("externalReferences").get(i).get("url") != null &&
                            node.get("metadata").get("component").get("externalReferences").get(i).get("type") != null) {
                        ExternalReference externalReference = new ExternalReference(
                                node.get("metadata").get("component").get("externalReferences").get(i).get("url").asText(),
                                node.get("metadata").get("component").get("externalReferences").get(i).get("type").asText());
                        // add hashes to the external reference
                        if (node.get("metadata").get("component").get("externalReferences").get(i).get("hashes") != null) {
                            for (int j = 0; j < node.get("metadata").get("component").get("externalReferences").get(i).get("hashes").size(); j++) {
                                componentBuilder.addHash(node.get("metadata").get("component").get("externalReferences").get(i).get("hashes").get(j).get("alg").asText(),
                                        node.get("metadata").get("component").get("externalReferences").get(i).get("hashes").get(j).get("content").asText());
                            }
                        }
                        componentBuilder.addExternalReference(externalReference);
                    }
                }
            }
            // ROOT COMPONENT PROPERTIES
            if (node.get("metadata").get("component").get("properties") != null) {
                for (int i = 0; i < node.get("component").get("properties").size(); i++) {
                    componentBuilder.addProperty(node.get("component").get("properties").get(i).get("name").asText(), node.get("component").get("properties").get(i).get("value").asText());
                }
            }
            // ROOT COMPONENT RELATIONSHIPS
            if (node.get("metadata").get("component").get("pedigree") != null) {
                if (node.get("metadata").get("component").get("ancestors") != null) {
                    for (int i = 0; i < node.get("metadata").get("component").get("ancestors").size(); i++) {
                        Relationship relationship = new Relationship(node.get("metadata").get("component").get("ancestors").get(i).asText(),
                                "ancestor");
                    }
                }
                if (node.get("metadata").get("component").get("descendants") != null) {
                    for (int i = 0; i < node.get("metadata").get("component").get("descendants").size(); i++) {
                        Relationship relationship = new Relationship(node.get("metadata").get("component").get("descendants").get(i).asText(),
                                "descendant");
                    }
                }
                if (node.get("metadata").get("component").get("variants") != null) {
                    for (int i = 0; i < node.get("metadata").get("component").get("variants").size(); i++) {
                        Relationship relationship = new Relationship(node.get("metadata").get("component").get("variants").get(i).asText(),
                                "variant");
                    }
                }
            }
            // add the component to the sbom builder
            sbomBuilder.setRootComponent(componentBuilder.buildAndFlush());
        }

        // Add the rest of the components
        if (node.get("components") != null) {
            for (int i = 0; i < node.get("components").size(); i++) {
                // TYPE
                if (node.get("components").get(i).get("type") != null) {
                    componentBuilder.setType(node.get("components").get(i).get("type").asText());
                }
                // MIME TYPE
                if (node.get("components").get(i).get("mime-type") != null) {
                    componentBuilder.setMimeType(node.get("components").get(i).get("mime-type").asText());
                }
                // UID
                if (node.get("components").get(i).get("bom-ref") != null) {
                    componentBuilder.setUID(node.get("components").get(i).get("bom-ref").asText());
                }
                // SUPPLIER
                if (node.get("components").get(i).get("supplier") != null) {
                    if (node.get("components").get(i).get("supplier").get("name") != null && node.get("components").get(i).get("supplier").get("url") != null) {
                        Organization supplier = new Organization(node.get("components").get(i).get("supplier").get("name").asText(),
                                node.get("components").get(i).get("supplier").get("url").asText());
                        if (node.get("components").get(i).get("supplier").get("contact") != null) {
                            // Contact requires all fields in its constructor, it might be more usable with an empty constructor and "add" functions
                            if (node.get("components").get(i).get("supplier").get("contact").get("name") != null &&
                                    node.get("components").get(i).get("supplier").get("contact").get("email") != null &&
                                    node.get("components").get(i).get("supplier").get("contact").get("phone") != null) {
                                Contact contact = new Contact(node.get("components").get(i).get("supplier").get("contact").get("name").asText(),
                                        node.get("components").get(i).get("supplier").get("contact").get("email").asText(),
                                        node.get("components").get(i).get("supplier").get("contact").get("phone").asText());
                                supplier.addContact(contact);
                            }
                        }
                        componentBuilder.setSupplier(supplier);
                    }
                }
                // AUTHOR
                if (node.get("components").get(i).get("author") != null) {
                    componentBuilder.setAuthor(node.get("components").get(i).get("author").asText());
                }
                // PUBLISHER
                if (node.get("components").get(i).get("publisher") != null) {
                    componentBuilder.setPublisher(node.get("components").get(i).get("publisher").asText());
                }
                // GROUP
                if (node.get("components").get(i).get("group") != null) {
                    componentBuilder.setGroup(node.get("component").get(i).get("group").asText());
                }
                // NAME
                if (node.get("components").get(i).get("name") != null) {
                    componentBuilder.setName(node.get("components").get(i).get("name").asText());
                }
                // VERSION
                if (node.get("components").get(i).get("version") != null) {
                    componentBuilder.setVersion(node.get("components").get(i).get("version").asText());
                }
                // DESCRIPTION
                if (node.get("components").get(i).get("description") != null) {
                    Description description = new Description(node.get("components").get(i).get("description").asText());
                    componentBuilder.setDescription(description);
                }
                // SCOPE
                if (node.get("components").get(i).get("scope") != null) {
                    componentBuilder.setScope(node.get("components").get(i).get("scope").asText());
                }
                // HASHES
                if (node.get("components").get(i).get("hashes") != null) {
                    for (int j = 0; j < node.get("components").get(i).get("hashes").size(); j++) {
                        componentBuilder.addHash(node.get("components").get(i).get("hashes").get(j).get("alg").asText(), node.get("components").get(i).get("hashes").get(j).get("content").asText());
                    }
                }
                // Licenses
                if (node.get("components").get(i).get("licenses") != null) {
                    LicenseCollection componentLicenses = new LicenseCollection();
                    for (int j = 0; j < node.get("components").get(i).get("licenses").size(); j++) {
                        componentLicenses.addLicenseInfoFromFile(node.get("components").get(i).get("licenses").get(j).asText());
                    }
                    componentBuilder.setLicenses(componentLicenses);
                }
                // COPYRIGHT
                if (node.get("components").get(i).get("copyright") != null) {
                    componentBuilder.setCopyright(node.get("components").get(i).get("copyright").asText());
                }
                // CPE
                if (node.get("components").get(i).get("cpe") != null) {
                    componentBuilder.addCPE(node.get("components").get(i).get("cpe").asText());
                }
                // PURL
                if (node.get("components").get(i).get("purl") != null) {
                    componentBuilder.addPURL(node.get("components").get(i).get("purl").asText());
                }
                // EXTERNAL REFERENCES
                if (node.get("components").get(i).get("externalReferences") != null) {
                    for (int j = 0; j < node.get("components").get(i).get("externalReferences").size(); j++) {
                        if (node.get("components").get(i).get("externalReferences").get(j).get("url") != null &&
                                node.get("components").get(i).get("externalReferences").get(j).get("type") != null) {
                            ExternalReference externalReference = new ExternalReference(
                                    node.get("components").get(i).get("externalReferences").get(j).get("url").asText(),
                                    node.get("components").get(i).get("externalReferences").get(j).get("type").asText());
                            // add hashes to the external reference
                            if (node.get("components").get(i).get("externalReferences").get(j).get("hashes") != null) {
                                for (int k = 0; k < node.get("components").get(i).get("externalReferences").get(j).get("hashes").size(); k++) {
                                    componentBuilder.addHash(node.get("components").get(i).get("externalReferences").get(j).get("hashes").get(k).get("alg").asText(),
                                            node.get("components").get(i).get("externalReferences").get(j).get("hashes").get(k).get("content").asText());
                                }
                            }
                            componentBuilder.addExternalReference(externalReference);
                        }
                    }
                }
                // PROPERTIES
                if (node.get("components").get(i).get("properties") != null) {
                    for (int j = 0; j < node.get("components").get(i).get("properties").size(); j++) {
                        componentBuilder.addProperty(node.get("components").get(i).get("properties").get(j).get("name").asText(), node.get("components").get(i).get("properties").get(j).get("value").asText());
                    }
                }
                // RELATIONSHIPS
                if (node.get("components").get(i).get("pedigree") != null) {
                    if (node.get("components").get(i).get("ancestors") != null) {
                        for (int j = 0; j < node.get("components").get(i).get("ancestors").size(); j++) {
                            Relationship relationship = new Relationship(node.get("components").get(i).get("ancestors").get(j).asText(),
                                    "ancestor");
                        }
                    }
                    if (node.get("components").get(i).get("descendants") != null) {
                        for (int j = 0; j < node.get("components").get(i).get("descendants").size(); j++) {
                            Relationship relationship = new Relationship(node.get("components").get(i).get("descendants").get(j).asText(),
                                    "descendant");
                        }
                    }
                    if (node.get("components").get(i).get("variants") != null) {
                        for (int j = 0; j < node.get("components").get(i).get("variants").size(); j++) {
                            Relationship relationship = new Relationship(node.get("components").get(i).get("variants").get(j).asText(),
                                    "variant");
                        }
                    }
                }
                // add the component to the sbom builder
                sbomBuilder.addCDX14Package(componentBuilder.buildAndFlush());
            }
        }
        // Build the SBOM
        return sbomBuilder.buildCDX14SBOM();
    }
}
