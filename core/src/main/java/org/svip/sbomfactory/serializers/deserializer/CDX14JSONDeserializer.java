package org.svip.sbomfactory.serializers.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;

import java.io.IOException;

/**
 * File: CDX14JSONDeserializer.java
 * This class implements the Deserializer interface and the Jackson StdDeserializer to provide all functionality to
 * read a CDX1.4 SBOM object from a CDX 1.4 JSON file string.
 *
 * @author Ian Dunn
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

        return mapper; // TODO ensure this returns the correct mapper instance
    }

    @Override
    public CDX14SBOM deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException,
            JacksonException {
        // TODO
        return null;
    }
}
