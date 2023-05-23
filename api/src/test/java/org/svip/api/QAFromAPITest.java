package org.svip.api;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.sbomanalysis.qualityattributes.QualityReport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
    private SVIPApiController ctrl;

    /**

    @Test
    public void qaTest() {
        try {
            String contents = new String(Files.readAllBytes(Paths.get(smallDockerSBOM)));

            ResponseEntity<QualityReport> qa = ctrl.qa(contents, smallDockerSBOM);
            assertEquals(qa.getStatusCode(), HttpStatus.OK);
        }
        catch (Exception e) {
            System.out.println(e);
            assertEquals(1, 0);
        }
    }
                                                        // todo wait for metrics restructure
    @Test
    public void qaFastTest() {
        try {
            String contents = new String(Files.readAllBytes(Paths.get(pythonSBOM)));

            ResponseEntity<QualityReport> qa = ctrl.qa(contents, pythonSBOM);
            assertEquals(qa.getStatusCode(), HttpStatus.OK);
        }
        catch (Exception e) {
            System.out.println(e);
            assertEquals(1, 0);
        }
    }

     **/

    @BeforeEach
    public void setup(){
        ctrl = new SVIPApiController();
    }

}
