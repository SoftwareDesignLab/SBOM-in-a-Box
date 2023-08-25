package org.svip.api.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.svip.api.entities.QualityReportFile;
import org.svip.api.entities.SBOM;
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
        SBOM sbom = buildMockSBOMFile(CDX_JSON_SBOM_FILE);
        QualityReport qualityReport = this.qualityReportFileService.generateQualityReport(sbom.toSBOMObject());
        assertNotNull(qualityReport);
    }

    @Test
    @DisplayName("Generate spdx23 json quality report")
    void generate_SPDX23_JSON_quality_report() throws Exception {
        SBOM sbom = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);
        QualityReport qualityReport = this.qualityReportFileService.generateQualityReport(sbom.toSBOMObject());
        assertNotNull(qualityReport);
    }

    @Test
    @DisplayName("Generate spdx23 tag value quality report")
    void generate_SPDX23_tag_value_quality_report() throws Exception {
        SBOM sbom = buildMockSBOMFile(SPDX_TAG_VALUE_SBOM_FILE);
        QualityReport qualityReport = this.qualityReportFileService.generateQualityReport(sbom.toSBOMObject());
        assertNotNull(qualityReport);
    }

    @Test
    @DisplayName("Upload quality report")
    void upload_quality_report() throws Exception {
        when(qualityReportFileRepository.save(any())).thenReturn(new QualityReportFile());

        SBOM sbom = buildMockSBOMFile(SPDX_TAG_VALUE_SBOM_FILE);
        QualityReport qualityReport = this.qualityReportFileService.generateQualityReport(sbom.toSBOMObject());

        QualityReportFile qualityReportFile = this.qualityReportFileService.upload(
                new UploadQRFileInput(qualityReport).toQualityReportFile(sbom));

        assertNotNull(qualityReportFile);
    }

    @Test
    @DisplayName("Upload quality report error")
    void upload_quality_report_error() throws Exception {
        when(qualityReportFileRepository.save(any())).thenThrow(IllegalArgumentException.class);

        SBOM sbom = buildMockSBOMFile(SPDX_TAG_VALUE_SBOM_FILE);
        QualityReport qualityReport = this.qualityReportFileService.generateQualityReport(sbom.toSBOMObject());

        assertThrows(Exception.class, () ->
                this.qualityReportFileService.upload(new UploadQRFileInput(qualityReport).toQualityReportFile(sbom)));
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
