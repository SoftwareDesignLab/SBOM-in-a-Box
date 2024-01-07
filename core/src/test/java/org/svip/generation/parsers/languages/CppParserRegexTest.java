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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.model.objects.SVIPComponentObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing for C++
 *
 * @author Derek Garcia
 */
class CppParserRegexTest extends ParseRegexTestCore {
    /**
     * Constructor initializes a given parser and assigns both the
     * regex to test it against and the source directory to test on.
     *
     */
    public CppParserRegexTest() {
        super(new CppParser(),
                "(?=//).*|(?=/\\*)[\\S\\s]*?\\*/|#include.*(?:(?=<.*>)<(.*)>|(?=\".*\")\"(.*)\")",
                "CPlusPlus");
    }
    //
    // Basic
    //

    @Test
    @DisplayName("#include <foo>")
    void includeBasic() {
        Matcher m = getMatcher("#include <foo>");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("foo", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
//        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("#include<foo>")
    void includeBasicNoSpace() {
        Matcher m = getMatcher("#include<foo>");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("foo", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
//        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// Language
    ///

    @Test
    @DisplayName("#include <ios>")
    void includeLanguage() {
        Matcher m = getMatcher("#include <ios>");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("ios", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
//        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("#include <ctype.h>")
    void includeCLib() {
        Matcher m = getMatcher("#include <ctype.h>");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("ctype.h", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
//        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// Internal
    ///

    @Test
    @DisplayName("#include \"foo.h\"")
    void includeInternal() {
        Matcher m = getMatcher("#include \"foo.h\"");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("foo.h", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
//        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("#include \"foobar.h\"")
    void includeFalseInternal() {
        Matcher m = getMatcher("#include \"foobar.h\"");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("foobar.h", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
//        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("#include\"foo.h\"")
    void includeInternalNoSpace() {
        Matcher m = getMatcher("#include\"foo.h\"");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("foo.h", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
//        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("#include \"foobar\\bar.h\"")
    void includeInternalPath() {
        Matcher m = getMatcher("#include \"foobar\\bar.h\"");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("foobar\\bar.h", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
//        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// False Cases
    ///

    @Test
    @DisplayName("<foo>")
    void angleBrackets() {
        Matcher m = getMatcher("<foo>");

        assertFalse(m.find());   // Should be a match
    }

    @Test
    @DisplayName("\"foo.h\"")
    void quotes() {
        Matcher m = getMatcher("\"foo.h\"");

        assertFalse(m.find());   // Should be a match
    }

    ///
    /// Comments
    ///

    @Test
    @DisplayName("// #include <foo>")
    void includeDoubleSlash() {
        Matcher m = getMatcher("// #include <foo>");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(0, results.size());
    }

    @Test
    @DisplayName("/*\n#include <foo>\n*/")
    void includeBlockComment() {
        Matcher m = getMatcher("/*\n#include <foo>\n*/");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(0, results.size());
    }


}