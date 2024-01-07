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

    @Test
    @DisplayName("Generate diff report with exception")
    void generate_diff_report_with_exception() throws Exception {
        // Given
        Long[] ids = new Long[2];
        ids[0] = 0L;
        ids[1] = 1L;
        // When
        when(this.diffService.generateDiffReportAsJSON(sbomFileService, 0L, ids)).thenThrow(Exception.class);
        ResponseEntity<String> response = this.diffController.compare(0, ids);
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
