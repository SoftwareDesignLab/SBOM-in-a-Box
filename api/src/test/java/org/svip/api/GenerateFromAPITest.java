package org.svip.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.svip.sbomfactory.generators.generators.utils.GeneratorSchema;
import org.svip.sbomfactory.generators.utils.Debug;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * File: GenerateFromAPITest.java
 * Unit test for API regarding the parsing of SBOMs
 * <p>
 * Tests:<br>
 * - generateTest: Test that the API can generate three SBOMs
 *
 * @author Juan Francisco Patino
 */
public class GenerateFromAPITest {

    /**
     * Controller to test
     */
    private SVIPApiController ctrl;

    /**
     * Test that the API can Merge three SBOMs
     * @throws IOException If the SBOM merging is broken
     */
    @Test
    public void generateTest() throws IOException{

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
                    Debug.log(Debug.LOG_TYPE.SUMMARY, "PASSED " + schema + " " + format + "!\n-----------------\n");
                }
            }
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
