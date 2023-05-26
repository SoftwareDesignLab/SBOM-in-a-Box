package org.svip.api.utils;

import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.generators.SBOMGenerator;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;
import org.svip.sbomfactory.translators.TranslatorController;

public class Utils {
    public static void logTestRequest(String fileContents, String fileNames, String schema, String format) {
        String parameters = String.format("""
                {
                    fileContents: "%s",
                    fileNames: "%s",
                    schema: "%s",
                    format: "%s",
                }
                """, fileContents, fileNames, schema, format);
        Debug.log(Debug.LOG_TYPE.SUMMARY, "POST /SVIP/generateSBOM:\n" + parameters);
    }

    public static GeneratorSchema getSchemaFromSBOM(String sbom) {
        SBOM translated = TranslatorController.toSBOM(sbom, buildTestFilepath(sbom));
        return GeneratorSchema.valueOfArgument(translated.getOriginFormat().toString());
    }

    public static SBOM buildSBOMFromString(String sbom) {
        return TranslatorController.toSBOM(sbom.replaceAll("\r", ""), buildTestFilepath(sbom));
    }

    private static String buildTestFilepath(String sbom) {
        GeneratorSchema.GeneratorFormat format = SBOMGenerator.assumeSBOMFormat(sbom);
        return "/SBOMOut/SBOM." + format.toString().toLowerCase();
    }
}
