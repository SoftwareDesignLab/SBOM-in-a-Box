package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.entities.SBOMFile;
import org.svip.compare.DiffReport;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * file: CompareAPITest.java
 * Test for SBOM Comparison API Endpoint
 *
 * @author Thomas Roman
 **/
public class CompareAPITest extends APITest {
    private final static Long[] IDs = new Long[]{6L, 4L, 0L, 9L};
    private static Map<Long, SBOMFile> fileMap;

    @BeforeAll
    static void setupFileMap(){
        try{
            fileMap = getTestFileMap();
        } catch (Exception e){
            fail(e);
        }
    }

    @Test
    @DisplayName("Generate Diff Report")
    public void generateDiffReport() throws JsonProcessingException {
        // Get SBOM when requested
        when(repository.findById(IDs[0])).thenAnswer(i -> Optional.of(fileMap.get(IDs[0])));

        // Make API Request
        ResponseEntity<?> response = controller.compare(0, IDs);

        // Assert correct object was returned
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertInstanceOf(DiffReport.class, response.getBody());
    }
}
