package org.svip.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.sbomanalysis.comparison.Comparison;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * File: CompareFromAPITest.java
 * Unit test for API regarding Comparisons
 * <p>
 * Tests:<br>
 * - compareTest: Test that the API can compare three SBOMs
 *
 * @author Juan Francisco Patino
 */
public class CompareFromAPITest extends APITest {
    /**
     * Controller to test
     */
    private SVIPApiController ctrl;

    @ParameterizedTest
    @DisplayName("Null/Empty File Contents Array")
    @NullAndEmptySource
    void emptyContentsArrayTest(String fileContents) {
        ResponseEntity<Comparison> response = ctrl.compare(fileContents, TESTFILEARRAY_LENGTH1);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @DisplayName("Null/Empty File Names Array")
    @NullAndEmptySource
    void emptyFileNamesArrayTest(String fileNames) {
        ResponseEntity<Comparison> response = ctrl.compare(TESTCONTENTSARRAY_LENGTH1, fileNames);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Mismatched File Contents/Names Array Length")
    void mismatchedFileInfoTest() {
        // Longer contents array
        ResponseEntity<Comparison> response = ctrl.compare(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH1);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Longer file names array
        response = ctrl.compare(TESTCONTENTSARRAY_LENGTH1, TESTFILEARRAY_LENGTH2);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test that the API can compare multiple SBOMs
     * @throws IOException If the SBOM parsing is broken
     */
    @Test
    public void compareTest() throws IOException {
        String[] input = APITestInputInitializer.testInput();

        String contentsString = input[0];
        String fileNamesString = input[1];
        int inputLength = Integer.parseInt(input[2]);

        ResponseEntity<Comparison> report = ctrl.compare(contentsString, fileNamesString);
        assertEquals(HttpStatus.OK, report.getStatusCode());
        assertEquals(inputLength - 1, Objects.requireNonNull(report.getBody()).getDiffReports().size());
        assertNotEquals(0,report.getBody().getComparisons().size());

    }

    /**
     * SETUP: Start API before testing
     */
    @BeforeEach
    public void setup(){

        ctrl = new SVIPApiController();

    }

}
