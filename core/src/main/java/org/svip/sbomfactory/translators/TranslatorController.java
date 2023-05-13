package org.svip.sbomfactory.translators;

import org.svip.sbom.model.*;
import org.svip.sbomfactory.generators.parsers.Parser;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Stream;


/**
 * file: TranslatorController.java
 *
 * Driver class for SPDX and CDX Translators
 * @author Tyler Drake
 * @author Matt London
 */
public class TranslatorController {

    private static final HashMap<String, TranslatorCore> EXTENSION_MAP = new HashMap<>() {{

        final TranslatorCDXXML translatorCDXXML = new TranslatorCDXXML();
        put("xml", translatorCDXXML);

        final TranslatorCDXJSON translatorCDXJSON = new TranslatorCDXJSON();
        put("json", translatorCDXJSON);

        final TranslatorSPDX translatorSPDX = new TranslatorSPDX();
        put("spdx", translatorSPDX);

    }};

    public TranslatorController() {

    }


    /**
     * Parse an SBOM using the appropriate translator and return the object
     *
     * @param path Path to the SBOM to translate
     * @return SBOM object, null if failed
     */

    /**
     * Parse an SBOM using the appropriate translator and return the object based on the contents of the file
     *
     * @param contents contents of the bom
     * @param filePath path to the bom
     * @return SBOM object, null if failed
     */
    public SBOM translateContents(String contents, String filePath) {

        SBOM sbom = null;

        // TODO check the contents of the file rather than trusting the file extension
        // TODO address the parser exception rather than ignoring it

        final String extension = filePath.substring(filePath.toLowerCase().lastIndexOf('.'));

        try {

            switch (extension) {

//                case ".xml"  -> sbom = TranslatorCDXXML.translatorCDXXMLContents(contents, filePath);

                case ".json" -> {
//                    if (new JSONObject(new String(Files.readAllBytes(Paths.get(filePath)))).toMap().get("bomFormat").equals("CycloneDX")) {
//                        sbom = TranslatorCDXJSON.translateContents(contents, filePath);
//                    }
                }

//                case ".spdx" -> sbom = TranslatorSPDX.translatorSPDXContents(contents, filePath);

                default      -> System.err.println("\nInvalid SBOM format found at: " + filePath);

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
                final TranslatorCore translator = EXTENSION_MAP.get(
                        sbom_item.substring(sbom_item.lastIndexOf('.') + 1)
                );

                // If the translator exists and is an actual translator, translate and add the SBOM to the list
                // Otherwise, print out an error stating that the SBOM is not a correct format
                if(translator != null && translator instanceof TranslatorCore) {
                    sbom_objects.add(translator.translate(sbom_item));
                } else {
                    System.err.println("\nInvalid SBOM format found in: " + sbom_item);
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
                System.err.println("Error translating SBOM: " + sbom_item);
            }
        }

        // Remove all null sboms in sbom collection
        sbom_objects.removeAll(Collections.singleton(null));

        // Return ArrayList of Java SBOM Objects
        return sbom_objects;
    }

}