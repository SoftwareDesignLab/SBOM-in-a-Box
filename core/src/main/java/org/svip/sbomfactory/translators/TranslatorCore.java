package org.svip.sbomfactory.translators;

import org.cyclonedx.exception.ParseException;
import org.svip.sbom.model.Component;
import org.svip.sbom.model.SBOM;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * File: TranslatorCore.java
 * A generic abstract translator that holds shared functionality
 * amongst the translators. Other translators having unique
 * parsing methods will be extended off of this translator.
 *
 * @author Tyler Drake
 * @author Dylan Mulligan
 */
public abstract class TranslatorCore {

    // Current file's extension
    protected final String FILE_EXTN;

    // The internal SBOM object to be build
    protected SBOM sbom;

    // The top level component (what the SBOM is about)
    protected Component product;

    // Top level SBOM data
    protected HashMap<String, String> bom_data;

    // Top component data
    protected HashMap<String, String> product_data;

    // Map holding all components found
    protected HashMap<String, String> components;

    // Map of dependencies in SBOM between components
    protected HashMap<String, ArrayList<String>> dependencies;

    /**
     * Generic Translator core constructor.
     *
     * @param fileExtn The file extension
     */
    protected TranslatorCore(String fileExtn) {
        FILE_EXTN = fileExtn;
        bom_data = new HashMap<>();
        product_data = new HashMap<>();
        components = new HashMap<>();
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

    protected void createSBOM() {
        try {
            sbom = new SBOM(
                    bom_data.get("format"),
                    bom_data.get("specVersion"),
                    bom_data.get("sbomVersion"),
                    bom_data.get("author"),
                    bom_data.get("serialNumber"),
                    bom_data.get("timestamp"),
                    null
            );
        } catch (Exception e) {
            System.err.println(
                    "Error: Internal SBOM could not be created. Cancelling translation for this SBOM. \n " +
                    "File: " + this.FILE_EXTN + "\n"
            );
            e.printStackTrace();
            sbom = null;
            System.exit(0);
        }

        if (product == null) {
            try {
                product = new Component(
                        product_data.get("name"),
                        product_data.get("publisher"),
                        product_data.get("version"),
                        product_data.get("id")
                );
                sbom.addComponent(null, product);
            } catch (Exception e) {
                System.err.println("Error: Could not create top component from SBOM metadata. File: " + this.FILE_EXTN);
            }
        } else {
            sbom.addComponent(null, product);
        }

    }

    /**
     * A simple recursive function to build a dependencyTree out of a list of dependencies.
     *
     * @param components All components from the translated SBOM file
     * @param parent     The current parent to have the children components assigned to
     * @param visited    Components that have been visited to prevent circular dependencies
     */
    protected void dependencyBuilder(HashMap<String, Component> components, Component parent, Set<String> visited) {

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
                dependencyBuilder(components, child, new HashSet<>());
            }
            else {
                // Only explore if we haven't already visited this component
                if (!visited.contains(child.getUniqueID())) {
                    // Pass the child component as the new parent into dependencyBuilder
                    dependencyBuilder(components, child, visited);
                }
            }
        }
    }

    /**
     * Defaults all dependencies in the SBOM by adding them as children to the current parent component.
     *
     * @param components Components to be added as the children
     * @param parent     Parent (product) component
     */
    protected void defaultDependencies(HashMap<String, Component> components, Component parent) {
        if (dependencies == null) { return; }
        for(ArrayList<String> defaults : dependencies.values()) {
            defaults.stream().forEach(x -> sbom.addComponent(parent.getUUID(), components.get(x)));
        }
    }

    /**
     * Adds a dependency to the dependency list. Key being the parent, value as the child.
     *
     * @param key   The parent component
     * @param value The child component
     */
    protected void addDependency(String key, String value) {
        if (dependencies.get(key) == null || dependencies.get(key).isEmpty()) {
            dependencies.put(key, new ArrayList<>(Arrays.asList(value)));
        } else {
            ArrayList<String> oldDependencies = dependencies.get(key);
            oldDependencies.add(value);
            dependencies.replace(key, oldDependencies);
        }
    }

    /**
     * Defaults the dependency list. Asserts the given 'key' as the product or 'top component'
     * and then sets all other 'values' as the children components.
     *
     * @param key    The component to be asserted as the product (top component)
     * @param values The components to be assigned as children dependencies to the product
     */
    protected void defaultTopComponent(String key, ArrayList values) {
        values.remove(key);
        dependencies.put(key, values);
    }

    /**
     * Breaks the SBOM file into a String of contents. Then sends the contents to
     * the respective trnslator for translation. If the file can't be broken down,
     * return nothing.
     *
     * @param filePath path leading to the current SBOM file
     * @return an SBOM object if translation is successful
     * @throws IOException
     * @throws ParseException
     * @throws ParserConfigurationException
     */
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

}
