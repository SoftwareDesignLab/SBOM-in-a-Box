package org.svip.sbomfactory.serializers.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;

import java.io.IOException;

public class SPDX23JSONDeserializer extends StdDeserializer<SPDX23SBOM> implements Deserializer {
    public SPDX23JSONDeserializer() {
        super((Class<SPDX23SBOM>) null);
    }

    protected SPDX23JSONDeserializer(Class<SPDX23SBOM> t) {
        super(t);
    }

    @Override
    public SPDX23SBOM deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException,
            JacksonException {
        return null;
    }

    @Override
    public SBOM readFromString(String fileContents) {
        return null;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return null;
    }
}
