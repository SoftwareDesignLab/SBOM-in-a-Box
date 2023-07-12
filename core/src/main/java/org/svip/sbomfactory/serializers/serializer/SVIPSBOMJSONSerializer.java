package org.svip.sbomfactory.serializers.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.svip.sbom.model.objects.SVIPSBOM;

/**
 * File: SVIPSBOMJSONSerializer.java
 * This class implements the Serializer interface and the Jackson StdSerializer to provide all functionality to write an
 * SVIP SBOM object to a custom file string.
 *
 * @author Ian Dunn
 */
public class SVIPSBOMJSONSerializer implements Serializer {

    private boolean prettyPrint = false;

    /**
     * Serializes an SBOM to a CDX 1.4 JSON file.
     *
     * @param sbom The SBOM to serialize.
     * @return A string containing the final SBOM file.
     */
    @Override
    public String writeToString(SVIPSBOM sbom) throws JsonProcessingException {
        if (prettyPrint)
            return getObjectMapper().writer().with(SerializationFeature.INDENT_OUTPUT).writeValueAsString(sbom);
        else
            return getObjectMapper().writer().writeValueAsString(sbom);
    }

    /**
     * Gets the ObjectMapper of the serializer to expose configuration.
     *
     * @return A reference to the ObjectMapper of the serializer.
     */
    @Override
    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // We don't need to register a specific serializer because we only need the raw fields/values
//        SimpleModule module = new SimpleModule();
//        module.addSerializer(SVIPSBOM.class, this);
//        mapper.registerModule(module);

        return mapper;
    }

    /**
     * Sets the ObjectMapper of the serializer to enable or disable pretty printing.
     *
     * @param prettyPrint True to pretty-print, false otherwise.
     */
    @Override
    public void setPrettyPrinting(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }
}
