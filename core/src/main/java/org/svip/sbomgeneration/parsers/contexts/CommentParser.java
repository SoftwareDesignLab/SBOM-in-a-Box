package org.svip.sbomgeneration.parsers.contexts;

// Declares Imports

import org.apache.commons.lang3.StringUtils;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.utils.Debug;

import java.util.List;

import static org.svip.utils.Debug.log;

/**
 * file: CommentParser
 * Context parser class responsible for handling and parsing source file comments.
 *
 * @author Dylan Mulligan
 * @author Henry Keena
 * @author Ian Dunn
 */
public class CommentParser extends ContextParser {

    /**
     * Method for parsing comments from source files.
     *
     * @param fileContents file contents to be parsed
     */
    public void parseComments(String fileContents) {
        System.out.println("Parsing File Comments...\n");
        // Init StringBuilder for comments
        final StringBuilder runningComment = new StringBuilder();

        // Iterate over file lines
        for (final String line : fileContents.split("\n")) {
            // Checks already parsing a long form comment
            if (runningComment.length() == 0) {
                // Check for Single Line Comments
                if (line.contains("//") || line.contains("#")) {
                    //System.out.println(line);
                    // Determine comment suffix
                    String comchar = "";
                    if (line.contains("//")) comchar = "//";
                    else if (line.contains("#")) comchar = "#";

                    // Split line on comment prefix
                    final String[] segments = line.split(comchar);
                    // Extract comment
                    final String comment = segments[segments.length - 1];
                    // Add to this.fileComments
                    this.context.add(comment);
                }
                // Check for Multiline Comments
                else if (line.contains("/*") || line.contains("\"\"\"")) {
                    // In-line Multiline comment
                    if (line.contains("/*") && line.contains("*/")) {
                        this.context.add(StringUtils.substringBetween(line, "/*", "*/"));
                    } else if (line.contains("/*")) { // Multiline comment form 1
                        final String[] segments = line.split("/*");
                        runningComment.append(segments[segments.length - 1]);
                    } else if (line.contains("\"\"\"")) { // Multiline comment form 2
                        if (StringUtils.countMatches(line, "\"\"\"") == 1) {
                            final String[] segments = line.split("\"\"\"");
                            runningComment.append(segments[segments.length - 1]);
                        }
                        else this.context.add(StringUtils.substringBetween(line, "\"\"\"", "\"\"\""));
                    }
                }
            } else { // Otherwise, comment is already being parsed
                // Checks for Multiline closers
                if (line.contains("*/") || line.contains("\"\"\"")) {
                    // Determine comment suffix
                    String comchar = "";
                    if (line.contains("*/")) comchar = " */";
                    else if (line.contains("\"\"\"")) comchar = "\"\"\"";

                    final String[] segments = line.split(comchar);
                    runningComment.append(segments[0]);
                    this.context.add(runningComment.toString());
                    runningComment.setLength(0);
                } else {
                    runningComment.append(line);
                }
            }
        }
    }

    /**
     * Overridden method for handling parsing of source file comments.
     *
     * @param components A list of ParserComponents that the found components will be appended to.
     * @param fileContents file contents to be parsed
     */
    @Override
    public void parse(List<SVIPComponentBuilder> components, String fileContents) {
        // Parse Comments
        this.parseComments(fileContents);

        // Log found comments
        log(Debug.LOG_TYPE.DEBUG, "SOURCE CODE COMMENTS:\n\t" + String.join("\n\t", this.context));

        // Log number of found comments
        log(Debug.LOG_TYPE.DEBUG, String.format("%s Source Comments Detected", this.context.size()));

        // TODO: Better way to handle this? Currently the entire call is stored as name and type is set to EXTERNAL
        for(final String comment : this.context) {
            // Create ParserComponent
            final SVIPComponentBuilder builder = new SVIPComponentBuilder();
            builder.setName(comment);
            builder.setType("EXTERNAL");
            // Add comment to components
            components.add(builder);
        }
    }
}
