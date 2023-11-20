package org.svip.generation.serializers.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.serializer.CDX14JSONSerializer;
import org.svip.serializers.serializer.CDX14XMLSerializer;
import org.svip.serializers.serializer.Serializer;
import org.svip.utils.Debug;
import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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

        // Check if the SBOM was serialized to XML. If we can parse it using SAXParserFactory, it worked.
        try {
            SAXParserFactory.newInstance().newSAXParser().getXMLReader().parse(new InputSource(new StringReader(serialized)));
        } catch (Exception e) {
            // Fail the test if the SaxParserFactory could not parse the XML
            fail("SBOM was not serialized to XML.");
        }

        // TODO: execute rest of test once deserializer for CDX XML is implemented
        /**
            Debug.log(Debug.LOG_TYPE.SUMMARY, "Deserializing SBOM back to object.");
            CDX14SBOM sbom = (CDX14SBOM) SerializerFactory.createDeserializer(serialized).readFromString(serialized);
            assertEquals(3, sbom.getComponents().size());
         **/
    }

}