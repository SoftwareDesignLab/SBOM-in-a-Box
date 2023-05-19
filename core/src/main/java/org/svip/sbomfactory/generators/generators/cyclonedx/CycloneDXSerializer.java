package org.svip.sbomfactory.generators.generators.cyclonedx;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.svip.sbom.model.PURL;
import org.svip.sbomfactory.generators.generators.utils.License;
import org.svip.sbomfactory.generators.generators.utils.Tool;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * File: CycloneDXSerializer.java
 * <p>
 * A custom serializer for the <code>CycloneDXStore</code> class extended from the Jackson library's <code>STDSerializer</code>
 * class to convert the data of <code>CycloneDXStore</code> to a CDX v1.4 bill of materials in JSON or YAML format.
 * </p>
 * @author Ian Dunn
 */
public class CycloneDXSerializer extends StdSerializer<CycloneDXStore> {

    //#region Constructors

    /**
     * The default serializer constructor that takes in no arguments and serializes a null CycloneDXStore class.
     */
    public CycloneDXSerializer() { super((Class<CycloneDXStore>) null); }

    /**
     * A serializer constructor that takes in a CycloneDXStore class to serialize.
     *
     * @param t The CycloneDXStore class object.
     */
    protected CycloneDXSerializer(Class<CycloneDXStore> t) {
        super(t);
    }

    //#endregion

    //#region Overrides

    /**
     * The default serialize method called by Jackson ObjectMappers to serialize the CycloneDXStore class to a CDX
     * bill of materials.
     *
     * @param cycloneDXStore The CycloneDXStore instance with the bill of materials data.
     * @param jsonGenerator The JsonGenerator used by Jackson to serialize to a file.
     * @param serializerProvider The SerializerProvider used by Jackson to serialize to a file.
     * @throws IOException If an error writing to the file occurs.
     */
    @Override
    public void serialize(CycloneDXStore cycloneDXStore, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {

        jsonGenerator.writeStartObject(); // {

        //
        // CycloneDXStore information
        //

        jsonGenerator.writeStringField("bomFormat", cycloneDXStore.getBomFormat());
        jsonGenerator.writeStringField("specVersion", cycloneDXStore.getSpecVersion());
        jsonGenerator.writeStringField("serialNumber", cycloneDXStore.getSerialNumber());
        jsonGenerator.writeNumberField("version", cycloneDXStore.getBOMVersion());

        //
        // CycloneDXStore metadata
        //
        jsonGenerator.writeFieldName("metadata");
        jsonGenerator.writeStartObject(); // {

        /* Timestamp */
        jsonGenerator.writeStringField("timestamp", cycloneDXStore.getTimestamp());

        /* Tools */
        List<Tool> tools = cycloneDXStore.getTools();

        if(tools.size() > 0) {
            jsonGenerator.writeFieldName("tools");
            jsonGenerator.writeStartArray(); // [

            for(Tool tool : cycloneDXStore.getTools())
                writeTool(jsonGenerator, tool);

            jsonGenerator.writeEndArray(); // ]
        }

        /* Head Component */
        jsonGenerator.writeFieldName("component");
        writeComponent(jsonGenerator, cycloneDXStore, cycloneDXStore.getHeadComponent());
        jsonGenerator.writeEndObject(); // }

        //
        // Components
        //
        jsonGenerator.writeFieldName("components");
        jsonGenerator.writeStartArray(); // [

        for(ParserComponent c : cycloneDXStore.getComponents()) {
            writeComponent(jsonGenerator, cycloneDXStore, c);
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
     * @param cycloneDXStore The CycloneDXStore that this component belongs to (used to get any component children).
     * @param component The component represented as a ParserComponent.
     * @throws IOException If an error writing to the file occurs.
     */
    private void writeComponent(JsonGenerator jsonGenerator, CycloneDXStore cycloneDXStore, ParserComponent component) throws IOException {
        jsonGenerator.writeStartObject(); // {

        //
        // Package information
        //

        jsonGenerator.writeStringField("name", component.getName());
        writeFieldIfExists(jsonGenerator,"group", component.getGroup());
        writeFieldIfExists(jsonGenerator,"version", component.getVersion());
        if(component.getPublisher() != null && component.getPublisher().length() > 0 && !component.getPublisher().equals("Unknown"))
            jsonGenerator.writeStringField("publisher", "Organization: " + component.getPublisher());

        //
        // Package Hash
        //

        jsonGenerator.writeFieldName("hashes");
        jsonGenerator.writeStartArray();
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("alg", "SHA-256"); // ParserComponent only returns SHA-256 hashes
        jsonGenerator.writeStringField("content", component.generateHash());

        jsonGenerator.writeEndObject();
        jsonGenerator.writeEndArray();

        //
        // Licenses
        //

        writeLicenses(jsonGenerator, component.getResolvedLicenses());

        //
        // External identifiers
        //

        writeFieldIfExists(jsonGenerator, "purl",
                String.join(", ", component.getPurls().stream().map(PURL::toString).toList()));
        writeFieldIfExists(jsonGenerator, "cpe", String.join(", ", component.getCpes()));
        // TODO Do this for SWIDs once we support them

        //
        // Type
        //

        jsonGenerator.writeStringField("type", getCDXType(component.getType()));

        //
        // Properties (files analyzed)
        //

        if(component.getFiles().size() > 0) {
            jsonGenerator.writeFieldName("properties");
            jsonGenerator.writeStartArray();

            for(String file : component.getFiles()) {
                jsonGenerator.writeStartObject();

                // https://cyclonedx.org/docs/1.4/json/#components_items_properties_items_name
                // The value must be a string, but duplicate names with different values are explicitly allowed
                writeFieldIfExists(jsonGenerator, "fileAnalyzed", file);

                jsonGenerator.writeEndObject();
            }

            jsonGenerator.writeEndArray();
        }

        //
        // Nested child components
        //

        // Write children
        List<ParserComponent> children = cycloneDXStore.getChildren(component.getUUID());
        if(children.size() > 0) {
            jsonGenerator.writeFieldName("components");
            jsonGenerator.writeStartArray();

            for(ParserComponent child : children) {
                writeComponent(jsonGenerator, cycloneDXStore, child);
            }

            jsonGenerator.writeEndArray();
        }

        jsonGenerator.writeEndObject(); // }
    }

    /**
     * Private helper method to write the data of a tool used to create the CycloneDXStore as an object using the provided
     * JsonGenerator.
     *
     * @param jsonGenerator The JsonGenerator to use to write the tool to the file.
     * @param tool The tool used to create the CycloneDXStore.
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
    protected static String getCDXType(ParserComponent.Type type) {
        // Set component type based on what the parser reads it in as
        // https://cyclonedx.org/docs/1.4/json/#components_items_type TODO figure out if we can be more descriptive
        switch(type) {
            case LANGUAGE -> { return "framework"; }
            case INTERNAL -> { return "file"; }
            case APPLICATION -> { return "application"; }
            default -> { return "library"; }
        }
    }

    //#endregion
}


