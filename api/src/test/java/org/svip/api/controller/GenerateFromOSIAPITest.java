package org.svip.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;
import org.svip.sbomgeneration.osi.OSI;
import org.svip.sbomgeneration.serializers.SerializerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * File: GenerateFromOSIAPITest.java
 *
 * Holds the unit tests responsible for testing the OSI API endpoint.
 *
 * @author Ian Dunn
 */
public class GenerateFromOSIAPITest extends APITest {

    private final SerializerFactory.Schema[] schemas = {SerializerFactory.Schema.CDX14, SerializerFactory.Schema.SPDX23};
    private final SerializerFactory.Format[] formats = {SerializerFactory.Format.JSON, SerializerFactory.Format.TAGVALUE};
    private static final Logger LOGGER = LoggerFactory.getLogger(SVIPApiController.class);
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
        controller = new SVIPApiController(repository, true);

        // Ensure controller was able to construct OSI
        assumeTrue(controller.isOSIEnabled());
    }

    /**
     * Tests bad SBOMFiles
     */
    @Test
    @DisplayName("Invalid format test")
    public void sbomFilesNullPropertiesTest() {
        SBOMFile[] noName = new SBOMFile[]{new SBOMFile("", "int i = 3;")};
        SBOMFile[] noContents = new SBOMFile[]{new SBOMFile("name.java", "")};

        Collection<SBOMFile[]> files = testMap.values();
        SBOMFile[] empty = null;

        for (SBOMFile[] file: files)
            for (SBOMFile s: file)
                if(s.hasNullProperties()) {
                    empty = file;
                    break;
                }

        assertEquals(HttpStatus.BAD_REQUEST, controller.generateOSI(noName, "NoName",
                        SerializerFactory.Schema.SPDX23, SerializerFactory.Format.JSON).getStatusCode());

        assertEquals(HttpStatus.BAD_REQUEST, controller.generateOSI(noContents, "NoContents",
                        SerializerFactory.Schema.SPDX23, SerializerFactory.Format.JSON).getStatusCode());

        assertEquals(HttpStatus.BAD_REQUEST, controller.generateOSI(empty, "Empty",
                        SerializerFactory.Schema.SPDX23, SerializerFactory.Format.JSON).getStatusCode());
    }

    /**
     * CDX does not support Tag Value format
     */
    @Test
    @DisplayName("Convert to CDX tag value test")
    public void CDXTagValueTest() {
        Collection<SBOMFile[]> files = testMap.values();
        SBOMFile[] sbomFiles = (SBOMFile[]) files.toArray()[0];
        assertEquals(HttpStatus.BAD_REQUEST, controller.generateOSI(sbomFiles, "CDXTagValue",
                        SerializerFactory.Schema.CDX14, SerializerFactory.Format.TAGVALUE).getStatusCode());
    }

    @Test
    @DisplayName("Generate from OSI test")
    public void generateTest() {
        // Mock repository output (returns SBOMFile that it received)
        when(repository.save(any(SBOMFile.class))).thenAnswer(i -> i.getArgument(0));

        Collection<SBOMFile[]> files = testMap.values();

        int i = 0;
        for (SBOMFile[] file : files) {
            HashMap<Long, String> projKey = (HashMap<Long, String>) testMap.keySet().toArray()[i];
            String projectName = (String) projKey.values().toArray()[0];

            LOGGER.info("Parsing project: " + projectName);

            for (SerializerFactory.Schema schema : schemas) {
                for (SerializerFactory.Format format : formats) {
                    if (schema == SerializerFactory.Schema.CDX14 && format == SerializerFactory.Format.TAGVALUE
                            || projectName.equals("Empty"))
                        continue;

                    LOGGER.info("PARSING TO: " + schema + " + " + format);

                    ResponseEntity<?> response = controller.generateOSI(file, projectName, schema, format);

                    if (!projectName.contains("CSharp")) {
                        assertEquals(HttpStatus.OK, response.getStatusCode());
                        assertNotNull(response.getBody());
                        // TODO more assertions
                    } else assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
                }
            }

            i++;
            LOGGER.info("\n-------------\n");
        }
    }
}
