package org.svip.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.controller.SVIPApiController;
import org.svip.api.model.SBOMFile;
import org.svip.sbomgeneration.serializers.SerializerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.svip.sbomgeneration.serializers.SerializerFactory.Format.TAGVALUE;

/**
 * A static class containing helpful utilities for API calls and testing responses.
 *
 * @author Ian Dunn
 * @author Juan Francisco Patino
 */
public class Utils {

    /**
     * Spring-configured logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SVIPApiController.class);

    /**
     * Utility method to encode a response into a {@code ResponseEntity} of the same generic type. If there is an error
     * encoding the response, an empty ResponseEntity with the {@code HttpStatus.INTERNAL_SERVER_ERROR} code will be
     * thrown.
     *
     * @param response The response to encode.
     * @return The encoded ResponseEntity. Null if the response is null or if there was an error encoding.
     */
    public static <T> ResponseEntity<T> encodeResponse(T response) {
        if (response == null) return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        try {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Reusable code used in API controller to check if SBOMFile is empty/not found
     * Also eliminates the need for isPresent() check for Optionals
     */
    public static ResponseEntity<String> checkIfExists(long id, Optional<SBOMFile> sbomFile, String call) {
        if (sbomFile.isEmpty()) {
            LOGGER.info("DELETE /svip/" + call + "?id=" + id + " - FILE NOT FOUND. INVALID ID");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return null;
    }

    /**
     * Helper function for ConvertFromAPITest
     *
     * @return whether it is valid to proceed with this test
     */
    public static boolean convertTestController(String convertToSchema, String convertToFormat, Long id,
                                                SerializerFactory.Schema thisSchema, Map<Long, SBOMFile> testMap,
                                                SBOMFile original) {
        Long[] validTests = {0L, 2L, 6L, 7L};
        boolean contains = false;
        for (Long l : validTests)
            if (Objects.equals(id, l)) {
                contains = true;
                break;
            }

        if (!contains) return true;

        // don't convert to the same schema+format
        SerializerFactory.Format thisFormat = SerializerFactory.resolveFormat(original.getContents());
        if (thisFormat == null) return false;

        if (thisSchema == SerializerFactory.Schema.SPDX23 && (convertToSchema.equals("SPDX23")))
            if (Objects.equals(convertToFormat, thisFormat.toString()))
                return true;

        if (thisSchema == SerializerFactory.Schema.CDX14 && (convertToSchema.equals("CDX14"))) return true;

        // tagvalue format unsupported for cdx14
        if (convertToSchema.equals("CDX14") && convertToFormat.equals("TAGVALUE")) return true;

        // we don't support xml deserialization right now
        return testMap.get(id).getContents().contains("xml");
    }

    /**
     * Helper function for APITest
     *
     * @param projectFiles project source files
     * @return array of SBOMFiles we can use to test
     */
    public static SBOMFile[] configureProjectTest(String[] projectFiles) throws IOException {
        SBOMFile[] sbomFiles = new SBOMFile[projectFiles.length];
        for (int i = 0; i < projectFiles.length; i++) {
            try {
                sbomFiles[i] = new SBOMFile(projectFiles[i], new String(Files.readAllBytes(Paths.get(projectFiles[i]))));
            } catch (InvalidPathException e) {
                i = -1;
            }
        }
        return sbomFiles;
    }

}
