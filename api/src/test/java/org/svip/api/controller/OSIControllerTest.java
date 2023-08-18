package org.svip.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    void setup() {
        assumeTrue(OSIController.isOSIEnabled()); // Only run tests if container API is accessible
    }

    @Test
    @DisplayName("/tools")
    void should_return_tool_list() throws Exception {
        mockMvc.perform(get("/svip/generators/osi/tools"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(11)));
    }

    @Test
    @DisplayName("Generate with /osi")
    void should_generate_sbom() throws Exception {
        mockMvc.perform(multipart("/svip/generators/osi/")
                        .file(buildMockMultipartFile("Rust_noEmptyFiles.zip"))
                        .param("projectName", "Rust")
                        .param("schema", String.valueOf(SerializerFactory.Schema.CDX14))
                        .param("format", String.valueOf(SerializerFactory.Format.JSON)))
                .andExpect(status().isOk());
    }

    private static MockMultipartFile buildMockMultipartFile(String filename) throws IOException {
        String sampleProjectDirectory = System.getProperty("user.dir") + "/src/test/resources/sample_projects/";

        return new MockMultipartFile("zipFile", filename, "multipart/form-data",
                Files.readAllBytes(Path.of(sampleProjectDirectory + filename)));
    }
}
