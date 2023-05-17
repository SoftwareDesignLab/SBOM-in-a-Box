package org.svip.sbomfactory.translators;

import org.svip.sbom.model.SBOM;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * file: Translator.java
 *
 * Driver class for SPDX and CDX Translators
 * @author Tyler Drake
 * @author Matt London
 */
public class TranslatorPlugFest {
    /**
     * Parse an SBOM using the appropriate translator and return the object
     *
     * @param path Path to the SBOM to translate
     * @return SBOM object, null if failed
     */
    public static SBOM translate(String path) {
        // Read the contents at path into a string
        String contents = null;
        try {
            contents = new String(Files.readAllBytes(Paths.get(path)));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        return translateContents(contents, path);
    }

    /**
     * Parse an SBOM using the appropriate translator and return the object based on the contents of the file
     *
     * @param contents contents of the bom
     * @param filePath path to the bom
     * @return SBOM object, null if failed
     */
    public static SBOM translateContents(String contents, String filePath) {

        SBOM sbom = null;

        // TODO check the contents of the file rather than trusting the file extension
        // TODO address the parser exception rather than ignoring it

        final String extension = filePath.substring(filePath.toLowerCase().lastIndexOf('.'));

//        try {
//
//            //call the appropriate translator based on the file extension
//            switch (extension) {
//
//                case ".xml"  -> sbom = TranslatorCDXXML.translatorCDXXMLContents(contents, filePath);
//
//                case ".json" -> {
//                    if (new JSONObject(new String(Files.readAllBytes(Paths.get(filePath)))).toMap().get("bomFormat").equals("CycloneDX")) {
//                        sbom = TranslatorCDXJSON.translateContents(contents, filePath);
//                    }
//                }
//
//                case ".spdx" -> sbom = TranslatorSPDX.translatorSPDXContents(contents, filePath);
//
//                default      -> System.err.println("\nInvalid SBOM format found at: " + filePath);
//
//            }
//
//        }
//        catch (Exception e) {
//            System.err.println("Error: " + e.getMessage());
//        }

        return sbom;
    }
}
