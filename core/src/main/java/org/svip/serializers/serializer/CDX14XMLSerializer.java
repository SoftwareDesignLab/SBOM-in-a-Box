package org.svip.serializers.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.model.objects.SVIPSBOM;

public class CDX14XMLSerializer implements Serializer{
    @Override
    public String writeToString(SVIPSBOM sbom) {
        return null;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return null;
    }

    @Override
    public void setPrettyPrinting(boolean prettyPrint) {

    }
}
