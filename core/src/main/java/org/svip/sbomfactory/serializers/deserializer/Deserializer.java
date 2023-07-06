package org.svip.sbomfactory.serializers.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.model.interfaces.generics.SBOM;

public interface Deserializer {
    public abstract SBOM readFromString(String fileContents);

    public ObjectMapper getObjectMapper();
}
