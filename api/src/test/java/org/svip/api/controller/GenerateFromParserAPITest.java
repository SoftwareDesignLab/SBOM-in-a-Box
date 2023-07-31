package org.svip.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.MockMultipartFile;
import org.svip.api.model.SBOMFile;
import org.svip.sbomgeneration.serializers.SerializerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GenerateFromParserAPITest extends APITest {

    private final Map<Map<Long, String>, SBOMFile[]> testMap;
    private static final Logger LOGGER = LoggerFactory.getLogger(SVIPApiController.class);

    public GenerateFromParserAPITest() throws IOException {
        testMap = getTestProjectMap();
    }

    /**
     * CDX does not support Tag Value format
     */
    @Test
    @DisplayName("Convert to CDX tag value test")
    public void CDXTagValueTest() throws IOException {
        assertEquals(HttpStatus.BAD_REQUEST, controller.generateParsers((new MockMultipartFile(new File(System.getProperty("user.dir")
                                + "/src/test/java/org/svip/api/sample_projects/Scala.zip"))),
                        "Java", SerializerFactory.Schema.CDX14, SerializerFactory.Format.TAGVALUE).
                getStatusCode());
    }

    /**
     * File must be a zip file
     */
    @Test
    @DisplayName("Incorrect file type test")
    public void zipExceptionTest() throws IOException {
        assertEquals(HttpStatus.BAD_REQUEST, controller.generateParsers((new MockMultipartFile(new File(System.getProperty("user.dir")
                                + "/src/test/java/org/svip/api/sample_projects/Rust/lib/bar.rs"))),
                        "Java", SerializerFactory.Schema.CDX14, SerializerFactory.Format.JSON).
                getStatusCode());
    }


    @Test
    @DisplayName("Generate from parser test")
    public void generateTest() throws IOException {

        String[] zipFiles = {
                "Conan", "Conda_noEmptyFiles",
                // "Go", // todo Go doesn't work atm
                "Perl_noEmptyFiles", "Rust_noEmptyFiles", "Scala"};

        for (String file : zipFiles
        ) {

            LOGGER.info("Parsing project: " + file);

            ResponseEntity<Long> response = (ResponseEntity<Long>) controller.generateParsers(new MockMultipartFile(new File(System.getProperty("user.dir")
                            + "/src/test/java/org/svip/api/sample_projects/" + file + ".zip")), file,
                    SerializerFactory.Schema.SPDX23, SerializerFactory.Format.TAGVALUE);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

        }

    }

}
