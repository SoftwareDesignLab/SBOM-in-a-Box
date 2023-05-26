package org.svip.api.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.generators.SBOMGenerator;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;
import org.svip.sbomfactory.translators.TranslatorController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A static class containing helpful utilities for API calls and testing responses.
 *
 * @author Ian Dunn
 * @author Juan Francisco Patino
 */
public class Utils {

    /**
     * Helper method to get the schema of an SBOM string by translating it in and finding the SBOM object origin format.
     *
     * @param sbom The SBOM string to get the schema of.
     * @return The schema of the SBOM string.
     */
    public static GeneratorSchema getSchemaFromSBOM(String sbom) {
        SBOM translated = TranslatorController.toSBOM(sbom, buildTestFilepath(sbom));
        return GeneratorSchema.valueOfArgument(translated.getOriginFormat().toString());
    }

    /**
     * Helper method to build an SBOM object from a string by translating it in using
     * {@code TranslatorController.toSBOM()}
     *
     * @param sbom The SBOM string to convert to an object.
     * @return The SBOM object containing all the details from the SBOM string.
     */
    public static SBOM buildSBOMFromString(String sbom) {
        return TranslatorController.toSBOM(sbom, buildTestFilepath(sbom));
    }

    /**
     * Private helper method to build a test SBOM filepath from a string by assuming its format. This is used when
     * translating an SBOM.
     *
     * @param sbom The SBOM string to get the test filepath of.
     * @return The filepath of the SBOM with the proper file extension.
     */
    private static String buildTestFilepath(String sbom) {
        GeneratorSchema.GeneratorFormat format = SBOMGenerator.assumeSBOMFormat(sbom);
        return "/SBOMOut/SBOM." + format.toString().toLowerCase();
    }

    /**
     * Code shared by /compare and /merge used to deserialize multiple SBOMs
     *
     * @param fileContents JSON string array of the contents of all provided SBOMs
     * @param fileNames JSON string array of the filenames of all provided SBOMs
     * @return list of SBOM objects
     */
    public static List<SBOM> translateMultiple(List<String> fileContents, List<String> fileNames) {
        // Convert the SBOMs to SBOM objects
        ArrayList<SBOM> sboms = new ArrayList<>();

        for (int i = 0; i < fileContents.size(); i++) {
            // Get contents of the file
            sboms.add(TranslatorController.toSBOM(fileContents.get(i), fileNames.get(i)));
        }
        return sboms;
    }

    /**
     * Take an SBOM object and serialize it to a pretty-printed string given a schema and format.
     *
     * @param result SBOM to serialize
     * @param generatorSchema Document schema
     * @param generatorFormat Document format
     * @return Serialized SBOM document. Null if there was an error serializing.
     */
    public static String generateSBOM(SBOM result, GeneratorSchema generatorSchema,
                           GeneratorSchema.GeneratorFormat generatorFormat) {
        try {
            SBOMGenerator generator = new SBOMGenerator(result, generatorSchema);
            return generator.writeFileToString(generatorFormat, true);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Convert file contents and file names JSON arrays to two {@code List<String>}s stored in a string array of
     * length 2.
     *
     * @param contentsArray The string representation of the JSON contents array.
     * @param fileArray The string representation of the JSON file array.
     * @return A string array with the contents list stored in position 0 and the files list stored in position 1.
     * Null if malformed arrays or mismatching lengths.
     */
    public static List<String>[] validateContentsAndNamesArrays(String contentsArray, String fileArray) {
        // Resolve JSON arrays
        List<String> fileContents = Resolver.resolveJSONStringArray(contentsArray);
        List<String> filePaths = Resolver.resolveJSONStringArray(fileArray);

        if (fileContents == null || filePaths == null) return null;

        if(fileContents.size() != filePaths.size()) return null;

        List<String>[] result = new List[] { fileContents, filePaths };
        return result;
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
}
