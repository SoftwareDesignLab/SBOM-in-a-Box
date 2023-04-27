/**
 * @file SVIPCoreControllerTest.java
 *
 * Contains test class for the SVIPCoreController class
 *
 * @author Juan Francisco Patino
 */

package svip.sbom;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.http.ResponseEntity;
import org.svip.SVIPCoreController;

/**
 * Tests for the SVIPCoreController class
 */
public class SVIPCoreControllerTest {

    private final String currentDir = System.getProperty("user.dir") + "/";

    /**
     * Test that when report is empty, an exception is thrown with message "Failed to Generate SBOMs with OSI"
    */
    @Test
    public void whenEmptyReportThrowException() {

        Exception exception = assertThrows(Exception.class, () ->{
            SVIPCoreController controller = new SVIPCoreController();
            controller.buildOSI();
            controller.generateReport(currentDir + "src/test/java/com/svip/osi/core/sampleProjectEmpty");
            controller.close();
        });
        String expectedMessage = "Failed to Generate SBOMs with OSI";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }

    /**
     * Test that controller returns successful response when generating a Node Graph
     */
    @Test
    public void getNodePathTest(){
        SVIPCoreController ctrl = new SVIPCoreController();
        ctrl.buildOSI();
        ResponseEntity<String> s = ctrl.getNodeGraph(currentDir + "src/test/java/com/svip/osi/core/sampleProject");
        ctrl.close();
        assertEquals(200, s.getStatusCode().value());
    }

}
