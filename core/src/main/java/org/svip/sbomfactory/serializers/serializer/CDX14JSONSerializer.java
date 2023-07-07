package org.svip.sbomfactory.serializers.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbomfactory.serializers.Metadata;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * File: CDX14JSONSerializer.java
 * This class implements the Serializer interface and the Jackson StdSerializer to provide all functionality to write an
 * SBOM object to a CDX 1.4 JSON file string.
 *
 * @author Ian Dunn
 */
public class CDX14JSONSerializer extends StdSerializer<SVIPSBOM> implements Serializer {

    private boolean prettyPrint = false;

    public CDX14JSONSerializer() {
        super(SVIPSBOM.class);
    }

    protected CDX14JSONSerializer(Class<SVIPSBOM> t) {
        super(t);
    }

    /**
     * Serializes an SBOM to a CDX 1.4 JSON file.
     *
     * @param sbom The SBOM to serialize.
     *
     * @return A string containing the final SBOM file.
     */
    @Override
    public String writeToString(SVIPSBOM sbom) throws JsonProcessingException {
        if (prettyPrint)
            return getObjectMapper().writer().with(SerializationFeature.INDENT_OUTPUT).writeValueAsString(sbom);
        else return getObjectMapper().writer().writeValueAsString(sbom);
    }

    /**
     * Gets the ObjectMapper of the serializer to expose configuration.
     *
     * @return A reference to the ObjectMapper of the serializer.
     */
    @Override
    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(SVIPSBOM.class, this);
        mapper.registerModule(module);

