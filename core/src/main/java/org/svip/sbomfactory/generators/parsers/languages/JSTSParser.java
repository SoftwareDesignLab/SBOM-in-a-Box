package org.svip.sbomfactory.generators.parsers.languages;

import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.svip.sbomfactory.generators.utils.Debug.LOG_TYPE;
import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * file: JSTSParser.java
 * Description: Language specific implementation of the ParserCore (JavaScript/TypeScript)
 *
 * @author Dylan Mulligan
 */
public class JSTSParser extends LanguageParser {
    public JSTSParser() { super(""); }

    /**
     * Determines if the component is Internal
     *
     * @param component component to search for
     * @return true if internal, false otherwise
     */
    @Override
    protected boolean isInternalComponent(ParserComponent component) {
        String from = component.getGroup();
        log(LOG_TYPE.DEBUG, "FROM: " + from);

        // If "." contained within from, it is a path to a file
        if(from != null && from.contains(".")) {
            // Split path on "/"
            final String[] pathParts = from.split("/");
            // Get path terminus
            final String target = pathParts[pathParts.length - 1];

            // TODO
//            // Get project path from this.src and walk files to find component
//            try (Stream<Path> stream = Files.walk(this.PWD)) {
//                return stream.anyMatch(file -> file.getFileName().toString().toLowerCase().equals(target));
//            } catch (Exception e){
//                log(LOG_TYPE.EXCEPTION, e);
//            }
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
    protected boolean isLanguageComponent(ParserComponent component) {
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
    protected void parseRegexMatch(ArrayList<ParserComponent> components, Matcher matcher) {
        // Check for groups 1-5
        String match = null;
        for (int i = 1; i <= 5; i++) {
            if(matcher.group(i) != null) {
                match = matcher.group(i);
                break;
            }
        }
        // If no match is found in any group (1-5), log it and skip this component
        if(match == null) {
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
            ParserComponent c = null;
            // If no whitespace, component does not have an alias
            if (!token.contains(" ")) {
                // Ensure token is not commented in-line
                if(!token.contains("/*") && !token.contains("*/")) {
                    // Create component
                    c = new ParserComponent(token);
                }
                // Otherwise, do not create component
            }
            // Else has 'as' keyword (alias)
            else {
                Matcher m = Pattern.compile("(.*)(?: as )(.*)").matcher(token);
                // Check for alias match
                if (m.find()) {
                    // Create component (with name and alias)
                    c = new ParserComponent(m.group(1));    // import foo as f; foo = group(2)
                    c.setAlias(m.group(2));     // import foo as f; f = group(3)
                }
                // Otherwise, do not create component
            }
            if (c != null) {
                // Set from to the match in group 3
                final String from = matcher.group(3);

                // If the component imports an entire internal file,
                //from should be set to the component name (filepath).
                // Otherwise, use the value from matcher group 3
                c.setGroup(c.getName().contains(".") ? c.getName() : from);

                // Check if internal
                if (isInternalComponent(c)) {
                    c.setType(ParserComponent.Type.INTERNAL);

                // Otherwise, check if Language
                } else if (isLanguageComponent(c)) {
                    c.setType(ParserComponent.Type.LANGUAGE);
                }

                // Add Component
                components.add(c);
            }
        }
    }

}
