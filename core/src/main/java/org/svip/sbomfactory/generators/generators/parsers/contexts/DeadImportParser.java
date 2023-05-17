package org.svip.sbomfactory.generators.generators.parsers.contexts;

// Declares Imports
import org.svip.sbomfactory.generators.generators.utils.ParserComponent;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;

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
        System.out.println("Detecting Dead Code & Imports...\n");
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
        // Parses Dead Imports
        this.parseDeadImports(components, fileContents);
        // Prints all detected dead imports from source file
        for (String s : this.context) {
            System.out.println(s);
        }
        // TODO: Add info to components
    }
}
