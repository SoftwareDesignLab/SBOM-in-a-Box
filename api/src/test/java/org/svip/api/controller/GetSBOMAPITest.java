package org.svip.api.controller;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Derek Garcia
 **/

public class GetSBOMAPITest extends APITest{

    private final static long CDX14_JSON_ID = 6;
    private final static long CDX14_XML_ID = 4;
    private final static long SPDX23_TAGVALUE_ID = 0;
    private final static long SPDX23_JSON_ID = 9;
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
    @DisplayName("Parse Valid CDX14 JSON")
    public void get_valid_CDX_14_SBOM_JSON() {
        // Get CDX14 JSON SBOM when requested
        when(repository.findById(CDX14_JSON_ID)).thenAnswer(i -> Optional.of(fileMap.get(CDX14_JSON_ID)));

        // Make API Request
        ResponseEntity<?> response = controller.getSBOM(CDX14_JSON_ID);

        // Assert correct object was returned
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertInstanceOf(CDX14SBOM.class, response.getBody());
    }

    @Test
    @DisplayName("Parse Valid SPDX23 Tag Value")
    public void get_valid_SPDX_23_SBOM_TAGVALUE(){
        // Get SPDX23 Tagvalue SBOM when requested
        when(repository.findById(SPDX23_TAGVALUE_ID)).thenAnswer(i -> Optional.of(fileMap.get(SPDX23_TAGVALUE_ID)));

        // Make API Request
        ResponseEntity<?> response = controller.getSBOM(SPDX23_TAGVALUE_ID);

        // Assert correct object was returned
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertInstanceOf(SPDX23SBOM.class, response.getBody());
    }

    @Test
    @DisplayName("Parse Valid SPDX23 JSON")
    public void get_valid_SPDX23_SBOM_JSON(){
        // Get SPDX23 JSON SBOM when requested
        when(repository.findById(SPDX23_JSON_ID)).thenAnswer(i -> Optional.of(fileMap.get(SPDX23_JSON_ID)));

        // Make API Request
        ResponseEntity<?> response = controller.getSBOM(SPDX23_JSON_ID);

        // Assert correct object was returned
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertInstanceOf(SPDX23SBOM.class, response.getBody());
    }

    @Test
    @DisplayName("Parse Unsupported SBOM")
    public void get_unsupported_SBOM_format(){
        // alpine cdx
    }

    @Test
    @DisplayName("Request SBOM with invalid ID value")
    public void get_invalid_ID_SBOM(){

    }

    @Test
    @DisplayName("Request SBOM with valid, but missing ID")
    public void get_missing_ID_SBOM(){

    }

}
