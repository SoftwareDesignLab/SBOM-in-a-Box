package org.svip.api.controller;

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
    public void convertTest(){

        when(repository.findById(any(Long.class))).thenAnswer(i -> Optional.of(testMap.get(i.getArgument(0))));

        String[] schemas = {"spdx", "cdx"}; // temp

        for (String schema: schemas
             ) {
            for (Long id : testMap.keySet()) {
                ResponseEntity<String> response = controller.convert(id, schema);
                assertEquals(testMap.get(id).getContents(), response.getBody());
                assertTrue(Objects.requireNonNull(response.getBody()).contains(schema));
            }
        }

    }


}
