package org.svip.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.controller.SVIPApiController;
import org.svip.api.utils.Utils;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UploadToAPITest extends APITest {
    private SVIPApiController ctrl;

    public UploadToAPITest() {
        ctrl = new SVIPApiController();
    }

    @Test
    @DisplayName("Upload File")
    public void uploadFileTest() throws IOException {
        List<Utils.SBOMFile> files = testFileMap().entrySet().stream()
                .map(e -> new Utils.SBOMFile(e.getKey(), e.getValue())).toList();

        for (Utils.SBOMFile file : files) {
            ResponseEntity<String> response = ctrl.upload(file);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(file.fileName, response.getBody());
        }
    }

    @ParameterizedTest
    @DisplayName("Upload Empty File Name")
    @NullAndEmptySource
    public void uploadEmptyFileNameTest(String fileName) {
        Utils.SBOMFile file = new Utils.SBOMFile(fileName, "test contents");

        ResponseEntity<String> response = ctrl.upload(file);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @DisplayName("Upload Empty File Contents")
    @NullAndEmptySource
    public void uploadEmptyFileContentsTest(String fileContents) {
        Utils.SBOMFile file = new Utils.SBOMFile("filename", fileContents);

        ResponseEntity<String> response = ctrl.upload(file);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
