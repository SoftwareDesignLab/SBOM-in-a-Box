package org.svip.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbomgeneration.serializers.SerializerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class GenerateFromParserAPITest extends APITest {

    private final String[] schemas = {"CDX14", "SPDX23"};
    private final String[] formats = {"JSON", "TAGVALUE"};
    private static final Logger LOGGER = LoggerFactory.getLogger(SVIPApiController.class);
    private final Map<Map<Long, String>, SBOMFile[]> testMap;
    public GenerateFromParserAPITest() throws IOException {
        testMap = getTestProjectMap();
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
        SBOMFile[] empty = (SBOMFile[]) files.toArray()[2];

        assertEquals(HttpStatus.BAD_REQUEST, controller.generateParsers(noName,
                        "Java", SerializerFactory.Schema.SPDX23, SerializerFactory.Format.JSON).
                getStatusCode());

        assertEquals(HttpStatus.BAD_REQUEST, controller.generateParsers(noContents,
                        "cs", SerializerFactory.Schema.SPDX23, SerializerFactory.Format.JSON).
                getStatusCode());

        assertEquals(HttpStatus.BAD_REQUEST, controller.generateParsers(empty,
                        "empty", SerializerFactory.Schema.SPDX23, SerializerFactory.Format.JSON).
                getStatusCode());

    }

    /**
     * CDX does not support Tag Value format
     */
    @Test
    @DisplayName("Convert to CDX tag value test")
    public void CDXTagValueTest() {

        assertEquals(HttpStatus.BAD_REQUEST, controller.generateParsers((SBOMFile[]) testMap.entrySet().toArray()[0],
                        "Java", SerializerFactory.Schema.CDX14, SerializerFactory.Format.JSON).
                getStatusCode());
    }

    @Test
    @DisplayName("Generate from parser test")
    public void generateTest() {

        Collection<SBOMFile[]> files = testMap.values();

        long i = 0;
        for (SBOMFile[] file : files) {

            long projId = i*10L;
            HashMap<Long, String> projKey = (HashMap<Long, String>) testMap.keySet().toArray()[(int) i];
            String projectName = projKey.get(projId);

            LOGGER.info("Parsing project: " + projectName);

            for (String schema: schemas
                 ) {
                for (String format: formats
                     ) {

                    if(schema.equals("CDX14") && format.equals("TAGVALUE") || projectName.equals("Empty"))
                        continue;

                    if((projectName.equals("Java") || projectName.equals("CSharp/Nuget"))
                            && schema.equals("SPDX23") && format.equals("JSON")) // todo fix test
                        continue;

                    LOGGER.info("PARSING TO: " + schema + " + " + format);

                    ResponseEntity<?> response = controller.generateParsers(file, projectName,
                            SerializerFactory.Schema.valueOf(schema), SerializerFactory.Format.valueOf(format));

                    assertEquals(HttpStatus.OK, response.getStatusCode());
                }
            }
            i++;
            LOGGER.info("\n-------------\n");
        }
    }
}
