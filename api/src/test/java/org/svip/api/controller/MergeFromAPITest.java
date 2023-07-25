package org.svip.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;
import org.svip.api.utils.Utils;
import org.svip.sbomgeneration.serializers.SerializerFactory;

import java.io.IOException;
import java.util.*;

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
     * Rigorous test for /merge endpoint
     */
    @Test
    @DisplayName("Comprehensive merge test")
    public void mergeTest() {

        setupMockRepository();
        ArrayList<HashMap<Long, Long>> testedPairs = new ArrayList<>();

        for (Long id1 : testMap.keySet()) {

            SBOMFile sbom1 = testMap.get(id1);
            SerializerFactory.Schema schema1 = SerializerFactory.resolveSchema(sbom1.getContents());

            for (Long id2 : testMap.keySet()) {

                if (Objects.equals(id1, id2))
                    continue;

                SBOMFile sbom2 = testMap.get(id2);
                SerializerFactory.Schema schema2 = SerializerFactory.resolveSchema(sbom2.getContents());

                if (sbom1.getFileName().endsWith(".xml") || sbom2.getFileName().endsWith(".xml"))
                    continue;

                // to prevent testing different combinations of the same two SBOMs
                boolean ignoreTest = false;
                for (HashMap<Long, Long> pair : testedPairs
                ) {

                    if (pair.get(id2) == null)
                        continue;

                    if (pair.get(id2).equals(id1)) {
                        ignoreTest = true;
                        break;
                    }

                }
                if (ignoreTest)
                    continue;

                HashMap<Long, Long> thisPair = new HashMap<>();
                thisPair.put(id1, id2);
                testedPairs.add(thisPair);

                LOGGER.info("MERGING " + schema1 + " SBOM " + sbom1.getId() + "..." +
                        sbom1.getFileName().substring(sbom1.getFileName().length() * 2 / 3) +
                        " and " + schema2 + " SBOM " + sbom2.getId() + "..." +
                        sbom2.getFileName().substring(sbom2.getFileName().length() * 2 / 3));

                ResponseEntity<String> response = controller.merge(new long[]{id1, id2});
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
