package org.svip.sbomfactory.generators.generators.cyclonedx;

import org.svip.sbomfactory.generators.generators.utils.License;
import org.svip.sbomfactory.generators.generators.utils.Tool;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * File: BOMSerializer.java
 * <p>
 * A custom serializer for the <code>BOM</code> class extended from the Jackson library's <code>STDSerializer</code>
 * class to convert the data of <code>BOM</code> to an CDX v1.4 bill of materials.
 * </p>
 * @author Ian Dunn
 */
public class BOMSerializer extends StdSerializer<BOM> {

    //#region Constructors

    /**
     * The default serializer constructor that takes in no arguments and serializes a null BOM class.
     */
    protected BOMSerializer() { super((Class<BOM>) null); }

    /**
     * A serializer constructor that takes in a BOM class to serialize.
     *
     * @param t The BOM class object.
     */
    protected BOMSerializer(Class<BOM> t) {
        super(t);
    }

    //#endregion

    //#region Overrides

    /**
     * The default serialize method called by Jackson ObjectMappers to serialize the BOM class to a CDX
     * bill of materials.
     *
     * @param bom The BOM instance with the bill of materials data.
     * @param jsonGenerator The JsonGenerator used by Jackson to serialize to a file.
     * @param serializerProvider The SerializerProvider used by Jackson to serialize to a file.
     * @throws IOException If an error writing to the file occurs.
     */
    @Override
    public void serialize(BOM bom, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {

        jsonGenerator.writeStartObject(); // {

        //
        // BOM information
        //

        jsonGenerator.writeStringField("bomFormat", BOM.BOM_FORMAT);
        jsonGenerator.writeStringField("specVersion", BOM.SPEC_VERSION);
        jsonGenerator.writeStringField("serialNumber", bom.getSerialNumber());
        jsonGenerator.writeNumberField("version", bom.getVersion());

        //
        // BOM metadata
        //
        jsonGenerator.writeFieldName("metadata");
        jsonGenerator.writeStartObject(); // {

        /* Timestamp */
        jsonGenerator.writeStringField("timestamp", bom.getTimestamp());

        /* Tools */
        ArrayList<Tool> tools = bom.getTools();

        if(tools.size() > 0) {
            jsonGenerator.writeFieldName("tools");
            jsonGenerator.writeStartArray(); // [

            for(Tool tool : bom.getTools())
                writeTool(jsonGenerator, tool);

            jsonGenerator.writeEndArray(); // ]
        }

        /* Head Component */
        jsonGenerator.writeFieldName("component");
        writeComponent(jsonGenerator, bom, bom.getHeadComponent());
        jsonGenerator.writeEndObject(); // }

        //
        // Components
        //
        jsonGenerator.writeFieldName("components");
        jsonGenerator.writeStartArray(); // [

        for(ParserComponent c : bom.getComponents()) {
            writeComponent(jsonGenerator, bom, c);
        }

        jsonGenerator.writeEndArray(); // ]

        jsonGenerator.writeEndObject(); // }
    }

    //#endregion

    //#region Helper Methods

    /**
     * Private helper method to wite the data of a single component as an object using the provided JsonGenerator.
     *
     * @param jsonGenerator The JsonGenerator to use to write the package to the file.
     * @param bom The BOM that this component belongs to (used to get any component children).
     * @param component The component represented as a ParserComponent.
     * @throws IOException If an error writing to the file occurs.
     */
    private void writeComponent(JsonGenerator jsonGenerator, BOM bom, ParserComponent component) throws IOException {
        jsonGenerator.writeStartObject(); // {

        //
        // Package information
        //

        jsonGenerator.writeStringField("name", component.getName());
        if(component.getFile() != null) { // TODO put in CDX properties field
            writeFieldIfExists(jsonGenerator, "description", "File Analyzed: " + component.getFile());
        }
        writeFieldIfExists(jsonGenerator,"group", component.getGroup());
        writeFieldIfExists(jsonGenerator,"version", component.getVersion());

        //
        // Licenses
        //

        writeLicenses(jsonGenerator, component.getResolvedLicenses());

        //
        // External identifiers
        //

        writeFieldIfExists(jsonGenerator, "purl", String.join(", ", component.getPURL()));
        // TODO Add CPEs and SWIDs

        //
        // Type
        //

        jsonGenerator.writeStringField("type", getCDXType(component.getType()));

        //
        // Nested child components
        //

        // Write children
        List<ParserComponent> children = bom.getChildren(component.getUUID());
        if(children.size() > 0) {
            jsonGenerator.writeFieldName("components");
            jsonGenerator.writeStartArray();

            for(ParserComponent child : children) {
                writeComponent(jsonGenerator, bom, child);
            }

            jsonGenerator.writeEndArray();
        }

        jsonGenerator.writeEndObject(); // }
    }

    /**
     * Private helper method to write the data of a tool used to create the BOM as an object using the provided
     * JsonGenerator.
     *
     * @param jsonGenerator The JsonGenerator to use to write the tool to the file.
     * @param tool The tool used to create the BOM.
     * @throws IOException If an error writing to the file occurs.
     */
    private void writeTool(JsonGenerator jsonGenerator, Tool tool) throws IOException {
        jsonGenerator.writeStartObject(); // {

        jsonGenerator.writeStringField("vendor", tool.getVendor());
        jsonGenerator.writeStringField("name", tool.getName());
        jsonGenerator.writeStringField("version", tool.getVersion());

        jsonGenerator.writeFieldName("hashes");
        jsonGenerator.writeStartArray(); // [

        // Loop through hashes and print the algorithm and content
        for(Map.Entry<String, String> hash : tool.getHashes().entrySet()) {
            jsonGenerator.writeStartObject(); // {

            jsonGenerator.writeStringField("alg", hash.getValue());
            jsonGenerator.writeStringField("content", hash.getKey());

            jsonGenerator.writeEndObject(); // }
        }

        jsonGenerator.writeEndArray(); // ]

        jsonGenerator.writeEndObject(); // }
    }

    /**
     * Private helper method to write the data of a License as an object using the provided JsonGenerator.
     *
     * @param jsonGenerator The JsonGenerator to use to write the license to the file.
     * @param licenses A set of Licenses.
     * @throws IOException If an error writing to the file occurs.
     */
    private void writeLicenses(JsonGenerator jsonGenerator, Set<License> licenses) throws IOException {
        if(licenses.size() == 0) return; // If no licenses, do not write anything

        jsonGenerator.writeFieldName("licenses");
        jsonGenerator.writeStartArray(); // [

        for(License license : licenses) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeFieldName("license");
            jsonGenerator.writeStartObject(); // {

            if(license.getSpdxLicense() != null) {
                jsonGenerator.writeStringField("id", license.getSpdxLicense());
            } else {
                jsonGenerator.writeStringField("name", license.getLicenseName());
            }
            writeFieldIfExists(jsonGenerator, "url", license.getUrl());

            jsonGenerator.writeEndObject(); // }
            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray(); // ]
    }

    /**
     * Private helper method to write the data of a single field if the String value provided is not null and is not
     * empty. If the field does not exist, it will not be written.
     *
     * @param jsonGenerator The JsonGenerator to use to write the field to the file.
     * @param fieldName The name of the field to write.
     * @param value The value of the field to write. If null or empty, the field will not be written.
     * @throws IOException If an error writing to the file occurs.
     */
    private void writeFieldIfExists(JsonGenerator jsonGenerator, String fieldName, String value) throws IOException {
        if (value != null && value.length() > 0) { // Check if field exists
            jsonGenerator.writeStringField(fieldName, value); // Write key value pair to the file
        }
    }

    /**
     * Get the CDX-specific type of the component from our internal representation.
     * <ul>
     *     <li><code>LANGUAGE</code> maps to <code>framework</code></li>
     *     <li><code>INTERNAL</code> maps to <code>file</code></li>
     *     <li>Anything else maps to <code>library</code></li>
     * </ul>
     *
     * @param type The type of the ParserComponent.
     * @return The CDX-specific type that the ParserComponent type maps to.
     */
    private String getCDXType(ParserComponent.Type type) {
        // Set component type based on what the parser reads it in as
        // https://cyclonedx.org/docs/1.4/json/#components_items_type TODO figure out if we can be more descriptive
        switch(type) {
            case LANGUAGE -> { return "framework"; }
            case INTERNAL -> { return "file"; }
            default -> { return "library"; }
        }
    }

    //#endregion
}


