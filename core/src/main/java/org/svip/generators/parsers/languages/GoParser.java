package org.svip.generators.parsers.languages;

import org.svip.generators.utils.Debug;
import org.svip.generators.utils.ParserComponent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


/**
 * file: GoParser.java
 * Description: Language specific implementation of the ParserCore (Go)
 *
 * @author Dylan Mulligan
 */
public class GoParser extends LanguageParser {
    public GoParser() { super("https://pkg.go.dev/"); }

    ///
    /// Abstract Method Implementation
    ///

    /**
     * Determines if the component is Internal
     *
     * @param component component to search for
     * @return true if internal, false otherwise
     */
    @Override
    protected boolean isInternalComponent(ParserComponent component) {
        // Build file name
        final String target = component.getName().toLowerCase() + ".go";

        // Get project path from this.src and walk files to find component
        try (Stream<Path> stream = Files.walk(this.PWD)) {
            // Return result of recursive file search
            return stream.anyMatch(file -> file.getFileName().toString().toLowerCase().equals(target));
        } catch (Exception e){
            Debug.log(Debug.LOG_TYPE.EXCEPTION, e);
            // Return false if an error was thrown
            return false;
        }
    }


    /**
     * Determines if the component is Language
     *
     * @param component component to search for
     * @return true if language, false otherwise
     */
    @Override
    protected boolean isLanguageComponent(ParserComponent component) {
        String endpoint;
        // Endpoint will either be "/from/name" or "/name"
        // If from is found, it is "/from/name"
        if(component.getGroup() != null) {
            endpoint = component.getGroup().toLowerCase();
            // If name is not "*", endpoint is "/from/name"
            if(!component.getName().equals("*"))
                endpoint += "/" + component.getName().toLowerCase();
            // Otherwise, endpoint is "/from"
        } else endpoint = component.getName(); // Otherwise, it is "/name"

        // Return connection response (200 = true, else = false)
        try { return queryURL(STD_LIB_URL + endpoint, false).getResponseCode() == 200; }
        // If an error is thrown, return false
        catch (Exception ignored) { return false; }
    }


    /**
     * Get the Go regex Pattern to check against the file
     *
     * @return the Go regex Pattern
     */
    @Override
    protected Pattern getRegex() {
        /*
         * ^(?:(?!//).)*
        Prevents matches that are commented out in one line

        (?:import (?:(?=.*\(\n?)([\S\s]*?\))|[\w.]?[ \('\"]*(\S*)[\('\"]+))
        Matches any import statement with or without parentheses
        For non parentheses cases it removes outer quotes.

        (?![^\*]*\*\/)
        Ignores matches in multi-line comments
        EXCEPT for comments that have an ‘*’ in them

        Regex101: https://regex101.com/r/ZlJJeu/1
         */
        return Pattern.compile("^(?:(?!//).)*(?:import (?:(?=.*\\(\\n?)\\(([\\S\\s]*?)\\)|([\\w.]* ?\\\"[\\S]*)))(?![^\\*]*\\*\\/)", Pattern.MULTILINE);
    }


    /**
     * Given a regex match, parse the result accordingly to get the correct component information
     *
     * @param matcher regex match pattern
     * @return new component
     */
    @Override
    protected void parseRegexMatch(ArrayList<ParserComponent> components, Matcher matcher) {
        // LinkedHashMap used to maintain match order for testing and output consistency
        final LinkedHashMap<String, String> matches = new LinkedHashMap<>();
        ParserComponent c;
        String match;

        // Import validation
        // Look for multi-line imports
        if(matcher.group(1) != null) match = matcher.group(1);
        // Look for single-line imports
        else if(matcher.group(2) != null) match = matcher.group(2);
        else { // Otherwise, invalid import
            Debug.log(Debug.LOG_TYPE.WARN, "Match (" + matcher.group(0) + ") has no Groups; Skipping. . .");
            return;
        }

        // Clean string
        match = match
                .replace("\r", "")  // rm carriage return
                .replace("\"", "")  // rm quotes
                .replace("\t", "")  // rm tab
                .replace("\n", ";") // replace newline with ";"
                .trim();                            // trim whitespace

        // Single-line imports contain one import
        if (matcher.group(2) != null) {
            // Tokenize on "/" and store last token as match and all but last token as from
            final String[] tokens = match.split("/");
            // If there is more than one token
            if(tokens.length > 1) {
                // Check for alias
                if(tokens[0].contains(" ")) {
                    // Split on space (there will always be at least 2 tokens here)
                    final String[] innerTokens = tokens[0].split(" ");
                    // Remove alias from first token
                    tokens[0] = innerTokens[1];
                    // Prepend alias to last token
                    tokens[tokens.length - 1] = innerTokens[0] + " " + tokens[tokens.length - 1];
                }
                // Mapped in the form of:  "component_name":"from"
                matches.put(tokens[tokens.length - 1], String.join("/", Arrays.copyOfRange(tokens, 0, tokens.length - 1)).trim());
            } else matches.put(match.trim(), null); // Otherwise, from is null
        }
        // Multiline imports may contain multiple components
        else if (matcher.group(1) != null) {
            // Tokenize on ";"
            final String[] tokens = match.split(";");
            // Find all components
            for (String token : tokens) {
                if(token.trim().equals("")) continue; // Skip any empty tokens

                // Tokenize on "/" and store last token as match and all but last token as from
                final String[] parts = token.split("/");
                // If there is more than one part
                if(parts.length > 1) {
                    matches.put(parts[parts.length - 1], String.join("/", Arrays.copyOfRange(parts, 0, parts.length - 1)).trim());
                } else matches.put(token.trim(), null); // Otherwise, from is null
            }
        }

        // Format, build, and add all matches to components
        for (final String name : matches.keySet()) {
            // Matcher captures name in group 2 and alias in group 1,
            //however, if there is no alias, name is captured in group 1.
            // Raw string: Match: Group 1  Group 2
            // "f foo"   ->       "f"      "foo"
            // "bar"     ->       "bar"
            // Regex101: https://regex101.com/r/CGa0gG/1
            Pattern aliasRegex = Pattern.compile("^([\\w.]*)(?: (.*))?", Pattern.MULTILINE);
            Matcher aliasMatcher = aliasRegex.matcher(name);

            // Match for name and alias
            if(aliasMatcher.find()) {
                String alias = null;
                String cName = aliasMatcher.group(1); // Set group 1 to name by default
                // Get "from" information from the matches LinkedHashMap with name as the key
                String from = matches.get(name);

                // If group 2 is found, group 1 is alias and group 2 is name
                if(aliasMatcher.group(2) != null) {
                    alias = aliasMatcher.group(1); // Alias = Group 1
                    cName = aliasMatcher.group(2); // Name = Group 2
                    if(alias.equals("_")) continue; // Ignore "_" imports as they are unused
                    // If alias is equal to "."
                    if(alias.equals(".")) {
                        // Change "import bar as ." -> "import * from bar"
                        from = cName;
                        cName = "*";
                        alias = null;
                    }
                }

                c = new ParserComponent(cName); // Create component with cName
                if(alias != null) c.setAlias(alias); // Set alias if found
                if(from != null) c.setGroup(from); // Set from if found

            } else continue; // If no match found, import must be invalid, skip component

            // Check if component is Internal
            if (isInternalComponent(c)) c.setType(ParserComponent.Type.INTERNAL);
                // Otherwise, check component is Language
            else if (isLanguageComponent(c)) c.setType(ParserComponent.Type.LANGUAGE);

            // Add Component
            components.add(c);
        }
    }
}
