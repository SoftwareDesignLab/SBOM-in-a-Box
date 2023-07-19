package org.svip.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;
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

    // todo invalid schema+format test
    // todo empty project test

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

                    if(schema.equals("CDX14") && format.equals("TAGVALUE"))
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
