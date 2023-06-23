//package org.svip.api;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.NullAndEmptySource;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.svip.api.utils.Resolver;
//import org.svip.api.utils.Utils;
//import org.svip.sbom.model.SBOM;
//import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;
//import org.svip.sbomfactory.translators.TranslatorException;
//
//import java.io.IOException;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//
///**
// * Unit tests for the parse API endpoint that cover input validation and parsing.
// *
// * @author Juan Francisco Patino
// */
//public class ParseFromAPITest extends APITest {
//
//    /**
//     * Controller to test
//     */
//    private SVIPApiController ctrl;
//
//    public ParseFromAPITest() {
//        ctrl = new SVIPApiController();
//    }
//
//    @ParameterizedTest
//    @DisplayName("Null/Empty File Contents Array")
//    @NullAndEmptySource
//    void emptyContentsArrayTest(String fileContents) throws TranslatorException {
//        ResponseEntity<?> response = ctrl.parse(fileContents, TESTFILEARRAY_LENGTH1);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
//
//    @ParameterizedTest
//    @DisplayName("Null/Empty File Names Array")
//    @NullAndEmptySource
//    void emptyFileNamesArrayTest(String fileNames) throws TranslatorException {
//        ResponseEntity<?> response = ctrl.parse(TESTCONTENTSARRAY_LENGTH1, fileNames);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("Mismatched File Contents/Names Array Length")
//    void mismatchedFileInfoTest() throws TranslatorException {
//        // Longer contents array
//        ResponseEntity<?> response = ctrl.parse(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH1);
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//
//        // Longer file names array
//        response = ctrl.parse(TESTCONTENTSARRAY_LENGTH1, TESTFILEARRAY_LENGTH2);
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//    }
//
//    @Nested
//    @DisplayName("Parse SBOMs")
//    class ParseSBOMTest {
//        private final List<String> contents;
//        private final List<String> fNames;
//
//        private final List<String> schemas;
//        private final List<String> formats;
//
//        /**
//         *
//         * @throws IOException If the test files cannot be read.
//         */
//        public ParseSBOMTest() throws IOException {
//            String[] input = APITest.testInput();
//            String contentsString = input[0];
//            String fileNamesString = input[1];
//            String schemaString = input[3];
//            String formatString = input[4];
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            contents = objectMapper.readValue(contentsString, new TypeReference<>(){});
//            fNames = objectMapper.readValue(fileNamesString, new TypeReference<>(){});
//            schemas = objectMapper.readValue(schemaString, new TypeReference<>(){});
//            formats = objectMapper.readValue(formatString, new TypeReference<>(){});
//        }
//
//        @Test
//        public void parseTest() throws TranslatorException {
//            for(int i = 0; i < contents.size(); i++) {
//                String c = contents.get(i);
//
//                SBOM res = (SBOM) ctrl.parse(c, fNames.get(i)).getBody();
//                assertNotNull(res);
//
//                GeneratorSchema generatorSchema = Resolver.resolveSchema(schemas.get(i), false);
//                GeneratorSchema.GeneratorFormat generatorFormat = Resolver.resolveFormat(formats.get(i), false);
//
//                String sbom = Utils.generateSBOM(res, generatorSchema, generatorFormat);
//                assertNotNull(sbom);
//
//                SBOM backTranslate = Utils.buildSBOMFromString(sbom);
//                assertNotNull(backTranslate);
//            }
//        }
//    }
//}
