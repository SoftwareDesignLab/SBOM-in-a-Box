package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class DownloadToAPITest extends APITest {

    private Map<Long, SBOMFile> testMap;


    private Map<String, String> testUIMap  = new HashMap<String, String>() {{
        put("id", "0");
        put("filename", "somefile");
        put("cpn", "1");
    }};


    public DownloadToAPITest() throws IOException {
        testMap = getTestFileMap();
    }


    @Test
    @DisplayName("Download File")
    public void downloadFileTest() throws Exception {

        // Mock a map of test files in the DB
        when(repository.findById(any(Long.class))).thenAnswer(i -> Optional.of(testMap.get(i.getArgument(0))));

        for (Long id : testMap.keySet()) {
            testUIMap.replace("id", id.toString());
            testUIMap.replace("filename", testMap.get(id).getFileName());
            ResponseEntity<String> response = controller.download(testUIMap);  // (id);
            System.out.println(response.getBody());
            //assertEquals(testMap.get(id).getContents(), response.getBody());
        }

    }

    @Test
    @DisplayName("Download Empty/Invalid File")
    public void downloadFileEmptyTest() throws Exception {
        long testId = 0;

        // Simulate empty DB
        when(repository.findById(any())).thenAnswer(i -> Optional.empty());

        ResponseEntity<String> response = null; //(testId);
        try {
            response = controller.download(testUIMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
