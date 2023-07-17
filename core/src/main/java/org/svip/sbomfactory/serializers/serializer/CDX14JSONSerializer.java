package org.svip.sbomfactory.serializers.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.sbomfactory.serializers.Metadata;

import java.io.IOException;
import java.util.*;
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
        if (sbom.getCreationData() != null)
            writeCreationData(jsonGenerator, sbom.getCreationData(), sbom.getRootComponent());

        //
        // Components
        //
        jsonGenerator.writeFieldName("components");
        jsonGenerator.writeStartArray(); // [

        for (Component component : sbom.getComponents()) {
            if (component == null) continue;
            SVIPComponentObject svipComponent = (SVIPComponentObject) component;
            writeComponent(jsonGenerator, svipComponent);
        }

        jsonGenerator.writeEndArray(); // ]

        // Services

        // External References
        if (sbom.getExternalReferences() != null)
            writeExternalRefs(jsonGenerator, sbom.getExternalReferences());

        // Dependencies
        jsonGenerator.writeFieldName("dependencies");
        jsonGenerator.writeStartArray();

        for (Map.Entry<String, Set<Relationship>> dep : sbom.getRelationships().entrySet()) {
            jsonGenerator.writeStartObject();

            jsonGenerator.writeStringField("ref", dep.getKey());

            jsonGenerator.writeFieldName("dependsOn");
            jsonGenerator.writeStartArray();
            for(Relationship rel : dep.getValue())
                jsonGenerator.writeString(rel.getOtherUID());
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();

        // Compositions

        // Vulnerabilities

        // Signature

        jsonGenerator.writeEndObject(); // }
    }

    //#endregion

    //#region Helper Methods

    private void writeCreationData(JsonGenerator jsonGenerator, CreationData data, SVIPComponentObject rootComponent) throws IOException {
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
            jsonGenerator.writeFieldName("supplier");
            writeOrganization(jsonGenerator, data.getSupplier());
        }

        /* Comment / Properties */
        if (data.getProperties() != null) {
            if (data.getProperties().get("creatorComment") == null ||
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
            writeLicenses(jsonGenerator, data.getLicenses());
        }

        /* Root Component */
        if (rootComponent != null) {
            jsonGenerator.writeFieldName("component");
            writeComponent(jsonGenerator, rootComponent);
        }
        jsonGenerator.writeEndObject(); // }
    }

    private void writeTool(JsonGenerator jsonGenerator, CreationTool tool) throws IOException {
        jsonGenerator.writeStartObject(); // {

        jsonGenerator.writeStringField("vendor", tool.getVendor());
        jsonGenerator.writeStringField("name", tool.getName());
        jsonGenerator.writeStringField("version", tool.getVersion());

        writeHashes(jsonGenerator, tool.getHashes());

        // TODO external references (we don't store this on a per tool basis)

        jsonGenerator.writeEndObject(); // }
    }

    private void writeContact(JsonGenerator jsonGenerator, Contact contact) throws IOException {
        if (contact == null || contact.getName() == null) return;

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("name", contact.getName());

        if (contact.getEmail() != null)
            jsonGenerator.writeStringField("email", contact.getEmail());
        if (contact.getPhone() != null)
            jsonGenerator.writeStringField("phone", contact.getPhone());

        jsonGenerator.writeEndObject();
    }

    private void writeOrganization(JsonGenerator jsonGenerator, Organization organization) throws IOException {
        if (organization == null || organization.getName() == null) return;
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("name", organization.getName());

        if (organization.getUrl() != null) jsonGenerator.writeStringField("url", organization.getUrl());

        jsonGenerator.writeFieldName("contact");
        jsonGenerator.writeStartArray();

        for (Contact contact : organization.getContacts()) {
            writeContact(jsonGenerator, contact);
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }

    private void writeProperties(JsonGenerator jsonGenerator, Map<String, Set<String>> properties) throws IOException {
        if (properties == null) return;

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

    private void writeHashes(JsonGenerator jsonGenerator, Map<String, String> hashes) throws IOException {
        if (hashes == null) return;

        jsonGenerator.writeFieldName("hashes");
        jsonGenerator.writeStartArray(); // [

        // Loop through hashes and print the algorithm and content
        for (Map.Entry<String, String> hash : hashes.entrySet()) {
            jsonGenerator.writeStartObject(); // {

            jsonGenerator.writeStringField("alg", hash.getKey());
            jsonGenerator.writeStringField("content", hash.getValue());

            jsonGenerator.writeEndObject(); // }
        }

        jsonGenerator.writeEndArray(); // ]
    }

    private void writeLicenses(JsonGenerator jsonGenerator, Set<String> licenses) throws IOException {
        jsonGenerator.writeFieldName("licenses");
        jsonGenerator.writeStartArray();

        for (String license : licenses) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("name", license);
            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();
    }

    private void writeExternalRefs(JsonGenerator jsonGenerator, Set<ExternalReference> refs) throws IOException {
        if (refs == null) return;

        jsonGenerator.writeFieldName("externalReferences");
        jsonGenerator.writeStartArray();
        for (ExternalReference ref : refs) {
            jsonGenerator.writeStartObject();

            jsonGenerator.writeStringField("url", ref.getUrl());
            jsonGenerator.writeStringField("comment", "Category: " + ref.getCategory());
            jsonGenerator.writeStringField("type", ref.getType());
            writeHashes(jsonGenerator, ref.getHashes());

            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }

    private void writeComponent(JsonGenerator jsonGenerator, SVIPComponentObject component) throws IOException {
        jsonGenerator.writeStartObject();

        // Identifiers
        jsonGenerator.writeStringField("bom-ref", component.getUID());
        jsonGenerator.writeStringField("name", component.getName());

        jsonGenerator.writeStringField("type", component.getType());
        jsonGenerator.writeStringField("mime-type", component.getMimeType());
        jsonGenerator.writeStringField("group", component.getGroup());
        jsonGenerator.writeStringField("version", component.getVersion());
        jsonGenerator.writeStringField("scope", component.getScope());
        jsonGenerator.writeStringField("copyright", component.getCopyright());
        // TODO is this the right way to represent multiple CPEs/PURLs?
        if (component.getCPEs() != null)
            jsonGenerator.writeStringField("cpe", String.join(", ", component.getCPEs()));
        if (component.getPURLs() != null)
        jsonGenerator.writeStringField("purl", String.join(", ", component.getPURLs()));

        if (component.getSupplier() != null) {
            jsonGenerator.writeFieldName("supplier");
            writeOrganization(jsonGenerator, component.getSupplier());
        }

        jsonGenerator.writeStringField("author", component.getAuthor());
        jsonGenerator.writeStringField("publisher", component.getPublisher());

        if (component.getDescription() != null)
            jsonGenerator.writeStringField("description",
                "Summary: " + component.getDescription().getSummary() + " | Details: " + component.getDescription().getDescription());

        writeHashes(jsonGenerator, component.getHashes());

        // Licenses
        if (component.getLicenses() != null) {
            LicenseCollection licenses = component.getLicenses();
            Set<String> allLicenses = new HashSet<>();
            allLicenses.addAll(licenses.getConcluded());
            allLicenses.addAll(licenses.getDeclared());
            allLicenses.addAll(licenses.getInfoFromFiles());
            writeLicenses(jsonGenerator, allLicenses);
        }

        // External Refs
        writeExternalRefs(jsonGenerator, component.getExternalReferences());

        jsonGenerator.writeStringField("releaseNotes", "Release Date: " + component.getReleaseDate());
        writeProperties(jsonGenerator, component.getProperties());

//        jsonGenerator.writeStringField("swid", String.join(", ", component.getSWID()));
//        jsonGenerator.writeStringField("pedigree", );
//        jsonGenerator.writeStringField("evidence", );
//        jsonGenerator.writeStringField("signature");
//        jsonGenerator.writeStringField("components", );

        jsonGenerator.writeEndObject();
    }
}
