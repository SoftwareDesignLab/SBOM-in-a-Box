package org.svip.sbomfactory.serializers.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.model.interfaces.generics.SBOM;

/**
 * File: SPDX23TagValueDeserializer.java
 * This class implements the Deserializer interface and the Jackson StdDeserializer to provide all functionality to
 * read an SPDX 2.3 SBOM object from an SPDX 2.3 tag-value file string.
 *
 * @author Ian Dunn
 */
public class SPDX23TagValueDeserializer implements Deserializer {

    /**
     * Deserializes an SPDX 2.3 tag-value SBOM from a string.
     *
     * @param fileContents The file contents of the SPDX 2.3 tag-value SBOM to deserialize.
     * @return The deserialized SPDX 2.3 SBOM object.
     */
    @Override
    public SBOM readFromString(String fileContents) {
        // TODO replace with code from TranslatorSPDX
        return null;
//        return new SPDX23SBOM();
    }

    /**
     * Gets the ObjectMapper of the serializer to expose configuration.
     *
     * @return A reference to the ObjectMapper of the serializer.
     */
    @Override
    public ObjectMapper getObjectMapper() {
        // We don't need an objectmapper for tag value but removing this breaks tests
        return new ObjectMapper();
    }
}
