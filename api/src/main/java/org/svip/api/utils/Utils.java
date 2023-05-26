package org.svip.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.generators.SBOMGenerator;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;
import org.svip.sbomfactory.translators.TranslatorController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A static class containing helpful utilities for API calls and testing responses.
 *
 * @author Ian Dunn
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
     * Code shared by /compare and /merge used to configure optional parameters
     *
     * @param schema schema string value
     * @param format format string value
     */
    public static Map<GeneratorSchema, GeneratorSchema.GeneratorFormat> configureSchema(String schema, String format) {

        GeneratorSchema resultSchema;
        try { resultSchema = GeneratorSchema.valueOfArgument(schema.toUpperCase()); }
        catch (IllegalArgumentException i) { return null;}

        GeneratorSchema.GeneratorFormat resultFormat;
        try { resultFormat = GeneratorSchema.GeneratorFormat.valueOf(format.toUpperCase()); }
        catch (IllegalArgumentException i) { return null;}

        return Map.of(resultSchema, resultFormat);

    }

    /**
     * Code shared by /compare and /merge used to deserialize multiple SBOMs
     *
     * @param fileContents JSON string array of the contents of all provided SBOMs
     * @param fileNames JSON string array of the filenames of all provided SBOMs
     * @return list of SBOM objects
     */
    public static List<SBOM> translateMultiple(String fileContents, String fileNames) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> contents = objectMapper.readValue(fileContents, new TypeReference<>(){});
        List<String> fNames = objectMapper.readValue(fileNames, new TypeReference<>(){});

        // Convert the SBOMs to SBOM objects
        ArrayList<SBOM> sboms = new ArrayList<>();

        for (int i = 0; i < contents.size(); i++) {
            // Get contents of the file
            sboms.add(TranslatorController.toSBOM(contents.get(i), fNames.get(i)));
        }
        return sboms;
    }

    /**
     * Take an SBOM object and serialize it to a pretty-printed string given a schema and format.
     *
     * @param result SBOM to serialize
     * @param generatorSchema Document schema
     * @param generatorFormat Document format
     * @return Serialized SBOM document
     */
    public static String generateSBOM(SBOM result, GeneratorSchema generatorSchema,
                           GeneratorSchema.GeneratorFormat generatorFormat) throws IOException {
        SBOMGenerator generator = new SBOMGenerator(result, generatorSchema);
        return generator.writeFileToString(generatorFormat, true);
    }

//    /**
//     * assert SBOM object can be serialized and then translated back
//     * @param schema SBOM schema
//     * @param format SBOM format
//     * @param sbom SBOM object
//     */
//    public static boolean sbomCanBeBackTranslated(SBOM sbom, GeneratorSchema schema,
//                                                  GeneratorSchema.GeneratorFormat format) throws IOException {
//
//        String serialized = Utils.generateSBOM(sbom, schema, format);
//        SBOM translated = Utils.buildSBOMFromString(serialized);
//
//        assertNotNull(translated);
//        assertEquals(schema, GeneratorSchema.valueOfArgument(translated.getOriginFormat().toString()));
//    }

}
