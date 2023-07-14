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
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.svip.sbomfactory.serializers.SerializerFactory.Format.TAGVALUE;
import static org.svip.sbomfactory.serializers.SerializerFactory.Schema.SPDX23;

public class ConvertFromAPITest extends APITest{
    private Map<Long, SBOMFile> testMap;
    private static final Logger LOGGER = LoggerFactory.getLogger(SVIPApiController.class);

    public ConvertFromAPITest() throws IOException {
        testMap = getTestFileMap();
    }

    @Test
    @DisplayName("Convert Sbom")
    public void convertTest() throws JsonProcessingException {

        when(repository.findById(any(Long.class))).thenAnswer(i -> Optional.of(testMap.get(i.getArgument(0))));

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

                    if (testController(convertToSchema, convertToFormat, id, thisSchema)) continue;

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
     * For faster testing
     */
    private boolean testController(String convertToSchema, String convertToFormat, Long id, SerializerFactory.Schema thisSchema) {
        Long[] validTests = {0L,2L,6L,7L};
        boolean contains = false;
        for (Long l: validTests
             ) {
            if(Objects.equals(id, l)){
                contains = true;
                break;
            }
        }
        if(!contains)
            return true;

        // todo this is bad practice
        // todo implement these checks in the controller and return a bad request error

        // don't convert to the same schema
        if(thisSchema == SerializerFactory.Schema.SPDX23 && (convertToSchema.equals("SPDX23")))
            return true;
        if(thisSchema == SerializerFactory.Schema.CDX14 && (convertToSchema.equals("CDX14")))
            return true;
        // tagvalue format unsupported for cdx14
        if(convertToSchema.equals("CDX14") && convertToFormat.equals("TAGVALUE"))
            return true;
        // we don't support xml deserialization right now
        if(testMap.get(id).getContents().contains("xml"))
            return true;
        return false;
    }


}
