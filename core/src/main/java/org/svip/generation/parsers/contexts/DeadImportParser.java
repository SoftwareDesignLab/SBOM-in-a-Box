/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

package org.svip.generation.parsers.contexts;

// Declares Imports

import org.apache.commons.lang3.StringUtils;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.utils.Debug;

import java.util.List;

import static org.svip.utils.Debug.log;

/**
 * file: DeadImportParser.java
 * Context parser class responsible for parsing source files for dead or unused imports and dependency calls.
 *
 * @author Dylan Mulligan
 * @author Henry Keena
 * @author Ian Dunn
 */
public class DeadImportParser extends ContextParser {

    /**
     * Method for detecting unused Imports or Dead Code.
     *
     * @param components   A list of ParserComponents that the found components will be appended to.
     * @param fileContents file contents to be parsed
     */
    public void parseDeadImports(List<SVIPComponentBuilder> components, String fileContents) {
        Debug.log(Debug.LOG_TYPE.DEBUG, "Detecting Dead Code & Imports...");
        // Splits source file to be read line by line
        final String[] lines = fileContents.split("\n");
        // Iterates through each component of the source file
        for (SVIPComponentBuilder component : components) {
            // Counter for how many times component appears in the file
            int importCount = 0;
            // Iterates through each line of the file
            for (String line : lines) {
                // Checks if component/import appears in the line
                if (StringUtils.contains(line, getName(component))) {
                    // If the component does appear in the line, increments import counter
                    importCount++;
                }
            }
            // Checks to see if the component/import is a singular entity or multiple, and if it has been counted multiple times in file past import declaration
            if (!(getName(component).split(" ").length > 1) && importCount <= 1) {
                // Adds component to context list of Dead Imports
                this.context.add(getName(component));
            }
        }
    }

    /**
     * Overridden method for handling parsing of dead imports from source file.
     *
     * @param components   A list of ParserComponents that the found components will be appended to.
     * @param fileContents file contents to be parsed
     */
    @Override
    public void parse(List<SVIPComponentBuilder> components, String fileContents) {
        // Parse Dead Imports
        this.parseDeadImports(components, fileContents);

        // Log found dead imports
        log(Debug.LOG_TYPE.DEBUG, "DETECTED DEAD IMPORTS:\n\t" + String.join("\n\t", this.context));

        // Log number of dead imports
        log(Debug.LOG_TYPE.DEBUG, String.format("%s Dead Imports Detected", this.context.size()));

        // TODO: Better way to handle this? Currently the entire call is stored as name and type is set to EXTERNAL
        for (final String deadImport : this.context) {
            // Create ParserComponent
            final SVIPComponentBuilder builder = new SVIPComponentBuilder();
            builder.setName(deadImport);
            builder.setType("DEAD_IMPORT");
            // Add dead imports to components
            components.add(builder);
        }
    }
}
