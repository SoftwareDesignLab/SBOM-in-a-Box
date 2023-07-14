package org.svip.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;

import java.util.List;

/**
 * A static class containing helpful utilities for API calls and testing responses.
 *
 * @author Ian Dunn
 * @author Juan Francisco Patino
 */
public class Utils {
    /**
     * Checks an array of {@code SBOMFile} objects for one containing any null properties.
     *
     * @param arr The array of {@code SBOMFile} objects to check.
     * @return -1 if no SBOMFiles have null properties. Otherwise, return the index of the first SBOM with null
     * properties.
     */
    public static int sbomFileArrNullCheck(SBOMFile[] arr){
        for (int i = 0; i < arr.length; i++) {
            SBOMFile file = arr[i];
            if(file == null || file.hasNullProperties())
                return i;
        }

        return -1;
    }

    /**
     * Utility method to encode a response into a {@code ResponseEntity} of the same generic type. If there is an error
     * encoding the response, an empty ResponseEntity with the {@code HttpStatus.INTERNAL_SERVER_ERROR} code will be
     * thrown.
     *
     * @param response The response to encode.
     * @return The encoded ResponseEntity. Null if the response is null or if there was an error encoding.
     */
    public static <T> ResponseEntity<T> encodeResponse(T response) {
        if(response == null) return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        try {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static SBOMFile[] fromJSONString(String fileNames, String contents) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        List<String> contentsList = objectMapper.readValue(contents, typeFactory.constructCollectionType(List.class, String.class));
        List<String> filenamesList = objectMapper.readValue(fileNames, typeFactory.constructCollectionType(List.class, String.class));

        SBOMFile[] arr = new SBOMFile[contentsList.size()];

        for (int i = 0; i < contentsList.size(); i++) {

            arr[i] = new SBOMFile(filenamesList.get(i), contentsList.get(i));

        }

        return arr;

    }
}
