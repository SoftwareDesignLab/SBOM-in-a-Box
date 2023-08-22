package org.svip.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.svip.api.controller.QAController;
import org.svip.api.entities.QualityReportFile;
import org.svip.api.entities.SBOM;
import org.svip.api.requests.UploadSBOMFileInput;
import org.svip.api.services.QualityReportFileService;
import org.svip.api.services.SBOMFileService;
import org.svip.metrics.pipelines.QualityReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * File: QAControllerTest.java
 * Description: Unit test for QA Controller
 *
 * @author Thomas Roman
 */

@ExtendWith(MockitoExtension.class)
@DisplayName("QA Controller Test")
public class QAControllerTest {
    @Mock
    private QualityReportFileService qualityReportFileService;

    @Mock
    private SBOMFileService sbomFileService;

    @InjectMocks
    private QAController qaController;

    // Test SBOMs
    private static final String CDX_JSON_SBOM_FILE = "./src/test/resources/sample_sboms/cdx-gomod-1.4.0-bin.json";

    ///
    /// Generate
    ///
    @Test
    @DisplayName("Generate QA")
    void generate_QA() throws Exception {
        // Given
        Long id = 1L;
        SBOM sbom = buildMockSBOMFile(CDX_JSON_SBOM_FILE);
        QualityReport qualityReport = new QualityReport("mock");
        QualityReportFile uploadedQAF = new QualityReportFile();

        // When
        when(this.sbomFileService.getSBOMFile(id)).thenReturn(sbom);
        when(this.qualityReportFileService.generateQualityReport(any())).thenReturn(qualityReport);
        when(qualityReportFileService.upload(any())).thenReturn(uploadedQAF);

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ResponseEntity<String> response = this.qaController.qa(id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(sbomFileService).getSBOMFile(id);
        verify(qualityReportFileService).generateQualityReport(any());
        verify(qualityReportFileService).upload(any());
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
