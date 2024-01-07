/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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
* /

package org.svip.generation.parsers.languages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.model.objects.SVIPComponentObject;

import java.util.ArrayList;
import java.util.List;
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
                "Ruby");
    }

    ///
    /// External
    ///

    @Test
    @DisplayName("require fee")
    void requireBasic() {
        Matcher m = getMatcher("require fee");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertEquals(0, c.getDepth());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("load fee")
    void loadBasic() {
        Matcher m = getMatcher("load fee");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertEquals(0, c.getDepth());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("autoload fee")
    void loadAuto() {
        Matcher m = getMatcher("autoload fee");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertEquals(0, c.getDepth());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require bar/fee")
    void requireMultipart() {
        Matcher m = getMatcher("require bar/fee");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        // assertEquals(0, c.getDepth());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require bar/foo/fee")
    void requireLongMultipart() {
        Matcher m = getMatcher("require bar/foo/fee");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("bar/foo", c.getGroup());
        // assertEquals(0, c.getDepth());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require 'bar/fee'")
    void requireSingleQuotes() {
        Matcher m = getMatcher("require 'bar/fee'");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        // assertEquals(0, c.getDepth());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require \"bar/fee\"")
    void requireDoubleQuotes() {
        Matcher m = getMatcher("require \"bar/fee\"");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        // assertEquals(0, c.getDepth());
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
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("bar", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("lib", c.getGroup());
        // assertEquals(0, c.getDepth());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require './bar'")
    void requireInternalSingleQuotes() {
        Matcher m = getMatcher("require './bar'");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("bar", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertEquals(0, c.getDepth());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require \"lib/bar\"")
    void requireInternalDoubleQuotes() {
        Matcher m = getMatcher("require \"lib/bar\"");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("bar", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("lib", c.getGroup());
        // assertEquals(0, c.getDepth());
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
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("session", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("cgi", c.getGroup());
        // assertEquals(0, c.getDepth());
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
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        // assertEquals(0, c.getDepth());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("=begin=end load bar/fee =begin=end")
    void loadBetweenBlockComments() {
        Matcher m = getMatcher("=begin=end load bar/fee =begin=end");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        // assertEquals(0, c.getDepth());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require bar/fee # this imports fee")
    void requireBeforeHashtag() {
        Matcher m = getMatcher("require bar/fee # this imports fee");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        // assertEquals(0, c.getDepth());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("load bar/fee # this imports fee")
    void loadBeforeHashtag() {
        Matcher m = getMatcher("load bar/fee # this imports fee");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        // assertEquals(0, c.getDepth());
        // assertNull(c.getChildren());
    }
}
