package org.svip.api.services;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.svip.api.entities.SBOMFile;
import org.svip.api.repository.SBOMFileRepository;
import org.svip.api.requests.UploadSBOMFileInput;
import org.svip.serializers.SerializerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * File: SBOMFileServiceTest.java
 * Description: SBOM service unit tests
 *
 * @author Derek Garcia
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SBOM Service Test")
public class SBOMFileServiceTest {

    @Mock
    private SBOMFileRepository sbomFileRepository;      // Mock repo

    @InjectMocks
    private SBOMFileService sbomFileService;    // Instance of service for testing

    // Test SBOMs
    private static final String CDX_JSON_SBOM_FILE = "./src/test/resources/sample_sboms/cdx-gomod-1.4.0-bin.json";
    private static final String SPDX_JSON_SBOM_FILE = "./src/test/resources/sample_sboms/syft-0.80.0-source-spdx-json.json";
    private static final String SPDX_TAG_VALUE_SBOM_FILE = "./src/test/resources/sample_sboms/sbom.alpine-compare.2-3.spdx";

    ///
    /// Upload
    ///
    @Test
    @DisplayName("Upload CDX14 JSON SBOM")
    void upload_cdx14_json_sbom() {
        try{
            // Given
            SBOMFile sbomFile = buildMockSBOMFile(CDX_JSON_SBOM_FILE);
            // When
            this.sbomFileService.upload(sbomFile);
            // Then
            verify(this.sbomFileRepository).save(sbomFile);
        } catch (IOException e){
            fail("Failed to parse file: " + CDX_JSON_SBOM_FILE);
        } catch (Exception e){
            fail("Valid CDX14 SBOM");
        }
    }

