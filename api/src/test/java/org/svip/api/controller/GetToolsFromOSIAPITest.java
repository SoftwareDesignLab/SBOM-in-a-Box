package org.svip.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.utils.Debug;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.svip.generation.osi.OSIClient.dockerCheck;

public class GetToolsFromOSIAPITest extends APITest {
    @Override
    @BeforeEach
    public void setup() {
        // Use OSI.dockerCheck() to check if docker is running
        assumeTrue(dockerCheck() == 0);

        // Init controller with mocked repository and enable OSI
        controller = new SVIPApiController(repository, true);

        // Ensure controller was able to construct OSI
        assumeTrue(controller.isOSIEnabled());
    }

    @Test
    public void getToolsTest() {
        ResponseEntity<?> tools = controller.getOSITools();

        assertEquals(HttpStatus.OK, tools.getStatusCode());

        List<String> toolList = (List<String>) tools.getBody();
        Debug.log(Debug.LOG_TYPE.INFO, toolList);
        assertNotNull(toolList);
        assertEquals(11, toolList.size());
    }
}
