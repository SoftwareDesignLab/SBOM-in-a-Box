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

    protected HashMap<String, ArrayList<String>> dependencies;

    protected TranslatorCore(String fileExtn) {
        FILE_EXTN = fileExtn;
        dependencies = new HashMap<>();
    }

    /**
     * Parse an SBOM using the appropriate translator and return the object based on the contents of the file
     *
     * @param contents contents of the bom
     * @param filePath path to the bom
     * @return SBOM object, null if failed
     */
    protected abstract SBOM translateContents(String contents, String filePath) throws IOException, ParseException, ParserConfigurationException;

    protected SBOM createSBOM(HashMap<String, String> bom_data) {
        try {
            return new SBOM(
                    bom_data.get("format"),
                    bom_data.get("specVersion"),
                    bom_data.get("sbomVersion"),
                    bom_data.get("author"),
                    bom_data.get("serialNumber"),
                    bom_data.get("timestamp"),
                    null
            );
        } catch (Exception e) {
            System.err.println("Error: Internal SBOM could not be created. File: " + this.FILE_EXTN);
            e.printStackTrace();
            return null;
        }
    }

    protected void dependencyBuilder(HashMap<String, Component> components, Component parent, SBOM sbom, Set<String> visited) {

        // If top component is null, return. There is nothing to process.
        if (parent == null) { return; }

        if (visited != null) {
            // Add this parent to the visited set
            visited.add(parent.getUniqueID());
        }

        // Get the parent's dependencies as a list
        String parent_id = parent.getUniqueID();
        ArrayList<String> childrenID = dependencies.get(parent_id);
        dependencies.remove(parent_id);

        // If there are no
        if( childrenID == null ) { return; }

        // Cycle through each dependency the parent component has
        for (String childID: childrenID) {
            // Retrieve the component the parent has a dependency for
            Component child = components.get(childID);

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
                dependencyBuilder(components, child, sbom, new HashSet<>());
            }
            else {
                // Only explore if we haven't already visited this component
                if (!visited.contains(child.getUniqueID())) {
                    // Pass the child component as the new parent into dependencyBuilder
                    dependencyBuilder(components, child, sbom, visited);
                }
            }
        }
    }

    protected void defaultDependencies(HashMap<String, Component> components, Component parent, SBOM sbom) {
        if (dependencies == null) { return; }
        for(ArrayList<String> defaults : dependencies.values()) {
            defaults.stream().forEach(x -> sbom.addComponent(parent.getUUID(), components.get(x)));
        }
    }

    protected void addDependency(String key, String value) {
        if (dependencies.get(key) == null || dependencies.get(key).isEmpty()) {
            dependencies.put(key, new ArrayList<>(Arrays.asList(value)));
        } else {
            ArrayList<String> oldDependencies = dependencies.get(key);
            oldDependencies.add(value);
            dependencies.replace(key, oldDependencies);
        }
    }

    protected void setDependencies(String key, ArrayList values) {
        values.remove(key);
        dependencies.put(key, values);
    }

    public SBOM translate(String filePath) throws IOException, ParseException, ParserConfigurationException {
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
                    sbom_objects.add(this.translate(sbom_item.toString()));
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
