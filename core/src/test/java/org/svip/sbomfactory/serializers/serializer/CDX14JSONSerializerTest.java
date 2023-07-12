package org.svip.sbomfactory.serializers.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.serializers.SerializerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CDX14JSONSerializerTest extends SerializerTest {
    public CDX14JSONSerializerTest() {
        super(new CDX14JSONSerializer());
    }

    @Test
    public void writeToStringTest() throws JsonProcessingException {
        Debug.logBlockTitle("CDX 1.4 JSON");
        String serialized = getSerializer().writeToString(getTestSBOM());
        Debug.log(Debug.LOG_TYPE.DEBUG, "\n" + serialized);
        Debug.log(Debug.LOG_TYPE.SUMMARY, "Successfully serialized SBOM.");
        Debug.logBlock();

        Debug.log(Debug.LOG_TYPE.SUMMARY, "Deserializing SBOM back to object.");
        SBOM sbom = SerializerFactory.createDeserializer(serialized).readFromString(serialized);

        // TODO more assertions / equals checker?
        assertEquals(4, sbom.getComponents().size());
    }
}
