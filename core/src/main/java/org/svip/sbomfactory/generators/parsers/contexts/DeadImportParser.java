package org.svip.sbomfactory.generators.parsers.contexts;

// Declares Imports

import org.apache.commons.lang3.StringUtils;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.util.ArrayList;

import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * file: DeadImportParser.java
 * @description Context parser class responsible for parsing source files for dead or unused imports and dependency calls.
 *
 * @author Henry Keena , Dylan Mulligan
 */
public class DeadImportParser extends ContextParser {

    /**
     * Method for detecting unused Imports or Dead Code.
     *
     * @param components A list of ParserComponents that the found components will be appended to.
     * @param fileContents file contents to be parsed
     */
    public void parseDeadImports(ArrayList<ParserComponent> components, String fileContents) {
        Debug.log(Debug.LOG_TYPE.DEBUG, "Detecting Dead Code & Imports...");
        // Splits source file to be read line by line
        final String[] lines = fileContents.split("\n");
        // Iterates through each component of the source file
        for(ParserComponent component : components) {
            // Counter for how many times component appears in the file
            int importCount = 0;
            // Iterates through each line of the file
            for(String line : lines) {
                // Checks if component/import appears in the line
                if(StringUtils.contains(line, component.getName())) {
                    // If the component does appear in the line, increments import counter
                    importCount++;
                }
            }
            // Checks to see if the component/import is a singular entity or multiple, and if it has been counted multiple times in file past import declaration
            if(!(component.getName().split(" ").length > 1) && importCount <= 1) {
                // Adds component to context list of Dead Imports
                this.context.add(component.getName());
            }
        }
    }

    /**
     * Overridden method for handling parsing of dead imports from source file.
     *
     * @param components A list of ParserComponents that the found components will be appended to.
     * @param fileContents file contents to be parsed
     */
    @Override
    public void parse(ArrayList<ParserComponent> components, String fileContents) {
        // Parse Dead Imports
        this.parseDeadImports(components, fileContents);

        // Log found dead imports
        log(Debug.LOG_TYPE.DEBUG, "DETECTED DEAD IMPORTS:\n\t" + String.join("\n\t", this.context));

        // Log number of dead imports
        log(Debug.LOG_TYPE.DEBUG, String.format("%s Dead Imports Detected", this.context.size()));

        // TODO: Better way to handle this? Currently the entire call is stored as name and type is set to EXTERNAL
        for(final String deadImport: this.context) {
            // Create ParserComponent
            final ParserComponent c = new ParserComponent(deadImport);
            c.setType(ParserComponent.Type.EXTERNAL);
            // Add dead imports to components
            components.add(c);
        }
    }
}
