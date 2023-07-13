package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;
import org.svip.sbomfactory.serializers.SerializerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

        String[] schemas = {"CDX14", "SPDX23", "SVIP"};
        String[] formats = {"JSON", "TAGVALUE"};

        for (String schema: schemas
             ) {
            for (String format: formats
                 ) {
                for (Long id : testMap.keySet()) {

                    String testString = testMap.get(id).getContents().toLowerCase();

                    SerializerFactory.Schema thisSchema = (testString.contains("spdx")) ? SerializerFactory.Schema.SPDX23 : SerializerFactory.Schema.CDX14;

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
                        continue;

                    // don't convert to the same schema
                    if(thisSchema == SerializerFactory.Schema.SPDX23 && (schema.equals("SPDX23") || schema.equals("SVIP")))
                        continue; // todo implement this check in /convert and return a bad request error
                    if(thisSchema == SerializerFactory.Schema.CDX14 && (schema.equals("CDX14")))
                        continue;

                    // we don't support xml deserialization right now
                    if(testMap.get(id).getContents().contains("xml"))
                        continue;

                    LOGGER.info("ID: " + id + " Converting " + thisSchema.name() + " --> " + schema);

                    ResponseEntity<String> response = controller.convert(id, schema, format,true);
                    String res = response.getBody();
                    int x = 0;

                    if(response.getStatusCode() == HttpStatus.OK)
                        LOGGER.info("HTTP STATUS OK");
                    if(response.getBody() != null)
                        LOGGER.info("NOT NULL");

                    //assertEquals(HttpStatus.OK, response.getStatusCode());
                    //assertEquals(testMap.get(id).getContents(), response.getBody());
                }
            }
        }
    }


}
