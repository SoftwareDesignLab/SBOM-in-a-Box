package org.svip.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.svip.sbom.model.SBOM;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


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
     *  Example SBOMs to use for testing
     */
    private final String alpineSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/sbom.alpine-compare.2-3.spdx";
    private final String pythonSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/sbom.python.2-3.spdx";
    private final String dockerSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/sbom.docker.2-2.spdx";

    /**
     * Controller to test
     */
    private SVIPApiController ctrl;

    /**
     * Test that the API can Merge three SBOMs
     * @throws IOException If the SBOM merging is broken
     */
    @Test
    public void mergeTest() throws IOException {
        List<String> contentsArray = new ArrayList<>();
        List<String> fileNamesArray = new ArrayList<>();

        contentsArray.add(new String(Files.readAllBytes(Paths.get(alpineSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(pythonSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(dockerSBOM))));

        ObjectMapper objectMapper = new ObjectMapper();

        String contentsString = objectMapper.writeValueAsString(contentsArray);

        fileNamesArray.add(alpineSBOM);
        fileNamesArray.add(pythonSBOM);
        fileNamesArray.add(dockerSBOM);

        String fileNamesString = objectMapper.writeValueAsString(fileNamesArray);

        ResponseEntity<SBOM> report = ctrl.merge(contentsString, fileNamesString, "SPDX", "SPDX");

        int x = 0;

    }

    /**
     * SETUP: Start API before testing
     */
    @BeforeEach
    public void setup(){

        ctrl = new SVIPApiController();

    }

}