    @Test
    @DisplayName("Upload SPDX23 JSON SBOM")
    void upload_spdx23_json_sbom() {
        try{
            // Given
            SBOMFile sbomFile = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);
            // When
            this.sbomFileService.upload(sbomFile);
            // Then
            verify(this.sbomFileRepository).save(sbomFile);
        } catch (IOException e){
            fail("Failed to parse file: " + SPDX_JSON_SBOM_FILE);
        } catch (Exception e){
            fail("Valid SPDX23 JSON SBOM");
        }

    }

    @Test
    @DisplayName("Upload SPDX23 Tag Value SBOM")
    void upload_spdx23_tag_value_sbom() {
        try {
            // Given
            SBOMFile sbomFile = buildMockSBOMFile(SPDX_TAG_VALUE_SBOM_FILE);
            // When
            this.sbomFileService.upload(sbomFile);
            // Then
            verify(this.sbomFileRepository).save(sbomFile);
        } catch (IOException e){
            fail("Failed to parse file: " + SPDX_TAG_VALUE_SBOM_FILE);
        } catch (Exception e){
            fail("Valid SPDX23 Tag Value SBOM");
        }
    }

    ///
    /// Convert
    ///

    @Test
    @DisplayName("Convert bad id")
    void convert_with_bad_id() {
        try {
            // Given empty repo
            // When
            when(this.sbomFileRepository.findById(0L)).thenReturn(Optional.empty());    // id not in repo
            this.sbomFileService.convert(0L, SerializerFactory.Schema.CDX14, SerializerFactory.Format.JSON, false);

            fail("Convert has no sbom target"); // should fail

        } catch (Exception e){
            // Then ok
        }
    }
    @Test
    @DisplayName("Attempt to convert SPDX23 JSON to CDX14 Tag Value")
    void convert_SPDX23_JSON_to_CDX14_TAG_VALUE() {
        try {
            // Given
            SBOMFile spdx23json = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);
            // When
            when(this.sbomFileRepository.findById(0L)).thenReturn(Optional.of(spdx23json));
            this.sbomFileService.convert(0L, SerializerFactory.Schema.CDX14, SerializerFactory.Format.TAGVALUE, false);

            fail("Cannot convert to CDX14 Tag Value"); // should fail

        } catch (Exception e){
            // Then ok
        }
    }

    @Test
    @DisplayName("Convert SPDX23 JSON to CDX14 JSON with no overwrite")
    void convert_SPDX23_JSON_to_CDX14_JSON_no_overwrite() {
        try {
            // Given
            SBOMFile spdx23json = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);

            // When
            when(this.sbomFileRepository.findById(0L)).thenReturn(Optional.of(spdx23json));
            this.sbomFileService.convert(0L, SerializerFactory.Schema.CDX14, SerializerFactory.Format.JSON, false);

            // Then
            verify(this.sbomFileRepository, times(1)).findById(0L); // need multiple queries for overwriting

        } catch (Exception e){
            fail(e.getMessage());
        }
    }


    @Test
    @DisplayName("Convert SPDX23 JSON to CDX14 JSON with overwrite")
    void convert_SPDX23_JSON_to_CDX14_JSON_overwrite() {
        try {
            // Given
            SBOMFile spdx23json = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);

            // When
            when(this.sbomFileRepository.findById(0L)).thenReturn(Optional.of(spdx23json));
            long id = this.sbomFileService.convert(0L, SerializerFactory.Schema.CDX14, SerializerFactory.Format.JSON, true);

            // Then
            verify(this.sbomFileRepository, times(2)).findById(0L); // need multiple queries for overwriting
            assertEquals(0L, id);

        } catch (Exception e){
            fail(e.getMessage());
        }
    }

    ///
    /// Merge
    ///
    @Test
    @DisplayName("Merge no sboms")
    void merge_no_sboms() {
        // Given
        Long[] ids = new Long[0];

        // When
        assertThrows(Exception.class, () -> this.sbomFileService.merge(ids));
    }

    @Disabled("Fails during deserialization due to \n\n being generated in SPDX TagValue file")
    @Test
    @DisplayName("Merge 2 sboms")
    void merge_two_sboms() throws Exception{
        // Given
        SBOMFile spdx23json = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);
        SBOMFile cdx14json = buildMockSBOMFile(CDX_JSON_SBOM_FILE);
        Long[] ids = new Long[2];
        ids[0] = 0L;
        ids[1] = 1L;


        // When
        when(this.sbomFileRepository.findById(0L)).thenReturn(Optional.of(spdx23json));
        when(this.sbomFileRepository.findById(1L)).thenReturn(Optional.of(cdx14json));
        this.sbomFileService.merge(ids);

        // Then
        verify(this.sbomFileRepository).findById(0L);
        verify(this.sbomFileRepository).findById(1L);
    }

    ///
    /// Get SBOM File
    ///

    @Test
    @DisplayName("Get missing sbom file")
    void get_missing_sbom_file(){
        try {
            // Given

            // When
            when(this.sbomFileRepository.findById(0L)).thenReturn(Optional.empty());
            SBOMFile sbomFile = this.sbomFileService.getSBOMFile(0L);
            // Then
            assertNull(sbomFile);

        } catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Get sbom file")
    void get_sbom_file(){
        try {
            // Given
            SBOMFile spdx23json = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);
            // When
            when(this.sbomFileRepository.findById(0L)).thenReturn(Optional.of(spdx23json));
            SBOMFile sbomFile = this.sbomFileService.getSBOMFile(0L);
            // Then
            assertEquals(spdx23json.getId(), sbomFile.getId());
            assertEquals(spdx23json.getName(), sbomFile.getName());
            assertEquals(spdx23json.getContent(), sbomFile.getContent());
            assertEquals(spdx23json.getSchema(), sbomFile.getSchema());
            assertEquals(spdx23json.getFileType(), sbomFile.getFileType());

        } catch (Exception e){
            fail(e.getMessage());
        }
    }

    ///
    /// Get all ids
    ///
    @Test
    @DisplayName("Get all ids")
    void get_all_ids_from_database(){
        try {
            // Given
            SBOMFile spdx23json = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);
            List<SBOMFile> sbomFiles = new ArrayList<>();
            sbomFiles.add(spdx23json);
            // When
            when(this.sbomFileRepository.findAll()).thenReturn(new ArrayList<>(sbomFiles));
            Long[] ids = this.sbomFileService.getAllIDs();
            // Then
            assertEquals(1, ids.length);
            verify(sbomFileRepository).findAll();

        } catch (Exception e){
            fail(e.getMessage());
        }
    }

    ///
    /// Delete SBOM
    ///

    @Test
    @DisplayName("Delete sbom file")
    void delete_sbom_file(){
        try {
            // Given
            SBOMFile spdx23json = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);
            // When
            this.sbomFileService.deleteSBOMFile(spdx23json);
            // Then
            verify(this.sbomFileRepository).delete(spdx23json);

        } catch (Exception e){
            fail(e.getMessage());
        }
    }

    ///
    /// Helper methods
    ///

    /**
     * Generate SBOM File from a filepath
     *
     * @param filepath path of the sbom
     * @return Valid Mock SBOM file
     * @throws IOException failed to open file
     */
    private SBOMFile buildMockSBOMFile(String filepath) throws IOException {
        // Get file contents
        String content = new String(Files.readAllBytes(Paths.get(filepath)));
        // Create SBOM
        return new UploadSBOMFileInput(filepath, content).toSBOMFile();
    }



}
