package org.svip.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.utils.Utils;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.generators.SBOMGenerator;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;
import org.svip.sbomfactory.generators.utils.virtualtree.VirtualNode;
import org.svip.sbomfactory.generators.utils.virtualtree.VirtualPath;
import org.svip.sbomfactory.generators.utils.virtualtree.VirtualTree;

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

    public GenerateFromAPITest() {
        Debug.enableSummary();
        ctrl = new SVIPApiController();
    }

    @ParameterizedTest
    @DisplayName("Null/Empty File Contents Array")
    @NullAndEmptySource
    void emptyContentsArrayTest(String fileContents) {
        ResponseEntity<String> response = ctrl.generate(fileContents, TESTFILEARRAY_LENGTH1, CDX_SCHEMA, JSON_FORMAT);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @DisplayName("Null/Empty File Names Array")
    @NullAndEmptySource
    void emptyFileNamesArrayTest(String fileNames) {
        ResponseEntity<String> response = ctrl.generate(TESTCONTENTSARRAY_LENGTH1, fileNames, CDX_SCHEMA, JSON_FORMAT);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Mismatched File Contents/Names Array Length")
    void mismatchedFileInfoTest() {
        // Longer contents array
        ResponseEntity<String> response = ctrl.generate(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH1, CDX_SCHEMA, JSON_FORMAT);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Longer file names array
        response = ctrl.generate(TESTCONTENTSARRAY_LENGTH1, TESTFILEARRAY_LENGTH2, CDX_SCHEMA, JSON_FORMAT);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @DisplayName("Null/Empty/Invalid Schema")
    @NullAndEmptySource
    @ValueSource(strings = { INVALID_SCHEMA })
    void invalidSchemaNameTest(String schemaName) {
        ResponseEntity<String> response = ctrl.generate(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH2, schemaName, JSON_FORMAT);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(GeneratorSchema.CycloneDX, Utils.getSchemaFromSBOM(response.getBody()));
    }

    @ParameterizedTest
    @DisplayName("Null/Empty/Invalid/Unsupported Schema")
    @NullAndEmptySource
    @ValueSource(strings = { INVALID_FORMAT, "SPDX" })
    void invalidFormatNameTest(String formatName) {
        ResponseEntity<String> response = ctrl.generate(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH2, CDX_SCHEMA, formatName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(GeneratorSchema.CycloneDX.getDefaultFormat(), SBOMGenerator.assumeSBOMFormat(response.getBody()));
    }

    /**
     * Nested test class to generate SBOMs from the API endpoint and then back-translate them to ensure correctness.
     *
     * This is a nested class due to the file parsing and setup required for the single test method.
     */
    @Nested
    @DisplayName("Generate and Back-Translate SBOMs")
    class GenerateSBOMTest {
        private final static VirtualPath TEST_PROJECT_PATH = new VirtualPath(System.getProperty("user.dir") +
                "/src/test/java/org/svip/api/sample_projects/Java");

        private final String fileContents;
        private final String fileNames;

        public GenerateSBOMTest() throws JsonProcessingException {
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

        /**
         * Test that the API can generate an SBOM from a test project
         */
        @Test
        @DisplayName("Generate SBOMs")
        public void generateTest() {
            for(GeneratorSchema schema : GeneratorSchema.values()) {
                // Test all possible formats
                for(GeneratorSchema.GeneratorFormat format : GeneratorSchema.GeneratorFormat.values()) {
                    if(schema.supportsFormat(format)) {
                        // Test logic per merge
                        Debug.logBlockTitle(schema + " " + format);
                        ResponseEntity<String> report =
                                ctrl.generate(this.fileContents, this.fileNames, schema.toString(), format.toString());
                        String sbom = report.getBody();

                        assertEquals(HttpStatus.OK, report.getStatusCode());
                        assertNotNull(sbom);

                        GeneratorSchema.GeneratorFormat assumedFormat = SBOMGenerator.assumeSBOMFormat(sbom);
                        assertEquals(format, assumedFormat);
                        Debug.log(Debug.LOG_TYPE.SUMMARY, "SBOM generated in expected format: " + assumedFormat);

                        // TODO unsupported translator formats for SPDX
                        if(schema == GeneratorSchema.SPDX &&
                                (format == GeneratorSchema.GeneratorFormat.XML ||
                                        format == GeneratorSchema.GeneratorFormat.JSON ||
                                        format == GeneratorSchema.GeneratorFormat.YAML)) {
                            Debug.log(Debug.LOG_TYPE.WARN, "Unsupported SPDX translator format: " + format + ", skipping " +
                                    "translator portion of test.");
                            Debug.logBlock();
                            continue;
                        }

                        SBOM translated = Utils.buildSBOMFromString(sbom);
                        assertNotNull(translated);
                        Debug.log(Debug.LOG_TYPE.SUMMARY, "SBOM back-translated successfully without any errors");

                        GeneratorSchema assumedSchema = GeneratorSchema.valueOfArgument(translated.getOriginFormat().toString());
                        assertEquals(schema, assumedSchema);
                        Debug.log(Debug.LOG_TYPE.SUMMARY, "SBOM generated in expected schema: " + assumedSchema);

                        Debug.logBlock();
                    }
                }
            }
        }
    }
}
