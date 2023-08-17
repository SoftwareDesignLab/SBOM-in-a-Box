package org.svip.api.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.svip.api.entities.QualityReportFile;
import org.svip.api.entities.SBOM;
import org.svip.api.entities.diff.ComparisonFile;
import org.svip.api.repository.ComparisonFileRepository;
import org.svip.api.repository.ConflictFileRepository;
import org.svip.api.repository.QualityReportFileRepository;
import org.svip.api.repository.SBOMRepository;
import org.svip.api.requests.UploadSBOMFileInput;
import org.svip.api.requests.diff.UploadComparisonFileInput;
import org.svip.compare.Comparison;
import org.svip.metrics.pipelines.QualityReport;
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
 * File: DiffServiceTest.java
 * Description: Diff Report service unit tests
 *
 * @author Thomas Roman
 * @author Derek Garcia
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SBOM Service Test")
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
    void generate_CDX14_JSON_quality_report() {
        try {
            SBOM sbom = buildMockSBOMFile(CDX_JSON_SBOM_FILE);
            QualityReport qualityReport = this.qualityReportFileService.generateQualityReport(sbom.toSBOMObject());
            assertNotNull(qualityReport);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Generate spdx23 json quality report")
    void generate_SPDX23_JSON_quality_report() {
        try {
            SBOM sbom = buildMockSBOMFile(SPDX_JSON_SBOM_FILE);
            QualityReport qualityReport = this.qualityReportFileService.generateQualityReport(sbom.toSBOMObject());
            assertNotNull(qualityReport);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Generate spdx23 tag value quality report")
    void generate_SPDX23_tag_value_quality_report() {
        try {
            SBOM sbom = buildMockSBOMFile(SPDX_TAG_VALUE_SBOM_FILE);
            QualityReport qualityReport = this.qualityReportFileService.generateQualityReport(sbom.toSBOMObject());
            assertNotNull(qualityReport);
        } catch (Exception e) {
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
