package org.svip.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.api.utils.Utils;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
public class ParseFromAPITest {

    /**
     * Controller to test
     */
    private SVIPApiController ctrl;

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
