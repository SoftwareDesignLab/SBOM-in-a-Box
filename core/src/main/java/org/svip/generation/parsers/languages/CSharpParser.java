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

package org.svip.generation.parsers.languages;

import org.svip.sbom.builder.objects.SVIPComponentBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.svip.utils.Debug.LOG_TYPE;
import static org.svip.utils.Debug.log;


/**
 * <b>File</b>: CSharpParser.java<br>
 * <b>Description</b>: Language specific implementation of Parser (C#)
 *
 * @author Dylan Mulligan
 * @author Derek Garcia
 * @author Ian Dunn
 */
public class CSharpParser extends LanguageParser {
    public CSharpParser() {
        super("https://learn.microsoft.com/en-us/dotnet/api/");
    }

    ///
    /// Abstract Method Implementation
    ///

    /**
     * Using the .NET api to check if the component is base C#
     *
     * @param component component to search for
     * @return true if language, false otherwise
     */
    @Override
    protected boolean isLanguageComponent(SVIPComponentBuilder component) {
        // Return connection response (200 = true, else = false)
        String endpoint =
                getGroup(component) == null ?
                        getName(component) :
                        getGroup(component).replace('/', '.') + "." + getName(component);

        // If component is typed, count types and go to correct URL
        // "System.Func<string, string>" -> "System.Func-2"
        final int index = endpoint.indexOf('<');
        if (index != -1) {
            // Rebuild endpoint
            final int params = endpoint.substring(index).split(",").length;
            endpoint = endpoint.substring(0, index) + "-" + params;

            // Strip typing from component name
            final String name = getName(component);
            component.setName(name.substring(0, name.indexOf('<')));
        }

        // Query URL
        try {
            return queryURL(this.STD_LIB_URL + endpoint, true).getResponseCode() == 200;
        }
        // If an error is thrown, return false
        catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Get a list of C# regexes to check against the file
     *
     * @return a list of C# regexes
     */
    @Override
    protected Pattern getRegex() {
        /*
        Regex Breakdown:
        ^(?:(?!//).)*
        Don’t match any imports preceded by //

        (?:(?:|global )using(?: static | )(?:(.*) = |)(.*);)
        Match any C# import statement.
        Check if global precedes, then the using keyword, if static exists
        Checks if alias, otherwise get everything

        (?![^\/\*]*\*\/)
        Don’t match anything between multi-line comments

        Regex101: https://regex101.com/r/C4Uoy6/7
        */
        return Pattern.compile("^(?:(?!//).)*(?:(?:|global )using(?: static | )(?:(.*) = (?!new)|)([\\w.<> =,]*);)(?![^\\/\\*]*\\*\\/)", Pattern.MULTILINE);
    }

    // TODO: Rework to fit updated regex

    /**
     * Given a regex match, parse the result accordingly to get the correct component information
     *
     * @param matcher regex match pattern
     * @return new component
     */
    @Override
    protected void parseRegexMatch(List<SVIPComponentBuilder> components, Matcher matcher) {
        // Capture match data
        String match;
        if (matcher.group(2) != null) match = matcher.group(2);
        else {
            log(LOG_TYPE.WARN, "Match (" + matcher.group(0) + ") has no Groups; Skipping. . .");
            return;
        }

        // Clean string
        match = match.trim();

        // Split on "."
        String[] tokens = match.split("\\.");

        // Determine if "from" is present, if so split "from" and "name"
        String from = null;
        if (tokens.length > 1) {
            match = tokens[tokens.length - 1];
            from = String.join("/", Arrays.copyOfRange(tokens, 0, tokens.length - 1));
        }

        // Create Component
        SVIPComponentBuilder builder = new SVIPComponentBuilder();
        builder.setName(match);
        builder.setType("EXTERNAL"); // Default to EXTERNAL

        // Add "from" if found
        if (from != null) builder.setGroup(from);

        // If alias
//        if(matcher.group(1) != null) {
//            builder.setAlias(matcher.group(1).trim());
//        }

        // Check if internal
        if (isInternalComponent(builder)) builder.setType("INTERNAL");
            // Otherwise, check if Language
        else if (isLanguageComponent(builder)) builder.setType("LANGUAGE");

        // Remove generic type if found
        if (match.contains("<")) builder.setName(match.substring(0, match.indexOf("<")));

        // Add Component
        components.add(builder);
    }
}
