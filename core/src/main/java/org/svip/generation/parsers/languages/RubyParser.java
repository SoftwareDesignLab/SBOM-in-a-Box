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
import org.svip.generation.parsers.utils.VirtualPath;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.svip.utils.Debug.LOG_TYPE;
import static org.svip.utils.Debug.log;


/**
 * file: RubyParser.java
 * Description: Language specific implementation of the ParserCore (Ruby)
 *
 * @author Dylan Mulligan
 * @author Ian Dunn
 */
public class RubyParser extends LanguageParser {
    public RubyParser() {
        super("https://docs.ruby-lang.org/en/2.1.0/");
    }

    private final Map<String, String> RUBY_STD_PACKAGES_AND_CLASSES = fetchRubyPackagesClasses();

    /**
     * Performs a GET request on RUBY_URL and scrapes
     * the page content to build and return a map of
     * package import aliases (as the key) and their
     * standard language library URL. (as the value)
     *
     * @return a map of Ruby standard package/class names
     * (as they appear in imports: "all_lower/case") and
     * corresponding URLs (RUBY_URL + "Package/Name.html")
     */
    private Map<String, String> fetchRubyPackagesClasses() {
        final HashMap<String, String> packages = new HashMap<>();

        // Attempt to get list of std packages
        try {
            // Attempt to perform a GET request on ORACLE_URL
            final HttpURLConnection connection = Parser.queryURL(STD_LIB_URL + "index.html", false);

            // If page cannot be reached, log failure and return empty set
            if (connection.getResponseCode() != 200) {
                log(LOG_TYPE.ERROR, "Failed to fetch package list");
                return packages;
            }

            // Otherwise, parse page data
            BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                    StandardCharsets.UTF_8));

            // Variable initialization
            String line;

            // Matcher explanation:
            // Raw string:                                         Match: Group 1            Group 2
            // "<li><a href="./ACL/ACLList.html">ACL::ACLList</a>"   ->   ACL/ACLList.html   acl/acllist

            // Group 1 captures the API endpoint of any class or module and group 2 captures the import alias
            // Imports are not case-sensitive in Ruby and best practice shows most imports are formed all lowercase.
            // Note: They also use "/" in the code instead of "::" (docs format) as a delineator
            Pattern regex = Pattern.compile("^<li><a href=\\\"\\./(.*)\\\">([\\w\\.:]*)", Pattern.MULTILINE);
            boolean found = false; // Waits until package list is found on page

            // Parsing loop
            while ((line = r.readLine()) != null) {
                // Trim leading/trailing whitespace
                line = line.trim();

                // Wait for line containing page element parent to package list
                if (!found) {
                    if (line.equals("<h3>Class and Module Index</h3>")) {
                        found = true;
                    }
                }
                // Once found, begin capturing packages
                else {
                    // Check line against regex
                    Matcher m = regex.matcher(line);

                    // If package is found add to hashset
                    if (m.find() && m.group(1) != null) {
                        packages.put(m.group(2).replace("::", "/").toLowerCase(), m.group(1));
                    }
                    // Terminate when classes/modules list ends
                    else if (line.equals("</ul>")) break;
                }
            }
        } catch (Exception e) {
            log(LOG_TYPE.EXCEPTION, e);
        }

        // Return set of all found packages
        return packages;
    }

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
    protected boolean isInternalComponent(SVIPComponentBuilder component) {
        String name = getName(component);
        String group = getGroup(component);

        VirtualPath internalPath = new VirtualPath((group == null ? "" : group) + "/" + name);

        for (VirtualPath internalComponent : sourceFiles) {
            if (internalComponent.removeFileExtension().endsWith(internalPath)) return true;
        }

        return false;
    }


    /**
     * Determines if the component is Language
     *
     * @param component component to search for
     * @return true if language, false otherwise
     */
    @Override
    protected boolean isLanguageComponent(SVIPComponentBuilder component) {
        // Define variables
        String key;

        // If from is defined, append name after a "/" and check full component path
        if (getGroup(component) != null) {
            key = getGroup(component).toLowerCase() + "/" + getName(component).toLowerCase();
        } else {
            // Otherwise, use name only
            key = getName(component).toLowerCase();
        }

        // RUBY_STD_PACKAGES_AND_CLASSES contains appropriate values of the standard language
        //URL endpoint, currently they are not used, but stored for future use as "import_name":"Import/Url.html"
        return RUBY_STD_PACKAGES_AND_CLASSES.containsKey(key);
    }


    /**
     * Get the Ruby regex Pattern to check against the file
     *
     * @return the Ruby regex Pattern
     */
    @Override
    protected Pattern getRegex() {
        /*
        ^(?:(?!//).)*
        Checks and ignores anything after and including //

        (?:require [ '\"]*([\w\.\/]+)[ '\"]*
        Checks for require statement, and groups the package that is being imported omitting spaces and quotes

        |load [ '\"]*([\w\.\/]+)[ '\"]*)
        If the keyword isn’t require it could be load, so it then isolates the package from spaces and quotes

        (?![^\=]*\=end)
        This doesn’t include anything surrounded by multi line comments even if the import is on a different line.
        This breaks if there is a ‘=’ in the comment, but this type of multi-line commenting is rare, and it is even more unlikely that there will be an import statement and var f = foo in the same comment

        regex101 URL: https://regex101.com/r/ixgZoD/5
         */

        return Pattern.compile("^(?:(?!#).)*(?:require [ '\\\"]*([\\w\\.\\/]+)[ '\\\"]*|load [ '\\\"]*([\\w\\.\\/]+)[ '\\\"]*)(?![^\\=]*\\=end)", Pattern.MULTILINE);
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
        SVIPComponentBuilder builder = new SVIPComponentBuilder();
        builder.setType("EXTERNAL"); // Default to EXTERNAL
        String match = "";

        // Import validation
        if (matcher.group(1) != null) match = matcher.group(1); // Look for Ruby "require" imports
        else if (matcher.group(2) != null) match = matcher.group(2); // Look for Ruby "load" and "autoload" imports
        else
            log(LOG_TYPE.WARN, "Match (" + matcher.group(0) + ") has no Groups; Skipping. . ."); // Otherwise, invalid import

        // Clean string and tokenize
        final String[] tokens = match
                .replace("\r", "")  // rm carriage return
                .replace("\n", "")  // rm newline
                .split("/");

        // If match has more than one token
        if (tokens.length > 1) {
            // Set name to last token (what exactly is being imported)
            builder.setName(tokens[tokens.length - 1]);

            // Start index for copy is 0 by default
            int start = 0;
            // If tokens[0] equals ".", set start to 1
            if (tokens[0].equals(".")) start = 1;

            // Set from to sublist of tokens joined on "/"
            final String from = String.join("/", Arrays.copyOfRange(tokens, start, tokens.length - 1));

            // If from is not an empty string, set c.from to from
            if (!from.equals("")) builder.setGroup(from);
        } else {
            builder.setName(match);
        }

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
