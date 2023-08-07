package org.svip.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.entities.SBOMFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UploadToAPITest extends APITest {

    @Test
    @DisplayName("Upload File")
    public void uploadFileTest() throws IOException {
        List<SBOMFile> files = getTestFileMap().values().stream()
                .filter(sbomFile -> !sbomFile.getFileName().endsWith(".xml"))
                .map(sbomFile -> new SBOMFile(sbomFile.getFileName(), sbomFile.getContents()))
                .toList();

        // Mock repository output (returns SBOMFile that it recieived)
        when(oldRepository.save(any(SBOMFile.class))).thenAnswer(i -> i.getArgument(0));

        for (SBOMFile file : files) {
//            ResponseEntity<?> response = oldController.upload(file);
//
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            assertEquals(file.getId(), response.getBody());
        }
    }

    @ParameterizedTest
    @DisplayName("Upload Empty File Name")
    @NullAndEmptySource
    public void uploadEmptyFileNameTest(String fileName) {
        SBOMFile file = new SBOMFile(fileName, "test contents");

//        ResponseEntity<?> response = oldController.upload(file);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @DisplayName("Upload Empty File Contents")
    @NullAndEmptySource
    public void uploadEmptyFileContentsTest(String fileContents) {
        SBOMFile file = new SBOMFile("filename", fileContents);

//        ResponseEntity<?> response = oldController.upload(file);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
