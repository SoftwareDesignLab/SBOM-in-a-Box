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

package org.svip.api.requests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.api.entities.SBOMFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * File: SBOMFileInputTest.java
 * Description: SBOM input unit tests
 *
 * @author Derek Garcia
 */
@DisplayName("SBOM File Input Test")
public class UploadSBOMFileInputTest {

    // Test SBOMs
    private static final String CDX_JSON_SBOM_FILE = "./src/test/resources/sample_sboms/cdx-gomod-1.4.0-bin.json";
    private static final String SPDX_JSON_SBOM_FILE = "./src/test/resources/sample_sboms/syft-0.80.0-source-spdx-json.json";
    private static final String SPDX_TAG_VALUE_SBOM_FILE = "./src/test/resources/sample_sboms/sbom.alpine-compare.2-3.spdx";
    
    @Test
    @DisplayName("Create CDX14 JSON SBOM")
    void create_new_CDX14_JSON_SBOM_FILE(){
        try{
            // Given
            String contents = fileToContents(CDX_JSON_SBOM_FILE);
            // When
            SBOMFile sbomFile = new UploadSBOMFileInput("CDX14_JSON", contents).toSBOMFile();
            // Then
            assertEquals("CDX14_JSON", sbomFile.getName());
            assertEquals(contents, sbomFile.getContent());
            assertEquals(SBOMFile.Schema.CYCLONEDX_14, sbomFile.getSchema());
            assertEquals(SBOMFile.FileType.JSON, sbomFile.getFileType());
        } catch (IOException e){
            fail("Failed to parse file: " + CDX_JSON_SBOM_FILE);
        } catch (Exception e){
            fail("Valid CDX14 SBOM");
        }
    }

    @Test
    @DisplayName("Create SPDX23 JSON SBOM")
    void create_new_SPDX23_JSON_SBOM_FILE(){
        try{
            // Given
            String contents = fileToContents(SPDX_JSON_SBOM_FILE);
            // When
            SBOMFile sbomFile = new UploadSBOMFileInput("SPDX23_JSON", contents).toSBOMFile();
            // Then
            assertEquals("SPDX23_JSON", sbomFile.getName());
            assertEquals(contents, sbomFile.getContent());
            assertEquals(SBOMFile.Schema.SPDX_23, sbomFile.getSchema());
            assertEquals(SBOMFile.FileType.JSON, sbomFile.getFileType());
        } catch (IOException e){
            fail("Failed to parse file: " + SPDX_JSON_SBOM_FILE);
        } catch (Exception e){
            fail("Valid SPDX23 SBOM");
        }
    }

    @Test
    @DisplayName("Create SPDX23 TAG VALUE SBOM")
    void create_new_SPDX23_TAGVALUE_SBOM_FILE(){
        try{
            // Given
            String contents = fileToContents(SPDX_TAG_VALUE_SBOM_FILE);
            // When
            SBOMFile sbomFile = new UploadSBOMFileInput("SPDX23_TAG_VALUE", contents).toSBOMFile();
            // Then
            assertEquals("SPDX23_TAG_VALUE", sbomFile.getName());
            assertEquals(contents, sbomFile.getContent());
            assertEquals(SBOMFile.Schema.SPDX_23, sbomFile.getSchema());
            assertEquals(SBOMFile.FileType.TAG_VALUE, sbomFile.getFileType());
        } catch (IOException e){
            fail("Failed to parse file: " + SPDX_TAG_VALUE_SBOM_FILE);
        } catch (Exception e){
            fail("Valid SPDX23 SBOM");
        }
    }
    
    ///
    /// Helper Methods
    ///
    private String fileToContents(String filepath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filepath)));
    }
}
