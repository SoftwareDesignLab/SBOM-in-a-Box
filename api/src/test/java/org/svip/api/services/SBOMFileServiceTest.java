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
    void upload_cdx14_json_sbom() {
        // Given
        SBOM sbom = buildMockCDX14JSONSBOMFile();
        // When
        try{
            this.sbomFileService.upload(sbom);
        } catch (Exception e){
            fail("Valid CDX14 SBOM");
        }
        // Then
        verify(this.sbomRepository).save(sbom);
    }

    ///
    /// Helper methods
    ///

    /**
     * @return CycloneDX14 JSON SBOM File
     */
    private SBOM buildMockCDX14JSONSBOMFile(){
        // Skip Input record
        return new SBOM().setName("TEST_SBOM")
                         .setContent("CDX14_SBOM")
                         .setSchema(SBOM.Schema.CYCLONEDX_14)
                         .setFileType(SBOM.FileType.JSON);
    }
}
