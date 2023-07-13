package org.svip.sbomfactory.parsers.languages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.util.ArrayList;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <b>File</b>: CSharpParserRegexTest.java<br>
 * <b>Description</b>: Testing for CSharpParser regex.
 *
 * @author Dylan Mulligan
 */
class CSharpParserRegexTest extends ParseRegexTestCore {
    /**
     * Constructor initializes a given parser and assigns both the
     * regex to test it against and the source directory to test on.
     *
     */
    public CSharpParserRegexTest() {
        super(new CSharpParser(),
                "^(?:(?!//).)*(?:(?:|global )using(?: static | )(?:(.*) = (?!new)|)([\\w.<> =,]*);)(?![^\\/\\*]*\\*\\/)",
                "src/test/java/org/svip/sbomfactory/generators/TestData/CSharp/Bar");
    }

    //
    // Basic
    //

    @Test
    @DisplayName("using foo;")
    void usingBasic() {
        Matcher m = getMatcher("using foo;");

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
    @DisplayName("using foo.bar;")
    void usingBasicWithDelim() {
        Matcher m = getMatcher("using foo.bar;");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("bar", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("foo", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using f = foo;")
    void usingAlias() {
        Matcher m = getMatcher("using f = foo;");

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
        assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using f = Faz.Foo.Fee<string>;")
    void usingAliasTyped() {
        Matcher m = getMatcher("using f = Faz.Foo.Fee<string>;");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("Fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("Faz/Foo", c.getGroup());
        assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using f = Faz.Foo.Fee<string>;")
    void usingAliasMultiTyped() {
        Matcher m = getMatcher("using f = Faz.Foo.Fee<string>;");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("Fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("Faz/Foo", c.getGroup());
        assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// Modifiers
    ///

    @Test
    @DisplayName("using static foo;")
    void usingStatic() {
        Matcher m = getMatcher("using static foo;");

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
    @DisplayName("global using foo;")
    void usingGlobal() {
        Matcher m = getMatcher("global using foo;");

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
    @DisplayName("global using static foo;")
    void usingGlobalStatic() {
        Matcher m = getMatcher("global using static foo;");

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
    /// Using statements
    ///

    @Test
    @DisplayName("using (var reader = new StringReader(manyLines))")
    void usingStatementParenthesis() {
        Matcher m = getMatcher("using (var reader = new StringReader(manyLines))");

        assertFalse(m.find());   // Should no matches
    }

    @Test
    @DisplayName("using StringReader reader = new StringReader(manyLines);")
    void usingStatementObject() {
        Matcher m = getMatcher("using StringReader reader = new StringReader(manyLines);");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("using var reader = new StringReader(manyLines);")
    void usingStatementVar() {
        Matcher m = getMatcher("using var reader = new StringReader(manyLines);");

        assertFalse(m.find());   // Should not be a match
    }

    ///
    /// Language
    ///

    @Test
    @DisplayName("using System.Net;")
    void usingLanguage() {
        Matcher m = getMatcher("using System.Net;");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("Net", c.getName());
        assertEquals(ParserComponent.Type.LANGUAGE, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("System", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using System.IO.Compression;")
    void usingLanguage2() {
        Matcher m = getMatcher("using System.IO.Compression;");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("Compression", c.getName());
        assertEquals(ParserComponent.Type.LANGUAGE, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("System/IO", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using s = System.Net;")
    void usingLanguageAlias() {
        Matcher m = getMatcher("using s = System.Net;");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("Net", c.getName());
        assertEquals(ParserComponent.Type.LANGUAGE, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("System", c.getGroup());
        assertEquals("s", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using f = System.Func<string>;")
    void usingLanguageAliasTyped() {
        Matcher m = getMatcher("using f = System.Func<string>;");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("Func", c.getName());
        assertEquals(ParserComponent.Type.LANGUAGE, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("System", c.getGroup());
        assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using f = System.Func<string, string>;")
    void usingLanguageAliasMultiTyped() {
        Matcher m = getMatcher("using f = System.Func<string, string>;");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("Func", c.getName());
        assertEquals(ParserComponent.Type.LANGUAGE, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("System", c.getGroup());
        assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// Internal
    ///

    @Test
    @DisplayName("using CSharp.Bar;")
    void usingInternal() {
        Matcher m = getMatcher("using CSharp.Bar;");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("Bar", c.getName());
        assertEquals(ParserComponent.Type.INTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("CSharp", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using b = CSharp.Bar;")
    void usingInternalAlias() {
        Matcher m = getMatcher("using b = CSharp.Bar;");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("Bar", c.getName());
        assertEquals(ParserComponent.Type.INTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("CSharp", c.getGroup());
        assertEquals("b", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using CSharp.Bar;")
    void usingInternalPath() {
        Matcher m = getMatcher("using CSharp.Bar;");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("Bar", c.getName());
        assertEquals(ParserComponent.Type.INTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("CSharp", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using CSharp.Bar.foobar.foofoobar;")
    void usingInternalClass() {
        Matcher m = getMatcher("using CSharp.Bar.foobar.foofoobar;");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("foofoobar", c.getName());
        assertEquals(ParserComponent.Type.INTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("CSharp/Bar/foobar", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// Comments
    ///

    @Test
    @DisplayName("// using foo;")
    void usingDoubleSlash() {
        Matcher m = getMatcher("// using foo;");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("/// using foo;")
    void usingXML() {
        Matcher m = getMatcher("/// using foo;");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("/*\n using foo;\n*/")
    void usingBlockComment() {
        Matcher m = getMatcher("/*\n using foo;\n*/");

        assertFalse(m.find());   // Should not be a match
    }

}