package org.svip.api;

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
 * Unit tests for the compare API endpoint that cover input validation and comparison.
 *
 * @author Juan Francisco Patino
 */
public class CompareFromAPITest extends APITest {

    /**
     * Controller to test
     */
    private final SVIPApiController ctrl;

    public CompareFromAPITest() {
        ctrl = new SVIPApiController();
    }

    @ParameterizedTest
    @DisplayName("Null/Empty File Contents Array")
    @NullAndEmptySource
    void emptyContentsArrayTest(String fileContents) {
        ResponseEntity<?> response = ctrl.compare(fileContents, TESTFILEARRAY_LENGTH1);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @DisplayName("Null/Empty File Names Array")
    @NullAndEmptySource
    void emptyFileNamesArrayTest(String fileNames) {
        ResponseEntity<?> response = ctrl.compare(TESTCONTENTSARRAY_LENGTH1, fileNames);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Mismatched File Contents/Names Array Length")
    void mismatchedFileInfoTest() {
        // Longer contents array
        ResponseEntity<?> response = ctrl.compare(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH1);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Longer file names array
        response = ctrl.compare(TESTCONTENTSARRAY_LENGTH1, TESTFILEARRAY_LENGTH2);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Compare SBOMs Test")
    public void compareTest() throws IOException {
        String[] input = APITest.testInput();

        String contentsString = input[0];
        String fileNamesString = input[1];
        int inputLength = Integer.parseInt(input[2]);

        ResponseEntity<?> response = ctrl.compare(contentsString, fileNamesString);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Comparison comparison = (Comparison) response.getBody();

        assertEquals(inputLength - 1, Objects.requireNonNull(comparison).getDiffReports().size());
        assertNotEquals(0, comparison.getComparisons().size());
    }
}
