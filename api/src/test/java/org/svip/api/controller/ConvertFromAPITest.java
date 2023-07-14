package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;
import org.svip.api.utils.Utils;
import org.svip.sbomfactory.serializers.SerializerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.svip.sbomfactory.serializers.SerializerFactory.Format.TAGVALUE;

public class ConvertFromAPITest extends APITest{
    private Map<Long, SBOMFile> testMap;
    private static final Logger LOGGER = LoggerFactory.getLogger(SVIPApiController.class);

    public ConvertFromAPITest() throws IOException {
        testMap = getTestFileMap();
    }

    /**
     * Test bad requests regarding schema and format
     */
    @Test
    @DisplayName("Invalid format test")
    public void invalidSchemaAndFormatTest() throws JsonProcessingException {
        setupMockRepository();

        assertEquals(HttpStatus.BAD_REQUEST, controller.convert(0L, "123", "JSON", true).
                getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, controller.convert(0L, "SPDX23", "321", true).
                getStatusCode());

    }

    /**
     * CDX does not support Tag Value format
     */
    @Test
    @DisplayName("Convert to CDX tag value test")
    public void CDXTagValueTest() throws JsonProcessingException {
        setupMockRepository();

        assertEquals(HttpStatus.BAD_REQUEST, controller.convert(0L, "CDX14", "TAGVALUE", true).
                getStatusCode());
    }

    /**
     * Ensure that something goes wrong when trying to convert to the same format as the SBOMFile
     */
    @Test
    @DisplayName("Same format test")
    public void sameFormatTest() throws JsonProcessingException {
        setupMockRepository();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.convert(0L, "SPDX23", "TAGVALUE", true).
                getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.convert(6L, "CDX14", "JSON", true).
                getStatusCode());
    }

    /**
     * Rigorous test for /convert endpoint. Tests conversion to a valid schema + format, then tests converting back
     */
    @Test
    @DisplayName("Convert, then convert back to original schema and format")
    public void convertTest() throws JsonProcessingException {

        setupMockRepository();

        String[] schemas = {"CDX14", "SPDX23"};
        String[] formats = {"JSON", "TAGVALUE"};

        for (String convertToSchema: schemas
             ) {
            for (String convertToFormat: formats
                 ) {
                for (Long id : testMap.keySet()) {

                    SBOMFile sbom = testMap.get(id);
                    String testString = sbom.getContents().toLowerCase();

                    SerializerFactory.Schema thisSchema = (testString.contains("spdx")) ?
                            SerializerFactory.Schema.SPDX23 : SerializerFactory.Schema.CDX14;

                    if (Utils.convertTestController(convertToSchema, convertToFormat, id, thisSchema, testMap)) continue;

                    LOGGER.info("ID: " + id + " Converting " + thisSchema.name() + " --> " + convertToSchema);
                    LOGGER.info("From             " + ((sbom.getFileName()).contains("json")
                            ? "JSON" : "TAGVALUE") + " --> " + convertToFormat);
                    ResponseEntity<String> response = controller.convert(id, convertToSchema, convertToFormat,true);


                    String responseBody = response.getBody();

                    // check if OK
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertNotNull(responseBody);

                    // assert we can convert again
                    try{
                        String originalFormat = "JSON";
                        if (sbom.getContents().contains("DocumentName:") || sbom.getContents().contains("DocumentNamespace:"))
                            originalFormat = TAGVALUE.name();

                       assertEquals("", Utils.convert(new SBOMFile("convertBack." +
                        (originalFormat.equals("TAGVALUE") ? "json" : "spdx") ,

                            responseBody), thisSchema.name(), originalFormat).values().toArray()[0]);
                        LOGGER.info( "Reconversion successful!");

                    }catch (Exception e){

                        LOGGER.error("Cannot reconvert: " + e.getMessage());
                        fail();

                    }
                    LOGGER.info( "\n-------------\n");
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
