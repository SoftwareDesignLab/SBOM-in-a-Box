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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.svip.api.services.OSIService;
import org.svip.api.services.SBOMFileService;
import org.svip.serializers.SerializerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * File: OSIControllerTest.java
 * Description: OSI controller unit tests
 *
 * @author Ian Dunn
 */
@WebMvcTest(OSIController.class)
@DisplayName("OSI Controller Test")
public class OSIControllerTest {

    @MockBean
    private SBOMFileService sbomFileService;

    @MockBean
    private OSIService osiService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        assumeTrue(osiService.isEnabled()); // Only run tests if container API is accessible
    }

    @Test
    @DisplayName("Get tool list")
    void getToolListTest() throws Exception {
        mockMvc.perform(get("/svip/generators/osi/tools"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(11)));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Rust_noEmptyFiles" })
    @DisplayName("Generate with default tools")
    void generateWithDefaultToolsTest(String projectName) throws Exception {
        mockMvc.perform(multipart("/svip/generators/osi")
                        .file(buildMockMultipartFile(projectName))
                        .param("projectName", projectName)
                        .param("schema", String.valueOf(SerializerFactory.Schema.CDX14))
                        .param("format", String.valueOf(SerializerFactory.Format.JSON)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = { "Conda_noEmptyFiles" })
    @DisplayName("Generate with invalid tool")
    void generateWithInvalidToolTest(String projectName) throws Exception {
        mockMvc.perform(multipart("/svip/generators/osi")
                        .file(buildMockMultipartFile(projectName))
                        .param("projectName", projectName)
                        .param("schema", String.valueOf(SerializerFactory.Schema.CDX14))
                        .param("format", String.valueOf(SerializerFactory.Format.JSON))
                        .param("toolNames", "JBOM"))
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @ValueSource(strings = { "Java" })
    @DisplayName("Generate with valid tool")
    void generateWithValidToolTest(String projectName) throws Exception {
        mockMvc.perform(multipart("/svip/generators/osi")
                        .file(buildMockMultipartFile(projectName))
                        .param("projectName", projectName)
                        .param("schema", String.valueOf(SerializerFactory.Schema.CDX14))
                        .param("format", String.valueOf(SerializerFactory.Format.JSON))
                        .param("toolNames", "Syft SPDX"))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = { "sampleProjectEmpty", "sampleProjectNullProperties" })
    @DisplayName("Empty Projects")
    void generateWithInvalidProjectTest(String projectName) throws Exception {
        mockMvc.perform(multipart("/svip/generators/osi")
                        .file(buildMockMultipartFile(projectName))
                        .param("projectName", projectName)
                        .param("schema", String.valueOf(SerializerFactory.Schema.CDX14))
                        .param("format", String.valueOf(SerializerFactory.Format.JSON)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Incorrect File Type")
    void generateWithIncorrectFileTypeTest() throws Exception {
        mockMvc.perform(multipart("/svip/generators/osi")
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
        mockMvc.perform(multipart("/svip/generators/osi")
                        .file(buildMockMultipartFile(projectName))
                        .param("projectName", projectName)
                        .param("schema", String.valueOf(SerializerFactory.Schema.CDX14))
                        .param("format", String.valueOf(SerializerFactory.Format.TAGVALUE)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = { "Rust_noEmptyFiles" })
    @DisplayName("Invalid Conversion")
    void generateWithInvalidConversionTest(String projectName) throws Exception {
        when(sbomFileService.convert(any(), any(), any(), any())).thenThrow(JsonProcessingException.class);

        mockMvc.perform(multipart("/svip/generators/osi")
                        .file(buildMockMultipartFile(projectName))
                        .param("projectName", projectName)
                        .param("schema", String.valueOf(SerializerFactory.Schema.CDX14))
                        .param("format", String.valueOf(SerializerFactory.Format.JSON)))
                .andExpect(status().isInternalServerError());
    }

    @ParameterizedTest
    @ValueSource(strings = { "Rust_noEmptyFiles" })
    @DisplayName("Invalid Upload")
    void generateWithInvalidUploadTest(String projectName) throws Exception {
        when(sbomFileService.upload(any())).thenThrow(JsonProcessingException.class);

        mockMvc.perform(multipart("/svip/generators/osi")
                        .file(buildMockMultipartFile(projectName))
                        .param("projectName", projectName)
                        .param("schema", String.valueOf(SerializerFactory.Schema.CDX14))
                        .param("format", String.valueOf(SerializerFactory.Format.JSON)))
                .andExpect(status().isInternalServerError());
    }

    @ParameterizedTest
    @ValueSource(strings = { "Rust_noEmptyFiles" })
    @DisplayName("Invalid Merge")
    void generateWithInvalidMergeTest(String projectName) throws Exception {
        when(sbomFileService.merge(any())).thenThrow(Exception.class);

        mockMvc.perform(multipart("/svip/generators/osi")
                        .file(buildMockMultipartFile(projectName))
                        .param("projectName", projectName)
                        .param("schema", String.valueOf(SerializerFactory.Schema.CDX14))
                        .param("format", String.valueOf(SerializerFactory.Format.JSON)))
                .andExpect(status().isNoContent());
    }

    private static MockMultipartFile buildMockMultipartFile(String projectName) throws IOException {
        String sampleProjectDirectory = System.getProperty("user.dir") + "/src/test/resources/sample_projects/";

        return new MockMultipartFile("zipFile", projectName, "multipart/form-data",
                Files.readAllBytes(Path.of(sampleProjectDirectory + projectName + ".zip")));
    }
}
