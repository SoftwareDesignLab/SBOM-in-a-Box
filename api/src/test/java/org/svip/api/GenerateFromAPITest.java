package org.svip.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.generators.SBOMGenerator;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;
import org.svip.sbomfactory.generators.utils.virtualtree.VirtualNode;
import org.svip.sbomfactory.generators.utils.virtualtree.VirtualPath;
import org.svip.sbomfactory.generators.utils.virtualtree.VirtualTree;
import org.svip.sbomfactory.translators.TranslatorController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * File: GenerateFromAPITest.java
 * Unit test for API regarding the parsing of SBOMs
 * <p>
 * Tests:<br>
 * - generateTest: Test that the API can generate three SBOMs
 *
 * @author Juan Francisco Patino
 * @author Ian Dunn
 */
public class GenerateFromAPITest {

    /**
     * Controller to test
     */
    private SVIPApiController ctrl;

    private final static String TESTCONTENTSARRAY_LENGTH1 = "[\"Example File Contents\"]";
    private final static String TESTFILEARRAY_LENGTH1 = "[\"TestFileName1.java\"]";
    private final static String TESTCONTENTSARRAY_LENGTH2 = "[\"Example File Contents 1\", \"Example File Contents 2\"]";
    private final static String TESTFILEARRAY_LENGTH2 = "[\"src/java/SBOM/sbom2/TestFileName1.java\", \"src/java/SBOM/TestFileName2.java\"]";
    private final static String CDX_SCHEMA = "CycloneDX";
    private final static String INVALID_SCHEMA = "Invalid Test Schema";
    private final static String JSON_FORMAT = "JSON";
    private final static String INVALID_FORMAT = "GIF";
    private final static VirtualPath TEST_PROJECT_PATH = new VirtualPath(System.getProperty("user.dir") +
            "/src/test/java/org/svip/api/sample_projects/Java");

    private final String fileContents;
    private final String fileNames;

    public GenerateFromAPITest() throws JsonProcessingException {
        Debug.enableSummary();
        ctrl = new SVIPApiController();

        // Build fileContents and fileNames for testing
        VirtualTree fileTree = VirtualTree.buildVirtualTree(TEST_PROJECT_PATH);
        List<String> fileContents = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();
        for(VirtualNode file : fileTree.getAllFiles()) {
            fileContents.add(file.getFileContents());
            fileNames.add(file.getPath().toString());
        }

        // Save contents and names as JSON strings
        ObjectMapper mapper = new ObjectMapper();
        this.fileContents = mapper.writeValueAsString(fileContents);
        this.fileNames = mapper.writeValueAsString(fileNames);
    }

