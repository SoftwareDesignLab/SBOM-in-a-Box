package org.svip.sbomfactory.translators;

import org.svip.sbom.model.SBOM;

import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class Translator {

    // File path of the target SBOM
    String filePath;

    public Translator(String filePath) {
        this.filePath = filePath;
    }

    public static String getContents(String path) {
        // Read the contents at path into a string
        String contents = null;
        try {
            contents = new String(Files.readAllBytes(Paths.get(path)));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        return contents;
    }

    public abstract void translate(String fileContents, String filePath);


}
