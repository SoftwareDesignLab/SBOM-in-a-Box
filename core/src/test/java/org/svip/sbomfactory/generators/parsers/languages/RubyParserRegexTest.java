package org.svip.sbomfactory.generators.parsers.languages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.util.ArrayList;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;

/**
 * file: RubyParserRegexTest.java
 * Description: Testing for Ruby, extends ParseRegexTestCore
 *
 * @author Dylan Mulligan
 */
public class RubyParserRegexTest extends ParseRegexTestCore {
    /**
     * Constructor initializes a given parser and assigns both the
     * regex to test it against and the source directory to test on.
     *
     */
    public RubyParserRegexTest() {
        super(new RubyParser(),
                "^(?:(?!#).)*(?:require [ '\\\"]*([\\w\\.\\/]+)[ '\\\"]*|load [ '\\\"]*([\\w\\.\\/]+)[ '\\\"]*)(?![^\\=]*\\=end)",
                "src/test/java/org/svip/sbomfactory/generators/TestData/Ruby");
    }

    ///
    /// External
    ///

    @Test
    @DisplayName("require fee")
    void requireBasic() {
        Matcher m = getMatcher("require fee");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("load fee")
    void loadBasic() {
        Matcher m = getMatcher("load fee");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("autoload fee")
    void loadAuto() {
        Matcher m = getMatcher("autoload fee");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require bar/fee")
    void requireMultipart() {
        Matcher m = getMatcher("require bar/fee");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require bar/foo/fee")
    void requireLongMultipart() {
        Matcher m = getMatcher("require bar/foo/fee");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar/foo", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require 'bar/fee'")
    void requireSingleQuotes() {
        Matcher m = getMatcher("require 'bar/fee'");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require \"bar/fee\"")
    void requireDoubleQuotes() {
        Matcher m = getMatcher("require \"bar/fee\"");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// INTERNAL
    ///

    @Test
    @DisplayName("require lib/bar")
    void requireInternal() {
        Matcher m = getMatcher("require lib/bar");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("bar", c.getName());
        assertEquals(ParserComponent.Type.INTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("lib", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require './bar'")
    void requireInternalSingleQuotes() {
        Matcher m = getMatcher("require './bar'");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("bar", c.getName());
        assertEquals(ParserComponent.Type.INTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require \"lib/bar\"")
    void requireInternalDoubleQuotes() {
        Matcher m = getMatcher("require \"lib/bar\"");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("bar", c.getName());
        assertEquals(ParserComponent.Type.INTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("lib", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// LANGUAGE
    ///

    @Test
    @DisplayName("require cgi/session")
    void requireLanguage() {
        Matcher m = getMatcher("require cgi/session");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("session", c.getName());
        assertEquals(ParserComponent.Type.LANGUAGE, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("cgi", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// Comments
    ///

    @Test
    @DisplayName("# require bar/foo")
    void requireHashtag() {
        Matcher m = getMatcher("# require bar/foo");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("=begin\nrequire \"bar\"\n=end")
    void requireBlockComment() {
        Matcher m = getMatcher("=begin\nrequire \"bar\"\n=end");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("=begin\nload \"bar\"\n=end")
    void loadBlockComment() {
        Matcher m = getMatcher("=begin\nload \"bar\"\n=end");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("=begin=end require bar/fee =begin=end")
    void requireBetweenBlockComments() {
        Matcher m = getMatcher("=begin=end require bar/fee =begin=end");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("=begin=end load bar/fee =begin=end")
    void loadBetweenBlockComments() {
        Matcher m = getMatcher("=begin=end load bar/fee =begin=end");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require bar/fee # this imports fee")
    void requireBeforeHashtag() {
        Matcher m = getMatcher("require bar/fee # this imports fee");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("load bar/fee # this imports fee")
    void loadBeforeHashtag() {
        Matcher m = getMatcher("load bar/fee # this imports fee");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }
}