    @ParameterizedTest
    @DisplayName("Null/Empty File Contents Array")
    @NullAndEmptySource
    void emptyContentsArrayTest(String fileContents) {
        ResponseEntity<String> response = ctrl.generate(fileContents, TESTFILEARRAY_LENGTH1, CDX_SCHEMA, JSON_FORMAT);
        logTestRequest(fileContents, TESTFILEARRAY_LENGTH1, CDX_SCHEMA, JSON_FORMAT);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @DisplayName("Null/Empty File Names Array")
    @NullAndEmptySource
    void emptyFileNamesArrayTest(String fileNames) {
        ResponseEntity<String> response = ctrl.generate(TESTCONTENTSARRAY_LENGTH1, fileNames, CDX_SCHEMA, JSON_FORMAT);
        logTestRequest(TESTCONTENTSARRAY_LENGTH1, fileNames, CDX_SCHEMA, JSON_FORMAT);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Mismatched File Contents/Names Array Length")
    void mismatchedFileInfoTest() {
        // Longer contents array
        ResponseEntity<String> response = ctrl.generate(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH1, CDX_SCHEMA, JSON_FORMAT);
        logTestRequest(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH1, CDX_SCHEMA, JSON_FORMAT);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Longer file names array
        response = ctrl.generate(TESTCONTENTSARRAY_LENGTH1, TESTFILEARRAY_LENGTH2, CDX_SCHEMA, JSON_FORMAT);
        logTestRequest(TESTCONTENTSARRAY_LENGTH1, TESTFILEARRAY_LENGTH2, CDX_SCHEMA, JSON_FORMAT);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @DisplayName("Null/Empty/Invalid Schema")
    @NullAndEmptySource
    @ValueSource(strings = { INVALID_SCHEMA })
    void invalidSchemaNameTest(String schemaName) {
        ResponseEntity<String> response = ctrl.generate(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH2, schemaName, JSON_FORMAT);
        logTestRequest(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH2, schemaName, JSON_FORMAT);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(GeneratorSchema.CycloneDX, getSchemaFromSBOM(response.getBody()));
    }

    @ParameterizedTest
    @DisplayName("Null/Empty/Invalid/Unsupported Schema")
    @NullAndEmptySource
    @ValueSource(strings = { INVALID_FORMAT, "SPDX" })
    void invalidFormatNameTest(String formatName) {
        ResponseEntity<String> response = ctrl.generate(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH2, CDX_SCHEMA, formatName);
        logTestRequest(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH2, CDX_SCHEMA, formatName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(GeneratorSchema.CycloneDX.getDefaultFormat(), SBOMGenerator.assumeSBOMFormat(response.getBody()));
    }

    /**
     * Test that the API can generate an SBOM from a test project
     *
     * @throws IOException If the SBOM merging is broken
     */
    @Test
    @DisplayName("Generate SBOMs")
    public void generateTest() throws IOException {
        for(GeneratorSchema schema : GeneratorSchema.values()) {
            // Test all possible formats
            for(GeneratorSchema.GeneratorFormat format : GeneratorSchema.GeneratorFormat.values()) {
                if(schema.supportsFormat(format)) {
                    // Test logic per merge
                    Debug.log(Debug.LOG_TYPE.SUMMARY, "Generating " + schema + " " + format);
                    ResponseEntity<String> report =
                            ctrl.generate(this.fileContents, this.fileNames, schema.toString(), format.toString());
                    String sbom = report.getBody();

                    assertEquals(HttpStatus.OK, report.getStatusCode());
                    assertNotNull(sbom);

                    // TODO unsupported translator formats for SPDX
                    if(schema == GeneratorSchema.SPDX &&
                            (format == GeneratorSchema.GeneratorFormat.XML ||
                                    format == GeneratorSchema.GeneratorFormat.JSON ||
                                    format == GeneratorSchema.GeneratorFormat.YAML)) continue;
                    SBOM translated = TranslatorController.toSBOM(report.getBody(), buildTestFilepath(sbom));
                    assertNotNull(translated);

                    Debug.log(Debug.LOG_TYPE.SUMMARY, "PASSED " + schema + " " + format + "!\n-----------------\n");
//                    Debug.log(Debug.LOG_TYPE.SUMMARY, "Generated SBOM:\n" + sbom);
                }
            }
        }
    }

    private void logTestRequest(String fileContents, String fileNames, String schema, String format) {
        String parameters = String.format("""
                {
                    fileContents: "%s",
                    fileNames: "%s",
                    schema: "%s",
                    format: "%s",
                }
                """, fileContents, fileNames, schema, format);
        Debug.log(Debug.LOG_TYPE.SUMMARY, "POST /SVIP/generateSBOM:\n" + parameters);
    }

    private GeneratorSchema getSchemaFromSBOM(String sbom) {
        SBOM translated = TranslatorController.toSBOM(sbom, buildTestFilepath(sbom));
        return GeneratorSchema.valueOfArgument(translated.getOriginFormat().toString());
    }

    private String buildTestFilepath(String sbom) {
        GeneratorSchema.GeneratorFormat format = SBOMGenerator.assumeSBOMFormat(sbom);
        return "/SBOMOut/SBOM." + format.toString().toLowerCase();
    }
}
