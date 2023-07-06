package org.svip.sbomfactory.serializers.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.model.interfaces.generics.SBOM;

public class SPDX23TagValueDeserializer implements Deserializer {
    @Override
    public SBOM readFromString(String fileContents) {
        return null;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return null;
    }
}
