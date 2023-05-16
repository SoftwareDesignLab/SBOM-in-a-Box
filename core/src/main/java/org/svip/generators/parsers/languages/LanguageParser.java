package org.svip.generators.parsers.languages;

import org.svip.generators.parsers.Parser;
import org.svip.generators.utils.Debug;
import org.svip.generators.utils.ParserComponent;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>File</b>: LanguageParser.java<br>
 * <b>Description</b>: Abstract core Class for language file parsers.
 * This handles all general parsing logic and defines the required
 * methods to be implemented for child LanguageParser instances.
 *
 * @author Dylan Mulligan
 * @author Derek Garcia
 */
public abstract class LanguageParser extends Parser {
    /**
     * Protected Constructor meant for use by language implementations
     * to store their language-specific static values in their respective
     * attributes.
     *
     * @param STD_LIB_URL a URL to the Standard Language Library of the language
     *                    parser
     */
    protected LanguageParser(String STD_LIB_URL) {
        super(STD_LIB_URL);
    }

    //#region Abstract Methods For Language Specific Implementation

    /**
     * Determines if the component is Internal
     *
     * @param component component to search for
     * @return true if internal, false otherwise
     */
    protected abstract boolean isInternalComponent(ParserComponent component);

    /**
     * Determines if the component is from the language maintainers
     * This will require access to some sort of database to check against
     *
     * @param component component to search for
     * @return true if language, false otherwise
     */
    protected abstract boolean isLanguageComponent(ParserComponent component);

    /**
     * Get the regex to parse with.
     * Implementation: return Pattern.compile("REGEX", Pattern.MULTILINE);
     *
     * @return a list of language specific regexes
     */
    protected abstract Pattern getRegex();

    /**
     * Given a regex match, parse the result accordingly to get the correct component information
     * and append found components to the provided ArrayList.
     *
     * @param components A list of ParserComponents that the found components will be appended to
     * @param matcher regex match pattern
     */
    protected abstract void parseRegexMatch(ArrayList<ParserComponent> components, Matcher matcher);

    //#endregion

    @Override
    public void parse(ArrayList<ParserComponent> components, String fileContents) {
        // Get regex
        final Matcher m = getRegex().matcher(fileContents);

        // Continue testing until find all matches
        while (m.find()) {
            Debug.log(Debug.LOG_TYPE.DEBUG, String.format("MATCH: [ %s ]; FILE: [ %s ]", m.group(0), this.PWD));

            // Parse match
            final long t1 = System.currentTimeMillis();

            parseRegexMatch(components, m);

            final long t2 = System.currentTimeMillis();
            Debug.log(Debug.LOG_TYPE.DEBUG, String.format("Component parsing done in %s ms.", t2 - t1));
        }
    }
}
