package org.svip.sbomfactory.serializers.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;

import java.io.IOException;

/**
 * File: SPDX23JSONDeserializer.java
 * This class implements the Deserializer interface and the Jackson StdDeserializer to provide all functionality to
 * read an SPDX 2.3 SBOM object from an SPDX 2.3 JSON file string.
 *
 * @author Ian Dunn
 */
public class SPDX23JSONDeserializer extends StdDeserializer<SPDX23SBOM> implements Deserializer {
    public SPDX23JSONDeserializer() {
        super(SPDX23SBOM.class);
    }

    protected SPDX23JSONDeserializer(Class<SPDX23SBOM> t) {
        super(t);
    }

    /**
     * Deserializes an SPDX 2.3 JSON SBOM from a string.
     *
     * @param fileContents The file contents of the SPDX 2.3 JSON SBOM to deserialize.
     * @return The deserialized SPDX 2.3 SBOM object.
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
        module.addDeserializer(SPDX23SBOM.class, this);
        mapper.registerModule(module);

        return mapper; // TODO ensure this returns the correct mapper instance
    }

    @Override
    public SPDX23SBOM deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException,
            JacksonException {
        return null;
    }
}
