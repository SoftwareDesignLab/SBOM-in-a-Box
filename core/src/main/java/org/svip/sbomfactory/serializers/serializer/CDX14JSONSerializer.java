package org.svip.sbomfactory.serializers.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;

import java.io.IOException;

public class CDX14JSONSerializer extends StdSerializer<CDX14SBOM> implements Serializer {
    public CDX14JSONSerializer() {
        super((Class<CDX14SBOM>) null);
    }

    protected CDX14JSONSerializer(Class<CDX14SBOM> t) {
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
    public void serialize(CDX14SBOM sbom, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {

    }
}
