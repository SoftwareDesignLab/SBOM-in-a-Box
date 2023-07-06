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
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.LicenseCollection;

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
        return getObjectMapper().readValue(fileContents, SPDX23SBOM.class);
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

        // add MetaData to the builder
        sbomBuilder.setFormat(node.get("bomFormat").asText());
        sbomBuilder.setUID(node.get("serialNumber").asText());
        sbomBuilder.setVersion(node.get("version").asText());
        sbomBuilder.setSpecVersion(node.get("specVersion").asText());
        // TO DO: add all the licenses, not just the first one
        sbomBuilder.addLicense(node.get("MD:licenses").get(0).asText());
        if (node.get("component") != null) {
            componentBuilder.setType(node.get("component").get("type").asText());
            componentBuilder.setMimeType(node.get("component").get("mime-type").asText());
            Organization supplier = new Organization(node.get("component").get("supplier:name").asText(), node.get("component").get("supplier:url").asText());
            componentBuilder.setSupplier(supplier);
            componentBuilder.setAuthor(node.get("component").get("author").asText());
            componentBuilder.setPublisher(node.get("component").get("publisher").asText());
            componentBuilder.setGroup(node.get("component").get("group").asText());
            componentBuilder.setName(node.get("component").get("name").asText());
            componentBuilder.setVersion(node.get("component").get("version").asText());
            Description description = new Description(node.get("component").get("description").asText());
            componentBuilder.setDescription(description);
            componentBuilder.setScope(node.get("component").get("scope").asText());
            componentBuilder.addHash(node.get("component").get("hashes").get(0).asText(), node.get("component").get("hashes").get(1).asText());
            // TO DO: add all the licenses, not just the first one
            LicenseCollection componentLicenses = new LicenseCollection();
            componentLicenses.addDeclaredLicense(node.get("component").get("licenses").get(0).asText());
            componentBuilder.setLicenses(componentLicenses);
            // TO DO: add purl, swid, pedigree, externalReferences, evidence, properties, releaseNotes, and signature
        }

        // TO DO: add the rest of the components

        // TO DO: build the SBOM
        return null;
    }
}
