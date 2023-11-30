package org.svip.serializers.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14Builder;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14PackageBuilder;
import org.svip.sbom.factory.objects.CycloneDX14.CDX14PackageBuilderFactory;
import org.svip.sbom.factory.objects.CycloneDX14.CDX14SBOMBuilderFactory;
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

import java.io.IOException;
import java.util.*;

/**
 * File: CDX14XMLDeserializer.java
 * This class implements the Deserializer interface and the Jackson StdDeserializer to provide all functionality to
 * read a CDX1.4 SBOM object from a CDX1.4 JSON file string.
 *
 * @author Thomas Roman
 * @author Ian Dunn
 */
public class CDX14XMLDeserializer extends StdDeserializer<CDX14SBOM> implements Deserializer {
    public CDX14XMLDeserializer() {
        super(CDX14SBOM.class);
    }

    protected CDX14XMLDeserializer(Class<CDX14SBOM> t) {
        super(t);
    }

    /**
     * Deserializes a CDX 1.4 XML SBOM from a string.
     *
     * @param fileContents The file contents of the CDX 1.4 XML SBOM to deserialize.
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
        ObjectMapper mapper = new XmlMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(CDX14SBOM.class, this);
        mapper.registerModule(module);
        PrettyPrinter prettyPrinter = new DefaultXmlPrettyPrinter();
        mapper.setDefaultPrettyPrinter(prettyPrinter);
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
        sbomBuilder.setFormat("CycloneDX");

        // UID
        if (node.get("serialNumber") != null) sbomBuilder.setUID(node.get("serialNumber").asText());

        // VERSION
        if (node.get("version") != null) sbomBuilder.setVersion(node.get("version").asText());

        // TODO: Spec Version (regex?)

        if (node.get("metadata") != null && node.get("metadata").asText() != "") {

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
        if (node.get("components") != null && !node.get("components").isEmpty()) {
            for (JsonNode component : node.get("components").get("component")) {
                CDX14ComponentObject comp = resolveComponent(componentBuilder, component);
                sbomBuilder.addCDX14Package(comp);
            }
        }


        // todo, it's strange, but it looks like node.get("components").get("component") is actually the list of components, could be wrong though.. same thing might go for licenses and refs
//        if (node.get("components") != null && node.get("components").get("component") != null)
//            for (JsonNode component : node.get("components").get("component"))
//                sbomBuilder.addCDX14Package(resolveComponent(componentBuilder, component));

        // EXTERNAL REFERENCES
        if (node.get("externalReferences") != null && node.get("externalReferences").asText() != "")
            for (JsonNode ref : node.get("externalReferences"))
                sbomBuilder.addExternalReference(resolveExternalRef(ref));

        if (node.get("dependencies") != null && node.get("dependencies").asText() != "")
            for (JsonNode depNode : node.get("dependencies").get("dependency")) {
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
                if (tool.get("hashes") != null && tool.get("hashes").asText() != "") resolveHashes(tool.get("hashes").get("hash")).forEach(creationTool::addHash);

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

        if (metadata.get("properties") != null) {
            JsonNode properties = metadata.get("properties").get("property");
            if(properties instanceof ArrayNode) {
                for (JsonNode prop : properties) {
                    String name = prop.get("name").asText();
                    String value = prop.get("").asText();
                    if (name.equals("creatorComment")) creationData.setCreatorComment(value);
                    else creationData.addProperty(name, value);
                }
            } else {
                String name = properties.get("name").asText();
                String value = properties.get("").asText();
                if (name.equals("creatorComment")) creationData.setCreatorComment(value);
                else creationData.addProperty(name, value);
            }

        }

        if (metadata.get("licenses") != null && metadata.get("licenses").asText() != "")
            for (JsonNode license : metadata.get("licenses")) {
                // TODO will it cause problems duplicating these?
                sbomBuilder.addLicense(license.get("id").asText());
                creationData.addLicense(license.get("id").asText());
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

        if (component.get("hashes") != null && component.get("hashes").asText() != "") resolveHashes(component.get("hashes").get("hash")).forEach(builder::addHash);

        // COMPONENT Licenses
        JsonNode licenses = component.get("licenses");
        if (licenses != null) {
            LicenseCollection componentLicenses = new LicenseCollection();
            for (JsonNode license : licenses) {
                if (license.get("id") == null) continue;
                componentLicenses.addLicenseInfoFromFile(license.get("id").asText());
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
        if (externalRefs != null && externalRefs.asText() != "") {
            // If more than 1 external reference, iterate through all
            if (externalRefs.get("reference") instanceof ArrayNode){
                for (JsonNode ref : externalRefs.get("reference"))
                    builder.addExternalReference(resolveExternalRef(ref));
            // Else add single JsonObject external reference
            } else {
                builder.addExternalReference(resolveExternalRef(externalRefs.get("reference")));
            }
        }

        // COMPONENT PROPERTIES
        if (component.get("properties") != null && component.get("properties").asText() != "") {
            JsonNode properties = component.get("properties").get("property");
            if(properties instanceof ArrayNode) {
                for (JsonNode prop : properties) {
                    builder.addProperty(prop.get("name").asText(), prop.get("").asText());
                }
            } else {
                builder.addProperty(properties.get("name").asText(), properties.get("").asText());
            }
        }

        // add the component to the sbom builder
        return builder.buildAndFlush();
    }

    private Map<String, String> resolveHashes(JsonNode hashes) {
        Map<String, String> hashMap = new HashMap<>(); // Literally a hash map lol

        for (JsonNode hash : hashes) {
            hashMap.put(hash.get("hashAlg").asText(), hash.get("hashValue").asText());
        }

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
        if (hashes != null && hashes.asText() != "")
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
