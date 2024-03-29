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
 * file: JavaParserRegexTest.java
 * Description: Testing for Java import regex edge cases, extends ParseRegexTestCore
 *
 * @author Dylan Mulligan
 */
public class JavaParserRegexTest extends ParseRegexTestCore {
    /**
     * Constructor initializes a given parser and assigns both the
     * regex to test it against and the source directory to test on.
     *
     */
    public JavaParserRegexTest() {
        super(new JavaParser(),
                "^(?:(?!//).)*import(?: static)?(.*?)([\\w\\*]*);(?![^\\/\\*]*\\*\\/)",
                "Java");
        // Cast generic instance to its correct type, such that protected fields can be accessed
    }

    ///
    /// External
    ///

    @Test
    @DisplayName("import bar;")
    void importBasic() {
        Matcher m = getMatcher("import bar;");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("bar", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import foo.bar;")
    void importMultipart() {
        Matcher m = getMatcher("import foo.bar;");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("foo/bar", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import foo.bar.baz.fee.fye;")
    void importLongMultipart() {
        Matcher m = getMatcher("import foo.bar.baz.fee.fye;");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("foo/bar/baz/fee/fye", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import foo.bar.*;")
    void importStar() {
        Matcher m = getMatcher("import foo.bar.*;");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("*", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("foo/bar", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import foo.bar.A*;")
    void importAlphaStar() {
        Matcher m = getMatcher("import foo.bar.A*;");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("A*", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("foo/bar", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import static foo.bar;")
    void importStatic() {
        Matcher m = getMatcher("import static foo.bar;");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("foo/bar", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import foo.Baz;")
    void importClass() {
        Matcher m = getMatcher("import foo.Baz;");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Baz", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("foo", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import foo.bar.fee.Baz;")
    void importLongClass() {
        Matcher m = getMatcher("import foo.bar.fee.Baz;");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Baz", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("foo/bar/fee", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// INTERNAL
    ///

    @Test
    @DisplayName("import Java.lib.Foo;")
    void importInternal() {
        Matcher m = getMatcher("import Java.lib.Foo;");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Foo", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("Java/lib", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// LANGUAGE
    ///

    @Test
    @DisplayName("import java.awt.color;")
    void importLanguagePackage() {
        Matcher m = getMatcher("import java.awt.color;");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("java/awt/color", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import java.awt.color.ColorSpace;")
    void importLanguageClass() {
        Matcher m = getMatcher("import java.awt.color.ColorSpace;");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("ColorSpace", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("java/awt/color", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// Comments
    ///

    @Test
    @DisplayName("// import bar;")
    void importDoubleSlash() {
        Matcher m = getMatcher("// import bar;");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("/*\nimport bar;\n*/")
    void importBlockComment() {
        Matcher m = getMatcher("/*\nimport bar;\n*/");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("/**/ import bar; /**/")
    void importBetweenBlockComments() {
        Matcher m = getMatcher("/**/ import bar; /**/");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("bar", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import bar; // this imports bar")
    void importBeforeDoubleSlash() {
        Matcher m = getMatcher("import bar; // this imports bar");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("bar", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

}
