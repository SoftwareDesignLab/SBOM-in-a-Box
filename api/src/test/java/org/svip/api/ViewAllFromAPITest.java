package org.svip.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.controller.SVIPApiController;
import org.svip.api.model.SBOMFile;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ViewAllFromAPITest extends APITest {
    private SVIPApiController ctrl;

    private final Map<String, String> testFiles;

    public ViewAllFromAPITest() throws IOException {
        ctrl = new SVIPApiController();

        testFiles = testFileMap();
        testFiles.forEach((k, v) -> ctrl.upload(new SBOMFile(k, v)));
    }

    @Test
    @DisplayName("View All Files")
    public void viewAllFilesTest() {
        ResponseEntity<String[]> response = ctrl.viewFiles();

        assertEquals(HttpStatus.OK, response.getStatusCode());

        for (String fileName : response.getBody()) {
            assertTrue(testFiles.keySet().contains(fileName));
        }
    }
}
