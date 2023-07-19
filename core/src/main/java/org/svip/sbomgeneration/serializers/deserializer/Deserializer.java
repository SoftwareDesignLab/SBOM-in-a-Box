package org.svip.sbomgeneration.serializers.deserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.model.interfaces.generics.SBOM;

/**
 * File: Deserializer.java
 * This interface is to be implemented by all deserializers.
 *
 * @author Ian Dunn
 */
public interface Deserializer {

    /**
     * Deserializes an SBOM from a string with the schema and format of the specific serializer.
     *
     * @param fileContents The file contents of the SBOM to deserialize.
     * @return The deserialized SBOM object.
     */
    public abstract SBOM readFromString(String fileContents) throws JsonProcessingException;

    /**
     * Gets the ObjectMapper of the serializer to expose configuration.
     *
     * @return A reference to the ObjectMapper of the serializer.
     */
    public ObjectMapper getObjectMapper();
}
