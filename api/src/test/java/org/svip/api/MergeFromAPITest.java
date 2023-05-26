package org.svip.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.svip.api.utils.Utils;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * File: MergeFromAPITest.java
 * Unit test for API regarding Merging SBOMs
 * <p>
 * Tests:<br>
 * - mergeTest: Test that the API can merge three SBOMs
 *
 * @author Juan Francisco Patino
 */
public class MergeFromAPITest {

    /**
     * Controller to test
     */
    private SVIPApiController ctrl;


    /**
     * Test that the API can Merge three SBOMs
     * @throws IOException If the SBOM merging is broken
     */
    @Test
    public void mergeTest() throws IOException{

        String[] input = APITestInputInitializer.testInput();

        String contentsString = input[0];
        String fileNamesString = input[1];

        for(GeneratorSchema schema : GeneratorSchema.values()) {
            // Test all possible formats
            for(GeneratorSchema.GeneratorFormat format : GeneratorSchema.GeneratorFormat.values()) {

                // TODO unsupported translator formats for SPDX
                if(schema == GeneratorSchema.SPDX &&
                        (format == GeneratorSchema.GeneratorFormat.XML ||
                                format == GeneratorSchema.GeneratorFormat.JSON ||
                                format == GeneratorSchema.GeneratorFormat.YAML)) continue;


                if(schema.supportsFormat(format)) {
                    // Test logic per merge
                    Debug.logBlockTitle(schema + " " + format);
                    ResponseEntity<String> report = ctrl.merge(contentsString, fileNamesString,
                            schema.toString().toUpperCase(), format.toString().toUpperCase());
                    String sbom = report.getBody();
                    assertNotNull(sbom);

                    SBOM translated = Utils.buildSBOMFromString(sbom);
                    assertNotNull(translated);
                    Debug.log(Debug.LOG_TYPE.SUMMARY, "SBOM back-translated successfully without any errors");

                    GeneratorSchema assumedSchema = GeneratorSchema.valueOfArgument(translated.getOriginFormat().toString());
                    assertEquals(schema, assumedSchema);
                    Debug.log(Debug.LOG_TYPE.SUMMARY, "SBOM generated in expected schema: " + assumedSchema);

                    Debug.log(Debug.LOG_TYPE.SUMMARY, "Merged SBOM:\n" + report.getBody());
                    Debug.logBlock();
                }
            }
        }

    }



    /**
     * SETUP: Start API before testing
     */
    @BeforeEach
    public void setup(){
        Debug.enableSummary();
        ctrl = new SVIPApiController();

    }

}