        return mapper;
    }

    /**
     * Sets the ObjectMapper of the serializer to enable or disable pretty printing.
     *
     * @param prettyPrint True to pretty-print, false otherwise.
     */
    @Override
    public void setPrettyPrinting(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    @Override
    public void serialize(SVIPSBOM sbom, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeStartObject();

        //
        // Top-level info
        //

        jsonGenerator.writeStringField("bomFormat", sbom.getFormat());
        jsonGenerator.writeStringField("specVersion", sbom.getSpecVersion());
        jsonGenerator.writeStringField("version", sbom.getVersion());
        jsonGenerator.writeStringField("serialNumber", sbom.getUID());

        //
        // Metadata
        //
        writeCreationData(jsonGenerator, sbom.getCreationData());

        //
        // Components
        //
        jsonGenerator.writeFieldName("components");
        jsonGenerator.writeStartArray(); // [

//        for (ParserComponent c : cycloneDXStore.getComponents()) {
//            writeComponent(jsonGenerator, cycloneDXStore, c);
//        }

        jsonGenerator.writeEndArray(); // ]

        // Services

        // External References

        // Dependencies

        // Compositions

        // Vulnerabilities

        // Signature

        jsonGenerator.writeEndObject(); // }
    }

    //#endregion

    //#region Helper Methods

    private void writeCreationData(JsonGenerator jsonGenerator, CreationData data) throws IOException {
        jsonGenerator.writeFieldName("metadata");
        jsonGenerator.writeStartObject();

        /* Timestamp */

        jsonGenerator.writeStringField("timestamp", data.getCreationTime());

        /* Tools */

        Set<CreationTool> tools = data.getCreationTools();
        if (!tools.stream().map(CreationTool::getVendor).collect(Collectors.toSet()).contains("SVIP")) {
            tools.add(Metadata.getCreationTool());
        }

        jsonGenerator.writeFieldName("tools");
        jsonGenerator.writeStartArray(); // [

        for (CreationTool tool : tools) {
            writeTool(jsonGenerator, tool);
        }

        jsonGenerator.writeEndArray(); // ]

        /* Authors */

        if (data.getAuthors().size() > 0) {
            jsonGenerator.writeFieldName("authors");
            jsonGenerator.writeStartArray(); // [

            for (Contact author : data.getAuthors()) {
                writeContact(jsonGenerator, author);
            }

            jsonGenerator.writeEndArray(); // ]
        }

        /* Manufacture */

        if (data.getManufacture() != null) {
            jsonGenerator.writeFieldName("manufacture");
            writeOrganization(jsonGenerator, data.getManufacture());
        }

        /* Supplier */

        if (data.getSupplier() != null) {
            jsonGenerator.writeFieldName("manufacture");
            writeOrganization(jsonGenerator, data.getSupplier());
        }

        /* Comment / Properties */
        if (data.getProperties() != null) {
            if (data.getProperties().get("creatorComment") != null &&
                    !data.getProperties().get("creatorComment").contains(Metadata.SERIALIZED_COMMENT)) {
                data.addProperty("creatorComment", Metadata.SERIALIZED_COMMENT);
            }

            if (data.getCreatorComment() != null && !data.getCreatorComment().isEmpty()) {
                data.addProperty("creatorComment", data.getCreatorComment());
            }

            writeProperties(jsonGenerator, data.getProperties());
        }

        /* Licenses */
        Set<String> licenses = data.getLicenses();
        if (licenses != null) {
            jsonGenerator.writeFieldName("licenses");
            jsonGenerator.writeStartArray();

            for (String license : licenses) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("name", license);
                jsonGenerator.writeEndObject();
            }

            jsonGenerator.writeEndArray();
        }

        /* Root Component */
//        jsonGenerator.writeFieldName("component");
//        writeComponent(jsonGenerator, cycloneDXStore, cycloneDXStore.getHeadComponent());
        jsonGenerator.writeEndObject(); // }
    }

    private void writeTool(JsonGenerator jsonGenerator, CreationTool tool) throws IOException {
        jsonGenerator.writeStartObject(); // {

        jsonGenerator.writeStringField("vendor", tool.getVendor());
        jsonGenerator.writeStringField("name", tool.getName());
        jsonGenerator.writeStringField("version", tool.getVersion());

        jsonGenerator.writeFieldName("hashes");
        jsonGenerator.writeStartArray(); // [

        // Loop through hashes and print the algorithm and content
        for (Map.Entry<String, String> hash : tool.getHashes().entrySet()) {
            jsonGenerator.writeStartObject(); // {

            jsonGenerator.writeStringField("alg", hash.getKey());
            jsonGenerator.writeStringField("content", hash.getValue());

            jsonGenerator.writeEndObject(); // }
        }

        jsonGenerator.writeEndArray(); // ]

        // TODO external references (we don't store this on a per tool basis)

        jsonGenerator.writeEndObject(); // }
    }

    private void writeContact(JsonGenerator jsonGenerator, Contact contact) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("name", contact.getName());
        jsonGenerator.writeStringField("email", contact.getEmail());
        jsonGenerator.writeStringField("phone", contact.getPhone());

        jsonGenerator.writeEndObject();
    }

    private void writeOrganization(JsonGenerator jsonGenerator, Organization organization) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("name", organization.getName());
        jsonGenerator.writeStringField("url", organization.getUrl());

        jsonGenerator.writeFieldName("contact");
        jsonGenerator.writeStartArray();

        for (Contact contact : organization.getContacts()) {
            writeContact(jsonGenerator, contact);
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }

    private void writeProperties(JsonGenerator jsonGenerator, Map<String, Set<String>> properties) throws IOException {
        jsonGenerator.writeFieldName("properties");
        jsonGenerator.writeStartArray();

        for(Map.Entry<String, Set<String>> prop : properties.entrySet()) {
            // Need a nested loop since we can't have multiple values under a single property
            // Instead, CDX supports having multiple properties with the same name
            for(String value : prop.getValue()) {
                jsonGenerator.writeStartObject();

                jsonGenerator.writeStringField("name", prop.getKey());
                jsonGenerator.writeStringField("value", value);

                jsonGenerator.writeEndObject();
            }
        }

        jsonGenerator.writeEndArray();
    }

//    private void writeComponent(JsonGenerator jsonGenerator, SVIPComponentObject component) throws IOException {
//        jsonGenerator.writeStartObject();
//
//        jsonGenerator.writeStringField("type", );
//        jsonGenerator.writeStringField("mime-type", );
//        jsonGenerator.writeStringField("bom-ref", );
//        jsonGenerator.writeStringField("supplier", );
//        jsonGenerator.writeStringField("author", );
//        jsonGenerator.writeStringField("publisher", );
//        jsonGenerator.writeStringField("group", );
//        jsonGenerator.writeStringField("name", );
//        jsonGenerator.writeStringField("version", );
//        jsonGenerator.writeStringField("description", );
//        jsonGenerator.writeStringField("scope", );
//        jsonGenerator.writeStringField("hashes", );
//        jsonGenerator.writeStringField("copyright", );
//        jsonGenerator.writeStringField("cpe", );
//        jsonGenerator.writeStringField("purl", );
//        jsonGenerator.writeStringField("swid", );
//        jsonGenerator.writeStringField("pedigree", );
//        jsonGenerator.writeStringField("externalReferences", );
//        jsonGenerator.writeStringField("components", );
//        jsonGenerator.writeStringField("evidence", );
//        jsonGenerator.writeStringField("releaseNotes", );
//        jsonGenerator.writeStringField("properties", );
//        jsonGenerator.writeStringField("signature", );
//
//        jsonGenerator.writeEndObject();
//    }

