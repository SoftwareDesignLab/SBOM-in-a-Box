package org.svip.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MergeFromAPITest extends APITest {

    private final Map<Long, SBOMFile> testMap;
    private static final Logger LOGGER = LoggerFactory.getLogger(SVIPApiController.class);

    public MergeFromAPITest() throws IOException {
        testMap = getTestFileMap();
    }

    /**
     * Rigorous test for /merge endpoint. Tests conversion to a valid schema + format, then tests converting back
     */
    @Test
    @DisplayName("Comprehensive merge test")
    public void mergeTest() {

        setupMockRepository();

        for (Long id1 : testMap.keySet()) {

            SBOMFile sbom1 = testMap.get(id1);

            for(Long id2: testMap.keySet()){

                if(Objects.equals(id1, id2))
                    continue;

                SBOMFile sbom2 = testMap.get(id2);

                if(sbom1.getFileName().contains("xml") || sbom2.getFileName().contains("xml"))
                    continue;

                // todo no cross schema merging?

                LOGGER.info("MERGING " + sbom1.getFileName() + " and " +sbom2.getFileName());

                ResponseEntity<String> response = controller.merge(new long[] {id1, id2});
                String responseBody = response.getBody();

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(responseBody);

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
