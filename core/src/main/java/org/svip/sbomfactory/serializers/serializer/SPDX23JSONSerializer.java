package org.svip.sbomfactory.serializers.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;

import java.io.IOException;

/**
 * File: SPDX23JSONSerializer.java
 * This class implements the Serializer interface and the Jackson StdSerializer to provide all functionality to write an
 * SBOM object to an SPDX 2.3 JSON file string.
 *
 * @author Ian Dunn
 */
public class SPDX23JSONSerializer extends StdSerializer<SPDX23SBOM> implements Serializer {
    public SPDX23JSONSerializer() {
        super((Class<SPDX23SBOM>) null);
    }

    protected SPDX23JSONSerializer(Class<SPDX23SBOM> t) {
        super(t);
    }

    /**
     * Serializes an SBOM to an SPDX 2.3 JSON file.
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

    @Override
    public void serialize(SPDX23SBOM sbom, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {

    }
}
