package org.svip.generation.parsers.languages;

import org.svip.generation.parsers.utils.VirtualPath;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.svip.utils.Debug.LOG_TYPE;
import static org.svip.utils.Debug.log;

/**
 * file: JSTSParser.java
 * Description: Language specific implementation of the ParserCore (JavaScript/TypeScript)
 *
 * @author Dylan Mulligan
 * @author Ian Dunn
 */
public class JSTSParser extends LanguageParser {
    public JSTSParser() {
        super("");
    }

    /**
     * Determines if the component is Internal
     *
     * @param component component to search for
     * @return true if internal, false otherwise
     */
    @Override
    protected boolean isInternalComponent(SVIPComponentBuilder component) {
        String name = getName(component);
        String group = getGroup(component);

        for (VirtualPath internalComponent : sourceFiles) {
            if (internalComponent.endsWith(new VirtualPath(name))) return true;
            if (group != null && internalComponent.endsWith(new VirtualPath(group))) return true;
        }

        return false;
    }

    /**
     * Determines if the component is base JS.
     * As JS has no base language library, this
     * method will always return false.
     *
     * @param component component to search for
     * @return true if language, false otherwise
     */
    @Override
    protected boolean isLanguageComponent(SVIPComponentBuilder component) {
        // No language library for JS, packages use 3rd party managers like npm and yarn
        return false;
    }

    /**
     * Generate a pattern from a JS/TS regex statement.
     *
     * @return a pattern encoded with the language-specific regex
     */
    @Override
    protected Pattern getRegex() {
        /*
        ^(?:(?!//).)*
        Checks and ignores anything after and including //

        (?:import (?:(?:(?=.*\\{\\n?)([\\S\\s]*?\\})|(.*))\\s*from [ '\\"]*([\\w\\.\\/]+)[ '\\"]*|[ '\\"]*([\\w\\.\\/]+)[ '\\"]*)
        Checks if import statement, and then looks ahead to see if it's a multi-line statement.\s

        If so it groups all functions and waits for a ‘}’ and then "from" keyword and records the package
        Otherwise it just groups the package

        |require[ \\('\\"]*([\\w\\.\\/]+)[ \\)'\\"]*)
        If the keyword isn’t import, it could be require, so it then isolates the package from spaces,
        quotes, and parentheses

        (?![^\\/\\*]*\\*\\/)
        This doesn’t include anything surrounded by multi line comments even if the import is on a different line.
        */
        return Pattern.compile("^(?:(?!//).)*(?:import (?:(?:(?=.*\\{\\n?)([\\S\\s]*?\\})|(.*))\\s*from [ '\\\"]*([\\w\\.\\/]+)[ '\\\"]*|[ '\\\"]*([\\w\\.\\/]+)[ '\\\"]*)|require[ \\('\\\"]*([\\w\\.\\/]+)[ \\)'\\\"]*)(?![^\\/\\*]*\\*\\/)", Pattern.MULTILINE);
    }

    /**
     * Given a regex match, parse the result accordingly to get the correct component information
     *
     * @param matcher regex match pattern
     * @return new component
     */
    @Override
    protected void parseRegexMatch(List<SVIPComponentBuilder> components, Matcher matcher) {
        // Check for groups 1-5
        String match = null;
        for (int i = 1; i <= 5; i++) {
            if (matcher.group(i) != null) {
                match = matcher.group(i);
                break;
            }
        }
        // If no match is found in any group (1-5), log it and skip this component
        if (match == null) {
            log(LOG_TYPE.WARN, "Match has no Groups; Skipping. . ."); // warn because this may be intentional
            return;
        }


        // 1. Clean string
        match = match
                .replace("{", "")   // rm open braces
                .replace("}", "")   // rm close braces
                .replace("\r", "")  // rm carriage return
                .replace("\n", "")  // rm newline
                .replace("\"", "")  // rm double quotes
                .replace("'", "");  // rm single quotes

        // 2. Tokenize
        String[] tokens = match.split(","); // foo, bar as b, etc

        // 3. Parse tokens
        for (String token : tokens) {
            token = token.trim();   // remove leading/trailing whitespace
            // Ex: import foo, bar -> "foo" and "bar"
            SVIPComponentBuilder builder = new SVIPComponentBuilder();
            builder.setType("EXTERNAL"); // Default to EXTERNAL
            // If no whitespace, component does not have an alias
            if (!token.contains(" ")) {
                // Ensure token is not commented in-line
                if (!token.contains("/*") && !token.contains("*/")) {
                    // Create component
                    builder.setName(token);
                }
                // Otherwise, do not create component
            }
            // Else has 'as' keyword (alias)
            else {
                Matcher m = Pattern.compile("(.*)(?: as )(.*)").matcher(token);
                // Check for alias match
                if (m.find()) {
                    // Create component (with name and alias)
                    builder.setName(m.group(1));    // import foo as f; foo = group(2)
//                    c.setAlias(m.group(2));     // TODO import foo as f; f = group(3)
                }
                // Otherwise, do not create component
            }
            if (builder.build().getName() != null && !builder.build().getName().isEmpty()) {
                // Set from to the match in group 3
                final String from = matcher.group(3);

                // If the component imports an entire internal file,
                //from should be set to the component name (filepath).
                // Otherwise, use the value from matcher group 3
                builder.setGroup(getName(builder).contains(".") ? getName(builder) : from);

                // Check if internal
                if (isInternalComponent(builder)) {
                    builder.setType("INTERNAL");

                    // Otherwise, check if Language
                } else if (isLanguageComponent(builder)) {
                    builder.setType("LANGUAGE");
                }

                // Add Component
                components.add(builder);
            }
        }
    }

}
