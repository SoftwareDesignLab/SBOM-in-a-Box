package org.svip.generation.serializers.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.serializer.CDX14JSONSerializer;
import org.svip.serializers.serializer.CDX14XMLSerializer;
import org.svip.serializers.serializer.Serializer;
import org.svip.utils.Debug;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CDX14XMLSerializerTest extends SerializerTest {

    public CDX14XMLSerializerTest() {
        super(new CDX14XMLSerializer());
    }

    @Test
    public void writeToStringTest() throws JsonProcessingException {
        Debug.logBlockTitle("CDX 1.4 XML");
        String serialized = getSerializer().writeToString(getTestSBOM());
        Debug.log(Debug.LOG_TYPE.DEBUG, "\n" + serialized);
        Debug.logBlock();
        Debug.log(Debug.LOG_TYPE.SUMMARY, "Successfully serialized SBOM.");

        Debug.log(Debug.LOG_TYPE.SUMMARY, "Deserializing SBOM back to object.");
        CDX14SBOM sbom = (CDX14SBOM) SerializerFactory.createDeserializer(serialized).readFromString(serialized);

        // TODO Compare getTestSBOM() and sbom with Comparison when it's finished
        assertEquals(3, sbom.getComponents().size());
    }

}