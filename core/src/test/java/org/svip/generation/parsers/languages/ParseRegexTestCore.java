package org.svip.generation.parsers.languages;

import org.svip.generation.parsers.ParserTestCore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>File</b>: ParseRegexTestCore.java<br>
 * <b>Description</b>: Abstract test core for testing the regex of
 * language parsers.
 *
 * @author Dylan Mulligan
 */
public abstract class ParseRegexTestCore extends ParserTestCore<LanguageParser> {
    // Stores the regex Pattern to test PARSER against
    protected Pattern REGEX;

    /**
     * Constructor calls super with parser and src, and compiles a
     * given regex String into a Pattern object for testing.
     *
     * @param parser Parser object to be tested
     * @param regex Regex to test against
     * @param src Relative path to dummy directory
     */
    public ParseRegexTestCore(LanguageParser parser, String regex, String src) {
        // Call super
        super(parser, TEST_DATA_PATH + src);

        // Compile and store regex Pattern
        setRegex(regex);
    }

    /**
     * Sets regex String to test parser against.
     *
     * @param regex regex String to test parser against
     */
    private void setRegex(String regex) { this.REGEX = Pattern.compile(regex, Pattern.MULTILINE); }

    /**
     * Get a matcher object with the results of
     * applying this.REGEX to the given string.
     *
     * @param string String to be searched
     * @return a Matcher object with the results of the Pattern application
     */
    protected Matcher getMatcher(String string) {
        return this.REGEX.matcher(string);
    }
}
