package org.svip.serializers.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.model.objects.SVIPSBOM;

/**
 * File: Serializer.java
 * This interface is to be implemented by all serializers.
 *
 * @author Ian Dunn
 */
public interface Serializer {

    /**
     * Serializes an SBOM to a string with the schema and format of the specific serializer.
     *
     * @param sbom The SBOM to serialize.
     * @return A string containing the final SBOM file.
     */
    public String writeToString(SVIPSBOM sbom) throws JsonProcessingException;

    /**
     * Gets the ObjectMapper of the serializer to expose configuration.
     *
     * @return A reference to the ObjectMapper of the serializer.
     */
    public ObjectMapper getObjectMapper();

    /**
     * Sets the ObjectMapper of the serializer to enable or disable pretty printing.
     *
     * @param prettyPrint True to pretty-print, false otherwise.
     */
    public void setPrettyPrinting(boolean prettyPrint);
}
