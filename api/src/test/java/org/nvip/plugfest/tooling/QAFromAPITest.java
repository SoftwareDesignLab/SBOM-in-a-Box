package org.nvip.plugfest.tooling;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.api.PlugFestApiController;
import org.svip.sbomanalysis.qualityattributes.QualityReport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


/**
 * File: QAFromAPITest.java
 * Unit tests for API regarding QA
 * <p>
 *     Tests:<br>
 *     - qaTest: Test that the API can QA a small SBOM<br>
 *     - qaFastTest: Test that the API can QA a large SBOM
 * </p>
 *
 * @author Juan Francisco Patino
 */
public class QAFromAPITest {

    private final String smallDockerSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/nvip/plugfest/tooling/sample_sboms/sbom.docker.2-2_small.spdx";
    private final String pythonSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/nvip/plugfest/tooling/sample_sboms/sbom.python.2-3.spdx";
    private PlugFestApiController ctrl;

    @Test
    public void qaTest() {
        try {
            String contents = new String(Files.readAllBytes(Paths.get(smallDockerSBOM)));
            ResponseEntity<QualityReport> qa = ctrl.qa(contents, smallDockerSBOM);
            assertEquals(qa.getStatusCode(), HttpStatus.OK);
            assertNotEquals(qa.getBody().getPassedComponents(), 0);
        }
        catch (Exception e) {
            System.out.println(e);
            assertEquals(1, 0);
        }
    }

    @Test
    public void qaFastTest() {
        try {
            String contents = new String(Files.readAllBytes(Paths.get(pythonSBOM)));

            ResponseEntity<QualityReport> qa = ctrl.qa(contents, pythonSBOM); //todo refactor parameters,
            assertEquals(qa.getStatusCode(), HttpStatus.OK);                    // or add signature inn PlugFestApiController
            assertNotEquals(qa.getBody().getPassedComponents(), 0);
        }
        catch (Exception e) {
            System.out.println(e);
            assertEquals(1, 0);
        }
    }

    @BeforeEach
    public void setup(){

        ctrl = new PlugFestApiController();

    }

}
