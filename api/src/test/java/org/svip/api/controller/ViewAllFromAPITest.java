package org.svip.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ViewAllFromAPITest extends APITest {
    private List<SBOMFile> files;

    public ViewAllFromAPITest() throws IOException {
        files = getTestFileMap().values().stream()
                .map(sbomFile -> new SBOMFile(sbomFile.getFileName(), sbomFile.getContents()))
                .toList();
    }

    @Test
    @DisplayName("View All Files")
    public void viewAllFilesTest() {
        when(repository.findAll()).thenAnswer(i -> files);

        ResponseEntity<Long[]> response = controller.viewFiles();
        assertEquals(files.size(), response.getBody().length);
    }
}
