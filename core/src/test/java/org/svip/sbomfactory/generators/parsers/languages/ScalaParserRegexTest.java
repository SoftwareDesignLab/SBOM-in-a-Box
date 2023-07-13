package org.svip.sbomfactory.generators.parsers.languages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbomfactory.parsers.languages.ScalaParser;

import java.util.ArrayList;
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
                "src/test/java/org/svip/sbomfactory/generators/TestData/Scala");
    }

    ///
    /// External
    ///

    @Test
    @DisplayName("import bar.foo")
    void importBasic() {
        Matcher m = getMatcher("import bar.foo");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("bar/foo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import bar.foo;")
    void importBasicSemicolon() {
        Matcher m = getMatcher("import bar.foo;");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("bar/foo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import bar.*")
    void importStar() {
        Matcher m = getMatcher("import bar.*");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("*", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import bar._")
    void importUnderscore() {
        Matcher m = getMatcher("import bar._");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("*", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import bar.A*")
    void importAlphaStar() {
        Matcher m = getMatcher("import bar.A*");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("A*", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import bar.foo as barfoo")
    void importAlias() {
        Matcher m = getMatcher("import bar.foo as barfoo");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("bar/foo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertEquals("barfoo", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import bar.{foo, fee}")
    void importMultiple() {
        Matcher m = getMatcher("import bar.{foo, fee}");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(2, results.size());    // should only be 1 match

        // Test resulting component 1
        ParserComponent c = results.get(0);
        assertEquals("bar/foo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 2
        c = results.get(1);
        assertEquals("bar/fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import bar.{foo as f, fee => free}")
    void importMultipleAlias() {
        Matcher m = getMatcher("import bar.{foo as f, fee => free}");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(2, results.size());    // should only be 1 match

        // Test resulting component 1
        ParserComponent c = results.get(0);
        assertEquals("bar/foo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 2
        c = results.get(1);
        assertEquals("bar/fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertEquals("free", c.getAlias());
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
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("Bar", c.getName());
        assertEquals(ParserComponent.Type.INTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("Scala/lib", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import Scala.lib.Bar as Baz")
    void importInternalAlias() {
        Matcher m = getMatcher("import Scala.lib.Bar as Baz");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("Bar", c.getName());
        assertEquals(ParserComponent.Type.INTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("Scala/lib", c.getGroup());
        assertEquals("Baz", c.getAlias());
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
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("scala/collection/parallel/immutable", c.getName());
        assertEquals(ParserComponent.Type.LANGUAGE, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import scala.collection.parallel.immutable.*")
    void importLanguageStar() {
        Matcher m = getMatcher("import scala.collection.parallel.immutable.*");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("*", c.getName());
        assertEquals(ParserComponent.Type.LANGUAGE, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("scala/collection/parallel/immutable", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import scala.collection.parallel.immutable.ParHashMap")
    void importLanguageClass() {
        Matcher m = getMatcher("import scala.collection.parallel.immutable.ParHashMap");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("ParHashMap", c.getName());
        assertEquals(ParserComponent.Type.LANGUAGE, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("scala/collection/parallel/immutable", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import scala.collection.parallel.immutable.ParHashMap as phm;")
    void importLanguageClassAliasSemicolon() {
        Matcher m = getMatcher("import scala.collection.parallel.immutable.ParHashMap as phm;");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("ParHashMap", c.getName());
        assertEquals(ParserComponent.Type.LANGUAGE, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("scala/collection/parallel/immutable", c.getGroup());
        assertEquals("phm", c.getAlias());
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
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("bar/foo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import bar.foo // this imports bar")
    void importBeforeDoubleSlash() {
        Matcher m = getMatcher("import bar.foo // this imports bar");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("bar/foo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

}
