package org.svip.sbomfactory.parsers.contexts;

// Declares Imports

import org.apache.commons.lang3.StringUtils;
import org.svip.builders.component.SVIPComponentBuilder;
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
 * @description Context parser class responsible for parsing source files for calls to outside processes.
 *
 * @author Dylan Mulligan
 * @author Henry Keena
 * @author Ian Dunn
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
            if(StringUtils.startsWithAny(line, commentPrefix)) continue;

            // If line contains any keywords, continue
            if(!StringUtils.containsAny(line, keywords) && !StringUtils.containsAny(line, capKeywords)) continue;

            // If line is import, continue
            if (line.contains("import ") || line.contains("from ")) continue;

            // Match line against this.subprocessPattern
            final Matcher m = this.subprocessPattern.matcher(line);

            // Get results and skip line if no valid subprocess call was found
            final MatchResult[] results = m.results().toArray(MatchResult[]::new);
            if(results.length == 0) continue;

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
     * @param components A list of ParserComponents that the found components will be appended to.
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
