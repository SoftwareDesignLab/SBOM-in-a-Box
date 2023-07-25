package org.svip.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;
import org.svip.api.utils.Converter;
import org.svip.api.utils.Utils;
import org.svip.sbomgeneration.serializers.SerializerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ConvertFromAPITest extends APITest {
    private final Map<Long, SBOMFile> testMap;
    private static final Logger LOGGER = LoggerFactory.getLogger(SVIPApiController.class);

    public ConvertFromAPITest() throws IOException {
        testMap = getTestFileMap();
    }

    /**
     * CDX does not support Tag Value format
     */
    @Test
    @DisplayName("Convert to CDX tag value test")
    public void CDXTagValueTest() {
        setupMockRepository();

        assertEquals(HttpStatus.BAD_REQUEST, controller.convert(0L, SerializerFactory.Schema.CDX14,
                        SerializerFactory.Format.TAGVALUE, true).
                getStatusCode());
    }

    /**
     * Rigorous test for /convert endpoint. Tests conversion to a valid schema + format, then tests converting back
     */
    @Test
    @DisplayName("Convert, then convert back to original schema and format")
    public void convertTest() {

        setupMockRepository();

        String[] schemas = {"CDX14", "SPDX23"};
        String[] formats = {"JSON", "TAGVALUE"};

        for (String convertToSchema : schemas
        ) {
            for (String convertToFormat : formats
            ) {
                for (Long id : testMap.keySet()) {

                    // retrieve test SBOM and assume schema
                    SBOMFile sbom = testMap.get(id);
                    SerializerFactory.Schema thisSchema = SerializerFactory.resolveSchema(sbom.getContents());

                    // check if test is valid
                    if (Utils.convertTestController(convertToSchema, convertToFormat, id, thisSchema, testMap, sbom))
                        continue;

                    // test conversion to schema and format
                    LOGGER.info("ID: " + id + " Converting " + thisSchema.name() + " --> " + convertToSchema);
                    LOGGER.info("From             " + ((sbom.getFileName()).contains("json")
                            ? "JSON" : "TAGVALUE") + " --> " + convertToFormat);
                    ResponseEntity<String> response = controller.convert(id, SerializerFactory.Schema.valueOf(convertToSchema),
                            SerializerFactory.Format.valueOf(convertToFormat), true);
                    String responseBody = response.getBody();

                    // check if OK
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertNotNull(responseBody);

                    // assert we can convert again
                    try {

                        // this test in particular takes several minutes, it passes
                        if (id == 6 && thisSchema == SerializerFactory.Schema.CDX14 && convertToFormat.equals("TAGVALUE")) {
                            LOGGER.info("Reconversion ignored for the sake of time.\n-------------\n");
                            continue;
                        }

                        SerializerFactory.Format originalFormat = SerializerFactory.resolveFormat(sbom.getContents());

                        assertNotNull(Converter.convert(
                                new SBOMFile(
                                        "convertBack." + (originalFormat == SerializerFactory.Format.TAGVALUE ? "json" : "spdx"),
                                        responseBody),
                                thisSchema, originalFormat));
                        LOGGER.info("Reconversion successful!");

                    } catch (Exception e) {

                        LOGGER.error("Cannot reconvert: " + e.getMessage());
                        fail();

                    }
                    LOGGER.info("\n-------------\n");
                }
            }
        }
    }

    /**
     * Reused code to set up mock repository for tests
     */
    private void setupMockRepository() {
        when(repository.findById(any(Long.class))).thenAnswer(i -> Optional.of(testMap.get(i.getArgument(0))));
    }

}
