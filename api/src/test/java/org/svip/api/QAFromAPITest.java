package org.svip.api;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.svip.sbomanalysis.qualityattributes.QualityReport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;

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
            + "/src/test/java/org/svip/api/sample_sboms/sbom.docker.2-2_small.spdx";
    private final String pythonSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/sbom.python.2-3.spdx";
    private plugFestApiController ctrl;

    @Test
    public void qaTest() {
        try {
            MultipartFile testfile = new MockMultipartFile("sbom.docker.2-2_small.spdx", "sbom.docker.2-2_small.spdx", null, Files.readAllBytes(Paths.get(smallDockerSBOM)));

            ResponseEntity<QualityReport> qa = ctrl.qa(testfile);
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
            MultipartFile testfile = new MockMultipartFile("sbom.python.2-3.spdx", "sbom.python.2-3.spdx", null, Files.readAllBytes(Paths.get(pythonSBOM)));

            ResponseEntity<QualityReport> qa = ctrl.qa(testfile);
            assertEquals(qa.getStatusCode(), HttpStatus.OK);
            assertNotEquals(qa.getBody().getPassedComponents(), 0);
        }
        catch (Exception e) {
            System.out.println(e);
            assertEquals(1, 0);
        }
    }

    @BeforeEach
    public void setup(){

        ctrl = new plugFestApiController();

    }

}
