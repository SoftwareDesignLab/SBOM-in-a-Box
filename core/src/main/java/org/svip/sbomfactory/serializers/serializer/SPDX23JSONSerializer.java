package org.svip.sbomfactory.serializers.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;

import java.io.IOException;

public class SPDX23JSONSerializer extends StdSerializer<SPDX23SBOM> implements Serializer {
    public SPDX23JSONSerializer() {
        super((Class<SPDX23SBOM>) null);
    }

    protected SPDX23JSONSerializer(Class<SPDX23SBOM> t) {
        super(t);
    }

    @Override
    public String writeToString(SVIPSBOM sbom) {
        return null;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return null;
    }

    @Override
    public void serialize(SPDX23SBOM sbom, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {

    }
}
