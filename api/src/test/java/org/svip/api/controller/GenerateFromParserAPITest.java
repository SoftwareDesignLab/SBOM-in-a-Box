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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GenerateFromParserAPITest extends APITest {

    private final String[] schemas = {"CDX14", "SPDX23"};
    private final String[] formats = {"JSON", "TAGVALUE"};
    private final String[] projectNames = {"Foo.java"};
    private static final Logger LOGGER = LoggerFactory.getLogger(SVIPApiController.class);
    private final Map<Long, SBOMFile[]> testMap;
    public GenerateFromParserAPITest() throws IOException {
        testMap = getTestProjectMap();
    }

    @Test
    @DisplayName("Generate from parser test")
    public void generateTest() {

        Collection<SBOMFile[]> files = testMap.values();

        long i = 0;
        for (SBOMFile[] file : files) {

            long projId = i*10L;

            LOGGER.info("Parsing project: " + projectNames[(int) i]);

            for (String schema: schemas
                 ) {
                for (String format: formats
                     ) {

                    if(schema.equals("CDX14") && format.equals("TAGVALUE"))
                        continue;

                    if(schema.equals("SPDX23") && format.equals("JSON")) // todo fix test
                        continue;

                    LOGGER.info("Parsing to: " + schema + " + " + format);

                    ResponseEntity<?> response = controller.generateParsers(file, projectNames[(int) i],
                            SerializerFactory.Schema.valueOf(schema), SerializerFactory.Format.valueOf(format));

                    assertEquals(HttpStatus.OK, response.getStatusCode());
                }
            }
            i++;
        }
    }
}
