/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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
* /

package org.svip.api.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.svip.api.entities.QualityReportFile;
import org.svip.api.entities.SBOMFile;
import org.svip.api.repository.QualityReportFileRepository;
import org.svip.api.requests.UploadQRFileInput;
import org.svip.api.requests.UploadSBOMFileInput;
import org.svip.metrics.pipelines.QualityReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * File: DiffServiceTest.java
 * Description: Diff Report service unit tests
 *
 * @author Thomas Roman
 * @author Derek Garcia
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Quality Report Service Test")
public class QualityReportFileServiceTest {
    @Mock
    private QualityReportFileRepository qualityReportFileRepository;      // Mock repo

    @InjectMocks
    private QualityReportFileService qualityReportFileService;

    // Test SBOMs
    private static final String CDX_JSON_SBOM_FILE = "./src/test/resources/sample_sboms/cdx-gomod-1.4.0-bin.json";
    private static final String SPDX_JSON_SBOM_FILE = "./src/test/resources/sample_sboms/syft-0.80.0-source-spdx-json.json";
    private static final String SPDX_TAG_VALUE_SBOM_FILE = "./src/test/resources/sample_sboms/sbom.alpine-compare.2-3.spdx";

    // Generate

    @Test
    @DisplayName("Generate cdx14 json quality report")
    void generate_CDX14_JSON_quality_report() throws Exception {
        SBOMFile sbomFile = buildMockSBOMFile(CDX_JSON_SBOM_FILE);
        QualityReport qualityReport = this.qualityReportFileService.generateQualityReport(sbomFile.toSBOMObject());
        assertNotNull(qualityReport);
    }

    @Test
    @DisplayName("Generate spdx23 json quality report")
    void generate_SPDX23_JSON_quality_report() throws Exception {
        SBOMFile sbomFile = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);
        QualityReport qualityReport = this.qualityReportFileService.generateQualityReport(sbomFile.toSBOMObject());
        assertNotNull(qualityReport);
    }

    @Test
    @DisplayName("Generate spdx23 tag value quality report")
    void generate_SPDX23_tag_value_quality_report() throws Exception {
        SBOMFile sbomFile = buildMockSBOMFile(SPDX_TAG_VALUE_SBOM_FILE);
        QualityReport qualityReport = this.qualityReportFileService.generateQualityReport(sbomFile.toSBOMObject());
        assertNotNull(qualityReport);
    }

    @Test
    @DisplayName("Upload quality report")
    void upload_quality_report() throws Exception {
        when(qualityReportFileRepository.save(any())).thenReturn(new QualityReportFile());

        SBOMFile sbomFile = buildMockSBOMFile(SPDX_TAG_VALUE_SBOM_FILE);
        QualityReport qualityReport = this.qualityReportFileService.generateQualityReport(sbomFile.toSBOMObject());

        QualityReportFile qualityReportFile = this.qualityReportFileService.upload(
                new UploadQRFileInput(qualityReport).toQualityReportFile(sbomFile));

        assertNotNull(qualityReportFile);
    }

    @Test
    @DisplayName("Upload quality report error")
    void upload_quality_report_error() throws Exception {
        when(qualityReportFileRepository.save(any())).thenThrow(IllegalArgumentException.class);

        SBOMFile sbomFile = buildMockSBOMFile(SPDX_TAG_VALUE_SBOM_FILE);
        QualityReport qualityReport = this.qualityReportFileService.generateQualityReport(sbomFile.toSBOMObject());

        assertThrows(Exception.class, () ->
                this.qualityReportFileService.upload(new UploadQRFileInput(qualityReport).toQualityReportFile(sbomFile)));
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
