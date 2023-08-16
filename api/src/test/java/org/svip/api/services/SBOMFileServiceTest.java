package org.svip.api.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.svip.api.SVIPApplication;
import org.svip.api.entities.SBOM;
import org.svip.api.repository.SBOMRepository;
import org.svip.api.requests.UploadSBOMFileInput;
import org.svip.serializers.SerializerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.never;
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
    private SBOMRepository sbomRepository;      // Mock repo

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
            SBOM sbom = buildMockSBOMFile(CDX_JSON_SBOM_FILE);
            // When
            this.sbomFileService.upload(sbom);
            // Then
            verify(this.sbomRepository).save(sbom);
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
            SBOM sbom = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);
            // When
            this.sbomFileService.upload(sbom);
            // Then
            verify(this.sbomRepository).save(sbom);
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
            SBOM sbom = buildMockSBOMFile(SPDX_TAG_VALUE_SBOM_FILE);
            // When
            this.sbomFileService.upload(sbom);
            // Then
            verify(this.sbomRepository).save(sbom);
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
            when(this.sbomRepository.findById(0L)).thenReturn(Optional.empty());    // id not in repo
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
            SBOM spdx23json = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);
            // When
            when(this.sbomRepository.findById(0L)).thenReturn(Optional.of(spdx23json));
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
            SBOM spdx23json = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);

            // When
            when(this.sbomRepository.findById(0L)).thenReturn(Optional.of(spdx23json));
            this.sbomFileService.convert(0L, SerializerFactory.Schema.CDX14, SerializerFactory.Format.JSON, false);

            // Then
            verify(this.sbomRepository, times(1)).findById(0L); // need multiple queries for overwriting

        } catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Convert SPDX23 JSON to CDX14 JSON with overwrite")
    void convert_SPDX23_JSON_to_CDX14_JSON_overwrite() {
        try {
            // Given
            SBOM spdx23json = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);

            // When
            when(this.sbomRepository.findById(0L)).thenReturn(Optional.of(spdx23json));
            long id = this.sbomFileService.convert(0L, SerializerFactory.Schema.CDX14, SerializerFactory.Format.JSON, true);

            // Then
            verify(this.sbomRepository, times(2)).findById(0L); // need multiple queries for overwriting
            assertEquals(0L, id);

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
    private SBOM buildMockSBOMFile(String filepath) throws IOException {
        // Get file contents
        String content = new String(Files.readAllBytes(Paths.get(filepath)));
        // Create SBOM
        return new UploadSBOMFileInput(filepath, content).toSBOMFile();
    }



}
