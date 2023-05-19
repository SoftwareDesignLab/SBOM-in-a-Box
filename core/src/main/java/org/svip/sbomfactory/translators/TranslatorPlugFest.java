package org.svip.sbomfactory.translators;

import org.svip.sbom.model.*;

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

        try {

            String ext = filePath.substring(filePath.lastIndexOf('.') + 1);

            switch (ext) {
                case "xml" -> sbom =  new TranslatorCDXXML().translate(filePath);
                case "json" -> sbom =  new TranslatorCDXJSON().translate(filePath);
                case "spdx" -> sbom =  new TranslatorSPDX().translate(filePath);
                default -> System.err.println("\nError: Invalid SBOM format found in: " + filePath);

            }

        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        return sbom;
    }
}