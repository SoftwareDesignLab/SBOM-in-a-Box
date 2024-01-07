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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.svip.utils.Debug.LOG_TYPE;
import static org.svip.utils.Debug.log;

/**
 * file: ScalaParser.java
 * Description: Language specific implementation of the ParserCore (Scala)
 *
 * @author Dylan Mulligan
 * @author Ian Dunn
 */
public class ScalaParser extends LanguageParser {
    // Scala API
    public ScalaParser() {
        super("https://www.scala-lang.org/api/2.12.5/");
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
        // Attempt to find component
        try {
            // Initialize variables
            StringBuilder urlSB = new StringBuilder(STD_LIB_URL);
            final String from = getGroup(component);
            final String name = getName(component);

            // If component is not capitalized, component is package
            if (from == null) {
                // Append component name to URL
                urlSB.append(name);
                // package URLs end in "package/name/index.html"
                urlSB.append("/index");
            }
            // Otherwise, if component is not "*", component is Class
            else if (!name.equals("*")) {
                // Append component from to URL
                urlSB.append(from);
                urlSB.append("/");
                // Class URLs end in "package/name/ClassName.html"
                urlSB.append(name);
            } // "*" URLs end in "package/name/index.html" except name = from in this case
            else {
                // Append component from to URL
                urlSB.append(from);
                // package URLs end in "package/name/index.html"
                urlSB.append("/index");
            }
            urlSB.append(".html");

            log(LOG_TYPE.DEBUG, "URL: " + urlSB);

            // Test to see if page exists.
            return Parser.queryURL(urlSB.toString(), false).getResponseCode() == 200;
        } catch (Exception e) {
            log(LOG_TYPE.EXCEPTION, e);
        }
        return false;
    }


    /**
     * Get the Scala regex Pattern to check against the file
     *
     * @return the Scala regex Pattern
     */
    @Override
    protected Pattern getRegex() {
        /*
        ^(?:(?!//).)*
        Checks and ignores anything after and including //

        import(?: static)?
        Only matches "import" and an optional "static"

         ([\w\*]*)(.*);
        It then matches any letter or * and then grabs all values after ‘.’ for a list of specific functions, and ends in a ‘;’

        (?![^\/\*]*\*\/)
        This does not include anything surrounded by multi line comments even if the import is on a different line.
         */
        return Pattern.compile("^(?:(?!//).)*(?:import (?:([\\w.*]*)(?:(?=\\.\\{\\n?)([\\S\\s]*?\\})|(\\.[\\w.*]*(?: as [\\w*.]*)?))))(?![^\\/\\*]*\\*\\/)", Pattern.MULTILINE);
    }


    /**
     * Given a regex match, parse the result accordingly to get the correct component information
     *
     * @param matcher regex match pattern
     * @return new component
     */
    @Override
    protected void parseRegexMatch(List<SVIPComponentBuilder> components, Matcher matcher) {
        // Variable initialization
        Pattern aliasRegex = Pattern.compile("^([\\w\\*]*)(?: => | as )?(\\w*)?", Pattern.MULTILINE);
        String match = "";

        // Import validation
        if (matcher.group(2) != null) match = matcher.group(2); // Look for Scala2 imports "bar.{foo => f, baz as b}"
        else if (matcher.group(3) != null) match = matcher.group(3); // Look for Scala3 imports "bar.foo as f"
        else
            log(LOG_TYPE.WARN, "Match (" + matcher.group(0) + ") has no Groups; Skipping. . ."); // Otherwise, invalid import

        // Clean string and tokenize
        final String[] tokens = match
                .replace("{", "")   // rm open braces
                .replace("}", "")   // rm close braces
                .replace("\r", "")  // rm carriage return
                .replace("\n", "")  // rm newline
                .replace(".", "")   // rm leading "."
                .split(",");                   // split on ","

        // Iterate over all found imports (usually only one per line, but can be more)
        for (String token : tokens) {
            // Match for name and alias
            Matcher aliasMatcher = aliasRegex.matcher(token.trim());

            SVIPComponentBuilder builder = new SVIPComponentBuilder();
            builder.setType("EXTERNAL"); // Default to EXTERNAL

            if (aliasMatcher.find() && !aliasMatcher.group(1).equals("")) {
                // If component is not capitalized, component is package
                if (Character.isLowerCase(aliasMatcher.group(1).charAt(0))) {
                    // Create component with original matcher group 1 combined with aliasMatcher group 1
                    builder.setName(matcher.group(1).replace(".", "/") + "/" + aliasMatcher.group(1));
                }
                // Otherwise, component is Class
                else {
                    // Change "import bar._" to "import bar.*" for consistency
                    String name = aliasMatcher.group(1);
                    if (name.equals("_")) name = "*";

                    // Create component with aliasMatcher group 1
                    builder.setName(name);

                    // Set from as group 1 from the original matcher
                    builder.setGroup(matcher.group(1).replace(".", "/"));
                }


                // TODO If an alias is found, set alias to group 2
//                if(!aliasMatcher.group(2).equals("")) {
//                    builder.setAlias(aliasMatcher.group(2));
//                }

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
