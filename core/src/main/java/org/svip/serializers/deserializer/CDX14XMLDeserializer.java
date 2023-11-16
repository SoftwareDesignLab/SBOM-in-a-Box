package org.svip.serializers.deserializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.fasterxml.jackson.dataformat.xml.deser.XmlDeserializationContext;
import com.fasterxml.jackson.dataformat.xml.deser.XmlTextDeserializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;

import java.io.IOException;

public class CDX14XMLDeserializer implements Deserializer {

    public void deserialize(CDX14SBOM value, FromXmlParser parser, SerializerProvider provider) throws IOException {

    }

    @Override
    public SBOM readFromString(String fileContents) {
        return null;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return null;
    }
}
