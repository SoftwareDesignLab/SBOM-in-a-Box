package org.svip.sbomgeneration.parsers.languages;

import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbomgeneration.parsers.Parser;
import org.svip.utils.VirtualPath;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.svip.utils.Debug.LOG_TYPE;
import static org.svip.utils.Debug.log;


/**
 * <b>File</b>: PythonParser.java<br>
 * <b>Description</b>: Language specific implementation of Parser (Python)
 *
 * @author Dylan Mulligan
 * @author Derek Garcia
 * @author Ian Dunn
 */
public class PythonParser extends LanguageParser {

    public PythonParser() { super("https://docs.python.org/3/library/"); }

    /**
     * Checks for relative pathing and replaces with directory names.
     *
     * @param rawPath raw path to be formatted
     * @return a formatted path
     */
    private String formatPath(String rawPath) {
        // TODO this entire method **should** be able to be replaced by a VirtualPath instance
        int dirNum;
        if(rawPath.startsWith("..")) dirNum = 2; // Up one directory (second to last)
        else if(rawPath.startsWith(".")) dirNum = 1; // Current directory (last)
        else return rawPath; // Otherwise, no formatting to be done

        // Adjust path relatively and get terminus directory
        if(dirNum == 2) this.PWD = this.PWD.getParent();

        // Extract dir name and file extn
        String dir = this.PWD.getFileName().toString();
        final int extnIndex = dir.indexOf('.');
        if(extnIndex != -1) dir = dir.substring(0, extnIndex);

        // If token is only ".", set it to directory name
        if(rawPath.equals(".") || rawPath.equals("..")) return dir;
            // Otherwise, prepend directory name and a slash
        else return dir + "." + rawPath.substring(dirNum);
    }

    /**
     * Checks if component is provided by Python
     *
     * @param component component to search for
     * @return true if language, false otherwise
     */
    @Override
    protected boolean isLanguageComponent(SVIPComponentBuilder component) {
        // Get correct target
        final String endpoint = getGroup(component) != null ? getGroup(component) : getName(component);

        // Return connection response (200 = true, else = false)
        try {
            // Replace '/' with '.' since all Python submodule pages are top-level
            return Parser.queryURL(this.STD_LIB_URL + endpoint.replace('/', '.') + ".html", false).getResponseCode() == 200;
        }
        // If an error is thrown, return false
        catch (Exception ignored) { return false; }
    }

    /**
     * Get the Python regex to check against the file
     *
     * @return Python regex
     */
    @Override
    protected Pattern getRegex() {
        /*
        Regex Breakdown
        ^(?:(?!#))
        Don’t match anything after the #

        (?:[ \t]*(?:from ([\w.]*) )?import (?:(?=.*\(\n?)\(([\S\s]*?)\)|([\w. ,\*]*)))
        If there is a from, then group that, and then group the import statement with either () or normal. It also groups the alias.

        Regex101: https://regex101.com/r/AFW8kj/6
         */
        return Pattern.compile("^(?:(?!#))(?:[ \\t]*(?:from ([\\w.]*) )?import (?:(?=.*\\(\\n?)\\(([\\S\\s]*?)\\)|([\\w. ,\\*]*)))", Pattern.MULTILINE);
    }

    /**
     * Given a regex match, parse the result accordingly to get the correct component information
     *
     * @param matcher regex match pattern
     * @return new component
     */
    @Override
    protected void parseRegexMatch(List<SVIPComponentBuilder> components, Matcher matcher) {
        // Match for Group 2 and Group 3
        String match;
        if (matcher.group(2) != null) match = matcher.group(2);
        else if (matcher.group(3) != null) match = matcher.group(3);
        else {
            log(LOG_TYPE.WARN, "Match has no Groups; Skipping. . ."); // warn because this may be intentional
            return;
        }


        // 1. Clean string
        match = match
                .replace("\r", "")  // rm carriage return
                .replace("\n", "")  // rm newline
                .replace("\t", "")  // rm tab
                .trim();                            // trim whitespace

        // 2. Tokenize
        String[] tokens = match.split(","); // foo, bar as b, etc

        // 3. Parse tokens
        // Ex: import foo, bar -> "foo" and "bar"
        final VirtualPath tempPwd = this.PWD;
        boolean internal = false; // Checks for any .. filesystem references in case we can't see the outer directory
        for (String token : tokens) {
            token = token.trim();   // remove leading/trailing whitespace

            // Initialize variables
            SVIPComponentBuilder builder = new SVIPComponentBuilder();
            builder.setType("EXTERNAL"); // Default to EXTERNAL
            String name;
            String from = null;
            String alias;

            // If import is in the form "import foo"|"import foo as f"|"import foo.bar.baz as b"|"import (foo, bar, baz)"
            if(matcher.group(1) == null) {
                if(token.contains("..")) internal = true;
                token = formatPath(token);

                final String[] innerTokens = token.split("\\."); // Split on "."
                if(innerTokens.length > 1) { // Ensure more than 1 innerToken exists
                    // Set from to all but the last token in innerTokens rejoined on "/"
                    from = String.join("/", Arrays.copyOfRange(innerTokens, 0, innerTokens.length - 1));
                }
                // If no name is present, skip match
                if(innerTokens[innerTokens.length - 1].equals("")) continue;
                // Set name to last token
                name = innerTokens[innerTokens.length - 1];
            } else { // Otherwise: "from foo import bar"|"from foo.fee import bar as b"|"from foo import (fee, bar, baz)"
                if(matcher.group(1).equals(".") || matcher.group(1).equals("..")) internal = true;
                // Set name to token
                name = token;
                // Set from to Group 1 after replacing '.' with '/'
                from = formatPath(matcher.group(1)).replace('.', '/');
            }

            // Matcher captures name in Group 1 and import (optionally) in Group 2
            // Raw string: Match: Group 1  Group 2
            // "foo as f"   ->    "foo"    "f"
            // "bar"        ->    "bar"
            // Regex101: https://regex101.com/r/CGa0gG/1
            Pattern aliasRegex = Pattern.compile("^([\\w\\*/]*)(?: as (\\w*))?", Pattern.MULTILINE);
            Matcher aliasMatcher = aliasRegex.matcher(name);

            // Ensure match is successful
            if (aliasMatcher.find()) {
                name = aliasMatcher.group(1); // Name: import foo as f; foo = group(1)
                alias = aliasMatcher.group(2); // Alias: import foo as f; f = group(2)
            } else { // Otherwise, log and skip this token
                log(LOG_TYPE.WARN, "Match has no Groups; Skipping. . .");
                continue;
            }

            builder.setName(name); // Create Component
            if(from != null && !from.equals("/")) builder.setGroup(from);
//            if(alias != null) c.setAlias(alias); TODO

            // Check if internal
            if (isInternalComponent(builder) || internal) {
                builder.setType("INTERNAL");

                // Otherwise check if Language
            } else if (isLanguageComponent(builder)) {
                builder.setType("LANGUAGE");
            }
            // Add Component
            components.add(builder);
            // Set PWD back to stored value
            this.PWD = tempPwd;
        }
    }
}
