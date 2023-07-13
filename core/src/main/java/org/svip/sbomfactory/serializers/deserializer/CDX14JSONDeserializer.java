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

import javax.management.relation.Relation;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    public CDX14SBOM readFromString(String fileContents) throws JsonProcessingException {
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
    public CDX14SBOM deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        // get JSON node
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        // initialize builders
        CDX14SBOMBuilderFactory sbomFactory = new CDX14SBOMBuilderFactory();
        CDX14Builder sbomBuilder = sbomFactory.createBuilder();
        CDX14PackageBuilderFactory componentFactory = new CDX14PackageBuilderFactory();
        CDX14PackageBuilder componentBuilder = componentFactory.createBuilder();

        // FORMAT
        if (node.get("bomFormat") != null) sbomBuilder.setFormat(node.get("bomFormat").asText());

        // UID
        if (node.get("serialNumber") != null) sbomBuilder.setUID(node.get("serialNumber").asText());

        // VERSION
        if (node.get("version") != null) sbomBuilder.setVersion(node.get("version").asText());

        // SPEC VERSION
        if (node.get("specVersion") != null) sbomBuilder.setSpecVersion(node.get("specVersion").asText());

        // LICENSES
        JsonNode licenses = node.get("metadata").get("licenses");
        if (licenses != null)
            for (JsonNode license : licenses)
                sbomBuilder.addLicense(license.asText());

        // METADATA
        sbomBuilder.setCreationData(resolveMetadata(node.get("metadata")));

        // ROOT COMPONENT
        sbomBuilder.setRootComponent(resolveComponent(componentBuilder, node.get("metadata").get("component")));

        // COMPONENTS
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
                    componentBuilder.setGroup(node.get("components").get(i).get("group").asText());
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
                if (node.get("components").get(i).get("pedigree") != null && node.get("components").get(i).get("bom-ref") != null) {
                    if (node.get("components").get(i).get("pedigree").get("ancestors") != null) {
                        for (int j = 0; j < node.get("components").get(i).get("pedigree").get("ancestors").size(); j++) {
                            Relationship relationship = new Relationship(node.get("components").get(i).get("pedigree").get("ancestors").get(j).asText(),
                                    "ancestor");
                            sbomBuilder.addRelationship(node.get("components").get(i).get("bom-ref").asText(), relationship);
                        }
                    }
                    if (node.get("components").get(i).get("pedigree").get("descendants") != null) {
                        for (int j = 0; j < node.get("components").get(i).get("pedigree").get("descendants").size(); j++) {
                            Relationship relationship = new Relationship(node.get("components").get(i).get("pedigree").get("descendants").get(j).asText(),
                                    "descendant");
                            sbomBuilder.addRelationship(node.get("components").get(i).get("bom-ref").asText(), relationship);
                        }
                    }
                    if (node.get("components").get(i).get("pedigree").get("variants") != null) {
                        for (int j = 0; j < node.get("components").get(i).get("pedigree").get("variants").size(); j++) {
                            Relationship relationship = new Relationship(node.get("components").get(i).get("pedigree").get("variants").get(j).asText(),
                                    "variant");
                            sbomBuilder.addRelationship(node.get("components").get(i).get("bom-ref").asText(), relationship);
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

    private CreationData resolveMetadata(JsonNode metadata) {
        if (metadata == null) return null;
        CreationData creationData = new CreationData();

        // CREATED
        JsonNode timestamp = metadata.get("timestamp");
        if (timestamp != null) creationData.setCreationTime(timestamp.asText());

        // CREATION TOOLS
        JsonNode tools = metadata.get("tools");
        if (tools != null) {
            for (JsonNode tool : tools) {
                CreationTool creationTool = new CreationTool();

                // TOOL VENDOR
                if (tool.get("vendor") != null) creationTool.setVendor(tool.get("vendor").asText());

                // TOOL NAME
                if (tool.get("name") != null) creationTool.setName(tool.get("name").asText());

                // TOOL VERSION
                if (tool.get("version") != null) creationTool.setVersion(tool.get("version").asText());

                // TOOL HASHES
                if (tool.get("hashes") != null) resolveHashes(tool.get("hashes")).forEach(creationTool::addHash);

                // add the creation tool to the creation data
                creationData.addCreationTool(creationTool);
            }
        }

        JsonNode authors = metadata.get("authors");
        if (authors != null)
            for (JsonNode author : authors)
                creationData.addAuthor(resolveContact(author));

        JsonNode manufacture = metadata.get("manufacture");
        if (manufacture != null) creationData.setManufacture(resolveOrganization(manufacture));

        JsonNode supplier = metadata.get("supplier");
        if (supplier != null) creationData.setSupplier(resolveOrganization(supplier));

        return creationData;
    }
    
    private CDX14ComponentObject resolveComponent(CDX14PackageBuilder builder, JsonNode component) {
            // COMPONENT TYPE
            if (component.get("type") != null) builder.setType(component.get("type").asText());

            // COMPONENT MIME TYPE
            if (component.get("mime-type") != null) builder.setMimeType(component.get("mime-type").asText());

            // COMPONENT UID
            if (component.get("bom-ref") != null) builder.setUID(component.get("bom-ref").asText());

            // COMPONENT SUPPLIER
            JsonNode supplier = component.get("supplier");
            if (supplier != null) builder.setSupplier(resolveOrganization(supplier));

            // COMPONENT AUTHOR
            if (component.get("author") != null) builder.setAuthor(component.get("author").asText());

            // COMPONENT PUBLISHER
            if (component.get("publisher") != null) builder.setPublisher(component.get("publisher").asText());

            // COMPONENT GROUP
            if (component.get("group") != null)
                builder.setGroup(component.get("group").asText());

            // COMPONENT NAME
            if (component.get("name") != null)
                builder.setName(component.get("name").asText());

            // COMPONENT VERSION
            if (component.get("version") != null)
                builder.setVersion(component.get("version").asText());

            // COMPONENT DESCRIPTION
            if (component.get("description") != null)
                builder.setDescription(new Description(component.get("description").asText()));

            // COMPONENT SCOPE
            if (component.get("scope") != null)
                builder.setScope(component.get("scope").asText());

            // COMPONENT HASHES

            if (component.get("hashes") != null) resolveHashes(component.get("hashes")).forEach(builder::addHash);

            // COMPONENT Licenses
            JsonNode licenses = component.get("licenses");
            if (licenses != null) {
                LicenseCollection componentLicenses = new LicenseCollection();
                for (JsonNode license : licenses)
                    componentLicenses.addLicenseInfoFromFile(license.get("name").asText());

                builder.setLicenses(componentLicenses);
            }

            // COMPONENT COPYRIGHT
            if (component.get("copyright") != null) builder.setCopyright(component.get("copyright").asText());

            // COMPONENT CPE
            if (component.get("cpe") != null) builder.addCPE(component.get("cpe").asText());

            // COMPONENT PURL
            if (component.get("purl") != null) builder.addPURL(component.get("purl").asText());

            // COMPONENT EXTERNAL REFERENCES
            JsonNode externalRefs = component.get("externalReferences");
            if (component.get("externalReferences") != null) {
                for (JsonNode ref : externalRefs)
                    builder.addExternalReference(resolveExternalRef(ref));
            }
            // COMPONENT PROPERTIES
            if (component.get("properties") != null) {
                for (int i = 0; i < component.get("properties").size(); i++) {
                    builder.addProperty(component.get("properties").get(i).get("name").asText(), component.get("properties").get(i).get("value").asText());
                }
            }

        // add the component to the sbom builder
        return builder.buildAndFlush();
    }

    private Map<String, String> resolveHashes(JsonNode hashes) {
        Map<String, String> hashMap = new HashMap<>(); // Literally a hash map lol

        for (JsonNode hash : hashes)
            hashMap.put(hash.get("alg").asText(), hash.get("content").asText());

        return hashMap;
    }

    private Contact resolveContact(JsonNode ct) {
        return new Contact(ct.get("name").asText(),
                ct.get("email").asText(),
                ct.get("phone").asText());
    }

    private Organization resolveOrganization(JsonNode org) {
        Organization organization = new Organization(org.get("name").asText(), org.get("url").asText());
        if (org.get("contact") != null)
            for (JsonNode contact : org.get("contact"))
                organization.addContact(resolveContact(contact));

        return organization;
    }

    private ExternalReference resolveExternalRef(JsonNode ref) {
        ExternalReference externalReference = new ExternalReference(ref.get("url").asText(), ref.get("type").asText());

        // TODO do we want to store comments?

        JsonNode hashes = ref.get("hashes");
        if (hashes != null)
            resolveHashes(hashes).forEach(externalReference::addHash);

        return externalReference;
    }
}
