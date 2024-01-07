/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
 */

package org.svip.api.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.svip.api.entities.SBOMFile;
import org.svip.api.repository.ComparisonFileRepository;
import org.svip.api.repository.ConflictFileRepository;
import org.svip.api.repository.SBOMFileRepository;
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
@DisplayName("Diff Service Test")
public class DiffServiceTest {

    @Mock
    private SBOMFileRepository sbomFileRepository;      // Mock repo
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
            SBOMFile spdx23json = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);
            when(this.sbomFileRepository.findById(0L)).thenReturn(Optional.of(spdx23json));
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
            SBOMFile spdx23json = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);
            SBOMFile cdx14json = buildMockSBOMFile(CDX_JSON_SBOM_FILE);
            Long[] ids = new Long[2];
            ids[0] = 0L;
            ids[1] = 1L;

            // When
            when(this.sbomFileRepository.findById(0L)).thenReturn(Optional.of(spdx23json));
            when(this.sbomFileRepository.findById(1L)).thenReturn(Optional.of(cdx14json));
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
    private SBOMFile buildMockSBOMFile(String filepath) throws IOException {
        // Get file contents
        String content = new String(Files.readAllBytes(Paths.get(filepath)));
        // Create SBOM
        return new UploadSBOMFileInput(filepath, content).toSBOMFile();
    }
}
