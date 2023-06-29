package org.svip.api.controller.old;

/**
 * Unit tests for the compare API endpoint that cover input validation and comparison.
 *
 * @author Juan Francisco Patino
 */
public class CompareFromAPITest extends APITest {

    /**
     * Controller to test
     */
//    private final PlugFestApiController ctrl;
//
//    public CompareFromAPITest() {
//        ctrl = new PlugFestApiController();
//    }
//
//    // TODO ENSURE ALL TESTS WORK
//
//    @ParameterizedTest
//    @DisplayName("Null/Empty File Contents Array")
//    @Disabled
//    @NullAndEmptySource
//    void emptyContentsArrayTest(String fileContents) throws JsonProcessingException, TranslatorException {
//        ResponseEntity<?> response = ctrl.compare(0, Utils.fromJSONString(TESTFILEARRAY_LENGTH1,fileContents));
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
//
//    @ParameterizedTest
//    @DisplayName("Null/Empty File Names Array")
//    @Disabled
//    @NullAndEmptySource
//    void emptyFileNamesArrayTest(String fileNames) throws JsonProcessingException, TranslatorException {
//        ResponseEntity<?> response = ctrl.compare(0, Utils.fromJSONString(fileNames,TESTCONTENTSARRAY_LENGTH1));
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("Mismatched File Contents/Names Array Length")
//    @Disabled
//    void mismatchedFileInfoTest() throws JsonProcessingException, TranslatorException {
//        // Longer contents array
//        ResponseEntity<?> response = ctrl.compare(0, Utils.fromJSONString(TESTFILEARRAY_LENGTH1,TESTCONTENTSARRAY_LENGTH2));
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//
//        // Longer file names array
//        response = ctrl.compare(0, Utils.fromJSONString(TESTCONTENTSARRAY_LENGTH1,TESTFILEARRAY_LENGTH2));
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("Compare SBOMs Test")
//    @Disabled
//    public void compareTest() throws IOException, TranslatorException {
//        String[] input = APITest.testInput();
//
//        String contentsString = input[0];
//        String fileNamesString = input[1];
//        int inputLength = Integer.parseInt(input[2]);
//
//        ResponseEntity<?> response = ctrl.compare(0, Utils.fromJSONString(fileNamesString,contentsString));
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        DiffReport comparison = (DiffReport) response.getBody();
//
//        //  assertEquals(inputLength - 1, response.getBody());
//      //  assertNotEquals(0, comparison.getComparisons().size());
//    }
}