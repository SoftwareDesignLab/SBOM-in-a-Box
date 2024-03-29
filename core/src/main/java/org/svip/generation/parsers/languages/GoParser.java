/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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
 */

package org.svip.generation.parsers.languages;

import org.svip.generation.parsers.Parser;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.svip.utils.Debug.LOG_TYPE;
import static org.svip.utils.Debug.log;


/**
 * file: GoParser.java
 * Description: Language specific implementation of the ParserCore (Go)
 *
 * @author Dylan Mulligan
 * @author Ian Dunn
 */
public class GoParser extends LanguageParser {
    public GoParser() {
        super("https://pkg.go.dev/");
    }

    ///
    /// Abstract Method Implementation
    ///

    /**
     * Determines if the component is Language
     *
     * @param component component to search for
     * @return true if language, false otherwise
     */
    @Override
    protected boolean isLanguageComponent(SVIPComponentBuilder component) {
        String endpoint;
        // Endpoint will either be "/from/name" or "/name"
        // If from is found, it is "/from/name"
        if (getGroup(component) != null) {
            endpoint = getGroup(component).toLowerCase();
            // If name is not "*", endpoint is "/from/name"
            if (!getName(component).equals("*"))
                endpoint += "/" + getName(component).toLowerCase();
            // Otherwise, endpoint is "/from"
        } else endpoint = getName(component); // Otherwise, it is "/name"

        // Return connection response (200 = true, else = false)
        try {
            return Parser.queryURL(STD_LIB_URL + endpoint, false).getResponseCode() == 200;
        }
        // If an error is thrown, return false
        catch (Exception ignored) {
            return false;
        }
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

        Regex101: https://regex101.com/r/ZlJJeu/9
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
    protected void parseRegexMatch(List<SVIPComponentBuilder> components, Matcher matcher) {
        // LinkedHashMap used to maintain match order for testing and output consistency
        final LinkedHashMap<String, String> matches = new LinkedHashMap<>();
        String match;

        // Import validation
        // Look for multi-line imports
        if (matcher.group(1) != null) match = matcher.group(1);
            // Look for single-line imports
        else if (matcher.group(2) != null) match = matcher.group(2);
        else { // Otherwise, invalid import
            log(LOG_TYPE.WARN, "Match (" + matcher.group(0) + ") has no Groups; Skipping. . .");
            return;
        }

        // Clean string
        match = match
                .trim()                               // trim whitespace
                .replace("\r", "")   // rm carriage return
                .replace("\"", "")   // rm quotes
                .replace("\t", "")   // rm tab
                .replace("\n", ";"); // replace newline with ";"

        // Tokenize multi-import statements on ";"
        final String[] tokens = match.split(";");

        // Find all components from tokens
        for (String token : tokens) {
            if (token.trim().equals("")) continue; // Skip any empty tokens
            // Tokenize on "/" and store last token as match and all but last token as from
            final String[] parts = token.trim().split("/");
            // If there is more than one token
            if (parts.length > 0) {
                // Check for alias
                if (parts[0].trim().contains(" ")) {
                    // Split on space (there will always be at least 2 tokens here)
                    final String[] innerTokens = parts[0].trim().split(" ");
                    // Remove alias from first token
                    parts[0] = innerTokens[1];
                    // Prepend alias to last token
                    parts[parts.length - 1] = innerTokens[0] + " " + parts[parts.length - 1];
                }
                // Mapped in the form of:  "component_name":"from"
                final String from = String.join("/", Arrays.copyOfRange(parts, 0, parts.length - 1)).trim();
                matches.put(parts[parts.length - 1], from.isBlank() ? null : from);
            } else matches.put(match.trim(), null); // Otherwise, from is null
        }

        // Format, build, and add all matches to components
        for (final String name : matches.keySet()) {
            SVIPComponentBuilder builder = new SVIPComponentBuilder();
            builder.setType("EXTERNAL"); // Default to EXTERNAL

            // Matcher captures name in group 2 and alias in group 1,
            //however, if there is no alias, name is captured in group 1.
            // Raw string: Match: Group 1  Group 2
            // "f foo"   ->       "f"      "foo"
            // "bar"     ->       "bar"
            // Regex101: https://regex101.com/r/4n5U4A/1
            Pattern aliasRegex = Pattern.compile("^([\\w\\*:.]*)(?: (.*))?", Pattern.MULTILINE);
            Matcher aliasMatcher = aliasRegex.matcher(name);

            // Match for name and alias
            if (aliasMatcher.find()) {
                String alias = null;
                String cName = aliasMatcher.group(1); // Set group 1 to name by default
                // Get "from" information from the matches LinkedHashMap with name as the key
                String from = matches.get(name);

                // If group 2 is found, group 1 is alias and group 2 is name
                if (aliasMatcher.group(2) != null) {
                    alias = aliasMatcher.group(1); // Alias = Group 1
                    cName = aliasMatcher.group(2); // Name = Group 2
                    if (alias.equals("_")) continue; // Ignore "_" imports as they are unused
                    // If alias is equal to "."
                    if (alias.equals(".")) {
                        // Change "import bar as ." -> "import * from bar"
                        from = cName;
                        cName = "*";
                        alias = null;
                    }
                }

                builder.setName(cName);
//                if(alias != null) c.setAlias(alias); // TODO Set alias if found
                if (from != null) builder.setGroup(from); // Set from if found

            } else continue; // If no match found, import must be invalid, skip component

            // Check if component is Internal
            if (isInternalComponent(builder)) builder.setType("INTERNAL");
                // Otherwise, check component is Language
            else if (isLanguageComponent(builder)) builder.setType("LANGUAGE");

            // Add Component
            components.add(builder);
        }
    }
}
