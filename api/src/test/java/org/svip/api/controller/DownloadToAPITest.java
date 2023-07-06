package org.svip.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class DownloadToAPITest extends APITest {

    private Map<Long, SBOMFile> testMap;

    public DownloadToAPITest() throws IOException {
        testMap = getTestFileMap();
    }


    @Test
    @DisplayName("Download File")
    public void downloadFileTest() throws IOException {

        // Mock a map of test files in the DB
        when(repository.findById(any(Long.class))).thenAnswer(i -> Optional.of(testMap.get(i.getArgument(0))));

        for (Long id : testMap.keySet()) {
            ResponseEntity<String> response = controller.download(id);
            assertEquals(testMap.get(id).getContents(), response.getBody());
        }

    }

    @Test
    @DisplayName("Download Empty/Invalid File")
    public void downloadFileEmptyTest() {
        long testId = 0;

        // Simulate empty DB
        when(repository.findById(any())).thenAnswer(i -> Optional.empty());

        ResponseEntity<String> response = controller.download(testId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
