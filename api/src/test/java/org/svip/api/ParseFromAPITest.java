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
     * Example SBOMs to use for testing
     */
    private final static String alpineSBOM = "src/test/java/org/svip/api/sample_sboms/sbom.alpine-compare.2-3.spdx";
    private final static String pythonSBOM = "src/test/java/org/svip/api/sample_sboms/sbom.python.2-3.spdx";
    private final static String dockerSBOM =  "src/test/java/org/svip/api/sample_sboms/sbom.docker.2-2.spdx";
    private final static List<String> contentsArray = new ArrayList<>();
    private final static List<String> fileNamesArray = new ArrayList<>();

    /**
     * Test that the API can Merge three SBOMs
     * @throws IOException If the SBOM merging is broken
     */
    @Test
    public void parseTest() throws IOException{

        contentsArray.add(new String(Files.readAllBytes(Paths.get(alpineSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(pythonSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(dockerSBOM))));

        fileNamesArray.add(alpineSBOM);
        fileNamesArray.add(pythonSBOM);
        fileNamesArray.add(dockerSBOM);

        int i = 0;
        for (String c: contentsArray
             ) {
            SBOM res = ctrl.parse(c, fileNamesArray.get(i)).getBody();
            assertNotNull(res);
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
