package org.svip.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;
import org.svip.sbomgeneration.serializers.SerializerFactory;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GenerateFromParserAPITest extends APITest {

    private final String[] schemas = {"CDX14", "SPDX23"};
    private final String[] formats = {"JSON", "TAGVALUE"};

    @Test
    @DisplayName("Generate from parser test")
    public void generateTest() throws IOException {
        ArrayList<SBOMFile[]> files = (ArrayList<SBOMFile[]>) getTestProjectMap().values();

        // Mock repository output (returns SBOMFile that it recieived)
        when(repository.save(any(SBOMFile.class))).thenAnswer(i -> i.getArgument(0));

        long i = 0;
        for (SBOMFile[] file : files) {

            long projId = i*10L;

            for (String schema: schemas
                 ) {
                for (String format: formats
                     ) {
                    ResponseEntity<?> response = controller.generateParsers(file, "Foo.java",
                            SerializerFactory.Schema.valueOf(schema), SerializerFactory.Format.valueOf(format));
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                }
            }
            i++;
        }
    }
}
