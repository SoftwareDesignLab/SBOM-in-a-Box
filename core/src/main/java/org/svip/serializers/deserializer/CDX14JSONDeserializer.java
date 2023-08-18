package org.svip.serializers.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14Builder;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14PackageBuilder;
import org.svip.sbom.factory.objects.CycloneDX14.CDX14PackageBuilderFactory;
import org.svip.sbom.factory.objects.CycloneDX14.CDX14SBOMBuilderFactory;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        else sbomBuilder.setFormat("CycloneDX");

        // UID
        if (node.get("serialNumber") != null) sbomBuilder.setUID(node.get("serialNumber").asText());

        // VERSION
        if (node.get("version") != null) sbomBuilder.setVersion(node.get("version").asText());

        // SPEC VERSION
        if (node.get("specVersion") != null) sbomBuilder.setSpecVersion(node.get("specVersion").asText());

        if (node.get("metadata") != null) {

             // LICENSES
            if (node.get("metadata").get("licenses") != null) {
                JsonNode licenses = node.get("metadata").get("licenses");
                for (JsonNode license : licenses)
                    sbomBuilder.addLicense(license.asText());
            }

            // ROOT COMPONENT
            if (node.get("metadata").get("component") != null)
                sbomBuilder.setRootComponent(resolveComponent(componentBuilder, node.get("metadata").get("component")));
        }

        // METADATA
        sbomBuilder.setCreationData(resolveMetadata(node.get("metadata"), sbomBuilder));

        // COMPONENTS
        if (node.get("components") != null)
            for (JsonNode component : node.get("components"))
                sbomBuilder.addCDX14Package(resolveComponent(componentBuilder, component));

        // EXTERNAL REFERENCES
        if (node.get("externalReferences") != null)
            for (JsonNode ref : node.get("externalReferences"))
                sbomBuilder.addExternalReference(resolveExternalRef(ref));

        if (node.get("dependencies") != null)
            for (JsonNode depNode : node.get("dependencies")) {
                String ref = depNode.get("ref").asText();
                resolveDependency(depNode).forEach(d -> sbomBuilder.addRelationship(ref, d));
            }

        // Build the SBOM
        return sbomBuilder.buildCDX14SBOM();
    }

    private CreationData resolveMetadata(JsonNode metadata, CDX14Builder sbomBuilder) {
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

                // TOOL EXTERNAL REFERENCES
                JsonNode externalRefs = tool.get("externalReferences");
                if (tool.get("externalReferences") != null) {
                    for (JsonNode ref : externalRefs)
                        creationTool.addExternalReference(resolveExternalRef(ref));
                }

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

        if (metadata.get("properties") != null)
            for (JsonNode prop : metadata.get("properties")) {
                String name = prop.get("name").asText();
                String value = prop.get("value").asText();
                if (name.equals("creatorComment")) creationData.setCreatorComment(value);
                else creationData.addProperty(name, value);
            }

        if (metadata.get("licenses") != null)
            for (JsonNode license : metadata.get("licenses")) {
                // TODO will it cause problems duplicating these?
                sbomBuilder.addLicense(license.get("name").asText());
                creationData.addLicense(license.get("name").asText());
            }

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
            for (JsonNode license : licenses) {
                if (license.get("license").get("id") != null) {
                    componentLicenses.addLicenseInfoFromFile(license.get("license").get("id").asText());
                }
                else if (license.get("license").get("name") != null) {
                    componentLicenses.addLicenseInfoFromFile(license.get("license").get("name").asText());
                }
            }

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
        if (component.get("properties") != null)
            for (JsonNode prop : component.get("properties"))
                builder.addProperty(prop.get("name").asText(), prop.get("value").asText());

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
        try {
            return new Contact(ct.get("name").asText(),
                    ct.get("email").asText(),
                    ct.get("phone").asText());
        } catch (Exception e) {
            return new Contact("", "", "");
        }
    }

    private Organization resolveOrganization(JsonNode org) {
        try {
            Organization organization = new Organization(org.get("name").asText(), org.get("url").asText());
            if (org.get("contact") != null)
                for (JsonNode contact : org.get("contact"))
                    organization.addContact(resolveContact(contact));
            return organization;
        } catch (Exception e) {
            return new Organization("", "");
        }


    }

    private ExternalReference resolveExternalRef(JsonNode ref) {
        ExternalReference externalReference = new ExternalReference(ref.get("url").asText(), ref.get("type").asText());

        // TODO do we want to store comments?

        JsonNode hashes = ref.get("hashes");
        if (hashes != null)
            resolveHashes(hashes).forEach(externalReference::addHash);

        return externalReference;
    }

    private Set<Relationship> resolveDependency(JsonNode dep) {
        Set<Relationship> relationships = new HashSet<>();

        if (dep.get("dependsOn") == null) return relationships;

        for (JsonNode dependency : dep.get("dependsOn")) {
            if (dependency == null) continue;
            relationships.add(new Relationship(dependency.asText(), "DEPENDS_ON")); // TODO correct type?
        }

        return relationships;
    }
}
