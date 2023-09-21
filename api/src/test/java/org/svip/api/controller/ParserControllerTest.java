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
import org.svip.api.entities.SBOM;
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
    @ValueSource(strings = { "Conan", "Conda_noEmptyFiles", "Java", "Perl_noEmptyFiles", "Rust_noEmptyFiles",
            "Scala" })
    @DisplayName("Generate SBOMs")
    void generateTest(String projectName) throws Exception {
        mockMvc.perform(multipart("/svip/generators/parsers/")
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
        when(sbomFileService.upload(any(SBOM.class))).thenThrow(Exception.class);

        mockMvc.perform(multipart("/svip/generators/parsers/")
                        .file(buildMockMultipartFile(projectName))
                        .param("projectName", projectName)
                        .param("schema", String.valueOf(SerializerFactory.Schema.CDX14))
                        .param("format", String.valueOf(SerializerFactory.Format.JSON)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Incorrect File Type")
    void generateWithIncorrectFileTypeTest() throws Exception {
        mockMvc.perform(multipart("/svip/generators/parsers/")
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
        mockMvc.perform(multipart("/svip/generators/parsers/")
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
