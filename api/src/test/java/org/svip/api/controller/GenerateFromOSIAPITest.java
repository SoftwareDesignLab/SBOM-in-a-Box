package org.svip.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.entities.SBOMFile;
import org.svip.api.entities.SBOMFile;
import org.svip.api.entities.SBOMFile;
import org.svip.generation.osi.OSI;
import org.svip.serializers.SerializerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * File: GenerateFromOSIAPITest.java
 * <p>
 * Holds the unit tests responsible for testing the OSI API endpoint.
 *
 * @author Ian Dunn
 */
public class GenerateFromOSIAPITest extends APITest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SVIPApiController.class);
    private final String sampleProjectDirectory = System.getProperty("user.dir")
            + "/src/test/resources/sample_projects/";

    private final Map<Map<Long, String>, SBOMFile[]> testMap;

    public GenerateFromOSIAPITest() throws IOException {
        testMap = getTestProjectMap();
    }

    @Override
    @BeforeEach
    public void setup() {
        // Use OSI.dockerCheck() to check if docker is running
        assumeTrue(OSI.dockerCheck() == 0);

        // Init controller with mocked repository and enable OSI
        oldController = new SVIPApiController(oldRepository, true);

        // Ensure controller was able to construct OSI
        assumeTrue(oldController.isOSIEnabled());
    }

    /**
     * Tests invalid projects
     */
    @Test
    @DisplayName("Invalid Project Test")
    public void invalidProjectTest() throws FileNotFoundException {

        String[] zipFiles = {"sampleProjectEmpty", "sampleProjectNullProperties"};

        assertEquals(HttpStatus.BAD_REQUEST, oldController.generateOSI(new MockMultipartFile(new File(
                        sampleProjectDirectory + zipFiles[0] + ".zip")), "Empty Project Folder",
                SerializerFactory.Schema.SPDX23, SerializerFactory.Format.JSON).getStatusCode());

        assertEquals(HttpStatus.BAD_REQUEST, oldController.generateOSI(new MockMultipartFile(new File(
                        sampleProjectDirectory + zipFiles[1] + ".zip")), "Empty C file",
                SerializerFactory.Schema.SPDX23, SerializerFactory.Format.JSON).getStatusCode());
    }

    /**
     * CDX does not support Tag Value format
     */
    @Test
    @DisplayName("Convert to CDX tag value test")
    public void CDXTagValueTest() throws IOException {
        assertEquals(HttpStatus.BAD_REQUEST, oldController.generateOSI((new MockMultipartFile(new File(
                                sampleProjectDirectory + "Scala.zip"))),
                        "Java", SerializerFactory.Schema.CDX14, SerializerFactory.Format.TAGVALUE).
                getStatusCode());
    }

    /**
     * File must be a zip file
     */
    @Test
    @DisplayName("Incorrect file type test")
    public void zipExceptionTest() throws IOException {
        assertEquals(HttpStatus.BAD_REQUEST, oldController.generateOSI((new MockMultipartFile(new File(
                                sampleProjectDirectory + "Ruby/lib/bar.rb"))),
                        "Java", SerializerFactory.Schema.CDX14, SerializerFactory.Format.JSON).
                getStatusCode());
    }

    /**
     * Main SBOM Generation test
     */
    @Test
    @DisplayName("Generate from OSI test")
    public void generateTest() throws IOException {
        // Mock repository output (returns SBOMFile that it received)
        when(oldRepository.save(any(SBOMFile.class))).thenAnswer(i -> i.getArgument(0));

        // TODO No C#, Perl, or Scala OSI tools
        String[] zipFiles = {"Conan", "Conda_noEmptyFiles", "Rust_noEmptyFiles"};

        for (String file : zipFiles
        ) {
            LOGGER.info("Parsing project: " + file);

            for (SerializerFactory.Schema schema : schemas
            ) {

                for (SerializerFactory.Format format : formats
                ) {

                    if (schema == SerializerFactory.Schema.CDX14 && format == SerializerFactory.Format.TAGVALUE)
                        continue;

                    LOGGER.info("Into " + schema + ((format == SerializerFactory.Format.TAGVALUE) ? ".spdx" : ".json"));

                    ResponseEntity<Long> response = (ResponseEntity<Long>) oldController.generateOSI(new MockMultipartFile(
                                    new File(sampleProjectDirectory + file + ".zip")), file,
                            schema, format);
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertNotNull(response.getBody());
                }

            }


        }

    }

}
