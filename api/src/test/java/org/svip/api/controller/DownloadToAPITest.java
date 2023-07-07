package org.svip.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;

import java.io.IOException;
import java.util.*;

import static java.lang.Integer.parseInt;
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

        String content = "";
        //for (Long id : testMap.keySet())
        Long id = 1L;
        {
            testUIMap.replace("id", id.toString());
            testUIMap.replace("filename", testMap.get(id).getFileName());
            ResponseEntity<String> response = controller.download(testUIMap);  // (id);
            //System.out.println(response.getBody());
            HashMap<String,String> thashMap = new ObjectMapper().readValue(response.getBody(), HashMap.class);
            System.out.println("filename: " + thashMap.get("filename").substring(thashMap.get("filename").lastIndexOf('/') + 1) +
            ", tpn : " + thashMap.get("tpn") );

            content =  thashMap.get("content");
            int cpn = parseInt(thashMap.get("cpn"));
            int tpn = parseInt(thashMap.get("tpn"));
            for(int i=cpn+1; i <= tpn; i++) {
                testUIMap.replace("cpn", Integer.toString(i));
                ResponseEntity<String> lresponse = controller.download(testUIMap);
                HashMap<String,String> lhashMap = new ObjectMapper().readValue(lresponse.getBody(), HashMap.class);
                System.out.println("id: "   + lhashMap.get("id") +
                                   ", cpn: " + lhashMap.get("cpn") +
                                   ", tpn: " + lhashMap.get("tpn") //+
                                   //", filename: " + lhashMap.get("filename")
                );
                content = content + lhashMap.get("content");
            }
            assertEquals(testMap.get(id).getContents(), content);
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
