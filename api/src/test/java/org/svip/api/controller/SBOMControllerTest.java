package org.svip.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.entities.SBOM;
import org.svip.api.requests.UploadSBOMFileInput;
import org.svip.api.services.SBOMFileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * File: SBOMControllerTest.java
 * Description: Unit test for SBOM Controller
 *
 * @author Derek Garcia
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SBOM Controller Test")
public class SBOMControllerTest {

    @Mock
    private SBOMFileService sbomFileService;      // Mock service

    @InjectMocks
    private SBOMController sbomController;    // Instance of controller for testing

    // Test SBOMs
    private static final String CDX_JSON_SBOM_FILE = "./src/test/resources/sample_sboms/cdx-gomod-1.4.0-bin.json";
    private static final String SPDX_JSON_SBOM_FILE = "./src/test/resources/sample_sboms/syft-0.80.0-source-spdx-json.json";
    private static final String SPDX_TAG_VALUE_SBOM_FILE = "./src/test/resources/sample_sboms/sbom.alpine-compare.2-3.spdx";


    ///
    /// Upload
    ///
    @Test
    @DisplayName("Upload valid SBOM")
    void upload_valid_sbom() throws Exception {
        // Given
        UploadSBOMFileInput input = new UploadSBOMFileInput("CDX14_JSON", fileToContents(CDX_JSON_SBOM_FILE));
        SBOM sbom = input.toSBOMFile();
        // When
        when(this.sbomFileService.upload(any(SBOM.class))).thenAnswer(i -> i.getArgument(0));   // echo
        ResponseEntity<Long> response =  this.sbomController.upload(input);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode() );
        assertEquals(sbom.getId(), response.getBody());
    }

    @Test
    @DisplayName("Upload invalid SBOM")
    void upload_invalid_sbom(){
        // Given
        UploadSBOMFileInput input = new UploadSBOMFileInput("CDX14_JSON", "SBOM CONTENTS");
        // When
        ResponseEntity<Long> response = this.sbomController.upload(input);
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    ///
    /// Merge
    ///
    @Test
    @DisplayName("Merge 2 SBOMs")
    void merge_two_sboms() throws Exception {
        // Given
        Long[] ids = new Long[2];
        ids[0] = 0L;
        ids[1] = 1L;
        // When
        // TODO different types of exceptions?
        when(this.sbomFileService.merge(ids)).thenThrow(Exception.class);

        ResponseEntity<Long> response =  this.sbomController.merge(ids);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2L, response.getBody());
    }

    @Test
    @DisplayName("Merge 1 SBOM")
    void merge_one_sbom() throws Exception {
        // Given
        Long[] ids = new Long[1];
        ids[0] = 0L;

        // When
        // TODO different types of exceptions?
        when(this.sbomFileService.merge(ids)).thenThrow(Exception.class);
        ResponseEntity<Long> response = this.sbomController.merge(ids);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Fail to parse merge sbom")
    void fail_to_parse_merge_sbom() throws Exception {
        // Given
        Long[] ids = new Long[2];
        ids[0] = 0L;
        ids[1] = 1L;

        // When
        // TODO different types of exceptions?
        when(this.sbomFileService.merge(ids)).thenThrow(Exception.class);
        ResponseEntity<Long> response = this.sbomController.merge(ids);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("missing merge sbom")
    void missing_merge_sbom() throws Exception {
        // Given
        Long[] ids = new Long[2];
        ids[0] = 0L;
        ids[1] = 1L;

        // When
        // TODO different types of exceptions?
        when(this.sbomFileService.merge(ids)).thenThrow(Exception.class);
        ResponseEntity<Long> response = this.sbomController.merge(ids);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    ///
    /// Helper Methods
    ///
    private String fileToContents(String filepath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filepath)));
    }




}
