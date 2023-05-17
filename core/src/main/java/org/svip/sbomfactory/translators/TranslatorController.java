package org.svip.sbomfactory.translators;

import org.svip.sbom.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Stream;


/**
 * file: TranslatorController.java
 *
 * Driver class for SPDX and CDX Translators
 * @author Tyler Drake
 * @author Matt London
 */
public class TranslatorController {

    public enum TranslatorSchema {
        XML("xml", new TranslatorCDXXML()),
        JSON("json", new TranslatorCDXJSON()),
        SPDX("spdx", new TranslatorSPDX());

        private final String extension;

        private TranslatorCore translator;

        TranslatorSchema(String extension, TranslatorCore translator) {
            this.extension = extension;
            this.translator = translator;
        }

        public static TranslatorSchema getTranslator(String extension) {
            switch (extension) {
                case "xml" -> { return XML; }
                case "json" -> { return JSON; }
                case "spdx" -> { return SPDX; }
            }
            return null;
        }
    }


    /**
     * Parse an SBOM using the appropriate translator and
     * return the object based on the contents of the file
     *
     * @param filePath path to the bom
     * @return SBOM object, null if failed
     */
    public static SBOM toSBOM(String contents, String filePath) {

        SBOM sbom = null;

        try {
            // Get the respective translator based on the file's extension
            final TranslatorCore translator = TranslatorSchema.getTranslator(
                    filePath.substring(filePath.lastIndexOf('.') + 1)
            ).translator;

            // If the translator exists and is an actual translator, translate and add the SBOM to the list
            // Otherwise, print out an error stating that the SBOM is not a correct format
            if(translator != null && translator instanceof TranslatorCore) {
                sbom = translator.translate(filePath);
            } else {
                System.err.println("\nError: Invalid SBOM format found in: " + filePath);
            }

        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        return sbom;
    }

    /**
     * Takes in a filepath leading to SBOMs then coverts every
     * matching SBOM file into an internal SBOM object. Once
     * complete, then returns an ArrayList of Internal SBOMs.
     *
     * @param sbomPath folder containing SBOMs for translation
     * @return a list of internal SBOM objects
     * @throws IOException
     */
    public static ArrayList<SBOM> toReport(String sbomPath) throws IOException {
        // Collection for potential SBOM files
        final ArrayList<String> sbom_files = new ArrayList<>();

        // Collection for built SBOM Objects
        final ArrayList<SBOM> sbom_objects = new ArrayList<>();

        // Go through target folder and add files to sbom_file ArrayList
        try (Stream<Path> paths = Files.walk(Paths.get(sbomPath))) {
            paths.filter(Files::isRegularFile).forEach(x -> sbom_files.add(x.toString()));
        }

        /*
         * Iterate through every file found in SBOM folder. If a supported file is found, throw it into a translator.
         * Supported formats:
         *  - CYCLONE-DX XML
         *  - CYCLONE-DX JSON
         *  - SPDX TAG-VALUE
         */
        for (String sbom_item : sbom_files) {

            try {

                // Get the respective translator based on the file's extension
                final TranslatorCore translator = TranslatorSchema.getTranslator(
                        sbom_item.substring(sbom_item.lastIndexOf('.') + 1)
                ).translator;

                // If the translator exists and is an actual translator, translate and add the SBOM to the list
                // Otherwise, print out an error stating that the SBOM is not a correct format
                if(translator != null && translator instanceof TranslatorCore) {
                    sbom_objects.add(translator.translate(sbom_item));
                } else {
                    System.err.println("\nError: Invalid SBOM format found in: " + sbom_item);
                }
                // todo deleting gitignore
                try {
                    // Delete the file that was just translated
                    Files.delete(Path.of(sbom_item));
                } catch (IOException e) {
                    // This means it couldn't delete the file, which is fine
                }
            }
            catch (Exception e){
                // If there was any other issue translator the SBOM, output an error
                System.err.println("Error: Issue with translating SBOM: " + sbom_item);
            }
        }

        // Remove all null sboms in sbom collection
        sbom_objects.removeAll(Collections.singleton(null));

        // Return ArrayList of Java SBOM Objects
        return sbom_objects;
    }

}