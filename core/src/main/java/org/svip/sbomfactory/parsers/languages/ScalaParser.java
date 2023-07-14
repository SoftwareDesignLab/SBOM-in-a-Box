package org.svip.sbomfactory.parsers.languages;

import org.svip.builders.component.SVIPComponentBuilder;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbomfactory.parsers.Parser;

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
 */
public class ScalaParser extends LanguageParser {
    // Scala API
    public ScalaParser() { super("https://www.scala-lang.org/api/2.12.5/"); }

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
    protected boolean isLanguageComponent(SVIPComponentObject component) {
        // Attempt to find component
        try{
            // Initialize variables
            StringBuilder urlSB = new StringBuilder(STD_LIB_URL);
            final String from = component.getGroup();
            final String name = component.getName();

            // If component is not capitalized, component is package
            if(from == null) {
                // Append component name to URL
                urlSB.append(name);
                // package URLs end in "package/name/index.html"
                urlSB.append("/index");
            }
            // Otherwise, if component is not "*", component is Class
            else if(!name.equals("*")) {
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
        } catch (Exception e){
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
    protected void parseRegexMatch(List<SVIPComponentObject> components, Matcher matcher) {
        // Variable initialization
        SVIPComponentBuilder builder = new SVIPComponentBuilder();
        builder.setType("EXTERNAL"); // Default to EXTERNAL
        Pattern aliasRegex = Pattern.compile("^([\\w\\*]*)(?: => | as )?(\\w*)?", Pattern.MULTILINE);
        String match = "";

        // Import validation
        if(matcher.group(2) != null) match = matcher.group(2); // Look for Scala2 imports "bar.{foo => f, baz as b}"
        else if(matcher.group(3) != null) match = matcher.group(3); // Look for Scala3 imports "bar.foo as f"
        else log(LOG_TYPE.WARN, "Match (" + matcher.group(0) + ") has no Groups; Skipping. . ."); // Otherwise, invalid import

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
            if(aliasMatcher.find() && !aliasMatcher.group(1).equals("")) {
                // If component is not capitalized, component is package
                if(Character.isLowerCase(aliasMatcher.group(1).charAt(0))) {
                    // Create component with original matcher group 1 combined with aliasMatcher group 1
                    builder.setName(matcher.group(1).replace(".", "/") + "/" + aliasMatcher.group(1));
                }
                // Otherwise, component is Class
                else {
                    // Change "import bar._" to "import bar.*" for consistency
                    String name = aliasMatcher.group(1);
                    if(name.equals("_")) name = "*";

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
                if (isInternalComponent(builder.build())) {
                    builder.setType("INTERNAL");

                // Otherwise, check if Language
                } else if (isLanguageComponent(builder.build())) {
                    builder.setType("LANGUAGE");
                }

                // Add Component
                components.add(builder.build());
            }
        }
    }
}
