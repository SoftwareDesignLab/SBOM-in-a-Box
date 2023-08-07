package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.entities.SBOMFile;
import org.svip.api.utils.Utils;
import org.svip.sbom.builder.SBOMBuilderException;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.exceptions.DeserializerException;
import org.svip.serializers.exceptions.SerializerException;

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
        assertThrows(SerializerException.class, () -> controller.convert(0L, SerializerFactory.Schema.CDX14,
                SerializerFactory.Format.TAGVALUE, true));
    }

    /**
     * Rigorous test for /convert endpoint. Tests conversion to a valid schema + format, then tests converting back
     */
    @Test
    @DisplayName("Convert, then convert back to original schema and format")
    public void convertTest() {

        setupMockRepository();

        SerializerFactory.Schema[] schemas = {SerializerFactory.Schema.CDX14, SerializerFactory.Schema.SPDX23};
        SerializerFactory.Format[] formats = {SerializerFactory.Format.JSON, SerializerFactory.Format.TAGVALUE};

        for (SerializerFactory.Schema convertToSchema : schemas
        ) {
            for (SerializerFactory.Format convertToFormat : formats
            ) {
                for (Long id : testMap.keySet()) {

                    // retrieve test SBOM and assume schema
                    SBOMFile sbom = testMap.get(id);
                    SerializerFactory.Schema thisSchema = SerializerFactory.resolveSchema(sbom.getContents());

                    // check if test is valid
                    if (Utils.convertTestController(convertToSchema, convertToFormat, id,
                            thisSchema, testMap, sbom)) continue;

                    // test conversion to schema and format
                    LOGGER.info("ID: " + id + " Converting " + thisSchema.name() + " --> " + convertToSchema);
                    LOGGER.info("From             " + ((sbom.getFileName()).contains("json")
                            ? "JSON" : "TAGVALUE") + " --> " + convertToFormat);

                    ResponseEntity<?> resposeObject = controller.convert(id, convertToSchema, convertToFormat, false);
                    assertInstanceOf(Long.class, resposeObject.getBody());

                    ResponseEntity<Long> response = (ResponseEntity<Long>) resposeObject;
                    Long responseBody = response.getBody();

                    // check if OK
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertNotNull(responseBody);

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
