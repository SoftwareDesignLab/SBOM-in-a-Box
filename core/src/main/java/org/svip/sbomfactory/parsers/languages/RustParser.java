package org.svip.sbomfactory.parsers.languages;

import org.svip.builders.component.SVIPComponentBuilder;
import org.svip.sbom.model.objects.SVIPComponentObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.svip.utils.Debug.LOG_TYPE;
import static org.svip.utils.Debug.log;


/**
 * file: RustParser.java
 * Description: Language specific implementation of the ParserCore (Rust)
 *
 * @author Dylan Mulligan
 */
public class RustParser extends LanguageParser {
    public RustParser() { super("https://doc.rust-lang.org/"); }

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
            final StringBuilder urlSB = new StringBuilder(STD_LIB_URL);
            final String from = component.getGroup();
            final String name = component.getName();

            // Append component from to URL
            if(from != null) {
                urlSB.append(from);
                urlSB.append("/");
            }

            // If name is "*", import is module
            if(name.equals("*")) {
                urlSB.append("index.html");
            }
            // If component is not capitalized, component is function
            else if(Character.isLowerCase(name.charAt(0))) {
                // Function urls end in /fn.function_name.html
                // Append "fn.component_name.html" to URL
                urlSB.append("fn.");
                urlSB.append(name);
                urlSB.append(".html");
            }
            // Otherwise, component is either a Macro, Struct, Enum, Trait, or Type
            else {
                // Variable initialization
                final ArrayList<String> types = new ArrayList<>(Arrays.asList(
                        "struct", "macro", "trait", "enum", "type"
                ));
                final Iterator<String> iter = types.iterator();
                String temp; // Temp string to hold StringBuilder contents

                while (iter.hasNext()) { // Iterate through all link types defined in "types"
                    // Store urlSB contents before modification so that it can be reset to this state
                    temp = urlSB.toString();

                    // These urls end in /type_of_component.component_name.html
                    urlSB.append(iter.next()); // type_of_component
                    urlSB.append("."); // .
                    urlSB.append(name); // component_name
                    urlSB.append(".html"); // .html

                    // Attempt to connect and return true if successful
                    if(queryURL(urlSB.toString(), false).getResponseCode() == 200) return true;
                    // Otherwise, trim modified URL back to original state
                    else urlSB.setLength(temp.length());
                }
                // If no successful connection was found, return false
                return false;
            }

