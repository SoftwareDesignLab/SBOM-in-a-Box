package org.svip.sbomfactory.generators.generators.cyclonedx;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

public class CycloneDXXMLSerializer extends StdSerializer<CycloneDXStore> {
    public CycloneDXXMLSerializer() {
        super((Class<CycloneDXStore>) null);
    }

    protected CycloneDXXMLSerializer(Class<CycloneDXStore> t) {
        super(t);
    }

    @Override
    public void serialize(CycloneDXStore cycloneDXStore, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        ToXmlGenerator xmlGenerator = (ToXmlGenerator) jsonGenerator;
        QName rootName = new QName("bom");
        xmlGenerator.setNextName(rootName);
        xmlGenerator.initGenerator();
        XMLStreamWriter writer = xmlGenerator.getStaxWriter();

        try {
            writer.setPrefix("bom", "namespace");
            writer.setDefaultNamespace("testnamespace");
            writer.writeStartDocument();
            writer.writeStartElement("root");

            writer.writeStartElement("testName");
            writer.writeAttribute("testAttribute", "testAttributeValue");
            writer.writeCharacters("test value");
            writer.writeEndElement();

            writer.writeEndElement();
            writer.writeEndDocument();

        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }

//        xmlGenerator.writeStartObject();
//        xmlGenerator.setNextIsAttribute(false);
//        xmlGenerator.writeStringField("test", "test");
//        xmlGenerator.writeEndObject();
    }
}
