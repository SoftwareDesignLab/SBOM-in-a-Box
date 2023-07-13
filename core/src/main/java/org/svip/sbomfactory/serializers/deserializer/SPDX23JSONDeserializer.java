package org.svip.sbomfactory.serializers.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.svip.builderfactory.SPDX23SBOMBuilderFactory;
import org.svip.builders.component.SPDX23FileBuilder;
import org.svip.builders.component.SPDX23PackageBuilder;
import org.svip.componentfactory.SPDX23FileBuilderFactory;
import org.svip.componentfactory.SPDX23PackageBuilderFactory;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

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
        if (node.get("files") != null) {
            for (int i = 0; i < node.get("files").size(); i++) {
                // TYPE
                if (node.get("files").get(i).get("fileTypes") != null && node.get("files").get(i).get("fileTypes").size() > 0) {
                    // TODO get more filetypes
                    fileBuilder.setType(node.get("files").get(i).get("fileTypes").get(0).asText());
                }
                // UID
                if (node.get("files").get(i).get("SPDXID") != null) {
                    fileBuilder.setUID(node.get("files").get(i).get("SPDXID").asText());
                }
                // AUTHOR
                if (node.get("files").get(i).get("fileContributors") != null) {
                    fileBuilder.setAuthor(node.get("files").get(i).get("fileContributors").asText());
                }
                // NAME
                if (node.get("files").get(i).get("fileName") != null) {
                    fileBuilder.setName(node.get("files").get(i).get("fileName").asText());
                }
                // HASHES
                if (node.get("files").get(i).get("checksums") != null) {
                    for (int j = 0; j < node.get("files").get(i).get("checksums").size(); j++) {
                        fileBuilder.addHash(node.get("files").get(i).get("checksums").get(j).get("algorithm").asText(),
                                node.get("files").get(i).get("checksums").get(j).get("checksumValue").asText());
                    }
                }
                // Licenses
                LicenseCollection componentLicenses = new LicenseCollection();
                if (node.get("files").get(i).get("licenseConcluded") != null) {
                    componentLicenses.addConcludedLicenseString(node.get("files").get(i).get("licenseConcluded").asText());
                }
                if (node.get("files").get(i).get("licenseInfoInFiles") != null) {
                    componentLicenses.addLicenseInfoFromFile(node.get("files").get(i).get("licenseInfoInFiles").asText());
                }
                // add license collection to component builder
                fileBuilder.setLicenses(componentLicenses);
                // COPYRIGHT
                if (node.get("files").get(i).get("copyrightText") != null) {
                    fileBuilder.setCopyright(node.get("files").get(i).get("copyrightText").asText());
                }
                // add the component to the sbom builder
                sbomBuilder.addSPDX23Component(fileBuilder.buildAndFlush());
            }
        }

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

        // NAME
        if (pkg.get("name") != null) builder.setName(pkg.get("name").asText());

        // VERSION
        if (pkg.get("versionInfo") != null) builder.setVersion(pkg.get("versionInfo").asText());

        // DOWNLOAD LOCATION
        if (pkg.get("downloadLocation") != null) builder.setDownloadLocation(pkg.get("downloadLocation").asText());

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

        // add license collection to component builder
        builder.setLicenses(componentLicenses);

        // COPYRIGHT
        if (pkg.get("copyright") != null) builder.setCopyright(pkg.get("copyright").asText());

        // EXTERNAL REFERENCES
        if (pkg.get("externalRefs") != null)
            for (JsonNode ref : pkg.get("externalRefs"))
                if (ref.get("referenceCategory") != null && ref.get("referenceLocator") != null &&
                        ref.get("referenceType") != null) {

                    ExternalReference externalReference = new ExternalReference(
                            ref.get("referenceCategory").asText(),
                            ref.get("referenceLocator").asText(),
                            ref.get("referenceType").asText());

                    // add the external reference to the component builder
                    builder.addExternalReference(externalReference);
                }

        // add the component to the sbom builder
        return builder.buildAndFlush();
    }
}
