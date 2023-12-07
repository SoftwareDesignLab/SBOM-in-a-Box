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
                "CSharp/Bar");
    }

    //
    // Basic
    //

    @Test
    @DisplayName("using foo;")
    void usingBasic() {
        Matcher m = getMatcher("using foo;");

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
    @DisplayName("using foo.bar;")
    void usingBasicWithDelim() {
        Matcher m = getMatcher("using foo.bar;");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("bar", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertEquals("foo", c.getGroup());
//        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using f = foo;")
    void usingAlias() {
        Matcher m = getMatcher("using f = foo;");

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
//        assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using f = Faz.Foo.Fee<string>;")
    void usingAliasTyped() {
        Matcher m = getMatcher("using f = Faz.Foo.Fee<string>;");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertEquals("Faz/Foo", c.getGroup());
//        assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using f = Faz.Foo.Fee<string>;")
    void usingAliasMultiTyped() {
        Matcher m = getMatcher("using f = Faz.Foo.Fee<string>;");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertEquals("Faz/Foo", c.getGroup());
//        assertEquals("f", c.getAlias());
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
    @DisplayName("global using foo;")
    void usingGlobal() {
        Matcher m = getMatcher("global using foo;");

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
    @DisplayName("global using static foo;")
    void usingGlobalStatic() {
        Matcher m = getMatcher("global using static foo;");

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
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Net", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertEquals("System", c.getGroup());
//        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using System.IO.Compression;")
    void usingLanguage2() {
        Matcher m = getMatcher("using System.IO.Compression;");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Compression", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertEquals("System/IO", c.getGroup());
//        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using s = System.Net;")
    void usingLanguageAlias() {
        Matcher m = getMatcher("using s = System.Net;");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Net", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertEquals("System", c.getGroup());
//        assertEquals("s", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using f = System.Func<string>;")
    void usingLanguageAliasTyped() {
        Matcher m = getMatcher("using f = System.Func<string>;");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Func", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertEquals("System", c.getGroup());
//        assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using f = System.Func<string, string>;")
    void usingLanguageAliasMultiTyped() {
        Matcher m = getMatcher("using f = System.Func<string, string>;");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Func", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertEquals("System", c.getGroup());
//        assertEquals("f", c.getAlias());
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
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Bar", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertEquals("CSharp", c.getGroup());
//        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using b = CSharp.Bar;")
    void usingInternalAlias() {
        Matcher m = getMatcher("using b = CSharp.Bar;");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Bar", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertEquals("CSharp", c.getGroup());
//        assertEquals("b", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using CSharp.Bar;")
    void usingInternalPath() {
        Matcher m = getMatcher("using CSharp.Bar;");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Bar", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertEquals("CSharp", c.getGroup());
//        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("using CSharp.Bar.foobar.foofoobar;")
    void usingInternalClass() {
        Matcher m = getMatcher("using CSharp.Bar.foobar.foofoobar;");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("foofoobar", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
//        assertEquals(0, c.getDepth());
        assertEquals("CSharp/Bar/foobar", c.getGroup());
//        assertNull(c.getAlias());
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