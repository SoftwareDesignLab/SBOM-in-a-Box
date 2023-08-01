package org.svip.generation.serializers.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.serializer.SPDX23JSONSerializer;
import org.svip.utils.Debug;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SPDX23JSONSerializerTest extends SerializerTest {
    public SPDX23JSONSerializerTest() {
        super(new SPDX23JSONSerializer());
    }

    @Test
    public void writeToStringTest() throws JsonProcessingException {
        Debug.logBlockTitle("SPDX 2.3 JSON");
        String serialized = getSerializer().writeToString(getTestSBOM());
        Debug.log(Debug.LOG_TYPE.DEBUG, "\n" + serialized);
        Debug.log(Debug.LOG_TYPE.SUMMARY, "Successfully serialized SBOM.");
        Debug.logBlock();

        Debug.log(Debug.LOG_TYPE.SUMMARY, "Deserializing SBOM back to object.");
        SBOM sbom = SerializerFactory.createDeserializer(serialized).readFromString(serialized);

        // TODO Compare getTestSBOM() and sbom with Comparison when it's finished
        assertEquals(3, sbom.getComponents().size());
    }
}
