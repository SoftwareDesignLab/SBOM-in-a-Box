package org.svip.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ViewFromAPITest extends APITest {

    private Map<Long, SBOMFile> testMap;

    public ViewFromAPITest() throws IOException {
        testMap = getTestFileMap();
    }

    @Test
    @DisplayName("View File")
    public void viewFileTest() {
        // Mock a map of test files in the DB
        when(repository.findById(any(Long.class))).thenAnswer(i -> Optional.of(testMap.get(i.getArgument(0))));

        for (Long id : testMap.keySet()) {
            ResponseEntity<String> response = controller.view(id);
            assertEquals(testMap.get(id).getContents(), response.getBody());
        }
    }

    @Test
    @DisplayName("View Empty/Invalid File")
    public void viewFileEmptyTest() {
        long testId = 0;

        // Simulate empty DB
        when(repository.findById(any())).thenAnswer(i -> Optional.empty());

        ResponseEntity<String> response = controller.view(testId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
