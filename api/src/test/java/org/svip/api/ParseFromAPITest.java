package org.svip.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.sbom.model.SBOM;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * File: ParseFromAPITest.java
 * Unit test for API regarding the parsing of SBOMs
 * <p>
 * Tests:<br>
 * - parseTest: Test that the API can merge three SBOMs
 *
 * @author Juan Francisco Patino
 */
public class ParseFromAPITest extends APITest {

    /**
     * Controller to test
     */
    private SVIPApiController ctrl;

    @ParameterizedTest
    @DisplayName("Null/Empty File Contents Array")
    @NullAndEmptySource
    void emptyContentsArrayTest(String fileContents) {
        ResponseEntity<SBOM> response = ctrl.parse(fileContents, TESTFILEARRAY_LENGTH1);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @DisplayName("Null/Empty File Names Array")
    @NullAndEmptySource
    void emptyFileNamesArrayTest(String fileNames) {
        ResponseEntity<SBOM> response = ctrl.parse(TESTCONTENTSARRAY_LENGTH1, fileNames);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Mismatched File Contents/Names Array Length")
    void mismatchedFileInfoTest() {
        // Longer contents array
        ResponseEntity<SBOM> response = ctrl.parse(TESTCONTENTSARRAY_LENGTH2, TESTFILEARRAY_LENGTH1);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Longer file names array
        response = ctrl.parse(TESTCONTENTSARRAY_LENGTH1, TESTFILEARRAY_LENGTH2);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test that the API can parse multiple SBOMs
     * @throws IOException If the SBOM merging is broken
     */
    @Test
    public void parseTest() throws IOException{

        String[] input = APITestInputInitializer.testInput();
        String contentsString = input[0];
        String fileNamesString = input[1];
        String schemaString = input[3];
        String formatString = input[4];
        ObjectMapper objectMapper = new ObjectMapper();

        List<String> contents = objectMapper.readValue(contentsString, new TypeReference<>(){});
        List<String> fNames = objectMapper.readValue(fileNamesString, new TypeReference<>(){});

        List<String> schemas = objectMapper.readValue(schemaString, new TypeReference<>(){});
        List<String> formats = objectMapper.readValue(formatString, new TypeReference<>(){});


        int i = 0;
        for (String c: contents
             ) {
            SBOM res = ctrl.parse(c, fNames.get(i)).getBody();
            assertNotNull(res);

//            Map<GeneratorSchema, GeneratorSchema.GeneratorFormat> configured = Utils.configureSchema(schemas.get(i), formats.get(i)); // todo, this works just waste of computation until we figure out below
//            assert configured != null;
//            GeneratorSchema generatorSchema = (GeneratorSchema) configured.keySet().toArray()[0];
//            GeneratorSchema.GeneratorFormat generatorFormat = configured.get(generatorSchema);


//            Utils.assertSerializationAndTranslation(generatorSchema, generatorFormat, res); // todo, this messes up in translation

            i++;
        }

    }

    /**
     * SETUP: Start API before testing
     */
    @BeforeEach
    public void setup(){

        ctrl = new SVIPApiController();

    }

}
