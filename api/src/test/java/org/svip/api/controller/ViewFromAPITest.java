package org.svip.api.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.svip.api.controller.old.APITest;

import java.io.IOException;
import java.util.Map;

public class ViewFromAPITest extends APITest {
    private SVIPApiController ctrl;

    private final Map<String, String> testFiles;

    public ViewFromAPITest() throws IOException {
//        ctrl = new SVIPApiController();

        testFiles = testFileMap();
//        testFiles.forEach((k, v) -> ctrl.upload(new SBOMFile(k, v)));
    }

    @Test
    @DisplayName("View File")
    @Disabled("Fix API ID parameter")
    public void viewFileTest() {

        for (String fileName : testFiles.keySet()) {
//            ResponseEntity<String> response = ctrl.view(fileName);
//
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            assertEquals(testFiles.get(fileName), response.getBody());
        }
    }

    @ParameterizedTest
    @DisplayName("View Empty/Invalid File")
    @NullAndEmptySource
    @ValueSource(strings = { "INVALID FILE NAME" })
    @Disabled("Fix API ID parameter")
    public void viewFileEmptyTest(String fileName) {
//        ResponseEntity<String> response = ctrl.view(fileName);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
