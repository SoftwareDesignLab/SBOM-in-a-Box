package org.svip.serializers.serializer;

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
import org.svip.sbom.model.shared.util.ExternalReference;

import java.io.IOException;
import java.util.*;

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

        writeStringField(jsonGenerator, "SPDXID", "SPDXRef-DOCUMENT");
        writeStringField(jsonGenerator, "spdxVersion", "SPDX-" + sbom.getSpecVersion());
        writeStringField(jsonGenerator, "name", sbom.getName());
        writeStringField(jsonGenerator, "documentNamespace", sbom.getUID());
        writeStringField(jsonGenerator, "comment", sbom.getDocumentComment());
        writeStringField(jsonGenerator, "dataLicense", "CC0-1.0");

        jsonGenerator.writeFieldName("documentDescribes");
        jsonGenerator.writeObject(sbom.getComponents().stream().map(Component::getUID).toList());

        writeCreationData(jsonGenerator, sbom.getCreationData(), sbom.getSPDXLicenseListVersion());

        // TODO we don't store extracted licensing info or external document refs

        Set<SVIPComponentObject> files = new HashSet<>();
        Set<SVIPComponentObject> packages = new HashSet<>();

        for (Component component : sbom.getComponents()) {
            SVIPComponentObject c = (SVIPComponentObject) component;
            if (c.getFileNotice() != null && !c.getFileNotice().isEmpty())
                files.add(c);
            else
                packages.add(c);
        }

        jsonGenerator.writeArrayFieldStart("packages");
        for (SVIPComponentObject pkg : packages)
            writePackage(jsonGenerator, pkg);
        jsonGenerator.writeEndArray();

        jsonGenerator.writeArrayFieldStart("files");
        for (SVIPComponentObject file : files)
            writeFile(jsonGenerator, file);
        jsonGenerator.writeEndArray();

        jsonGenerator.writeArrayFieldStart("relationships");
        for (Map.Entry<String, Set<Relationship>> rel : sbom.getRelationships().entrySet()) {
            for (Relationship r : rel.getValue()) {
                writeRelationship(jsonGenerator, rel.getKey(), r);
            }
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();
    }

    private void writeCreationData(JsonGenerator jsonGenerator, CreationData data, String licenseListVersion) throws IOException {
        if (data == null) return;
        jsonGenerator.writeFieldName("creationInfo");
        jsonGenerator.writeStartObject();

        writeStringField(jsonGenerator, "comment", data.getCreatorComment());
        writeStringField(jsonGenerator, "created", data.getCreationTime());
        writeStringField(jsonGenerator, "licenseListVersion", licenseListVersion);

        // Process creators

        jsonGenerator.writeFieldName("creators");

        Set<String> creators = new HashSet<>(data.getAuthors().stream()
                .map(a -> getCreatorString("Person", a.getName(), a.getEmail())).toList());
        creators.addAll(data.getCreationTools().stream()
                .map(t -> getCreatorString("Tool", t.getName(), t.getVersion())).toList());

        if (data.getSupplier() != null) {
            Optional<Contact> supplierContact = data.getSupplier().getContacts().stream().findFirst();
            String supplierEmail = "";
            if (supplierContact.isPresent()) supplierEmail = supplierContact.get().getEmail();
            creators.add(getCreatorString("Organization", data.getSupplier().getName(), supplierEmail));
        }

        jsonGenerator.writeObject(creators);

        jsonGenerator.writeEndObject();
    }

    private String getCreatorString(String type, String primaryId, String secondaryId) {
        if (type.equalsIgnoreCase("tool"))
            return String.format("Tool: %s-%s", primaryId, secondaryId);

        return String.format("%s: %s (%s)", type, primaryId, secondaryId);
    }

    private void writeChecksums(JsonGenerator jsonGenerator, Map<String, String> checksums) throws IOException {
        if (checksums == null) return;
        jsonGenerator.writeFieldName("checksums");
        jsonGenerator.writeStartArray();
        for (Map.Entry<String, String> cs : checksums.entrySet()) {
            jsonGenerator.writeStartObject();
            writeStringField(jsonGenerator, "algorithm", cs.getKey());
            writeStringField(jsonGenerator, "checksumValue", cs.getValue());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }

    private void writePackage(JsonGenerator jsonGenerator, SVIPComponentObject pkg) throws IOException {
        if (pkg == null) return;
        jsonGenerator.writeStartObject();


        // Identifiers
        writeStringField(jsonGenerator, "name", pkg.getName());
        writeStringField(jsonGenerator, "SPDXID", pkg.getUID());
        writeStringField(jsonGenerator, "primaryPackagePurpose", pkg.getType());

        writeStringField(jsonGenerator, "comment", pkg.getComment()); // TODO may need to be an object

        writeStringField(jsonGenerator, "versionInfo", pkg.getVersion());
        writeStringField(jsonGenerator, "packageFileName", pkg.getFileName()); // TODO is this correct?
        if (pkg.getSupplier() != null) {
            Optional<Contact> supplierContact = pkg.getSupplier().getContacts().stream().findFirst();
            String supplierEmail = "";
            if (supplierContact.isPresent())
                supplierEmail = supplierContact.get().getEmail();

            writeStringField(jsonGenerator, "supplier",
                    getCreatorString("Organization", pkg.getSupplier().getName(), supplierEmail));
        }
        writeStringField(jsonGenerator, "originator", pkg.getAuthor());
        writeStringField(jsonGenerator, "downloadLocation", pkg.getDownloadLocation());
        if (pkg.getFilesAnalyzed() != null) jsonGenerator.writeBooleanField("filesAnalyzed", pkg.getFilesAnalyzed());
        writeStringField(jsonGenerator, "homepage", pkg.getHomePage());
        writeStringField(jsonGenerator, "sourceInfo", pkg.getSourceInfo());
        if (pkg.getLicenses() != null) {
            jsonGenerator.writeObjectField("licenseConcluded", pkg.getLicenses().getConcluded());
            jsonGenerator.writeObjectField("licenseDeclared", pkg.getLicenses().getDeclared());
            jsonGenerator.writeObjectField("licenseInfoFromFiles", pkg.getLicenses().getInfoFromFiles());
            writeStringField(jsonGenerator, "licenseComments", pkg.getLicenses().getComment());
        }
        writeStringField(jsonGenerator, "copyright", pkg.getCopyright());
        if (pkg.getDescription() != null) {
            writeStringField(jsonGenerator, "summary", pkg.getDescription().getSummary());
            writeStringField(jsonGenerator, "description", pkg.getDescription().getDescription());
        }
        writeStringField(jsonGenerator, "attributionText", pkg.getAttributionText()); // TODO may need to be a list
        writeStringField(jsonGenerator, "builtDate", pkg.getBuiltDate());
        writeStringField(jsonGenerator, "releaseDate", pkg.getReleaseDate());
        writeStringField(jsonGenerator, "validUntilDate", pkg.getValidUntilDate());
        // TODO may need to be an object
        writeStringField(jsonGenerator, "packageVerificationCode", pkg.getVerificationCode());

        writeChecksums(jsonGenerator, pkg.getHashes());

        jsonGenerator.writeFieldName("externalRefs");
        jsonGenerator.writeStartArray();

        Set<ExternalReference> refs = new HashSet<>();
        if (pkg.getExternalReferences() != null) refs = pkg.getExternalReferences();

        if (pkg.getCPEs() != null)
            refs.addAll(pkg.getCPEs().stream()
                    .map(cpe -> new ExternalReference("SECURITY", "cpe23", cpe))
                    .toList());
        if (pkg.getPURLs() != null)
            refs.addAll(pkg.getPURLs().stream()
                    .map(purl -> new ExternalReference("PACKAGE-MANAGER", "purl", purl))
                    .toList());

        for (ExternalReference ref : refs) {
            jsonGenerator.writeStartObject();
            writeStringField(jsonGenerator, "referenceCategory", ref.getCategory());
            writeStringField(jsonGenerator, "referenceLocator", ref.getUrl());
            writeStringField(jsonGenerator, "referenceType", ref.getType());
            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }

    private void writeFile(JsonGenerator jsonGenerator, SVIPComponentObject file) throws IOException {
        if (file == null) return;
        jsonGenerator.writeStartObject();

        writeStringField(jsonGenerator, "SPDXID", file.getUID());
        writeStringField(jsonGenerator, "fileName", file.getFileName());
        if (file.getType() != null)
            jsonGenerator.writeObjectField("fileTypes", List.of(file.getType()));
        if (file.getAuthor() != null)
            jsonGenerator.writeObjectField("fileContributors", List.of(file.getAuthor()));
        if (file.getLicenses() != null) {
            writeStringField(jsonGenerator, "licenseComments", file.getLicenses().getComment());
            jsonGenerator.writeObjectField("licenseConcluded", file.getLicenses().getConcluded());
            jsonGenerator.writeObjectField("licenseInfoInFiles", file.getLicenses().getInfoFromFiles());
        }
        writeStringField(jsonGenerator, "copyrightText", file.getCopyright());
        writeStringField(jsonGenerator, "comment", file.getComment());
        writeStringField(jsonGenerator, "noticeText", file.getFileNotice());
        writeStringField(jsonGenerator, "attributionText", file.getAttributionText());

        writeChecksums(jsonGenerator, file.getHashes());

        jsonGenerator.writeEndObject();
    }

    private void writeRelationship(JsonGenerator jsonGenerator, String elementId, Relationship rel) throws IOException {
        if (elementId == null || rel == null) return;
        jsonGenerator.writeStartObject();

        writeStringField(jsonGenerator, "spdxElementId", elementId);
        writeStringField(jsonGenerator, "relationshipType", rel.getRelationshipType());
        writeStringField(jsonGenerator, "relatedSpdxElement", rel.getOtherUID());
        writeStringField(jsonGenerator, "comment", rel.getComment());

        jsonGenerator.writeEndObject();
    }

    private void writeStringField(JsonGenerator jsonGenerator, String fieldName, String value) throws IOException {
        if (fieldName == null) return;
        jsonGenerator.writeStringField(fieldName, value != null ? value : "");
    }
}
