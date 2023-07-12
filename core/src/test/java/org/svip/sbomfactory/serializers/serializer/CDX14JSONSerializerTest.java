package org.svip.sbomfactory.serializers.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.generators.utils.Debug;

public class CDX14JSONSerializerTest extends SerializerTest {
    public CDX14JSONSerializerTest() {
        super(new CDX14JSONSerializer());
    }

    @Test
    public void writeToStringTest() throws JsonProcessingException {
        Debug.log(Debug.LOG_TYPE.SUMMARY, getSerializer().writeToString(getTestSBOM()));
    }
}
