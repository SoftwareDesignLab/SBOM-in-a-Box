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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.svip.api.entities.SBOMFile;
import org.svip.api.services.SBOMFileService;
import org.svip.serializers.SerializerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * File: ParserControllerTest.java
 * Description: Parser controller unit tests
 *
 * @author Ian Dunn
 */
@WebMvcTest(ParserController.class)
@DisplayName("Parser Controller Test")
public class ParserControllerTest {

    @MockBean
    private SBOMFileService sbomFileService;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = { "Conan", "Java", "Perl_noEmptyFiles", "Rust_noEmptyFiles",
            "Scala" })
    @DisplayName("Generate SBOMs")
    void generateTest(String projectName) throws Exception {
        mockMvc.perform(multipart("/svip/generators/parsers")
                        .file(buildMockMultipartFile(projectName))
                        .param("projectName", projectName)
                        .param("schema", String.valueOf(SerializerFactory.Schema.CDX14))
                        .param("format", String.valueOf(SerializerFactory.Format.JSON)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = { "Conan" })
    @DisplayName("Invalid Upload Test")
    void generateWithInvalidUploadTest(String projectName) throws Exception {
        when(sbomFileService.upload(any(SBOMFile.class))).thenThrow(Exception.class);

        mockMvc.perform(multipart("/svip/generators/parsers")
                        .file(buildMockMultipartFile(projectName))
                        .param("projectName", projectName)
                        .param("schema", String.valueOf(SerializerFactory.Schema.CDX14))
                        .param("format", String.valueOf(SerializerFactory.Format.JSON)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Incorrect File Type")
    void generateWithIncorrectFileTypeTest() throws Exception {
        mockMvc.perform(multipart("/svip/generators/parsers")
                        .file(new MockMultipartFile("zipFile",
                                Files.readAllBytes(Path.of(
                                        System.getProperty("user.dir") + "/src/test/resources/sample_projects/Ruby/lib/bar.rb"
                                ))))
                        .param("projectName", "Ruby")
                        .param("schema", String.valueOf(SerializerFactory.Schema.CDX14))
                        .param("format", String.valueOf(SerializerFactory.Format.JSON)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = { "Go" })
    @DisplayName("Convert to CDX tag value")
    void generateWithCDXTagValueTest(String projectName) throws Exception {
        mockMvc.perform(multipart("/svip/generators/parsers")
                        .file(buildMockMultipartFile(projectName))
                        .param("projectName", projectName)
                        .param("schema", String.valueOf(SerializerFactory.Schema.CDX14))
                        .param("format", String.valueOf(SerializerFactory.Format.TAGVALUE)))
                .andExpect(status().isBadRequest());
    }

    private static MockMultipartFile buildMockMultipartFile(String projectName) throws IOException {
        String sampleProjectDirectory = System.getProperty("user.dir") + "/src/test/resources/sample_projects/";

        return new MockMultipartFile("zipFile", projectName, "multipart/form-data",
                Files.readAllBytes(Path.of(sampleProjectDirectory + projectName + ".zip")));
    }
}
