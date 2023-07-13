package org.svip.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.controller.SVIPApiController;
import org.svip.api.model.SBOMFile;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.old.SBOM;
import org.svip.sbomfactory.generators.generators.SBOMGenerator;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;
import org.svip.sbomfactory.serializers.SerializerFactory;
import org.svip.sbomfactory.serializers.deserializer.Deserializer;
import org.svip.sbomfactory.serializers.serializer.Serializer;
import org.svip.sbomfactory.translators.TranslatorController;
import org.svip.sbomfactory.translators.TranslatorException;

import java.io.IOException;
import java.util.*;

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
     * Helper method to get the schema of an SBOM string by translating it in and finding the SBOM object origin format.
     *
     * @param sbom The SBOM string to get the schema of.
     * @return The schema of the SBOM string.
     */
    public static GeneratorSchema getSchemaFromSBOM(String sbom) throws TranslatorException {
        SBOM translated = TranslatorController.translateContents(sbom, buildTestFilepath(sbom));
        return GeneratorSchema.valueOfArgument(translated.getOriginFormat().toString());
    }

    /**
     * Helper method to build an SBOM object from a string by translating it in using
     * {@code TranslatorController.toSBOM()}
     *
     * @param sbom The SBOM string to convert to an object.
     * @return The SBOM object containing all the details from the SBOM string.
     */
    public static SBOM buildSBOMFromString(String sbom) throws TranslatorException {
        return TranslatorController.translateContents(sbom, buildTestFilepath(sbom));
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
    public static List<SBOM> translateMultiple(List<String> fileContents, List<String> fileNames) throws TranslatorException {
        // Convert the SBOMs to SBOM objects
        ArrayList<SBOM> sboms = new ArrayList<>();

        for (int i = 0; i < fileContents.size(); i++) {
            // Get contents of the file
            sboms.add(TranslatorController.translateContents(fileContents.get(i), fileNames.get(i)));
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

    /**
     * Convert an SBOM to a desired schema
     * @param schemaString the desired schema
     * @param formatString the desired format
     * @return converted SBOMFile and error message, if any
     */
    public static Map<SBOMFile, String> convert(SBOMFile sbom, String schemaString, String formatString) throws JsonProcessingException {

        HashMap<SBOMFile, String> ret = new HashMap<>();

        // deserialize into SBOM object
        Deserializer d;
        org.svip.sbom.model.interfaces.generics.SBOM deserialized;

        try{
            d = SerializerFactory.createDeserializer(sbom.getContents());
            deserialized = d.readFromString(sbom.getContents());
        }catch (Exception e){
            return internalSerializerError(ret, ": " + e.getMessage(),"DURING DESERIALIZATION");
        }
        if(deserialized == null)
            return internalSerializerError(ret, "","DURING DESERIALIZATION"
                    + ": SPDX tag value deserializer not yet implemented" // todo remove after implemented
            );

        // ensure schema is valid
        SerializerFactory.Schema schema;
        try{
            schema = SerializerFactory.Schema.valueOf(schemaString);
        }catch (Exception e){
            ret.put(new SBOMFile("",""), "SCHEMA " + schemaString + " NOT VALID: " +
                    e.getMessage());
            return ret;
        }

        // ensure format is valid
        SerializerFactory.Format format;
        try{
            format = SerializerFactory.Format.valueOf(formatString);
        }catch (Exception e){
            ret.put(new SBOMFile("",""), "FORMAT " + formatString + " NOT VALID: " +
                    e.getMessage());
            return ret;
        }

        // serialize into desired format
        Serializer s;
        String serialized = null;
        try{
            // serialize into SPDX23 schema


            int x;
            if(format == SerializerFactory.Format.TAGVALUE)
                x = 0;

            s = SerializerFactory.createSerializer(schema, format, true);
            if(schema == SerializerFactory.Schema.SPDX23){
               // serialized = s.writeToString((CDX14SBOM) deserialized);// todo fix
                return internalSerializerError(ret, "","UNIMPLIMENTED CASTING FIX");}
            else if(schema == SerializerFactory.Schema.CDX14){ // serialize into CDX14 schema
                serialized = s.writeToString(new SVIPSBOM((SPDX23SBOM) deserialized)); // todo fix
                    //return internalSerializerError(ret, "","UNIMPLIMENTED CASTING FIX");
            }

        }catch (Exception e){
            return internalSerializerError(ret, ": " + e.getMessage(),"DURING SERIALIZATION");
        }
        if(serialized == null){
            return internalSerializerError(ret, "","DURING SERIALIZATION");
        }

        ret.put(new SBOMFile("SUCCESS", serialized), "");
        return ret;

    }

    /**
     * Returns a message detailing what went wrong during serialization/deserialization
     * @param ret HashMap value to return containing message
     * @param exceptionMessage message from caught exception, if any
     * @param internalMessage message detailing what specifically happened in convert()
     * @return HashMap value to return containing message
     */
    private static HashMap<SBOMFile, String> internalSerializerError(HashMap<SBOMFile, String> ret,
                                                                     String exceptionMessage, String internalMessage) {
        ret.put(new SBOMFile("",""), internalMessage +
                exceptionMessage);
        return ret;
    }

    /**
     * Reusable code used in API controller to check if SBOMFile is empty/not found
     * Also eliminates the need for isPresent() check for Optionals
     */
    public static ResponseEntity<String> checkIfExists(long id, Optional<SBOMFile> sbomFile, String call) {
        if (sbomFile.isEmpty()) {
            LOGGER.info("DELETE /svip/"+ call + "?id=" + id + " - FILE NOT FOUND. INVALID ID");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return null;
    }

}
