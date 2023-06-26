package org.svip.api;

/**
 * Unit tests for the merge API endpoint that cover input validation and merging.
 *
 * @author Juan Francisco Patino
 */
public class MergeFromAPITest extends APITest{

    /**
     * Controller to test
     */
    private final SVIPApiController ctrl;

    public MergeFromAPITest() {
        ctrl = new SVIPApiController();
    }


//    @ParameterizedTest
//    @DisplayName("Null/Empty File Contents Array")
//    @NullAndEmptySource
//    void emptyContentsArrayTest(String fileContents) throws TranslatorException {
//        ResponseEntity<String> response = ctrl.merge(fileContents, TESTFILEARRAY_LENGTH1, CDX_SCHEMA, JSON_FORMAT);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
//
//    @ParameterizedTest
//    @DisplayName("Null/Empty File Names Array")
//    @NullAndEmptySource
//    void emptyFileNamesArrayTest(String fileNames) throws TranslatorException {
//        ResponseEntity<String> response = ctrl.merge(TESTCONTENTSARRAY_LENGTH1, fileNames, CDX_SCHEMA, JSON_FORMAT);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("Mismatched File Contents/Names Array Length")
//    void mismatchedFileInfoTest() throws TranslatorException {
//        // Longer contents array
//        ResponseEntity<String> response = ctrl.merge(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH1, CDX_SCHEMA, JSON_FORMAT);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//
//        // Longer file names array
//        response = ctrl.merge(TESTCONTENTSARRAY_LENGTH1, TESTFILEARRAY_LENGTH2, CDX_SCHEMA, JSON_FORMAT);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
//
//    @ParameterizedTest
//    @DisplayName("Null/Empty/Invalid Schema")
//    @NullAndEmptySource
//    @ValueSource(strings = { INVALID_SCHEMA })
//    void invalidSchemaNameTest(String schemaName) throws TranslatorException {
//        ResponseEntity<String> response = ctrl.merge(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH2, schemaName, JSON_FORMAT);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//    }
//
//    @ParameterizedTest
//    @DisplayName("Null/Empty/Invalid/Unsupported Schema")
//    @NullAndEmptySource
//    @ValueSource(strings = { INVALID_FORMAT, "SPDX" })
//    void invalidFormatNameTest(String formatName) throws TranslatorException {
//        ResponseEntity<String> response = ctrl.merge(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH2, CDX_SCHEMA, formatName);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//    }
//
//    /**
//     * Test that the API can Merge three SBOMs
//     * @throws IOException If the SBOM merging is broken
//     */
//    @Test
//    @DisplayName("Merge SBOMs Test")
//    public void mergeTest() throws IOException, TranslatorException {
//        String[] input = APITest.testInput();
//
//        String contentsString = input[0];
//        String fileNamesString = input[1];
//
//        for(GeneratorSchema schema : GeneratorSchema.values()) {
//            // Test all possible formats
//            for(GeneratorSchema.GeneratorFormat format : GeneratorSchema.GeneratorFormat.values()) {
//
//                // TODO unsupported translator formats for SPDX
//                if(schema == GeneratorSchema.SPDX &&
//                        (format == GeneratorSchema.GeneratorFormat.XML ||
//                                format == GeneratorSchema.GeneratorFormat.JSON ||
//                                format == GeneratorSchema.GeneratorFormat.YAML)) continue;
//
//
//                if(schema.supportsFormat(format)) {
//                    // Test logic per merge
//                    Debug.logBlockTitle(schema + " " + format);
//                    ResponseEntity<String> report = ctrl.merge(contentsString, fileNamesString,
//                            schema.toString().toUpperCase(), format.toString().toUpperCase());
//                    String sbom = report.getBody();
//                    assertNotNull(sbom);
//                    Debug.log(Debug.LOG_TYPE.SUMMARY, "Merged SBOM:\n" + report.getBody());
//
//                    SBOM translated = Utils.buildSBOMFromString(sbom);
//                    assertNotNull(translated);
//                    Debug.log(Debug.LOG_TYPE.SUMMARY, "SBOM back-translated successfully without any errors");
//
//                    GeneratorSchema assumedSchema = GeneratorSchema.valueOfArgument(translated.getOriginFormat().toString());
//                    assertEquals(schema, assumedSchema);
//                    Debug.log(Debug.LOG_TYPE.SUMMARY, "SBOM generated in expected schema: " + assumedSchema);
//
//                    Debug.logBlock();
//                }
//            }
//        }
//    }
}
