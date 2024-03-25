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
