package org.svip.sbomfactory.serializers.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.model.objects.SVIPSBOM;

/**
 * File: SPDX23TagValueSerializer.java
 * This class implements the Serializer interface to provide all functionality to write an SBOM object to an SPDX 2.3
 * tag-value file string.
 *
 * @author Ian Dunn
 */
public class SPDX23TagValueSerializer implements Serializer {

    /**
     * Serializes an SBOM to an SPDX 2.3 tag-value file.
     *
     * @param sbom The SBOM to serialize.
     * @return A string containing the final SBOM file.
     */
    @Override
    public String writeToString(SVIPSBOM sbom) {
        return null;
    }

    /**
     * Gets the ObjectMapper of the serializer to expose configuration.
     *
     * @return A reference to the ObjectMapper of the serializer.
     */
    @Override
    public ObjectMapper getObjectMapper() {
        return null;
    }
}
