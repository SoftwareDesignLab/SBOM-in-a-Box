package org.svip.api.controller;

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
import org.svip.api.services.SBOMFileService;
import org.svip.serializers.SerializerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
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

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() throws Exception {
        assumeTrue(OSIController.isOSIEnabled()); // Only run tests if container API is accessible
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
        mockMvc.perform(multipart("/svip/generators/osi/")
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
        mockMvc.perform(multipart("/svip/generators/osi/")
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
        mockMvc.perform(multipart("/svip/generators/osi/")
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
        mockMvc.perform(multipart("/svip/generators/osi/")
                        .file(buildMockMultipartFile(projectName))
                        .param("projectName", projectName)
                        .param("schema", String.valueOf(SerializerFactory.Schema.CDX14))
                        .param("format", String.valueOf(SerializerFactory.Format.JSON)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Incorrect File Type")
    void generateWithIncorrectFileTypeTest() throws Exception {
        mockMvc.perform(multipart("/svip/generators/osi/")
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
        mockMvc.perform(multipart("/svip/generators/osi/")
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
