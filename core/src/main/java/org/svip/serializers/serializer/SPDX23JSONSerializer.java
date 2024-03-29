/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
 */

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

        jsonGenerator.writeStringField("SPDXID", "SPDXRef-DOCUMENT");
        jsonGenerator.writeStringField("spdxVersion", "SPDX-" + sbom.getSpecVersion());
        jsonGenerator.writeStringField("name", sbom.getName());
        jsonGenerator.writeStringField("documentNamespace", sbom.getUID());
        jsonGenerator.writeStringField("comment", sbom.getDocumentComment());
        jsonGenerator.writeStringField("dataLicense", "CC0-1.0");

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

        jsonGenerator.writeStringField("comment", data.getCreatorComment());
        jsonGenerator.writeStringField("created", data.getCreationTime());
        jsonGenerator.writeStringField("licenseListVersion", licenseListVersion);

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
            jsonGenerator.writeStringField("algorithm", cs.getKey());
            jsonGenerator.writeStringField("checksumValue", cs.getValue());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }

    private void writePackage(JsonGenerator jsonGenerator, SVIPComponentObject pkg) throws IOException {
        if (pkg == null) return;
        jsonGenerator.writeStartObject();


        // Identifiers
        jsonGenerator.writeStringField("name", pkg.getName());
        jsonGenerator.writeStringField("SPDXID", pkg.getUID());
        jsonGenerator.writeStringField("primaryPackagePurpose", pkg.getType());

        jsonGenerator.writeStringField("comment", pkg.getComment()); // TODO may need to be an object

        jsonGenerator.writeStringField("versionInfo", pkg.getVersion());
        jsonGenerator.writeStringField("packageFileName", pkg.getFileName()); // TODO is this correct?
        if (pkg.getSupplier() != null) {
            Optional<Contact> supplierContact = pkg.getSupplier().getContacts().stream().findFirst();
            String supplierEmail = "";
            if (supplierContact.isPresent())
                supplierEmail = supplierContact.get().getEmail();

            jsonGenerator.writeStringField("supplier",
                    getCreatorString("Organization", pkg.getSupplier().getName(), supplierEmail));
        }
        jsonGenerator.writeStringField("originator", pkg.getAuthor());
        jsonGenerator.writeStringField("downloadLocation", pkg.getDownloadLocation());
        if (pkg.getFilesAnalyzed() != null) jsonGenerator.writeBooleanField("filesAnalyzed", pkg.getFilesAnalyzed());
        jsonGenerator.writeStringField("homepage", pkg.getHomePage());
        jsonGenerator.writeStringField("sourceInfo", pkg.getSourceInfo());
        if (pkg.getLicenses() != null) {
            jsonGenerator.writeObjectField("licenseConcluded", pkg.getLicenses().getConcluded());
            jsonGenerator.writeObjectField("licenseDeclared", pkg.getLicenses().getDeclared());
            jsonGenerator.writeObjectField("licenseInfoFromFiles", pkg.getLicenses().getInfoFromFiles());
            jsonGenerator.writeStringField("licenseComments", pkg.getLicenses().getComment());
        }
        jsonGenerator.writeStringField("copyright", pkg.getCopyright());
        if (pkg.getDescription() != null) {
            jsonGenerator.writeStringField("summary", pkg.getDescription().getSummary());
            jsonGenerator.writeStringField("description", pkg.getDescription().getDescription());
        }
        jsonGenerator.writeStringField("attributionText", pkg.getAttributionText()); // TODO may need to be a list
        jsonGenerator.writeStringField("builtDate", pkg.getBuiltDate());
        jsonGenerator.writeStringField("releaseDate", pkg.getReleaseDate());
        jsonGenerator.writeStringField("validUntilDate", pkg.getValidUntilDate());
        // TODO may need to be an object
        jsonGenerator.writeStringField("packageVerificationCode", pkg.getVerificationCode());

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
            jsonGenerator.writeStringField("referenceCategory", ref.getCategory());
            jsonGenerator.writeStringField("referenceLocator", ref.getUrl());
            jsonGenerator.writeStringField("referenceType", ref.getType());
            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }

    private void writeFile(JsonGenerator jsonGenerator, SVIPComponentObject file) throws IOException {
        if (file == null) return;
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("SPDXID", file.getUID());
        jsonGenerator.writeStringField("fileName", file.getFileName());
        if (file.getType() != null)
            jsonGenerator.writeObjectField("fileTypes", List.of(file.getType()));
        if (file.getAuthor() != null)
            jsonGenerator.writeObjectField("fileContributors", List.of(file.getAuthor()));
        if (file.getLicenses() != null) {
            jsonGenerator.writeStringField("licenseComments", file.getLicenses().getComment());
            jsonGenerator.writeObjectField("licenseConcluded", file.getLicenses().getConcluded());
            jsonGenerator.writeObjectField("licenseInfoInFiles", file.getLicenses().getInfoFromFiles());
        }
        jsonGenerator.writeStringField("copyrightText", file.getCopyright());
        jsonGenerator.writeStringField("comment", file.getComment());
        jsonGenerator.writeStringField("noticeText", file.getFileNotice());
        jsonGenerator.writeStringField("attributionText", file.getAttributionText());

        writeChecksums(jsonGenerator, file.getHashes());

        jsonGenerator.writeEndObject();
    }

    private void writeRelationship(JsonGenerator jsonGenerator, String elementId, Relationship rel) throws IOException {
        if (elementId == null || rel == null) return;
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("spdxElementId", elementId);
        jsonGenerator.writeStringField("relationshipType", rel.getRelationshipType());
        jsonGenerator.writeStringField("relatedSpdxElement", rel.getOtherUID());
        jsonGenerator.writeStringField("comment", rel.getComment());

        jsonGenerator.writeEndObject();
    }
}
