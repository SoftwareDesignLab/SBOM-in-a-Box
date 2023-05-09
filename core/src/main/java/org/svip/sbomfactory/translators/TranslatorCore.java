package org.svip.sbomfactory.translators;

import org.cyclonedx.exception.ParseException;
import org.svip.sbom.model.Component;
import org.svip.sbom.model.SBOM;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public abstract class TranslatorCore {
    protected final String FILE_EXTN;

    protected TranslatorCore(String fileExtn) {
        FILE_EXTN = fileExtn;
    }

    /**
     * Parse an SBOM using the appropriate translator and return the object based on the contents of the file
     *
     * @param contents contents of the bom
     * @param filePath path to the bom
     * @return SBOM object, null if failed
     */
    protected abstract SBOM translateContents(String contents, String filePath) throws IOException, ParseException, ParserConfigurationException;

    protected abstract void dependencyBuilder(Object dependencies, HashMap<String, Component> components, Component parent, SBOM sbom, Set<String> visited);
    protected void addDependency(Object dependencies, HashMap<String, Component> components, Component parent, Component child, SBOM sbom, Set<String> visited) {
        // If component is already in the dependency tree, add it as a child to the parent
        // Else, add it to the dependency tree while setting the parent
        if(sbom.hasComponent(child.getUUID())) {
            parent.addChild(child.getUUID());
        } else {
            sbom.addComponent(parent.getUUID(), child);
        }

        if (visited == null) {
            // This means we are in the top level component
            // Pass in a new hashset instead of the visited set
            visited = new HashSet<>();
            dependencyBuilder(dependencies, components, child, sbom, new HashSet<>());
        }
        else {
            // Only explore if we haven't already visited this component
            if (!visited.contains(child.getUniqueID())) {
                // Pass the child component as the new parent into dependencyBuilder
                dependencyBuilder(dependencies, components, child, sbom, visited);
            }
        }
    }

    public SBOM translatePath(String filePath) throws IOException, ParseException, ParserConfigurationException {
        // Read the file at filePath into a string
        String contents = null;
        try {
            contents = new String(Files.readAllBytes(Paths.get(filePath)));
        }
        catch (IOException e) {
            System.err.println("Could not read file: " + filePath);
            return null;
        }

        return this.translateContents(contents, filePath);
    }

    public ArrayList<SBOM> parseSBOMs(String sbomPath) throws IOException {
        // Collection for potential SBOM files
        final ArrayList<Path> sbom_files = new ArrayList<>();

        // Collection for built SBOM Objects
        final ArrayList<SBOM> sbom_objects = new ArrayList<>();

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
                if (sbom_item.toString().toLowerCase().endsWith(this.FILE_EXTN)) {
                    sbom_objects.add(this.translatePath(sbom_item.toString()));
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