            // Test to see if page exists.
            return queryURL(urlSB.toString(), false).getResponseCode() == 200;
        } catch (Exception e){
            log(LOG_TYPE.EXCEPTION, e);
        }
        return false;
    }


    /**
     * Get the Rust regex Pattern to check against the file
     *
     * @return the Rust regex Pattern
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

         Regex101: https://regex101.com/r/ZjaqkI/6
         */
        return Pattern.compile("^(?:(?!//).)*(?:use ([ '\\\"\\w:*]*)(?:::([\\w* ]+)|(?:(?=.*\\{\\n?)(?:\\{([\\S\\s]*?))\\};))|(?:extern ([\\w ]*)|mod ([\\w:]*));)(?![^\\/\\*]*\\*\\/)", Pattern.MULTILINE);
    }


    /**
     * Given a regex match, parse the result accordingly to get the correct component information
     *
     * @param matcher regex match pattern
     * @return new component
     */
    @Override
    protected void parseRegexMatch(List<SVIPComponentObject> components, Matcher matcher) {
        // LinkedHashMap used to maintain match order for testing and output consistency
        final LinkedHashMap<String, String> matches = new LinkedHashMap<>();
        SVIPComponentBuilder builder = new SVIPComponentBuilder();
        String match;

        // Set from to group 1 (crate being imported from)
        String from = matcher.group(1); // Can be null

        // Import validation
        // Look for imports "use crate::foo::bar;"
        if(matcher.group(2) != null) match = matcher.group(2);
        // Look for imports "use std::path::{self, Path, PathBuf};"
        else if(matcher.group(3) != null) match = matcher.group(3);
        else if(matcher.group(4) != null) { // Look for imports "extern crate bar"
            // Split on " ", since " " must be contained is group 4 matched
            String[] tokens = matcher.group(4).trim().split(" "); // There must be >= 2 tokens
            match = tokens[tokens.length - 1]; // Name of component will be last token
            // From will be the preceding tokens rejoined on "::"
            from = String.join("::", Arrays.copyOfRange(tokens, 0, tokens.length - 1));
        } else if(matcher.group(5) != null) { // Look for imports "mod bar" | "mod crate::bar"
            if(matcher.group(5).contains("::")) {
                // Split on "::", since "::" must be contained, there must be >= 2 tokens
                String[] tokens = matcher.group(5).trim().split("::");
                match = tokens[tokens.length - 1]; // Name of component will be last token
                // From will be the preceding tokens rejoined on "/"
                from = String.join("::", Arrays.copyOfRange(tokens, 0, tokens.length - 1));
            } else match = matcher.group(5);
        } else { // Otherwise, invalid import
            log(LOG_TYPE.WARN, "Match (" + matcher.group(0) + ") has no Groups; Skipping. . .");
            return;
        }

        // Clean string
        match = match
                .replace("\r", "")  // rm carriage return
                .replace("\n", "")  // rm newline
                .trim();                            // trim whitespace
        
        // Standard imports will collect in group 2
        if (matcher.group(2) != null) {
            // Store match info in LinkedHashMap, form is "name":"from"
            matches.put(match, from);
        }
        // Handles complex imports in this form:
        // use crate::{
        //     diff::graph::{get_set_neighbours as gsn, populate_change_map, Edge, Vertex},
        //     parse::syntax::Syntax, bar::syntax,
        //     diff::graph::{briar, bob,
        //     dern}, bar::fee, boo::foo::{soo, coo,
        //     doo}
        // }; // There are 13 imports in this one statement, all with varying roots and syntax challenges
        else if (matcher.group(3) != null) {
            // bracketMatcher will match imports in this form:
                // diff::changes::ChangeMap as CM,
                // diff::graph::{get_set_neighbours as gsn, populate_change_map, Edge, Vertex},
                // parse::syntax::Syntax, bar::syntax,
                // diff::graph::{briar, bob,
                // dern}, bar::fee, boo::foo::{soo, coo,
                // doo}
            // Comma separation is the goal, however, commas within inner brackets
            //prevent simply splitting on "," without losing "from" information
            // This regex groups regular imports like this:
            //  (          Group 1           )
            // (diff::changes::ChangeMap as CM)
            // And imports containing brackets like this:
            //  (Group 1 ) (  Group 2  )
            // (boo::foo::{soo, coo, doo})
            // Regex101: https://regex101.com/r/rR7P7K/11
            Pattern bracketRegex = Pattern.compile("([\\w: ]*)(?:(?=.*\\{\\n?)(?:\\{([^{}]*?))\\})?", Pattern.MULTILINE);
            Matcher bracketMatcher = bracketRegex.matcher(match);
            // Match for regular and bracket imports
            while (bracketMatcher.find()) {
                // Store found imports in tokens
                String[] tokens;

                // If group 2 captures, import has brackets and contains multiple components
                if (bracketMatcher.group(2) != null)
                    tokens = bracketMatcher.group(2).split(","); // Split matches on ","
                // Otherwise, import is regular and only contains one component
                else tokens = new String[]{bracketMatcher.group(1)}; // Group 1 is the match

                // Compile info found about any number of imports and put in matches
                for (String token : tokens) {
                    if(bracketMatcher.group(1).trim().equals("")) continue; // Skip empty matches

                    // Concatenate existing from data with inner from data (Group 1)
                    String tempFrom = from + bracketMatcher.group(1).trim();

                    // Trim tailing "::" if it exists
                    tempFrom = tempFrom.endsWith("::") ? tempFrom.substring(0, tempFrom.length() - 2) : tempFrom;

                    // If group 2 is captured, token is already component name
                    if(bracketMatcher.group(2) != null) {
                        match = token.trim();
                    } else { // Otherwise, it may contain multiple parts
                        // Split tempFrom on "::" to tokenize
                        final String[] tempTokens = tempFrom.split("::");
                        // Set match equal to the last token
                        match = tempTokens[tempTokens.length - 1];
                        // Rejoin all of the other tokens (not last) on "::"
                        tempFrom = String.join("::", Arrays.copyOfRange(tempTokens, 0, tempTokens.length - 1));
                    }

                    // Store match info in LinkedHashMap, form is "name as n":"from::something"
                    matches.put(match.trim(), tempFrom);
                }
            }
        }
        // Check for "extern" and "mod" cases
        else if (matcher.group(4) != null || matcher.group(5) != null)
            matches.put(match.trim(), from); // Add match

        // Format, build, and add all matches to components
        for (final String name : matches.keySet()) {
            // Get "from" information from the matches LinkedHashMap with name as the key
            final String storedFrom = matches.get(name);
            // Unless "from" info is an empty string, store info for component construction
            if(storedFrom != null) from = storedFrom;

            // Matcher captures name in Group 1 and import (optionally) in Group 2
            // Raw string: Match: Group 1  Group 2
            // "foo as f"   ->    "foo"    "f"
            // "bar"        ->    "bar"
            // Regex101: https://regex101.com/r/CGa0gG/1
            Pattern aliasRegex = Pattern.compile("^([\\w\\*/]*)(?: as (\\w*))?", Pattern.MULTILINE);
            Matcher aliasMatcher = aliasRegex.matcher(name);

            // Match for name and alias
            if(aliasMatcher.find()) {
                // Set component name to group 1, given it is not "self"
                // If it is self, replace with * for output consistency
                if(aliasMatcher.group(1).equals("self")) {
                    // If from is defined, replace "self" with "*"
                    if(from != null) match = "*";
                        // Otherwise, skip match, as is it importing "*" from nothing
                    else continue;
                }
                // Otherwise, use Group 1  as name
                else match = aliasMatcher.group(1);

                builder.setName(match); // Create component
                // TODO If group 2 is not null, set alias as group 2
//                if(aliasMatcher.group(2) != null) builder.setAlias(aliasMatcher.group(2));
            } else continue; // If no match found, import must be invalid, skip component

            // "From" information formatter
            // If from is not null
            if(from != null) {
                // Replace "::" with "/"
                from = from.replace("::", "/");
                // Remove leading "/" if found
                from = from.charAt(0) == '/' ? from.substring(1) : from;
                // Remove leading "self" if found
                if(from.startsWith("self")) {
                    // If string contains more than self, remove "self/" (from = "self/bar" -> from = "bar")
                    if(from.length() > 4) from = from.substring(5);
                    // Otherwise, from can be truncated entirely (from = "self" -> from = null)
                    else from = null;
                }

                // Assign from to c
                if(from != null) builder.setGroup(from);
            }

            // Check if component is Internal
            if (isInternalComponent(builder.build())) builder.setType("INTERNAL");
            // Otherwise, check component is Language
            else if (isLanguageComponent(builder.build())) builder.setType("LANGUAGE");

            // Add Component
            components.add(builder.build());
        }
    }
}
