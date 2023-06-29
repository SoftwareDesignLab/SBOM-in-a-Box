package org.svip.api.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ViewAllFromAPITest extends APITest {
    private final Map<String, String> testFiles;

    public ViewAllFromAPITest() throws IOException {
        testFiles = getTestFileMap();
//        testFiles.forEach((k, v) -> ctrl.upload(new SBOMFile(k, v)));
    }

    @Test
    @DisplayName("View All Files")
    @Disabled("Need to figure out how to simulate a MySQL instance")
    public void viewAllFilesTest() {
        ResponseEntity<Long[]> response = controller.viewFiles();

        assertEquals(HttpStatus.OK, response.getStatusCode());

//        for (Long id : response.getBody()) {
//            assertTrue(testFiles.keySet().contains(fileName));
//        }
    }
}
