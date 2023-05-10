package org.svip.sbomfactory.generators.generators.cyclonedx;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.dataformat.xml.util.StaxUtil;
import org.svip.sbomanalysis.qualityattributes.QualityReport;

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
//        xmlGenerator.initGenerator();
//        xmlGenerator.setNextName(new QName("", "root"));
        initWithRootName(xmlGenerator, new QName("namespace", "root", "bom"));
//        try {
//            xmlGenerator.getStaxWriter().writeStartDocument("1.1");
//            xmlGenerator.getStaxWriter().setDefaultNamespace("bom");
//        } catch (XMLStreamException e) {
//            throw new RuntimeException(e);
//        }

        // TODO Writing XML objects is currently broken in the library:
        //  https://github.com/FasterXML/jackson-dataformat-xml/issues/595
        xmlGenerator.writeStartObject();

        xmlGenerator.setNextName(new QName("bom:metadata"));
        xmlGenerator.writeFieldName("bom:metadata");
        xmlGenerator.writeStartObject();
        xmlGenerator.writeStringField("test", "value");
        xmlGenerator.writeEndObject();

        xmlGenerator.writeEndObject();

//        XMLStreamWriter writer = xmlGenerator.getStaxWriter();
//
//        try {
//            writer.writeStartDocument();
//            writer.writeStartElement("root");
//
//            writer.writeStartElement("testName");
//            writer.writeAttribute("testAttribute", "testAttributeValue");
//            writer.writeCharacters("test value");
//            writer.writeEndElement();
//
//            writer.writeEndElement();
//            writer.writeEndDocument();
//        } catch (XMLStreamException e) {
//            throw new RuntimeException(e);
//        }
    }

    protected void initWithRootName(ToXmlGenerator xgen, QName rootName) throws IOException
    {
        if (!xgen.setNextNameIfMissing(rootName)) {
            // however, if we are root, we... insist
            if (xgen.inRoot()) {
                xgen.setNextName(rootName);
            }
        }
        xgen.initGenerator();
        String ns = rootName.getNamespaceURI();
        if (ns != null && ns.length() > 0) {
            try {
                xgen.getStaxWriter().setDefaultNamespace(ns);
            } catch (XMLStreamException e) {
                StaxUtil.throwAsGenerationException(e, xgen);
            }
        }
    }
}
