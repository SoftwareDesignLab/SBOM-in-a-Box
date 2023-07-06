package org.svip.sbomfactory.serializers.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.model.objects.SVIPSBOM;

public interface Serializer {

    public String writeToString(SVIPSBOM sbom);

    public ObjectMapper getObjectMapper();
}
