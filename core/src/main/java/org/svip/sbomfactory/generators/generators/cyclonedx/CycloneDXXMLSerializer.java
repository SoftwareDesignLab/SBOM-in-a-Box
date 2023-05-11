package org.svip.sbomfactory.generators.generators.cyclonedx;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.dataformat.xml.util.StaxUtil;
import org.svip.sbomfactory.generators.generators.utils.GeneratorException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * File: CycloneDXXMLSerializer.java
 * <p>
 * A custom serializer for the <code>CycloneDXStore</code> class extended from the Jackson library's <code>STDSerializer</code>
 * class to convert the data of <code>CycloneDXStore</code> to a CDX v1.4 bill of materials in XML format.
 * </p>
 * @author Ian Dunn
 */
public class CycloneDXXMLSerializer extends StdSerializer<CycloneDXStore> {

    //#region Constants

    /**
     * The namespace URI of the CDX XML schema
     */
    private static final String NAMESPACE_URI = "http://cyclonedx.org/schema/bom/1.4";

    /**
     * The prefix used to represent the XML namespace
     */
    private static final String PREFIX = "bom";

    //#endregion

    //#region Constructors

    /**
     * The default serializer constructor that takes in no arguments and serializes a null CycloneDXStore class.
     */
    public CycloneDXXMLSerializer() {
        super((Class<CycloneDXStore>) null);
    }

    /**
     * A serializer constructor that takes in a CycloneDXStore class to serialize.
     *
     * @param t The CycloneDXStore class object.
     */
    protected CycloneDXXMLSerializer(Class<CycloneDXStore> t) {
        super(t);
    }

    //#endregion

    //#region Overrides

    /**
     * The default serialize method called by Jackson ObjectMappers to serialize the CycloneDXStore class to an XML CDX
     * bill of materials.
     *
     * @param cycloneDXStore The CycloneDXStore instance with the bill of materials data.
     * @param jsonGenerator The JsonGenerator used by Jackson to serialize to a file.
     * @param serializerProvider The SerializerProvider used by Jackson to serialize to a file.
     * @throws IOException If an error writing to the file occurs.
     */
    @Override
    public void serialize(CycloneDXStore cycloneDXStore, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {

        //
        // Initialize generator
        //

        ToXmlGenerator xmlGenerator = (ToXmlGenerator) jsonGenerator;
        QName defaultRootName = new QName(NAMESPACE_URI, PREFIX, PREFIX);
        initGenerator(xmlGenerator, defaultRootName);

        //
        // Root Component TODO
        //

        xmlGenerator.writeStartObject();
        writeAttribute(xmlGenerator, "serialNumber", cycloneDXStore.getSerialNumber());
        writeAttribute(xmlGenerator, "version", cycloneDXStore.getBOMVersion());

        //
        // Metadata
        //

        xmlGenerator.writeFieldName("metadata");
        xmlGenerator.writeStartObject();
        xmlGenerator.writeStringField("tool", "testvalue");
        xmlGenerator.writeEndObject();

        //
        // Components
        //

        xmlGenerator.writeFieldName("components");
        xmlGenerator.writeStartObject();
        xmlGenerator.writeStringField("component", "testvalue");
        xmlGenerator.writeEndObject();

        xmlGenerator.writeEndObject();
    }

    //#endregion

    //#region Helper Methods

    /**
     * A private helper method to initialize an instance of a {@code ToXmlGenerator} with a root {@code QName},
     * including a default prefix, namespace, and local root name.
     *
     * @param xmlGenerator The {@code ToXmlGenerator} to initialize.
     * @param rootName The {@code QName} that stores the default prefix, namespace, and local root name.
     * @throws IOException If the XML Stream Writer cannot set the default namespace.
     */
    private void initGenerator(ToXmlGenerator xmlGenerator, QName rootName) throws IOException {
        if (!xmlGenerator.setNextNameIfMissing(rootName) && xmlGenerator.inRoot()) {
            xmlGenerator.setNextName(rootName); // Set root name if current name is missing or in root
        }
        xmlGenerator.initGenerator(); // Initialize generator

        String ns = rootName.getNamespaceURI(); // Get the default namespace we want from rootName
        if (ns != null && ns.length() > 0) {
            try {
                xmlGenerator.getStaxWriter().setPrefix(PREFIX, ns);
            } catch (XMLStreamException e) {
                StaxUtil.throwAsGenerationException(e, xmlGenerator);
            }
        }
    }

    private void writeAttribute(ToXmlGenerator xmlGenerator, String name, Object value) throws IOException {
        xmlGenerator.setNextIsAttribute(true);
        xmlGenerator.writeObjectField(name, value);
        xmlGenerator.setNextIsAttribute(false);
    }

    //#endregion
}
