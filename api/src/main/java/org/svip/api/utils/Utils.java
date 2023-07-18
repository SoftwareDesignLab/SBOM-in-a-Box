package org.svip.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.controller.SVIPApiController;
import org.svip.api.model.SBOMFile;
import org.svip.sbomgeneration.serializers.SerializerFactory;

import java.util.*;

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
     * Returns a message detailing what went wrong during serialization/deserialization
     *
     * @param ret              HashMap value to return containing message
     * @param exceptionMessage message from caught exception, if any
     * @param internalMessage  message detailing what specifically happened in convert()
     * @return HashMap value to return containing message
     */
    static HashMap<SBOMFile, String> internalSerializerError(HashMap<SBOMFile, String> ret,
                                                             String exceptionMessage, String internalMessage) {
        ret.put(new SBOMFile("", ""), internalMessage +
                exceptionMessage);
        return ret;
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
        for (Long l : validTests
        ) {
            if (Objects.equals(id, l)) {
                contains = true;
                break;
            }
        }
        if (!contains)
            return true;

        // don't convert to the same schema+format
        if (thisSchema == SerializerFactory.Schema.SPDX23 && (convertToSchema.equals("SPDX23"))) {
            if ((convertToFormat).equalsIgnoreCase(assumeFormatFromDocument(original)))
                return true;
        }
        if (thisSchema == SerializerFactory.Schema.CDX14 && (convertToSchema.equals("CDX14")))
            return true;
        // tagvalue format unsupported for cdx14
        if (convertToSchema.equals("CDX14") && convertToFormat.equals("TAGVALUE"))
            return true;
        // we don't support xml deserialization right now
        return testMap.get(id).getContents().contains("xml");
    }

    /**
     * Helper function to assume format from raw SBOM document
     *
     * @param sbom SBOMFile to check
     * @return String representation of the format (JSON/TAGVALUE)
     */
    public static String assumeFormatFromDocument(SBOMFile sbom) {
        String originalFormat = "JSON";
        if (sbom.getContents().contains("DocumentName:") || sbom.getContents().contains("DocumentNamespace:"))
            originalFormat = TAGVALUE.name();
        return originalFormat;
    }

    /**
     * Helper function to assume schema from raw SBOM document
     *
     * @param contents SBOMFile to check
     * @return String representation of the schema
     */
    public static SerializerFactory.Schema assumeSchemaFromOriginal(String contents) {
        return (contents.toLowerCase().contains("spdxversion")) ?
                SerializerFactory.Schema.SPDX23 : SerializerFactory.Schema.CDX14;
    }

}
