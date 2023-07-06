package org.svip.sbomfactory.serializers.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;

import java.io.IOException;

/**
 * File: CDX14JSONSerializer.java
 * This class implements the Serializer interface and the Jackson StdSerializer to provide all functionality to write an
 * SBOM object to a CDX 1.4 JSON file string.
 *
 * @author Ian Dunn
 */
public class CDX14JSONSerializer extends StdSerializer<CDX14SBOM> implements Serializer {
    public CDX14JSONSerializer() {
        super((Class<CDX14SBOM>) null);
    }

    protected CDX14JSONSerializer(Class<CDX14SBOM> t) {
        super(t);
    }

    /**
     * Serializes an SBOM to a CDX 1.4 JSON file.
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
    public void serialize(CDX14SBOM sbom, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {

    }
}
