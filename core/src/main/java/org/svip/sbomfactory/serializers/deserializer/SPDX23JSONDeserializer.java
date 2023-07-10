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
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public SBOM readFromString(String fileContents) throws JsonProcessingException {
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

        // NAME
        if (node.get("name") != null) {
            sbomBuilder.setName(node.get("name").asText());
        }
        // UID
        if (node.get("documentNamespace") != null) {
            sbomBuilder.setUID(node.get("documentNamespace").asText());
        }
        // SPEC VERSION
        if (node.get("spdxVersion") != null) {
            sbomBuilder.setSpecVersion(node.get("spdxVersion").asText());
        }
        // LICENSE
        if (node.get("dataLicense") != null) {
            sbomBuilder.addLicense(node.get("dataLicense").asText());
        }

        if (node.get("creationInfo") != null) {
            // CREATION DATA
            CreationData creationData = new CreationData();
            // TIMESTAMP
            if (node.get("creationInfo").get("created") != null) {
                creationData.setCreationTime(node.get("creationInfo").get("created").asText());
            }
            // LICENSE LIST VERSION:
            if (node.get("creationInfo").get("licenseListVersion") != null) {
                sbomBuilder.setSPDXLicenseListVersion(node.get("creationInfo").get("licenseListVersion").asText());
            }
            // CREATION TOOL
            if (node.get("creationInfo").get("creators") != null) {
                for (int i = 0; i < node.get("creationInfo").get("creators").size(); i++) {
                    // TOOL
                    if (node.get("creationInfo").get("creators").get(i).asText().startsWith("Tool")) {
                        CreationTool creationTool = new CreationTool();

                        Matcher toolMatcher = Pattern.compile("(\\S*): (.*)-(.*)")
                                .matcher(node.get("creationInfo").get("creators").get(i).asText());

                        if (!toolMatcher.find()) continue;
                        creationTool.setName(toolMatcher.group(2));
                        creationTool.setVersion(toolMatcher.group(3));
                        creationData.addCreationTool(creationTool);
                    } else { // OTHER
                        Matcher authorMatcher = Pattern.compile("(\\S*): (.*) \\((\\S*)\\)")
                                .matcher(node.get("creationInfo").get("creators").get(i).asText());

                        if (!authorMatcher.find()) continue;
                        creationData.addAuthor(new Contact(authorMatcher.group(2), authorMatcher.group(3), null));
                    }
                }
            }
            // add the creation data to the builder
            sbomBuilder.setCreationData(creationData);
        }

        // COMPONENTS
        // Packages
        if (node.get("packages") != null) {
            for (int i = 0; i < node.get("packages").size(); i++) {
                // TYPE
                if (node.get("packages").get(i).get("primaryPackagePurpose") != null) {
                    packageBuilder.setType(node.get("packages").get(i).get("primaryPackagePurpose").asText());
                }
                // UID
                if (node.get("packages").get(i).get("SPDXID") != null) {
                    packageBuilder.setUID(node.get("packages").get(i).get("SPDXID").asText());
                }
                // AUTHOR
                if (node.get("packages").get(i).get("originator") != null) {
                    packageBuilder.setAuthor(node.get("packages").get(i).get("originator").asText());
                }
                // NAME
                if (node.get("packages").get(i).get("name") != null) {
                    packageBuilder.setName(node.get("packages").get(i).get("name").asText());
                }
                // VERSION
                if (node.get("packages").get(i).get("versionInfo") != null) {
                    packageBuilder.setVersion(node.get("packages").get(i).get("versionInfo").asText());
                }
                // DOWNLOAD LOCATION
                if (node.get("packages").get(i).get("downloadLocation") != null) {
                    packageBuilder.setDownloadLocation(node.get("packages").get(i).get("downloadLocation").asText());
                }
                // DESCRIPTION
                if (node.get("packages").get(i).get("summary") != null) {
                    Description description = new Description(node.get("packages").get(i).get("summary").asText());
                    packageBuilder.setDescription(description);
                }
                // HASHES
                if (node.get("packages").get(i).get("checksums") != null) {
                    for (int j = 0; j < node.get("packages").get(i).get("checksums").size(); j++) {
                        packageBuilder.addHash(node.get("packages").get(i).get("checksums").get(j).get("algorithm").asText(),
                                node.get("packages").get(i).get("checksums").get(j).get("checksumValue").asText());
                    }
                }
                // Licenses
                LicenseCollection componentLicenses = new LicenseCollection();
                if (node.get("packages").get(i).get("licenseConcluded") != null) {
                    componentLicenses.addConcludedLicenseString(node.get("packages").get(i).get("licenseConcluded").asText());
                }
                if (node.get("packages").get(i).get("licenseDeclared") != null) {
                    componentLicenses.addDeclaredLicense(node.get("packages").get(i).get("licenseDeclared").asText());
                }
                if (node.get("packages").get(i).get("licenseInfoFromFiles") != null) {
                    componentLicenses.addLicenseInfoFromFile(node.get("packages").get(i).get("licenseInfoFromFiles").asText());
                }
                // add license collection to component builder
                packageBuilder.setLicenses(componentLicenses);
                // COPYRIGHT
                if (node.get("packages").get(i).get("copyright") != null) {
                    packageBuilder.setCopyright(node.get("packages").get(i).get("copyright").asText());
                }
                // EXTERNAL REFERENCES
                if (node.get("packages").get(i).get("externalRefs") != null) {
                    for (int j = 0; j < node.get("packages").get(i).get("externalRefs").size(); j++) {
                        if (node.get("packages").get(i).get("externalRefs").get(j).get("referenceCategory") != null &&
                                node.get("packages").get(i).get("externalRefs").get(j).get("referenceLocator") != null &&
                                node.get("packages").get(i).get("externalRefs").get(j).get("referenceType") != null) {
                            ExternalReference externalReference = new ExternalReference(
                                    node.get("packages").get(i).get("externalRefs").get(j).get("referenceCategory").asText(),
                                    node.get("packages").get(i).get("externalRefs").get(j).get("referenceLocator").asText(),
                                    node.get("packages").get(i).get("externalRefs").get(j).get("referenceType").asText());
                            // add the external reference to the component builder
                            packageBuilder.addExternalReference(externalReference);
                        }
                    }
                }
                // add the component to the sbom builder
                sbomBuilder.addSPDX23Component(packageBuilder.buildAndFlush());
            }
        }
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
        if (node.get("relationships") != null) {
            for (int i = 0; i < node.get("relationships").size(); i++) {
                if (node.get("relationships").get(i).get("relatedSpdxElement") != null
                 && node.get("relationships").get(i).get("relationshipType") != null
                 && node.get("relationships").get(i).get("spdxElementId") != null) {
                    Relationship relationship = new Relationship(
                            node.get("relationships").get(i).get("relatedSpdxElement").asText(),
                            node.get("relationships").get(i).get("relationshipType").asText());
                    sbomBuilder.addRelationship(node.get("relationships").get(i).get("spdxElementId").asText(), relationship);
                }
            }
        }
        // Build the SBOM
        return sbomBuilder.buildSPDX23SBOM();
    }
}
