package org.svip.sbomfactory.generators.parsers.languages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.util.ArrayList;
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
                "src/test/java/org/svip/sbomfactory/generators/TestData/CPlusPlus");
    }
    //
    // Basic
    //

    @Test
    @DisplayName("#include <foo>")
    void includeBasic() {
        Matcher m = getMatcher("#include <foo>");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("foo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("#include<foo>")
    void includeBasicNoSpace() {
        Matcher m = getMatcher("#include<foo>");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("foo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
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
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("ios", c.getName());
        assertEquals(ParserComponent.Type.LANGUAGE, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("#include <ctype.h>")
    void includeCLib() {
        Matcher m = getMatcher("#include <ctype.h>");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("ctype.h", c.getName());
        assertEquals(ParserComponent.Type.LANGUAGE, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
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
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("foo.h", c.getName());
        assertEquals(ParserComponent.Type.INTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("#include \"foobar.h\"")
    void includeFalseInternal() {
        Matcher m = getMatcher("#include \"foobar.h\"");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("foobar.h", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("#include\"foo.h\"")
    void includeInternalNoSpace() {
        Matcher m = getMatcher("#include\"foo.h\"");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("foo.h", c.getName());
        assertEquals(ParserComponent.Type.INTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("#include \"foobar\\bar.h\"")
    void includeInternalPath() {
        Matcher m = getMatcher("#include \"foobar\\bar.h\"");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("foobar\\bar.h", c.getName());
        assertEquals(ParserComponent.Type.INTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
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
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(0, results.size());
    }

    @Test
    @DisplayName("/*\n#include <foo>\n*/")
    void includeBlockComment() {
        Matcher m = getMatcher("/*\n#include <foo>\n*/");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(0, results.size());
    }


}