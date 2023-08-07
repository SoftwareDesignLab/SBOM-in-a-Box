package org.svip.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.entities.SBOMFile;
import org.svip.utils.Debug;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class DeleteFromAPITest extends APITest {

    private Map<Long, SBOMFile> testMap;

    public DeleteFromAPITest() throws IOException {
        testMap = getTestFileMap();
    }

    @Test
    public void deleteInvalidIDTest() {
        long id = 0;

        // Mock no ids in the database since delete doesn't return any confirmation
        when(oldRepository.findById(any(Long.class))).thenAnswer(i -> Optional.empty());

//        ResponseEntity<?> response = oldController.delete(id);
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void deleteValidIDTest() throws IOException {
        // Mock a map of test files in the DB
        when(oldRepository.findById(any(Long.class))).thenAnswer(i -> Optional.of(testMap.get(i.getArgument(0))));

        // Remove value from test map when id is valid
        doAnswer(i -> {
            SBOMFile toRemove = i.getArgument(0);

            testMap.values().remove(toRemove);
            return null;
        }).when(oldRepository).delete(any(SBOMFile.class));

        for (Long id : getTestFileMap().keySet()) { // Use copy of map to avoid concurrent modification
            Debug.log(Debug.LOG_TYPE.SUMMARY, "Deleting ID " + id);
//            ResponseEntity<?> response = oldController.delete(id);
//            assertEquals(id, response.getBody());
        }
    }
}
