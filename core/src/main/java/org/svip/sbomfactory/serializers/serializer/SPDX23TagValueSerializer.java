package org.svip.sbomfactory.serializers.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.model.objects.SVIPSBOM;

public class SPDX23TagValueSerializer implements Serializer {
    @Override
    public String writeToString(SVIPSBOM sbom) {
        return null;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return null;
    }
}
