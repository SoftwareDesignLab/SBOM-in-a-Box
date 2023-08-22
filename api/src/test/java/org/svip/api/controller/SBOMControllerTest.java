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
import org.svip.serializers.SerializerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * File: SBOMControllerTest.java
 * Description: Unit test for SBOM Controller
 *
 * @author Derek Garcia
 * @author Thomas Roman
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
    private static final String CDX_SMALL = "./src/test/resources/sample_sboms/CDX_Test.json";

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

    @Test
    @DisplayName("Convert sbom")
    void convert_sbom() throws Exception {
        // Given
        Long id = 1L;
        SerializerFactory.Schema schema = SerializerFactory.Schema.CDX14;
        SerializerFactory.Format format = SerializerFactory.Format.JSON;
        Boolean overwrite = true;
        Long convertedID = 2L;

        // When
        when(sbomFileService.convert(id, schema, format, overwrite)).thenReturn(convertedID);
        ResponseEntity<?> response = sbomController.convert(id, schema, format, overwrite);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(convertedID, response.getBody());
        verify(sbomFileService).convert(id, schema, format, overwrite);
    }

    @Test
    @DisplayName("Get SBOM Object as JSON")
    void get_SBOM_object_as_JSON() throws Exception {
        // Given
        Long id = 1L;
        String jsonContent = "{\"format\":\"SPDX\",\"uid\":\"Test\",\"creationData\":{},\"components\":[{\"uid\":\"uid1\",\"name\":\"COMPONENT 1\",\"licenses\":{},\"version\":\"1\"},{\"uid\":\"uid3\",\"name\":\"COMPONENT 3\",\"licenses\":{},\"version\":\"1\"},{\"uid\":\"uid2\",\"name\":\"COMPONENT 2\",\"licenses\":{},\"version\":\"1\"}]}";
        SBOM sbom = buildMockSBOMFile(CDX_SMALL);

        // When
        when(sbomFileService.getSBOMFile(id)).thenReturn(sbom);
        ResponseEntity<String> response = sbomController.getSBOMObjectAsJSON(id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jsonContent, response.getBody());
        verify(sbomFileService).getSBOMFile(id);
    }

    @Test
    @DisplayName("Get SBOM Object as JSON no content")
    void get_SBOM_object_as_JSON_no_content() throws Exception {
        // Given
        Long id = 1L;

        // When
        when(sbomFileService.getSBOMFile(id)).thenReturn(null);
        ResponseEntity<String> response = sbomController.getSBOMObjectAsJSON(id);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Get SBOM content test")
    void get_SBOM_content() throws Exception {
        // Given
        Long id = 1L;
        SBOM sbom = buildMockSBOMFile(CDX_JSON_SBOM_FILE);

        // When
        when(sbomFileService.getSBOMFile(id)).thenReturn(sbom);
        ResponseEntity<SBOM> response = sbomController.getContent(id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("./src/test/resources/sample_sboms/cdx-gomod-1.4.0-bin.json", response.getBody().getName());
        verify(sbomFileService).getSBOMFile(id);
    }

    @Test
    @DisplayName("Get SBOM no content test")
    void get_SBOM_no_content() throws Exception {
        // Given
        Long id = 1L;

        // When
        when(sbomFileService.getSBOMFile(id)).thenReturn(null);
        ResponseEntity<SBOM> response = sbomController.getContent(id);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("get all ids")
    public void get_all_ids() {
        // Given
        Long[] ids = {1L, 2L, 3L}; // Set the expected array of IDs

        // When
        when(sbomFileService.getAllIDs()).thenReturn(ids);
        ResponseEntity<Long[]> response = sbomController.getAllIds();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals(ids, response.getBody());
        verify(sbomFileService).getAllIDs();
    }

    @Test
    @DisplayName("get all ids no content")
    public void get_all_ids_no_content() {
        // Given
        Long[] emptyIds = {};

        // When
        when(sbomFileService.getAllIDs()).thenReturn(emptyIds);
        ResponseEntity<Long[]> response = sbomController.getAllIds();

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(sbomFileService).getAllIDs();
    }

    @Test
    @DisplayName("Delete SBOM")
    public void delete_SBOM() throws Exception {
        // Given
        Long id = 1L;
        SBOM sbom = buildMockSBOMFile(CDX_JSON_SBOM_FILE);

        // When
        when(sbomFileService.getSBOMFile(id)).thenReturn(sbom);
        ResponseEntity<Long> response = sbomController.delete(id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody());
        verify(sbomFileService).getSBOMFile(id);
        verify(sbomFileService).deleteSBOMFile(sbom);
    }

    @Test
    @DisplayName("Delete SBOM no content")
    public void delete_SBOM_no_content() throws Exception {
        // Given
        Long id = 1L;

        // When
        when(sbomFileService.getSBOMFile(id)).thenReturn(null);
        ResponseEntity<Long> response = sbomController.delete(id);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    ///
    /// Helper Methods
    ///
    private String fileToContents(String filepath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filepath)));
    }

    /**
     * Generate SBOM File from a filepath
     *
     * @param filepath path of the sbom
     * @return Valid Mock SBOM file
     * @throws IOException failed to open file
     */
    private SBOM buildMockSBOMFile(String filepath) throws IOException {
        // Get file contents
        String content = new String(Files.readAllBytes(Paths.get(filepath)));
        // Create SBOM
        return new UploadSBOMFileInput(filepath, content).toSBOMFile();
    }


}
