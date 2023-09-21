package org.svip.api.requests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.api.entities.SBOM;

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
            SBOM sbom = new UploadSBOMFileInput("CDX14_JSON", contents).toSBOMFile();
            // Then
            assertEquals("CDX14_JSON", sbom.getName());
            assertEquals(contents, sbom.getContent());
            assertEquals(SBOM.Schema.CYCLONEDX_14, sbom.getSchema());
            assertEquals(SBOM.FileType.JSON, sbom.getFileType());
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
            SBOM sbom = new UploadSBOMFileInput("SPDX23_JSON", contents).toSBOMFile();
            // Then
            assertEquals("SPDX23_JSON", sbom.getName());
            assertEquals(contents, sbom.getContent());
            assertEquals(SBOM.Schema.SPDX_23, sbom.getSchema());
            assertEquals(SBOM.FileType.JSON, sbom.getFileType());
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
            SBOM sbom = new UploadSBOMFileInput("SPDX23_TAG_VALUE", contents).toSBOMFile();
            // Then
            assertEquals("SPDX23_TAG_VALUE", sbom.getName());
            assertEquals(contents, sbom.getContent());
            assertEquals(SBOM.Schema.SPDX_23, sbom.getSchema());
            assertEquals(SBOM.FileType.TAG_VALUE, sbom.getFileType());
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
