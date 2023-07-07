package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;

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

    public ConvertFromAPITest() throws IOException {
        testMap = getTestFileMap();
    }

    @Test
    @DisplayName("Convert Sbom")
    public void convertTest() throws JsonProcessingException {

        when(repository.findById(any(Long.class))).thenAnswer(i -> Optional.of(testMap.get(i.getArgument(0))));

        String[] schemas = {"CDX14", "SPDX23", "SVIP"}; // temp
        String[] formats = {"JSON", "TAGVALUE"};

        for (String schema: schemas
             ) {
            for (String format: formats
                 ) {
                for (Long id : testMap.keySet()) {
                    ResponseEntity<String> response = controller.convert(id, schema, format,true);
                    assertEquals(testMap.get(id).getContents(), response.getBody());
                }
            }
        }
    }


}
