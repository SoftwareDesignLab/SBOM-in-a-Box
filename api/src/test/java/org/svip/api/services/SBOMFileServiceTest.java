package org.svip.api.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.svip.api.entities.SBOM;
import org.svip.api.repository.SBOMRepository;
import org.svip.api.requests.UploadSBOMFileInput;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.Deserializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;
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
