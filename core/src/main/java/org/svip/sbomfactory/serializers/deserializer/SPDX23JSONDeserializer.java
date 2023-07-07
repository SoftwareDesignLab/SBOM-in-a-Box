package org.svip.sbomfactory.serializers.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.svip.builderfactory.CDX14SBOMBuilderFactory;
import org.svip.builderfactory.SPDX23SBOMBuilderFactory;
import org.svip.builders.component.CDX14PackageBuilder;
import org.svip.builders.component.SPDX23PackageBuilder;
import org.svip.componentfactory.CDX14PackageBuilderFactory;
import org.svip.componentfactory.SPDX23PackageBuilderFactory;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14Builder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.io.IOException;

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
    public SPDX23SBOM deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException,
            JacksonException {
        // SPDX SBOMS tend to be inconsistant with capitalization,
        // if this becomes a problem we should consider converting the JSON to all lower case

        // get JSON node
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        // initialize builders
        SPDX23SBOMBuilderFactory sbomFactory = new SPDX23SBOMBuilderFactory();
        SPDX23Builder sbomBuilder = sbomFactory.createBuilder();
        SPDX23PackageBuilderFactory componentFactory = new SPDX23PackageBuilderFactory();
        SPDX23PackageBuilder componentBuilder = componentFactory.createBuilder();

        // NAME
        if (node.get("DocumentName") != null) {
            sbomBuilder.setName(node.get("DocumentName").asText());
        }
        // UID
        if (node.get("DocumentNamespace") != null) {
            sbomBuilder.setUID(node.get("DocumentNamespace").asText());
        }
        // SPEC VERSION
        if (node.get("SPDXVersion") != null) {
            sbomBuilder.setSpecVersion(node.get("SPDXVersion").asText());
        }
        // LICENSE
        if (node.get("DataLicense") != null) {
            sbomBuilder.addLicense(node.get("DataLicense").asText());
        }

        if (node.get("CreationInfo") != null) {
            // CREATION DATA
            CreationData creationData = new CreationData();
            // TIMESTAMP
            if (node.get("CreationInfo").get("Created") != null) {
                creationData.setCreationTime(node.get("CreationInfo").get("Created").asText());
            }
            // LICENSE LIST VERSION:
            if (node.get("CreationInfo").get("Created").get("licenseListVersion") != null) {
                sbomBuilder.setSPDXLicenseListVersion(node.get("CreationInfo").get("Created").get("licenseListVersion").asText());
            }
            // CREATION TOOL
            if (node.get("CreationInfo").get("Creator") != null) {
                for (int i = 0; i < node.get("CreationInfo").get("Creator").size(); i++) {
                    CreationTool creationTool = new CreationTool();
                    // TOOL NAME
                    if (node.get("CreationInfo").get("Creator").get(i).get("Tool") != null) {
                        creationTool.setName(node.get("CreationInfo").get("Creator").get(i).get("Tool").asText());
                    }
                    // add the creation tool to the creation data
                    creationData.addCreationTool(creationTool);
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
                if (node.get("packages").get(i).get("PrimaryPackagePurpose") != null) {
                    componentBuilder.setType(node.get("packages").get(i).get("PrimaryPackagePurpose").asText());
                }
                // UID
                if (node.get("packages").get(i).get("SPDXID") != null) {
                    componentBuilder.setUID(node.get("packages").get(i).get("SPDXID").asText());
                }
                // AUTHOR
                if (node.get("packages").get(i).get("PackageOriginator") != null) {
                    componentBuilder.setAuthor(node.get("packages").get(i).get("PackageOriginator").asText());
                }
                // NAME
                if (node.get("packages").get(i).get("PackageName") != null) {
                    componentBuilder.setName(node.get("packages").get(i).get("PackageName").asText());
                }
                // VERSION
                if (node.get("packages").get(i).get("PackageVersion") != null) {
                    componentBuilder.setVersion(node.get("packages").get(i).get("PackageVersion").asText());
                }
                // DOWNLOAD LOCATION
                if (node.get("packages").get(i).get("PackageDownloadLocation") != null) {
                    componentBuilder.setDownloadLocation(node.get("packages").get(i).get("PackageDownloadLocation").asText());
                }
                // DESCRIPTION
                if (node.get("packages").get(i).get("PackageSummary") != null) {
                    Description description = new Description(node.get("packages").get(i).get("PackageSummary").asText());
                    componentBuilder.setDescription(description);
                }
                // HASHES
                if (node.get("packages").get(i).get("PackageChecksum") != null) {
                    for (int j = 0; j < node.get("packages").get(i).get("PackageChecksum").size(); j++) {
                        componentBuilder.addHash(node.get("packages").get(i).get("PackageChecksum").get(j).get("algorithm").asText(),
                                node.get("packages").get(i).get("PackageChecksum").get(j).get("checksumValue").asText());
                    }
                }
                // Licenses
                LicenseCollection componentLicenses = new LicenseCollection();
                if (node.get("packages").get(i).get("PackageLicenseConcluded") != null) {
                    componentLicenses.addConcludedLicenseString(node.get("packages").get(i).get("PackageLicenseConcluded").asText());
                }
                if (node.get("packages").get(i).get("PackageLicenseDeclared") != null) {
                    componentLicenses.addDeclaredLicense(node.get("packages").get(i).get("PackageLicenseDeclared").asText());
                }
                if (node.get("packages").get(i).get("PackageLicenseInfoFromFile") != null) {
                    componentLicenses.addLicenseInfoFromFile(node.get("packages").get(i).get("PackageLicenseInfoFromFile").asText());
                }
                // add license collection to component builder
                componentBuilder.setLicenses(componentLicenses);
                // COPYRIGHT
                if (node.get("packages").get(i).get("PackageCopyrightText") != null) {
                    componentBuilder.setCopyright(node.get("packages").get(i).get("PackageCopyrightText").asText());
                }
                // EXTERNAL REFERENCES
                if (node.get("packages").get(i).get("ExternalRef") != null) {
                    for (int j = 0; j < node.get("packages").get(i).get("ExternalRef").size(); j++) {
                        if (node.get("packages").get(i).get("ExternalRef").get(j).get("referenceCategory") != null &&
                                node.get("packages").get(i).get("ExternalRef").get(j).get("referenceLocator") != null &&
                                node.get("packages").get(i).get("ExternalRef").get(j).get("referenceType") != null) {
                            ExternalReference externalReference = new ExternalReference(
                                    node.get("packages").get(i).get("ExternalRef").get(j).get("referenceCategory").asText(),
                                    node.get("packages").get(i).get("ExternalRef").get(j).get("referenceLocator").asText(),
                                    node.get("packages").get(i).get("ExternalRef").get(j).get("referenceType").asText());
                            // add the external reference to the component builder
                            componentBuilder.addExternalReference(externalReference);
                        }
                    }
                }
                // TO DO: Add relationship data
                // add the component to the sbom builder
                sbomBuilder.addSPDX23Component(componentBuilder.buildAndFlush());
            }
        }
        // Files
        if (node.get("files") != null) {
            for (int i = 0; i < node.get("files").size(); i++) {
                // TYPE
                if (node.get("files").get(i).get("FileType") != null) {
                    componentBuilder.setType(node.get("files").get(i).get("FileType").asText());
                }
                // UID
                if (node.get("files").get(i).get("SPDXID") != null) {
                    componentBuilder.setUID(node.get("files").get(i).get("SPDXID").asText());
                }
                // AUTHOR
                if (node.get("files").get(i).get("FileContributer") != null) {
                    componentBuilder.setAuthor(node.get("files").get(i).get("FileContributer").asText());
                }
                // NAME
                if (node.get("files").get(i).get("FileName") != null) {
                    componentBuilder.setName(node.get("files").get(i).get("FileName").asText());
                }
                // HASHES
                if (node.get("files").get(i).get("FileChecksum") != null) {
                    for (int j = 0; j < node.get("files").get(i).get("FileChecksum").size(); j++) {
                        componentBuilder.addHash(node.get("files").get(i).get("FileChecksum").get(j).get("algorithm").asText(),
                                node.get("files").get(i).get("FileChecksum").get(j).get("checksumValue").asText());
                    }
                }
                // Licenses
                LicenseCollection componentLicenses = new LicenseCollection();
                if (node.get("files").get(i).get("LicenseConcluded") != null) {
                    componentLicenses.addConcludedLicenseString(node.get("files").get(i).get("LicenseConcluded").asText());
                }
                if (node.get("files").get(i).get("licenseInfoInFile") != null) {
                    componentLicenses.addLicenseInfoFromFile(node.get("files").get(i).get("licenseInfoInFile").asText());
                }
                // add license collection to component builder
                componentBuilder.setLicenses(componentLicenses);
                // COPYRIGHT
                if (node.get("files").get(i).get("FileCopyrightText") != null) {
                    componentBuilder.setCopyright(node.get("files").get(i).get("FileCopyrightText").asText());
                }
                // add the component to the sbom builder
                sbomBuilder.addSPDX23Component(componentBuilder.buildAndFlush());
            }
        }
        // Build the SBOM
        return sbomBuilder.buildSPDX23SBOM();
    }
}
