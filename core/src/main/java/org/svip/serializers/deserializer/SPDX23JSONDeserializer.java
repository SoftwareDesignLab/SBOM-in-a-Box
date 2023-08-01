package org.svip.serializers.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23FileBuilder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23PackageBuilder;
import org.svip.sbom.factory.objects.SPDX23.SPDX23FileBuilderFactory;
import org.svip.sbom.factory.objects.SPDX23.SPDX23PackageBuilderFactory;
import org.svip.sbom.factory.objects.SPDX23.SPDX23SBOMBuilderFactory;
import org.svip.sbom.model.objects.SPDX23.SPDX23FileObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * File: SPDX23JSONDeserializer.java
 * This class implements the Deserializer interface and the Jackson StdDeserializer to provide all functionality to
 * read an SPDX 2.3 SBOM object from an SPDX 2.3 JSON file string.
 *
 * @author Ian Dunn
 * @author Thomas Roman
 */
public class SPDX23JSONDeserializer extends StdDeserializer<SPDX23SBOM> implements Deserializer {
    public SPDX23JSONDeserializer() {
        super(SPDX23SBOM.class);
    }

    protected SPDX23JSONDeserializer(Class<SPDX23SBOM> t) {
        super(t);
    }

    /**
     * Deserializes an SPDX 2.3 JSON SBOM from a string.
     *
     * @param fileContents The file contents of the SPDX 2.3 JSON SBOM to deserialize.
     * @return The deserialized SPDX 2.3 SBOM object.
     */
    @Override
    public SPDX23SBOM readFromString(String fileContents) throws JsonProcessingException {
        return getObjectMapper().readValue(fileContents, SPDX23SBOM.class);
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
        module.addDeserializer(SPDX23SBOM.class, this);
        mapper.registerModule(module);

        return mapper;
    }

    @Override
    public SPDX23SBOM deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        // get JSON node
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        // initialize builders
        SPDX23SBOMBuilderFactory sbomFactory = new SPDX23SBOMBuilderFactory();
        SPDX23Builder sbomBuilder = sbomFactory.createBuilder();
        SPDX23PackageBuilderFactory packageFactory = new SPDX23PackageBuilderFactory();
        SPDX23PackageBuilder packageBuilder = packageFactory.createBuilder();
        SPDX23FileBuilderFactory fileFactory = new SPDX23FileBuilderFactory();
        SPDX23FileBuilder fileBuilder = fileFactory.createBuilder();

        // FORMAT
        sbomBuilder.setFormat("SPDX");

        // NAME
        if (node.get("name") != null) sbomBuilder.setName(node.get("name").asText());

        // UID
        if (node.get("documentNamespace") != null) sbomBuilder.setUID(node.get("documentNamespace").asText());

        // SPEC VERSION
        JsonNode spdxVersion = node.get("spdxVersion");
        if (spdxVersion != null)
            sbomBuilder.setSpecVersion(spdxVersion.asText().substring(spdxVersion.asText().indexOf("-") + 1));

        // LICENSE
        if (node.get("dataLicense") != null) sbomBuilder.addLicense(node.get("dataLicense").asText());

        // DOCUMENT COMMENT
        if (node.get("comment") != null) sbomBuilder.setDocumentComment(node.get("comment").asText());

        // CREATION INFO
        JsonNode creationInfo = node.get("creationInfo");
        if (creationInfo != null) {
            // CREATION DATA
            CreationData creationData = new CreationData();
            // TIMESTAMP
            if (creationInfo.get("created") != null) creationData.setCreationTime(creationInfo.get("created").asText());

            // COMMENT
            if (creationInfo.get("comment") != null) creationData.setCreatorComment(creationInfo.get("comment").asText());

            // LICENSE LIST VERSION:
            if (creationInfo.get("licenseListVersion") != null) sbomBuilder.setSPDXLicenseListVersion(creationInfo.get("licenseListVersion").asText());

            // CREATION TOOL
            if (creationInfo.get("creators") != null) {
                List<String> creators = new ArrayList<>();
                for (JsonNode creator : creationInfo.get("creators"))
                    creators.add(creator.asText());

                SPDX23TagValueDeserializer.parseSPDXCreatorInfo(creationData, creators);
            }
            // add the creation data to the builder
            sbomBuilder.setCreationData(creationData);
        }

