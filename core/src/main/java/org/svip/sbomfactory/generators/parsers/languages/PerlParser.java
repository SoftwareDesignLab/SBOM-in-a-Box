package parsers.languages;

import utils.ParserComponent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static utils.Debug.LOG_TYPE;
import static utils.Debug.log;


/**
 * file: PerlParser.java
 * Description: Language specific implementation of the ParserCore (Perl)
 *
 * @author Dylan Mulligan
 */
public class PerlParser extends LanguageParser {
    public PerlParser() { super("https://perldoc.perl.org/"); }

    /**
     * Determines if the given string is representative
     * of a version (in the form "1.2.3.4..." or "v1.2.3.4...")
     * @param string string to analyze
     * @return true if string is version, false otherwise
     */
    private boolean isVersion(String string) {

        // Split string on "." to tokenize then join elements back to one string
        // This will convert versions from this: "v1.2.3"/"1.2.3" to this: "v123"/"123"
        string = String.join("", string.split("\\."));

        // If string is longer than 1 character, check for leading "v" and strip if found
        if(string.length() >= 2 && string.charAt(0) == 'v') {
            // Check if starts with "v" and strip if so
            string = string.substring(1);
        }

        // Any valid versions should now be in the form of a valid double (technically integer also)
        // "123"
        try {
            // Attempt to parse string to double
            Double.parseDouble(string);

            // Return true if successful
            return true;
        } catch (Exception ignored) { }

        // Otherwise, return false
        return false;
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
    protected boolean isInternalComponent(ParserComponent component) {
        // Target is component name + ".pm"
        final String target = component.getName() + ".pm";

        // Get project path from this.src and walk files to find component
        try (Stream<Path> stream = Files.walk(this.PWD)) {
            return stream.anyMatch(file -> file.getFileName().toString().toLowerCase().equals(target));
        } catch (Exception e){
            log(LOG_TYPE.EXCEPTION, e);
        }

        return false;
    }


    /**
     * Checks the Perl standard module API for the existence
     * of the given component as a standard language module.
     *
     * @param component component to search for
     * @return true if language, false otherwise
     */
    @Override
    protected boolean isLanguageComponent(ParserComponent component) {
        try {
            String endpoint;
            // If from is defined, build URL with from and name
            // Replace "/" with "::"
            if(component.getGroup() != null) endpoint = String.join("::", component.getGroup().split("/")) + "::" + component.getName();
            else endpoint = component.getName();

            // Return connection response (200 = true, else = false)
            try { return queryURL(STD_LIB_URL + endpoint, false).getResponseCode() == 200; }
            // If an error is thrown, return false
            catch (Exception ignored) { return false; }

        } catch (Exception e) {
            log(LOG_TYPE.EXCEPTION, e);
            // If error occurs, return false;
            return false;
        }
    }


    /**
     * Get the Perl regex Pattern to check against the file
     *
     * @return the Perl regex Pattern
     */
    @Override
    protected Pattern getRegex() {
        //^(?:(?!#).)*
        //Checks and ignores anything after and including #

        //(?:(?:use (?:(?=if).*, [ '\"]*(\w+)[ '\"]|(?:[ '\"]*(.+)[ '\"]*));|(no v?[\d.]+))
        //Checks for the ‘use’ keyword, and gets the package w/o [‘ “] (except for alias imports). It also checks to see if the import is conditional and selects just the package instead of the whole if statement.
        //It also selects “no [digit],” which means that you are using Perl v[digit] or greater to compile this section

        //require [ '\"]*([\$\w.]+)[ '\"]*(?:(?=.*->VERSION).* .*->VERSION\(([\w.]*)|))
        //Checks for the “require” keyword and gets the package w/o [‘ “]. It also gets the version number if applicable

        //(?![^\=]*\=(?:cut|end))
        //Multi-line comments. Since it isn’t officially supported, perl has a way to get multi-line comments using =begin comment here =cut; which counts.
        //From what I can tell, regex does not let me differentiate between equal signs so this section has some issues
        //Eg =begin var = 10 =end; will not work, but that section won’t be picked up so as long as there isn’t a rogue equal sign after a commented import statement we should be good.
        //Worst case scenario it includes a package that isn’t in the program

        return Pattern.compile("^(?:(?!#).)*(?:(?:use (?:(?=if).*, [ '\\\"]*(\\w+)[ '\\\"]|(?:(?=.*alias.*)[ '\\\"]*(.+)[ '\\\"]*|[ '\\\"]*(.+)[ '\\\"]*));)|require [ '\\\"]*([\\$\\w./:]+)[ '\\\"]*(?:.*->VERSION\\(([\\w.]*)|.*->import\\((.*)\\)|))(?![^\\=]*\\=(?:cut|end))", Pattern.MULTILINE);
    }


    /**
     * Given a regex match, parse the result accordingly to get the correct component information
     *
     * @param matcher regex match pattern
     * @return new component
     */
    @Override
    protected void parseRegexMatch(ArrayList<ParserComponent> components, Matcher matcher) {
        // Initialize variables
        final HashSet<String> KEYWORDS = new HashSet<>(Arrays.asList(
                "strict", "warning", "integer", "bytes", "constant"
        )); // List of keywords to ignore from matches
        ParserComponent c; // ParserComponent declaration
        String match; // Match to parse
        int groupNum = 0; // Group counter

        // Check for groups 1-4, stop when a match is found
        do {
            groupNum++;
            if(groupNum > 4) return; // If nothing is found, return
        } while((match = matcher.group(groupNum)) == null);

        // Clean string
        match = match
                .replace("(", "")  // rm open parentheses
                .replace(")", "")  // rm close parentheses
                .replace("\r", "")  // rm carriage return
                .replace("\n", "")  // rm newline
                .trim();                            // rm leading/trailing whitespace

        // Determine if default import
        if(KEYWORDS.contains(match)) return; // If default import, return

        // Determine if Perl version declaration
        if(isVersion(match)) return; // If Perl version declaration, return

        // Determine if alias is present
        // Regex101 link: https://regex101.com/r/qatI67/1
        Pattern aliasRegex = Pattern.compile("^(?:package::alias) '([\\w:]*)' => '([\\w:]*)'|(?:aliased|namespace::alias) '([\\w:]*)' => '([\\w:]*)'", Pattern.MULTILINE);
        Matcher aliasMatcher = aliasRegex.matcher(match);

        String alias = null;
        String version = null;
        String from = null;
        if(aliasMatcher.find()) {
            // Alias edge case #1
            //                 Group 1   Group 2
            // (package::alias 'Fbbq' => 'Foo::Barista::Bazoo::Qux';)
            if(aliasMatcher.group(1) != null) {
                match = aliasMatcher.group(2);
                alias = aliasMatcher.group(1);
            }
            // Alias edge cases #2 & #3
            //          Group 3                       Group 4
            // (aliased 'Foo::Barista::Bazoo::Qux' => 'Fbbq';)
            //                   Group 3                       Group 4
            // (namespace::alias 'Foo::Barista::Bazoo::Qux' => 'Fbbq';)
            else if(aliasMatcher.group(4) != null) {
                match = aliasMatcher.group(3);
                alias = aliasMatcher.group(4);
            }
        }
        // Otherwise, normal import with no alias
        else {
            // Determine if module version declaration is present
            String[] tokens = match.split(" ");

            // If more than one token is found
            if(tokens.length > 1) {
                match = tokens[0]; // tokens[0] is the main match
                // Check if second token is a version and store it if so
                if(isVersion(tokens[1])) version = tokens[1];
            }

            // Check for version declarations in group 5
            if (matcher.group(5) != null) {
                if(isVersion(matcher.group(5))) version = matcher.group(5);
            }
            // If group 6 is found, a sub-dependency is being imported
            else if(matcher.group(6) != null) {
                // Store match as from then reassign match to group 6
                from = match; // from should be group 4
                match = matcher.group(6).trim();
            }
        }


        // If match has any number of delineations, use the last one
        //as the name and all but the last one as the from.
        if(from == null) {
            // If no from is found explicitly, check match for delineations on "::"
            if(match.contains("::")) {
                String[] tokens = match.split("::");
                match = tokens[tokens.length - 1];
                from = String.join("/", Arrays.copyOfRange(tokens, 0, tokens.length - 1));
            }
            // If no "::" is found, check match for delineations on "/"
            else if (match.contains("/")) {
                String[] tokens = match.split("/");
                match = tokens[tokens.length - 1];
                // Check for existing ".pm" file extension
                String extension = match.substring(match.length() - 3);
                // If found, strip extension as it will be added later in isInternalComponent
                match = extension.equals(".pm") ? match.substring(0, match.length() - 3) : match;
                from = String.join("/", Arrays.copyOfRange(tokens, 0, tokens.length - 1));
            }
        }

        // Construct component
        c = new ParserComponent(match);
        if(from != null) c.setGroup(from.replace("::", "/")); // If a from has been found, assign it to c
        if(alias != null) c.setAlias(alias); // If an alias has been found, assign it to c
        if(version != null) c.setVersion(version); // If a version has been found, assign it to c


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
