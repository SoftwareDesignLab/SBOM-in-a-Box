package org.svip.serializers.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;

import java.io.IOException;
import java.util.UUID;

public class CDX14XMLSerializer extends StdSerializer<SVIPSBOM> implements Serializer {

    private boolean prettyPrint = false;

    public CDX14XMLSerializer() {
        super(SVIPSBOM.class);
    }

    protected CDX14XMLSerializer(Class<SVIPSBOM> t) {
        super(t);
    }

    @Override
    public String writeToString(SVIPSBOM sbom) throws JsonProcessingException {
        if(prettyPrint)
            return getObjectMapper().writer().with(SerializationFeature.INDENT_OUTPUT).writeValueAsString(sbom);
        else return getObjectMapper().writer().writeValueAsString(sbom);
    }

    @Override
    public ObjectMapper getObjectMapper() {
        XmlMapper mapper = new XmlMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(SVIPSBOM.class, this);
        mapper.registerModule(module);
        return mapper;
    }

    @Override
    public void setPrettyPrinting(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    @Override
    public void serialize(SVIPSBOM sbom, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        ToXmlGenerator xmlGenerator = (ToXmlGenerator) gen;

        // Start XML bom object
        xmlGenerator.writeStartObject();

        //
        // Set top level attributes
        //
        xmlGenerator.setNextIsAttribute(true);

        // Schema and SpecVersion
        xmlGenerator.writeFieldName("xmlns");
        xmlGenerator.writeString("http://cyclonedx.org/schema/bom/1.4");

        // Serial Number
        xmlGenerator.writeFieldName("serialNumber");
        xmlGenerator.writeString("urn:uuid:" + UUID.randomUUID());

        // Version
        xmlGenerator.writeFieldName("version");
        xmlGenerator.writeString(sbom.getVersion());

        xmlGenerator.setNextIsAttribute(false);

        //
        // Metadata
        //

        //
        // Components
        //

        xmlGenerator.writeFieldName("components");
        xmlGenerator.writeStartObject();

        //xmlGenerator.writeFieldName("component");
        //xmlGenerator.writeStartArray("component");


        for (Component component : sbom.getComponents()) {
            if (component == null) continue;
            SVIPComponentObject svipComponent = (SVIPComponentObject) component;
            writeComponent(xmlGenerator, svipComponent);
        }


        // End components object
        xmlGenerator.writeEndObject();

        // End the bom object
        xmlGenerator.writeEndObject();

    }

    public void writeComponent(ToXmlGenerator xmlGenerator, SVIPComponentObject svipComponentObject) throws IOException {

        xmlGenerator.writeFieldName("component");
        xmlGenerator.writeStartObject();

        // Set the top level info for the component: bom-ref ID and type
        xmlGenerator.setNextIsAttribute(true);
        xmlGenerator.writeFieldName("bom-ref");
        xmlGenerator.writeString(svipComponentObject.getUID());
        xmlGenerator.writeFieldName("type");
        xmlGenerator.writeString(svipComponentObject.getType());
        xmlGenerator.setNextIsAttribute(false);

        xmlGenerator.writeStringField("name", svipComponentObject.getName());
        xmlGenerator.writeStringField("version", svipComponentObject.getVersion());
        xmlGenerator.writeStringField("scope", svipComponentObject.getScope());

        xmlGenerator.writeEndObject();
    }


}
