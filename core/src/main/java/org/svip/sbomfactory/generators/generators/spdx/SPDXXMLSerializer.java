package org.svip.sbomfactory.generators.generators.spdx;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.dataformat.xml.util.StaxUtil;
import org.svip.sbom.model.PURL;
import org.svip.sbomfactory.generators.generators.cyclonedx.CycloneDXSerializer;
import org.svip.sbomfactory.generators.generators.cyclonedx.CycloneDXStore;
import org.svip.sbomfactory.generators.generators.utils.License;
import org.svip.sbomfactory.generators.generators.utils.Tool;
import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbomfactory.generators.utils.QueryWorker;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * File: SPDXXMLSerializer.java
 * <p>
 * A custom serializer for the <code>SPDXStore</code> class extended from the Jackson library's <code>STDSerializer</code>
 * class to convert the data of <code>SPDXStore</code> to an SPDX v2.3 document in XML format.
 * </p>
 * @author Ian Dunn
 */
public class SPDXXMLSerializer extends StdSerializer<SPDXStore> {

    //#region Constants

    /** TODO Not version-specific, but /terms/2.3 does not exist so not much we can do?
     * The namespace URI of the SPDX XML schema
     */
    private static final String SPDX_NAMESPACE_URI = "https://spdx.org/rdf/terms/";
    private static final String RDF_NAMESPACE_URI = "http://www.w3.org/2000/01/rdf-schema";

    /**
     * The prefix used to represent the XML namespace
     */
    private static final String SPDX_PREFIX = "spdx";
    private static final String RDF_PREFIX = "rdf";

    /**
     * The object storing the root name, prefix, and namespace of the BOM
     */
    private static final QName ROOT_NAME = new QName(SPDX_NAMESPACE_URI, "SPDXDocument", SPDX_PREFIX);

    //#endregion

    //#region Constructors

    /**
     * The default serializer constructor that takes in no arguments and serializes a null SPDXStore class.
     */
    public SPDXXMLSerializer() {
        super((Class<SPDXStore>) null);
    }

    /**
     * A serializer constructor that takes in a SPDXStore class to serialize.
     *
     * @param t The CycloneDXStore class object.
     */
    protected SPDXXMLSerializer(Class<SPDXStore> t) {
        super(t);
    }

    //#endregion

    //#region Overrides

    /**
     * The default serialize method called by Jackson ObjectMappers to serialize the CycloneDXStore class to an XML SPDX
     * document.
     *
     * @param spdxStore The SPDXStore instance with the document data.
     * @param jsonGenerator The JsonGenerator used by Jackson to serialize to a file.
     * @param serializerProvider The SerializerProvider used by Jackson to serialize to a file.
     * @throws IOException If an error writing to the file occurs.
     */
    @Override
    public void serialize(SPDXStore spdxStore, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {

        //
        // Initialize generator
        //

        ToXmlGenerator xmlGenerator = (ToXmlGenerator) jsonGenerator;
        initGenerator(xmlGenerator, ROOT_NAME);

        //
        // Root Component
        //

        xmlGenerator.writeStartObject();
        writeAttribute(xmlGenerator, "xmlns:" + RDF_PREFIX, RDF_NAMESPACE_URI); // Write another namespace

        xmlGenerator.writeStringField("test", "testvalue");

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
                xmlGenerator.getStaxWriter().setPrefix(SPDX_PREFIX, ns);
            } catch (XMLStreamException e) {
                StaxUtil.throwAsGenerationException(e, xmlGenerator);
            }
        }
    }

    // TODO Exact same method from CycloneDXXMLSerializer
    private void writeAttribute(ToXmlGenerator xmlGenerator, String name, Object value) throws IOException {
        xmlGenerator.setNextIsAttribute(true);
        xmlGenerator.setNextName(new QName(name));
        xmlGenerator.writeObjectField(name, value);
        xmlGenerator.setNextIsAttribute(false);
        xmlGenerator.setNextName(ROOT_NAME);
    }

    // TODO Exact same method from CycloneDXXMLSerializer
    private void writeObject(ToXmlGenerator xmlGenerator, String objectName) throws IOException {
        xmlGenerator.writeFieldName(objectName);
        xmlGenerator.writeStartObject();
    }

    //#endregion
}
