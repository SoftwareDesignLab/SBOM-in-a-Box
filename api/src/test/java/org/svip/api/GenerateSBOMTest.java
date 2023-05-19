package org.svip.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.svip.sbomfactory.generators.utils.Debug;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * File: GenerateSBOMTest.java
 *
 * A suite of test cases to test the SVIP generateSBOM API endpoint.
 *
 * @author Ian Dunn
 */
public class GenerateSBOMTest {
    private final static String TESTCONTENTSARRAY_LENGTH1 = "[\"Example File Contents\"]";
    private final static String TESTFILEARRAY_LENGTH1 = "[\"TestFileName1.java\"]";
    private final static String TESTCONTENTSARRAY_LENGTH2 = "[\"Example File Contents 1\", \"Example File Contents 2\"]";
    private final static String TESTFILEARRAY_LENGTH2 = "[\"TestFileName1.java\", \"TestFileName2.java\"]";
    private final static String CDX_SCHEMA = "CycloneDX";
    private final static String INVALID_SCHEMA = "Invalid Test Schema";
    private final static String JSON_FORMAT = "JSON";
    private final static String INVALID_FORMAT = "GIF";

    private SVIPApiController controller;

    public GenerateSBOMTest() {
        Debug.enableSummary();
        controller = new SVIPApiController();
    }

    @Test
    void emptyContentsArrayTest() {
        ResponseEntity<String> response = controller.generate("", TESTFILEARRAY_LENGTH1, CDX_SCHEMA, JSON_FORMAT);
        logTestRequest("", TESTFILEARRAY_LENGTH1, CDX_SCHEMA, JSON_FORMAT);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void emptyFileNamesArrayTest() {
        ResponseEntity<String> response = controller.generate(TESTCONTENTSARRAY_LENGTH1, "", CDX_SCHEMA, JSON_FORMAT);
        logTestRequest(TESTCONTENTSARRAY_LENGTH1, "", CDX_SCHEMA, JSON_FORMAT);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void mismatchedFileInfoTest() {
        ResponseEntity<String> response = controller.generate(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH1, CDX_SCHEMA, JSON_FORMAT);
        logTestRequest(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH1, CDX_SCHEMA, JSON_FORMAT);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void emptySchemaNameTest() {
        // TODO Should default to CDX
    }

    @Test
    void invalidSchemaNameTest() {
        ResponseEntity<String> response = controller.generate(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH2, INVALID_SCHEMA, JSON_FORMAT);
        logTestRequest(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH2, INVALID_SCHEMA, JSON_FORMAT);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void emptyFormatNameTest() {
        // TODO Should default to JSON
    }

    @Test
    void invalidFormatNameTest() {
        ResponseEntity<String> response = controller.generate(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH2, CDX_SCHEMA, INVALID_FORMAT);
        logTestRequest(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH2, CDX_SCHEMA, INVALID_FORMAT);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void malformedContentsArrayTest() {
        // TODO
    }

    @Test
    void malformedFileArrayTest() {
        // TODO
    }

    // TODO Use translators to translate the result of calling generate on actual files to see if we get a valid SBOM back

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
