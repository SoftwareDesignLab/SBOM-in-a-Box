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
 * file: ScalaParserRegexTest.java
 * Description: Testing for Scala, extends ParseRegexTestCore
 *
 * @author Dylan Mulligan
 */
public class ScalaParserRegexTest extends ParseRegexTestCore {
    /**
     * Constructor initializes a given parser and assigns both the
     * regex to test it against and the source directory to test on.
     *
     */
    public ScalaParserRegexTest() {
        super(new ScalaParser(),
                "^(?:(?!//).)*(?:import (?:([\\w.*]*)(?:(?=\\.\\{\\n?)([\\S\\s]*?\\})|(\\.[\\w.*]*(?: as [\\w*.]*)?))))(?![^\\/\\*]*\\*\\/)",
                "Scala");
    }

    ///
    /// External
    ///

    @Test
    @DisplayName("import bar.foo")
    void importBasic() {
        Matcher m = getMatcher("import bar.foo");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("bar/foo", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import bar.foo;")
    void importBasicSemicolon() {
        Matcher m = getMatcher("import bar.foo;");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("bar/foo", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import bar.*")
    void importStar() {
        Matcher m = getMatcher("import bar.*");
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
        assertEquals("bar", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import bar._")
    void importUnderscore() {
        Matcher m = getMatcher("import bar._");
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
        assertEquals("bar", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import bar.A*")
    void importAlphaStar() {
        Matcher m = getMatcher("import bar.A*");
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
        assertEquals("bar", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import bar.foo as barfoo")
    void importAlias() {
        Matcher m = getMatcher("import bar.foo as barfoo");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("bar/foo", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
//        assertEquals("barfoo", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import bar.{foo, fee}")
    void importMultiple() {
        Matcher m = getMatcher("import bar.{foo, fee}");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(2, results.size());    // should only be 1 match

        // Test resulting component 1
        SVIPComponentObject c = results.get(0).build();
        assertEquals("bar/foo", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 2
        c = results.get(1).build();
        assertEquals("bar/fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import bar.{foo as f, fee => free}")
    void importMultipleAlias() {
        Matcher m = getMatcher("import bar.{foo as f, fee => free}");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(2, results.size());    // should only be 1 match

        // Test resulting component 1
        SVIPComponentObject c = results.get(0).build();
        assertEquals("bar/foo", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
//        assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 2
        c = results.get(1).build();
        assertEquals("bar/fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
//        assertEquals("free", c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// INTERNAL
    ///

    @Test
    @DisplayName("import Scala.lib.Bar")
    void importInternal() {
        Matcher m = getMatcher("import Scala.lib.Bar");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Bar", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("Scala/lib", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import Scala.lib.Bar as Baz")
    void importInternalAlias() {
        Matcher m = getMatcher("import Scala.lib.Bar as Baz");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Bar", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("Scala/lib", c.getGroup());
//        assertEquals("Baz", c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// LANGUAGE
    ///

    @Test
    @DisplayName("import scala.collection.parallel.immutable")
    void importLanguage() {
        Matcher m = getMatcher("import scala.collection.parallel.immutable");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("scala/collection/parallel/immutable", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import scala.collection.parallel.immutable.*")
    void importLanguageStar() {
        Matcher m = getMatcher("import scala.collection.parallel.immutable.*");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("*", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("scala/collection/parallel/immutable", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import scala.collection.parallel.immutable.ParHashMap")
    void importLanguageClass() {
        Matcher m = getMatcher("import scala.collection.parallel.immutable.ParHashMap");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("ParHashMap", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("scala/collection/parallel/immutable", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import scala.collection.parallel.immutable.ParHashMap as phm;")
    void importLanguageClassAliasSemicolon() {
        Matcher m = getMatcher("import scala.collection.parallel.immutable.ParHashMap as phm;");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("ParHashMap", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("scala/collection/parallel/immutable", c.getGroup());
//        assertEquals("phm", c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// Comments
    ///

    @Test
    @DisplayName("// import bar.foo")
    void importDoubleSlash() {
        Matcher m = getMatcher("// import bar.foo");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("/*\nimport bar.foo\n*/")
    void importBlockComment() {
        Matcher m = getMatcher("/*\nimport bar.foo\n*/");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("/**/ import bar.foo /**/")
    void importBetweenBlockComments() {
        Matcher m = getMatcher("/**/ import bar.foo /**/");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("bar/foo", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import bar.foo // this imports bar")
    void importBeforeDoubleSlash() {
        Matcher m = getMatcher("import bar.foo // this imports bar");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("bar/foo", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

}
