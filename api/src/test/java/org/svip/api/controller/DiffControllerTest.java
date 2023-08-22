package org.svip.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.services.DiffService;
import org.svip.api.services.SBOMFileService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * File: DiffControllerTest.java
 * Description: Unit test for Diff Controller
 *
 * @author Thomas Roman
 */

@ExtendWith(MockitoExtension.class)
@DisplayName("Diff Controller Test")
public class DiffControllerTest {
    @Mock
    private DiffService diffService;

    @Mock
    private SBOMFileService sbomFileService;

    @InjectMocks
    private DiffController diffController;

    ///
    /// Generate
    ///
    @Test
    @DisplayName("Generate diff report")
    void generate_diff_report() throws Exception {
        // Given
        Long[] ids = new Long[2];
        ids[0] = 0L;
        ids[1] = 1L;
        // When
        when(this.diffService.generateDiffReportAsJSON(sbomFileService, 0L, ids)).thenReturn("Diff Report");
        ResponseEntity<String> response = this.diffController.compare(0, ids);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Diff Report", response.getBody());
    }
}
