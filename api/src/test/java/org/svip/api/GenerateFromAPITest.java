package org.svip.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.sbomfactory.generators.generators.SBOMGenerator;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;
import org.svip.sbomfactory.translators.TranslatorController;

import java.io.IOException;

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
    @NullAndEmptySource
    void emptyContentsArrayTest(String fileContents) {
        ResponseEntity<String> response = ctrl.generate(fileContents, TESTFILEARRAY_LENGTH1, CDX_SCHEMA, JSON_FORMAT);
        logTestRequest(fileContents, TESTFILEARRAY_LENGTH1, CDX_SCHEMA, JSON_FORMAT);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void emptyFileNamesArrayTest(String fileNames) {
        ResponseEntity<String> response = ctrl.generate(TESTCONTENTSARRAY_LENGTH1, fileNames, CDX_SCHEMA, JSON_FORMAT);
        logTestRequest(TESTCONTENTSARRAY_LENGTH1, fileNames, CDX_SCHEMA, JSON_FORMAT);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void mismatchedFileInfoTest() {
        ResponseEntity<String> response = ctrl.generate(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH1, CDX_SCHEMA, JSON_FORMAT);
        logTestRequest(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH1, CDX_SCHEMA, JSON_FORMAT);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void invalidSchemaNameTest() {
        ResponseEntity<String> response = ctrl.generate(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH2, INVALID_SCHEMA, JSON_FORMAT);
        logTestRequest(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH2, INVALID_SCHEMA, JSON_FORMAT);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); TODO test nulls, empty string, default to CDX
    }

    @Test
    void invalidFormatNameTest() {
        ResponseEntity<String> response = ctrl.generate(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH2, CDX_SCHEMA, INVALID_FORMAT);
        logTestRequest(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH2, CDX_SCHEMA, INVALID_FORMAT);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); TODO test nulls, empty string, test default to schema default, check if valid format/can be used in schema
    }

    @Test
    void malformedContentsArrayTest() {
        // TODO
    }

    @Test
    void malformedFileArrayTest() {
        // TODO
    }

    /**
     * Test that the API can Merge three SBOMs
     * @throws IOException If the SBOM merging is broken
     */
    @Test
    public void generateTest() throws IOException {

        String[] input = APITestInputInitializer.testInput();

        String contentsString = input[0];
        String fileNamesString = input[1];

        for(GeneratorSchema schema : GeneratorSchema.values()) {
            // Test all possible formats
            for(GeneratorSchema.GeneratorFormat format : GeneratorSchema.GeneratorFormat.values()) {

                if(schema == GeneratorSchema.SPDX)
                    switch (format) {
                        case XML, JSON, YAML -> { // todo we don't support SPDX with these formats yet
                            continue;
                        }
                    }


                if(schema.supportsFormat(format)) {
                    // Test logic per merge
                    Debug.log(Debug.LOG_TYPE.SUMMARY, "generating " + schema + " " + format);
                    ResponseEntity<String> report = ctrl.generate(contentsString, fileNamesString, schema.toString().toUpperCase(), format.toString().toUpperCase());
                    assertNotNull(report.getBody());
                    Debug.log(Debug.LOG_TYPE.SUMMARY, "Generated SBOM:\n" + report.getBody());
                    Debug.log(Debug.LOG_TYPE.SUMMARY, "PASSED " + schema + " " + format + "!\n-----------------\n");

                    // TODO assert translators can parse this back in

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
}
