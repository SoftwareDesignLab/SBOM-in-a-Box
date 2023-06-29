package org.svip.sbomfactory.translators;

import org.svip.sbom.model.old.SBOM;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;


/**
 * file: TranslatorController.java
 *
 * Driver class for SPDX and CDX Translators
 * @author Tyler Drake
 * @author Matt London
 * @author Ian Dunn
 */
public class TranslatorController {
    private final static String INVALID_FILE_CONTENTS = "Invalid SBOM file contents (could not assume schema).";
    private final static Function<String, String> INVALID_FILE_TYPE = (ext) -> "File type " + ext + " not supported.";

    /**
     * Parse an SBOM using the appropriate translator and return the object
     *
     * @param path Path to the SBOM to translate
     * @return SBOM object
     * @throws TranslatorException if translation failed
     */
    public static SBOM translate(String path) throws TranslatorException {
        // Read the contents at path into a string
        String contents = null;
        try {
            contents = new String(Files.readAllBytes(Paths.get(path)));
        } catch (Exception e) {
            throw new TranslatorException(e.getMessage());
        }

        return translateContents(contents, path);
    }

    /**
     * Parse an SBOM using the appropriate translator and return the object based on the contents of the file
     *
     * @param contents contents of the bom
     * @param filePath path to the bom
     * @return SBOM object
     * @throws TranslatorException if translation failed
     */
    public static SBOM translateContents(String contents, String filePath) throws TranslatorException {
        TranslatorCore translator = getTranslator(filePath);

        SBOM result = translator.translateContents(contents, filePath);
        if (result == null) throw new TranslatorException("Unknown error while translating.");
        return result;
    }

    private static TranslatorCore getTranslator(String filePath) throws TranslatorException {
        String ext;
        try{
            ext = filePath.substring(filePath.lastIndexOf('.') + 1).trim().toLowerCase();
        }catch (NullPointerException e){
            throw new TranslatorException("File path is empty or null");
        }

        switch (ext.toLowerCase()) {
            case "json" -> { return new TranslatorCDXJSON(); }
            case "xml" -> { return new TranslatorCDXXML(); }
            case "spdx" -> { return new TranslatorSPDX(); }
            default -> { throw new TranslatorException(INVALID_FILE_TYPE.apply(ext)); }
        }
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
                final TranslatorCore translator = getTranslator(sbom_item.substring(sbom_item.lastIndexOf('.') + 1));

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