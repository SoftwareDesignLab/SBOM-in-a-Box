package org.svip.sbomfactory.serializers.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.svip.utils.Debug;

public class SVIPSBOMJSONSerializerTest extends SerializerTest {
    public SVIPSBOMJSONSerializerTest() {
        super(new SVIPSBOMJSONSerializer());
    }

    @Test
    public void writeToStringTest() throws JsonProcessingException {
        Debug.log(Debug.LOG_TYPE.SUMMARY, getSerializer().writeToString(getTestSBOM()));
    }
}
