package org.svip.sbomfactory.serializers.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * File: SPDX23JSONSerializer.java
 * This class implements the Serializer interface and the Jackson StdSerializer to provide all functionality to write an
 * SBOM object to an SPDX 2.3 JSON file string.
 *
 * @author Ian Dunn
 */
public class SPDX23JSONSerializer extends StdSerializer<SVIPSBOM> implements Serializer {

    private boolean prettyPrint = false;

    public SPDX23JSONSerializer() {
        super(SVIPSBOM.class);
    }

    protected SPDX23JSONSerializer(Class<SVIPSBOM> t) {
        super(t);
    }

    /**
     * Serializes an SBOM to an SPDX 2.3 JSON file.
     *
     * @param sbom The SBOM to serialize.
     * @return A string containing the final SBOM file.
     */
    @Override
    public String writeToString(SVIPSBOM sbom) throws JsonProcessingException {
        if (prettyPrint)
            return getObjectMapper().writer().with(SerializationFeature.INDENT_OUTPUT).writeValueAsString(sbom);
        else
            return getObjectMapper().writer().writeValueAsString(sbom);
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

        jsonGenerator.writeStringField("SPDXID", "SPDXRef-DOCUMENT");
        jsonGenerator.writeStringField("spdxVersion", "SPDX-" + sbom.getSpecVersion());
        jsonGenerator.writeStringField("name", sbom.getName());
        jsonGenerator.writeStringField("documentNamespace", sbom.getUID());
        jsonGenerator.writeStringField("comment", sbom.getDocumentComment());
        jsonGenerator.writeStringField("dataLicense", "CC0-1.0"); // TODO where should we get this from

        jsonGenerator.writeFieldName("documentDescribes");
        jsonGenerator.writeObject(sbom.getComponents().stream().map(Component::getUID).toList());

        writeCreationData(jsonGenerator, sbom.getCreationData(), sbom.getSPDXLicenseListVersion());

        jsonGenerator.writeEndObject();
    }

    private void writeCreationData(JsonGenerator jsonGenerator, CreationData data, String licenseListVersion) throws IOException {
        jsonGenerator.writeFieldName("creationInfo");
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("comment", data.getCreatorComment());
        jsonGenerator.writeStringField("created", data.getCreationTime());
        jsonGenerator.writeStringField("licenseListVersion", licenseListVersion);

        // Process creators

        jsonGenerator.writeFieldName("creators");

        Set<String> creators = new HashSet<>(data.getAuthors().stream()
                .map(a -> getCreatorString("Person", a.getName(), a.getEmail())).toList());
        creators.addAll(data.getCreationTools().stream()
                .map(t -> getCreatorString("Tool", t.getName(), t.getVersion())).toList());

        Optional<Contact> supplierContact = data.getSupplier().getContacts().stream().findFirst();
        String supplierEmail = "";
        if (supplierContact.isPresent())
            supplierEmail = supplierContact.get().getEmail();
        creators.add(getCreatorString("Organization", data.getSupplier().getName(), supplierEmail));

        jsonGenerator.writeObject(creators);

        jsonGenerator.writeEndObject();
    }

    private String getCreatorString(String type, String primaryId, String secondaryId) {
        if (type.equalsIgnoreCase("tool"))
            return String.format("Tool: %s-%s", primaryId, secondaryId);

        return String.format("%s: %s (%s)", type, primaryId, secondaryId);
    }
}
