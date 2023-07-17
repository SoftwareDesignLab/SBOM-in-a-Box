package org.svip.sbomfactory.parsers.languages;

import org.svip.builders.component.SVIPComponentBuilder;
import org.svip.sbomfactory.parsers.Parser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.svip.utils.Debug.LOG_TYPE;
import static org.svip.utils.Debug.log;


/**
 * file: JavaParser.java
 * Description: Language specific implementation of the Parser (Java)
 *
 * @author Dylan Mulligan
 */
public class JavaParser extends LanguageParser {
    // Oracle API Reference: This reference currently supports Java8 only for LTS. Other versions of
    //Java will need additional code to fetch relevant package lists
    public JavaParser() { super("https://docs.oracle.com/javase/8/docs/api/overview-frame.html"); }

    private final HashSet<String> JAVA8_STD_PACKAGES = fetchJava8Packages();

    /**
     * Performs a GET request on ORACLE_URL and scrapes
     * the page content to build and return a set of
     * package names.
     *
     * @return a set of Java 8 standard packages
     */
    private HashSet<String> fetchJava8Packages() {
        final HashSet<String> packages = new HashSet<>();

        // Attempt to get list of std packages
        try{
            // Attempt to perform a GET request on ORACLE_URL
            final HttpURLConnection connection = Parser.queryURL(STD_LIB_URL, false);

            // If page cannot be reached, log failure and return empty set
            if(connection.getResponseCode() != 200) {
                log(LOG_TYPE.ERROR, "Failed to fetch package list");
                return packages;
            }

            // Otherwise, parse page data
            BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                    StandardCharsets.UTF_8));

            // Variable initialization
            String line;
            Pattern regex = Pattern.compile("^(?:<li><a href=\\\".*\\\">)([\\w\\.]*)", Pattern.MULTILINE);
            boolean found = false; // Waits until package list is found on page

            // Parsing loop
            while ((line = r.readLine()) != null) {
                // Wait for line containing page element parent to package list
                if(!found) {
                    if(line.equals("<div class=\"indexContainer\">")) {
                        found = true;
                    }
                }
                // Once found, begin capturing packages
                else {
                    // Check line against regex
                    Matcher m = regex.matcher(line);

                    // If package is found add to hashset
                    if(m.find() && m.group(1) != null) {
                        packages.add(m.group(1));
                    }
                }
            }
        } catch (Exception e){
            log(LOG_TYPE.EXCEPTION, e);
        }

        // Return set of all found packages
        return packages;
    }

    ///
    /// Abstract Method Implementation
    ///


    /**
     * Using the fetched list of Java 8 standard
     * packages, check if a component either is
     * from one of these packages or is one itself.
     *
     * @param component component to search for
     * @return true if language, false otherwise
     */
    @Override
    protected boolean isLanguageComponent(SVIPComponentBuilder component) {
        // If component is a standard package, return true
        if(JAVA8_STD_PACKAGES.contains(getName(component).replace('/', '.'))) return true;
        else if(getGroup(component) == null) return false;

        // Otherwise, check if component is from a standard package
        return JAVA8_STD_PACKAGES.contains(getGroup(component).replace('/', '.'));
    }


    /**
     * Get the Java regex Pattern to check against the file
     *
     * @return the Java regex Pattern
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
        This doesn’t include anything surrounded by multi line comments even if the import is on a different line.
         */
        return Pattern.compile("^(?:(?!//).)*import(?: static)?(.*?)([\\w\\*]*);(?![^\\/\\*]*\\*\\/)", Pattern.MULTILINE);
    }


    /**
     * Given a regex match, parse the result accordingly to get the correct component information
     *
     * @param matcher regex match pattern
     * @return new component
     */
    @Override
    protected void parseRegexMatch(List<SVIPComponentBuilder> components, Matcher matcher) {
        SVIPComponentBuilder builder = new SVIPComponentBuilder();
        builder.setType("EXTERNAL"); // Default to EXTERNAL

        // Clean strings
        String match1 = matcher.group(1).trim();
        String match2 = matcher.group(2);

        // If valid import is found, matcher.group(2) will be true
        if(match2 != null) {
            // Check if import is package (java.awt.color)
            if(Character.isLowerCase(match2.charAt(0))) {
                // Combine capture groups to complete package name
                builder.setName((match1 + match2).replace('.', '/'));
            }
            // Otherwise, import is Class (java.awt.color.ColorSpace)
            else {
                // Set name to group 2
                builder.setName(match2);

                // If group 1 exists, it is the package that has been imported from
                if(!match1.equals("")) {
                    // Set from to group 1 (and trim last ".")
                    builder.setGroup(match1.substring(0, match1.length() - 1).replace('.', '/'));
                }
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
}
