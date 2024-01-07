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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.svip.utils.Debug.LOG_TYPE;
import static org.svip.utils.Debug.log;

/**
 * file: SubprocessParser.java
 *
 * @author Dylan Mulligan
 * @author Henry Keena
 * @author Ian Dunn
 * @description Context parser class responsible for parsing source files for calls to outside processes.
 */
public class SubprocessParser extends ContextParser {
    // Declares Class Attributes
    private final Pattern subprocessPattern;

    /**
     * SubprocessParser Class Constructor
     */
    public SubprocessParser() {
        super();
        // Regex101: https://regex101.com/r/Yb3Zsc/2
        this.subprocessPattern = Pattern.compile("['\\\"`]([^'\\\"`\\n]*)['\\\"`]");
    }

    /**
     * Method for detecting Subprocess calls or Outward Process calls.
     */
    private void detectSubprocessCall(String fileContents) {
        Debug.log(LOG_TYPE.DEBUG, "Searching File for SubProcess Call Edge Cases...");
        // Define array of subprocess call keywords
        final String[] keywords = {"process", "sub", "sys", "os", "external", "execute", "exec", "run", "popen", ".!", "%x", "spawn"};
        final String[] capKeywords = Arrays.stream(keywords).map(StringUtils::capitalize).toArray(String[]::new);
        final String[] commentPrefix = {"#", "//"};

        List<String> lines = new ArrayList<>(Arrays.stream(fileContents.split("\n")).toList());
        final ArrayList<String> linesToRemove = new ArrayList<>();
        // Iterate over file lines
        for (final String line : lines) {
            // If line starts with any comment prefix, continue
            if (StringUtils.startsWithAny(line, commentPrefix)) continue;

            // If line contains any keywords, continue
            if (!StringUtils.containsAny(line, keywords) && !StringUtils.containsAny(line, capKeywords)) continue;

            // If line is import, continue
            if (line.contains("import ") || line.contains("from ")) continue;

            // Match line against this.subprocessPattern
            final Matcher m = this.subprocessPattern.matcher(line);

            // Get results and skip line if no valid subprocess call was found
            final MatchResult[] results = m.results().toArray(MatchResult[]::new);
            if (results.length == 0) continue;

            // Build process call string and add to this.context
            this.context.add(String.join(" ", Arrays.stream(results).map(r -> r.group(1)).toList()).trim());

            // Mark line to be removed (as it has been successfully parsed) and break
            linesToRemove.add(line);
        }

        // Remove lines marked for removal
        lines.removeAll(linesToRemove);

        // Trim all lines
        lines = lines.stream().map(String::trim).toList();

        // Filter out empty lines and comments
        lines = lines.stream().filter(l -> !l.isEmpty() && !l.startsWith("#") && !l.startsWith("//")).toList();

        // Log any unparsed data (remaining lines)
//        log(LOG_TYPE.DEBUG, "UNPARSED DATA:\n\t" + String.join("\n\t", lines));
    }

    /**
     * Overridden method for handling parsing and logging subprocess calls.
     *
     * @param components   A list of ParserComponents that the found components will be appended to.
     * @param fileContents file contents to be parsed
     */
    @Override
    public void parse(List<SVIPComponentBuilder> components, String fileContents) {
        // Detect subprocess calls
        this.detectSubprocessCall(fileContents);

        // Log found calls
        log(LOG_TYPE.DEBUG, "FOUND SUBPROCESS CALLS:\n\t" + String.join("\n\t", this.context));

        // Log number of found calls
        log(LOG_TYPE.DEBUG, String.format("%s Subprocess Calls Detected", this.context.size()));

        // TODO: Better way to handle this? Currently the entire call is stored as name and type is set to EXTERNAL
        for (final String subprocessCall : this.context) {
            // Create ParserComponent
            final SVIPComponentBuilder builder = new SVIPComponentBuilder();
            builder.setName(subprocessCall.replaceAll("\\\\\\\\", "/"));
            builder.setType("APPLICATION");
            // Add ParserComponent to components
            components.add(builder);
        }
    }
}
