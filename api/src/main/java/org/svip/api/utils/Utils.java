package org.svip.api.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.generators.SBOMGenerator;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;
import org.svip.sbomfactory.translators.TranslatorController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A static class containing helpful utilities for API calls and testing responses.
 *
 * @author Ian Dunn
 * @author Juan Francisco Patino
 */
public class Utils {

    /**
     * Utility Class for sending SBOM JSON objects. Contains {@code fileName} & {@code contents} fields that are capable
     * of being automatically serialized to and from JSON.
     */
    public static class SBOMFile {
        @JsonProperty
        public String fileName;
        @JsonProperty
        public String contents;
        public boolean hasNullProperties;

        /**
         * Default constructor for SBOMFile. Used for test purposes.
         *
         * @param fileName The name of the SBOM file.
         * @param contents The contents of the SBOM file.
         */
        public SBOMFile(String fileName, String contents) {
            this.fileName = fileName;
            this.contents = contents;
            this.hasNullProperties = fileName == null || contents == null
                    || fileName.length() == 0 || contents.length() == 0;
        }
    }

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
     * Convert file contents and file names JSON arrays to a {@code Map<String, List<String>>} containing two keys:
     * <ul>
     *     <li>"fileContents": A parsed {@code List<String>} of the file contents.</li>
     *     <li>"filePaths": A parsed {@code List<String>} of the file paths.</li>
     * </ul>
     *
     * @param contentsArray The string representation of the JSON contents array.
     * @param fileArray The string representation of the JSON file array.
     * @return A {@code Map<String, List<String>>} containing keys of the fileContents and filePaths. Null if there is
     * an error parsing either array or if the sizes are unequal.
     */
    public static Map<String, List<String>> validateContentsAndNamesArrays(String contentsArray, String fileArray) {
        // Resolve JSON arrays
        List<String> fileContents = Resolver.resolveJSONStringArray(contentsArray);
        List<String> filePaths = Resolver.resolveJSONStringArray(fileArray);

        if (fileContents == null || filePaths == null) return null;

        if(fileContents.size() != filePaths.size()) return null;

        return new HashMap<>(){{
            this.put("fileContents", fileContents);
            this.put("filePaths", filePaths);
        }};
    }

    /**
     * Checks an array of {@code SBOMFile} objects for one containing any null properties.
     *
     * @param arr The array of {@code SBOMFile} objects to check.
     * @return -1 if no SBOMFiles have null properties. Otherwise, return the index of the first SBOM with null
     * properties.
     */
    public static int sbomFileArrNullCheck(SBOMFile[] arr){
        int i = 0;
        for (SBOMFile a: arr
        ) {
            if(a == null || a.hasNullProperties)
                return i;
            i++;
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

        Utils.SBOMFile[] arr = new Utils.SBOMFile[contentsList.size()];

        for (int i = 0; i < contentsList.size(); i++) {

            arr[i] = new Utils.SBOMFile(filenamesList.get(i), contentsList.get(i));

        }

        return arr;

    }
}