//    private void writeLicenses(JsonGenerator jsonGenerator, Set<License> licenses) throws IOException {
//        if (licenses.size() == 0) return; // If no licenses, do not write anything
//
//        jsonGenerator.writeFieldName("licenses");
//        jsonGenerator.writeStartArray(); // [
//
//        for (License license : licenses) {
//            jsonGenerator.writeStartObject();
//            jsonGenerator.writeFieldName("license");
//            jsonGenerator.writeStartObject(); // {
//
//            if (license.getSpdxLicense() != null) {
//                jsonGenerator.writeStringField("id", license.getSpdxLicense());
//            } else {
//                jsonGenerator.writeStringField("name", license.getLicenseName());
//            }
//            writeFieldIfExists(jsonGenerator, "url", license.getUrl());
//
//            jsonGenerator.writeEndObject(); // }
//            jsonGenerator.writeEndObject();
//        }
//
//        jsonGenerator.writeEndArray(); // ]
//    }

//    private void writeComponent(JsonGenerator jsonGenerator, CycloneDXStore cycloneDXStore, ParserComponent component) throws IOException {
//        jsonGenerator.writeStartObject(); // {
//
//        //
//        // Package information
//        //
//
//        jsonGenerator.writeStringField("name", component.getName());
//        writeFieldIfExists(jsonGenerator, "group", component.getGroup());
//        writeFieldIfExists(jsonGenerator, "version", component.getVersion());
//        if (component.getPublisher() != null && component.getPublisher().length() > 0 && !component.getPublisher().equals("Unknown"))
//            jsonGenerator.writeStringField("publisher", "Organization: " + component.getPublisher());
//
//        //
//        // Package Hash
//        //
//
//        jsonGenerator.writeFieldName("hashes");
//        jsonGenerator.writeStartArray();
//        jsonGenerator.writeStartObject();
//
//        jsonGenerator.writeStringField("alg", "SHA-256"); // ParserComponent only returns SHA-256 hashes
//        jsonGenerator.writeStringField("content", component.generateHash());
//
//        jsonGenerator.writeEndObject();
//        jsonGenerator.writeEndArray();
//
//        //
//        // Licenses
//        //
//
//        writeLicenses(jsonGenerator, component.getResolvedLicenses());
//
//        //
//        // External identifiers
//        //
//
//        writeFieldIfExists(jsonGenerator, "purl", String.join(", ", component.getPurls()));
//        writeFieldIfExists(jsonGenerator, "cpe", String.join(", ", component.getCpes()));
//        // TODO Do this for SWIDs once we support them
//
//        //
//        // Type
//        //
//
//        jsonGenerator.writeStringField("type", getCDXType(component.getType()));
//
//        //
//        // Properties (files analyzed)
//        //
//
//        if (component.getFiles().size() > 0) {
//            jsonGenerator.writeFieldName("properties");
//            jsonGenerator.writeStartArray();
//
//            for (String file : component.getFiles()) {
//                jsonGenerator.writeStartObject();
//
//                // https://cyclonedx.org/docs/1.4/json/#components_items_properties_items_name
//                // The value must be a string, but duplicate names with different values are explicitly allowed
//                writeFieldIfExists(jsonGenerator, "fileAnalyzed", file);
//
//                jsonGenerator.writeEndObject();
//            }
//
//            jsonGenerator.writeEndArray();
//        }
//
//        //
//        // Nested child components
//        //
//
//        // Write children
//        List<ParserComponent> children = cycloneDXStore.getChildren(component.getUUID());
//        if (children.size() > 0) {
//            jsonGenerator.writeFieldName("components");
//            jsonGenerator.writeStartArray();
//
//            for (ParserComponent child : children) {
//                writeComponent(jsonGenerator, cycloneDXStore, child);
//            }
//
//            jsonGenerator.writeEndArray();
//        }
//
//        jsonGenerator.writeEndObject(); // }
//    }
}
