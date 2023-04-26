package svip.sbomfactory.translators;

import com.svip.sbom.model.*;
import org.json.JSONObject;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Stream;


/**
 * file: Translator.java
 *
 * Driver class for SPDX and CDX Translators
 * @author Tyler Drake
 * @author Matt London
 */
public class Translator {
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

        try {

            switch (extension) {

                case ".xml"  -> sbom = TranslatorCDXXML.translatorCDXXMLContents(contents, filePath);

                case ".json" -> {
                    if (new JSONObject(new String(Files.readAllBytes(Paths.get(filePath)))).toMap().get("bomFormat").equals("CycloneDX")) {
                        sbom = TranslatorCDXJSON.translatorCDXJSONContents(contents, filePath);
                    }
                }

                case ".spdx" -> sbom = TranslatorSPDX.translatorSPDXContents(contents, filePath);

                default      -> System.err.println("\nInvalid SBOM format found at: " + filePath);

            }

        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        return sbom;
    }

    public static ArrayList<SBOM> toReport(String sbomPath) throws ParserConfigurationException, IOException {
        // Collection for potential SBOM files
        ArrayList<Path> sbom_files = new ArrayList<>();

        // Collection for built SBOM Objects
        ArrayList<SBOM> sbom_objects = new ArrayList<>();

        // Go through target folder and add files to sbom_file ArrayList
        try (Stream<Path> paths = Files.walk(Paths.get(sbomPath))) {
            paths.filter(Files::isRegularFile).forEach(sbom_files::add);
        }

        /*
         * Iterate through every file found in SBOM folder. If a supported file is found, throw it into a translator.
         * Supported formats:
         *  - CYCLONE-DX XML
         *  - SPDX TAG-VALUE
         */
        for (Path sbom_item : sbom_files) {
            try {
                if (sbom_item.toString().toLowerCase().endsWith(".xml")) {
                    sbom_objects.add(TranslatorCDXXML.translatorCDXXML(sbom_item.toString()));
                } else if (sbom_item.toString().toLowerCase().endsWith(".spdx")) {
                    sbom_objects.add(TranslatorSPDX.translatorSPDX(sbom_item.toString()));
                } else {
                    System.err.println("\nInvalid SBOM format found in: " + sbom_item);
                }
                // todo deleting gitignore
                try {
                    Files.delete(sbom_item);
                } catch (IOException e) {
                    // This means it couldn't delete the file, which is fine
                }
            }
            catch (Exception e){
                System.err.println("Error translating SBOM: " + sbom_item);
            }
        }

        // Remove all null sboms in sbom collection
        sbom_objects.removeAll(Collections.singleton(null));

        // Return ArrayList of Java SBOM Objects
        return sbom_objects;
    }

}