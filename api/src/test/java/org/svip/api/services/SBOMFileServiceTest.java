package org.svip.api.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.svip.api.entities.SBOM;
import org.svip.api.repository.SBOMRepository;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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

    ///
    /// Upload
    ///
    @Test
    @DisplayName("Upload CDX14 JSON SBOM")
    void upload_cdx14_json_sbom() {
        // Given
        SBOM sbom = buildMockSBOMFile(SBOM.Schema.CYCLONEDX_14, SBOM.FileType.JSON);
        // When
        try{
            this.sbomFileService.upload(sbom);
        } catch (Exception e){
            fail("Valid CDX14 SBOM");
        }
        // Then
        verify(this.sbomRepository).save(sbom);
    }

    @Test
    @DisplayName("Upload SPDX23 JSON SBOM")
    void upload_spdx23_json_sbom() {
        // Given
        SBOM sbom = buildMockSBOMFile(SBOM.Schema.SPDX_23, SBOM.FileType.JSON);
        // When
        try{
            this.sbomFileService.upload(sbom);
        } catch (Exception e){
            fail("Valid SPDX23 JSON SBOM");
        }
        // Then
        verify(this.sbomRepository).save(sbom);
    }

    @Test
    @DisplayName("Upload SPDX23 Tag Value SBOM")
    void upload_spdx23_tag_value_sbom() {
        // Given
        SBOM sbom = buildMockSBOMFile(SBOM.Schema.SPDX_23, SBOM.FileType.TAG_VALUE);
        // When
        try{
            this.sbomFileService.upload(sbom);
        } catch (Exception e){
            fail("Valid SPDX23 Tag Value SBOM");
        }
        // Then
        verify(this.sbomRepository).save(sbom);
    }

    ///
    /// Helper methods
    ///

    /**
     * @return Valid Mock SBOM file
     */
    private SBOM buildMockSBOMFile(SBOM.Schema schema, SBOM.FileType fileType){
        // Skip Input record
        return new SBOM().setName("SBOM_NAME")
                .setContent("SBOM_CONTENT")
                .setSchema(schema)
                .setFileType(fileType);
    }



}
