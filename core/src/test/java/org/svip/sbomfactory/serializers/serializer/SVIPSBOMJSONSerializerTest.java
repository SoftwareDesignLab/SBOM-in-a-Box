package org.svip.sbomfactory.serializers.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.svip.utils.Debug;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SVIPSBOMJSONSerializerTest extends SerializerTest {
    public SVIPSBOMJSONSerializerTest() {
        super(new SVIPSBOMJSONSerializer());
    }

    @Test
    public void writeToStringTest() throws JsonProcessingException {
        // TODO is this testable since it just serializes the class itself?
        String svipSBOM = getSerializer().writeToString(getTestSBOM());
        assertNotNull(svipSBOM);
        Debug.log(Debug.LOG_TYPE.SUMMARY, svipSBOM);
    }
}
