package org.svip.api.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.svip.api.entities.SBOM;
import org.svip.api.repository.ComparisonFileRepository;
import org.svip.api.repository.ConflictFileRepository;
import org.svip.api.repository.SBOMRepository;
import org.svip.api.requests.UploadSBOMFileInput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * File: DiffServiceTest.java
 * Description: Diff Report service unit tests
 *
 * @author Derek Garcia
 * @author Thomas Roman
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SBOM Service Test")
public class DiffServiceTest {

    @Mock
    private SBOMRepository sbomRepository;      // Mock repo
    @Mock
    private ComparisonFileRepository comparisonFileRepository;

    @Mock
    private ConflictFileRepository conflictFileRepository;

    @InjectMocks
    private DiffService diffService;    // Instance of service for testing
    @InjectMocks
    private SBOMFileService sbomFileService;

    // Test SBOMs
    private static final String CDX_JSON_SBOM_FILE = "./src/test/resources/sample_sboms/cdx-gomod-1.4.0-bin.json";
    private static final String SPDX_JSON_SBOM_FILE = "./src/test/resources/sample_sboms/syft-0.80.0-source-spdx-json.json";

    ///
    /// Compare
    ///

    @Test
    @DisplayName("Compare no sboms")
    void compare_no_sboms() {
        try {
            // Given
            Long[] ids = new Long[1];
            ids[0] = 0L;

            // When
            SBOM spdx23json = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);
            when(this.sbomRepository.findById(0L)).thenReturn(Optional.of(spdx23json));
            String diffJSON = this.diffService.generateDiffReportAsJSON(sbomFileService, 0L, ids);

            // Then
            assertEquals("{\"target\":0}", diffJSON);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Compare 2 sboms")
    void compare_two_sboms() {
        try {
            // Given
            SBOM spdx23json = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);
            SBOM cdx14json = buildMockSBOMFile(CDX_JSON_SBOM_FILE);
            Long[] ids = new Long[2];
            ids[0] = 0L;
            ids[1] = 1L;

            // When
            when(this.sbomRepository.findById(0L)).thenReturn(Optional.of(spdx23json));
            when(this.sbomRepository.findById(1L)).thenReturn(Optional.of(cdx14json));
            String diffJSON = this.diffService.generateDiffReportAsJSON(sbomFileService, 0L, ids);
            assertNotNull(diffJSON);
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
