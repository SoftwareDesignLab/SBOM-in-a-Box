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
import org.svip.api.repository.VEXFileRepository;
import org.svip.api.requests.UploadSBOMFileInput;
import org.svip.vex.VEXResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * File: DiffServiceTest.java
 * Description: Diff Report service unit tests
 *
 * @author Thomas Roman
 * @author Derek Garcia
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SBOM Service Test")
public class VexFileServiceTest {
    @Mock
    private VEXFileRepository vexFileRepository;      // Mock repo

    @InjectMocks
    private VEXFileService vexFileService;

    // Test SBOMs
    private static final String CDX_JSON_SBOM_FILE = "./src/test/resources/sample_sboms/cdx-gomod-1.4.0-bin.json";
    private static final String SPDX_JSON_SBOM_FILE = "./src/test/resources/sample_sboms/syft-0.80.0-source-spdx-json.json";
    private static final String SPDX_TAG_VALUE_SBOM_FILE = "./src/test/resources/sample_sboms/sbom.alpine-compare.2-3.spdx";

    // Generate
    @Test
    @DisplayName("Generate cyclonedx vex with osv")
    void generate_CDX_VEX_with_OSV() {
        try {
            SBOMFile sbomFile = buildMockSBOMFile(CDX_JSON_SBOM_FILE);
            VEXResult vexResult = this.vexFileService.generateVEX(sbomFile.toSBOMObject(), "osv", "cyclonedx", null);
            assertNotNull(vexResult);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Generate cyclonedx vex with nvd")
    void generate_CDX_VEX_with_NVD() {
        try {
            SBOMFile sbomFile = buildMockSBOMFile(CDX_JSON_SBOM_FILE);
            VEXResult vexResult = this.vexFileService.generateVEX(sbomFile.toSBOMObject(), "nvd", "cyclonedx", null);
            assertNotNull(vexResult);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Generate csaf vex with osv")
    void generate_CSAF_VEX_with_OSV() {
        try {
            SBOMFile sbomFile = buildMockSBOMFile(CDX_JSON_SBOM_FILE);
            VEXResult vexResult = this.vexFileService.generateVEX(sbomFile.toSBOMObject(), "osv", "csaf", null);
            assertNotNull(vexResult);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Generate csaf vex with nvd")
    void generate_CSAF_VEX_with_NVD() {
        try {
            SBOMFile sbomFile = buildMockSBOMFile(CDX_JSON_SBOM_FILE);
            VEXResult vexResult = this.vexFileService.generateVEX(sbomFile.toSBOMObject(), "nvd", "csaf", null);
            assertNotNull(vexResult);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    // todo upload vex file

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