        // COMPONENTS
        // Packages
        if (node.get("packages") != null)
            for (JsonNode pkg : node.get("packages"))
                sbomBuilder.addSPDX23Component(buildPackage(packageBuilder, pkg));

        // Files
        if (node.get("packages") != null)
            for (JsonNode file : node.get("files"))
                sbomBuilder.addSPDX23Component(buildFile(fileBuilder, file));

        // Relationships:
        if (node.get("relationships") != null)
            for (JsonNode rel : node.get("relationships"))
                if (rel.get("relatedSpdxElement") != null
                 && rel.get("relationshipType") != null
                 && rel.get("spdxElementId") != null) {

                    Relationship relationship = new Relationship(
                            rel.get("relatedSpdxElement").asText(), rel.get("relationshipType").asText());
                    if (rel.get("comment") != null) relationship.setComment(rel.get("comment").asText());
                    sbomBuilder.addRelationship(rel.get("spdxElementId").asText(), relationship);
                }

        // Build the SBOM
        return sbomBuilder.buildSPDX23SBOM();
    }

    private SPDX23PackageObject buildPackage(SPDX23PackageBuilder builder, JsonNode pkg) {
        // TYPE
        if (pkg.get("primaryPackagePurpose") != null) builder.setType(pkg.get("primaryPackagePurpose").asText());

        // UID
        if (pkg.get("SPDXID") != null) builder.setUID(pkg.get("SPDXID").asText());

        // AUTHOR
        if (pkg.get("originator") != null) builder.setAuthor(pkg.get("originator").asText());

        // SUPPLIER
        if (pkg.get("supplier") != null) {
            Contact supplierContact = SPDX23TagValueDeserializer.parseSPDXCreator(pkg.get("supplier").asText());
            Organization supplier = new Organization(supplierContact.getName(), null);
            supplier.addContact(supplierContact);
            builder.setSupplier(supplier);
        }

        // NAME
        if (pkg.get("name") != null) builder.setName(pkg.get("name").asText());

        // VERSION
        if (pkg.get("versionInfo") != null) builder.setVersion(pkg.get("versionInfo").asText());

        // DOWNLOAD LOCATION
        if (pkg.get("downloadLocation") != null) builder.setDownloadLocation(pkg.get("downloadLocation").asText());

        if (pkg.get("comment") != null) builder.setComment(pkg.get("comment").asText());

        if (pkg.get("packageFileName") != null) builder.setFileName(pkg.get("packageFileName").asText());

        if (pkg.get("filesAnalyzed") != null) builder.setFilesAnalyzed(pkg.get("filesAnalyzed").asBoolean());

        if (pkg.get("homepage") != null) builder.setHomePage(pkg.get("homepage").asText());

        if (pkg.get("sourceInfo") != null) builder.setSourceInfo(pkg.get("sourceInfo").asText());

        if (pkg.get("attributionText") != null) builder.setAttributionText(pkg.get("attributionText").asText());

        // DESCRIPTION
        if (pkg.get("summary") != null) {
            Description description = new Description(pkg.get("summary").asText());

            if (pkg.get("description") != null) description.setDescription(pkg.get("description").asText());
            builder.setDescription(description);
        }

        // HASHES
        if (pkg.get("checksums") != null)
            for (JsonNode cs : pkg.get("checksums"))
                builder.addHash(cs.get("algorithm").asText(), cs.get("checksumValue").asText());

        // Licenses
        LicenseCollection componentLicenses = new LicenseCollection();
        if (pkg.get("licenseConcluded") != null)
            for (JsonNode concluded : pkg.get("licenseConcluded"))
                componentLicenses.addConcludedLicenseString(concluded.asText());

        if (pkg.get("licenseDeclared") != null)
            for (JsonNode declared : pkg.get("licenseDeclared"))
                componentLicenses.addDeclaredLicense(declared.asText());

        if (pkg.get("licenseInfoFromFiles") != null)
            for (JsonNode fileInfo : pkg.get("licenseInfoFromFiles"))
                componentLicenses.addLicenseInfoFromFile(fileInfo.asText());

        if (pkg.get("licenseComments") != null) componentLicenses.setComment(pkg.get("licenseComments").asText());

        // add license collection to component builder
        builder.setLicenses(componentLicenses);

        // COPYRIGHT
        if (pkg.get("copyright") != null) builder.setCopyright(pkg.get("copyright").asText());

        // EXTERNAL REFERENCES
        if (pkg.get("externalRefs") != null)
            for (JsonNode ref : pkg.get("externalRefs")) {
                String category = ref.get("referenceCategory").asText();
                String url = ref.get("referenceLocator").asText();
                String type = ref.get("referenceType").asText();
                if (category == null || url == null || type == null) continue;

                if (category.equalsIgnoreCase("security") &&
                        type.equalsIgnoreCase("cpe23type"))
                    builder.addCPE(url);
                else if (category.equalsIgnoreCase("package-manager") &&
                        type.equalsIgnoreCase("purl"))
                    builder.addPURL(url);
                else
                    builder.addExternalReference(new ExternalReference(category, url, type));
            }

        if (pkg.get("builtDate") != null) builder.setBuildDate(pkg.get("builtDate").asText());
        if (pkg.get("releaseDate") != null) builder.setReleaseDate(pkg.get("releaseDate").asText());
        if (pkg.get("validUntilDate") != null) builder.setValidUntilDate(pkg.get("validUntilDate").asText());
        if (pkg.get("packageVerificationCode") != null)
            builder.setVerificationCode(pkg.get("packageVerificationCode").asText());


        // add the component to the sbom builder
        return builder.buildAndFlush();
    }

    private SPDX23FileObject buildFile(SPDX23FileBuilder builder, JsonNode file) {
        // UID
        if (file.get("SPDXID") != null) builder.setUID(file.get("SPDXID").asText());

        // NAME
        if (file.get("fileName") != null) builder.setName(file.get("fileName").asText());

        // TYPE
        if (file.get("fileTypes") != null && file.get("fileTypes").size() > 0)
            // TODO set more filetypes, sbom only supports 1
            builder.setType(file.get("fileTypes").get(0).asText());

        // AUTHOR
        // TODO store more than 1 author
        if (file.get("fileContributors") != null)
            for (JsonNode author : file.get("fileContributors"))
                builder.setAuthor(author.asText());

        // Licenses
        LicenseCollection licenses = new LicenseCollection();
        if (file.get("licenseConcluded") != null)
            for (JsonNode concluded : file.get("licenseConcluded"))
                licenses.addConcludedLicenseString(concluded.asText());

        if (file.get("licenseInfoInFiles") != null)
            for (JsonNode fileInfo : file.get("licenseInfoInFiles"))
                licenses.addLicenseInfoFromFile(fileInfo.asText());

        if (file.get("licenseComments") != null) licenses.setComment(file.get("licenseComments").asText());

        builder.setLicenses(licenses);

        // COPYRIGHT
        if (file.get("copyrightText") != null) builder.setCopyright(file.get("copyrightText").asText());

        // COMMENT
        if (file.get("comment") != null) builder.setComment(file.get("comment").asText());

        // CHECKSUMS
        if (file.get("checksums") != null)
            for (JsonNode cs : file.get("checksums"))
                builder.addHash(cs.get("algorithm").asText(), cs.get("checksumValue").asText());

        // NOTICE TEXT
        // Distinguishes this as a file.
        if (file.get("noticeText") != null) builder.setFileNotice(file.get("noticeText").asText());

        // ATTRIBUTION TEXT
        if (file.get("attributionText") != null) builder.setAttributionText(file.get("attributionText").asText());

        return builder.buildAndFlush();
    }
}
