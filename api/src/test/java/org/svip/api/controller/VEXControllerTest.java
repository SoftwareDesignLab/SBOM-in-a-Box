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
import org.svip.api.entities.SBOMFile;
import org.svip.api.entities.VEXFile;
import org.svip.api.requests.UploadSBOMFileInput;
import org.svip.api.services.SBOMFileService;
import org.svip.api.services.VEXFileService;
import org.svip.vex.VEXResult;
import org.svip.vex.model.VEX;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * File: VEXControllerTest.java
 * Description: Unit test for VEX Controller
 *
 * @author Thomas Roman
 */

@ExtendWith(MockitoExtension.class)
@DisplayName("VEX Controller Test")
public class VEXControllerTest {
    @Mock
    private SBOMFileService sbomFileService;      // Mock service
    @Mock
    private VEXFileService vexFileService;      // Mock service
    @InjectMocks
    VEXController vexController;    // Instance of controller for testing

    // Test SBOMs
    private static final String CDX_JSON_SBOM_FILE = "./src/test/resources/sample_sboms/cdx-gomod-1.4.0-bin.json";
    @Test
    @DisplayName("Generate Vex")
    void generate_vex() throws Exception {
        // Given
        Long id = 0L;
        String apiKey = "your-api-key";
        String format = "json";
        String client = "osv";
        SBOMFile sbom = buildMockSBOMFile(CDX_JSON_SBOM_FILE);
        VEXResult vexResult = new VEXResult(new VEX(new VEX.Builder()), new HashMap<>());
        VEXFile uploadedVF = new VEXFile();

        // When
        when(sbomFileService.getSBOMFile(id)).thenReturn(sbom);
        when(vexFileService.generateVEX(any(), anyString(), anyString(), anyString())).thenReturn(vexResult);
        when(vexFileService.upload(any())).thenReturn(uploadedVF);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ResponseEntity<String> response = vexController.vex(apiKey, id, format, client);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(sbomFileService).getSBOMFile(id);
        verify(vexFileService).generateVEX(any(), anyString(), anyString(), anyString());
        verify(vexFileService).upload(any());
    }

    @Test
    @DisplayName("Generate With Invalid SBOM")
    void generateVexWithInvalidSBOMTest() {
        // Given
        Long id = 0L;
        String apiKey = "your-api-key";
        String format = "json";
        String client = "osv";

        // When
        when(sbomFileService.getSBOMFile(id)).thenReturn(null);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ResponseEntity<String> response = vexController.vex(apiKey, id, format, client);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Generate With Vex Upload Error")
    void generateWithVexError() throws Exception {
        // Given
        Long id = 0L;
        String apiKey = "your-api-key";
        String format = "json";
        String client = "osv";
        SBOMFile sbom = buildMockSBOMFile(CDX_JSON_SBOM_FILE);
        VEXResult vexResult = new VEXResult(new VEX(new VEX.Builder()), new HashMap<>());

        // When
        when(sbomFileService.getSBOMFile(id)).thenReturn(sbom);
        when(vexFileService.generateVEX(any(), anyString(), anyString(), anyString())).thenReturn(vexResult);
        when(vexFileService.upload(any())).thenThrow(Exception.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ResponseEntity<String> response = vexController.vex(apiKey, id, format, client);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
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
