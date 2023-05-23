package org.svip.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.generators.utils.GeneratorSchema;
import org.svip.sbomfactory.generators.utils.Debug;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;


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
     * Example SBOMs to use for testing
     */
    private final static String alpineSBOM = "src/test/java/org/svip/api/sample_sboms/sbom.python.2-3.spdx";
    private final static String pythonSBOM = "src/test/java/org/svip/api/sample_sboms/sbom.python.2-3.spdx";
    private final static String dockerSBOM =  "src/test/java/org/svip/api/sample_sboms/sbom.docker.2-2.spdx";
    private final static List<String> contentsArray = new ArrayList<>();
    private final static List<String> fileNamesArray = new ArrayList<>();
    private final static ObjectMapper objectMapper = new ObjectMapper();


    /**
     * Test that the API can Merge three SBOMs
     * @throws IOException If the SBOM merging is broken
     */
    @Test
    public void mergeTest() throws IOException{

        contentsArray.add(new String(Files.readAllBytes(Paths.get(alpineSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(pythonSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(dockerSBOM))));
        String contentsString = objectMapper.writeValueAsString(contentsArray);

        fileNamesArray.add(alpineSBOM);
        fileNamesArray.add(pythonSBOM);
        fileNamesArray.add(dockerSBOM);
        String fileNamesString = objectMapper.writeValueAsString(fileNamesArray);

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
                    Debug.log(Debug.LOG_TYPE.SUMMARY, "testing " + schema + " " + format);
                    ResponseEntity<SBOM> report = ctrl.merge(contentsString, fileNamesString, schema.toString().toUpperCase(), format.toString().toUpperCase());
                    assertNotEquals(null, report.getBody());
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
