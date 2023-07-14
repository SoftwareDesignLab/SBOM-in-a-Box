package org.svip.sbomfactory.parsers.languages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.objects.SVIPComponentObject;

import java.util.ArrayList;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;

/**
 * file: GoParserRegexTest.java
 * Description: Testing for Go, extends ParseRegexTestCore
 *
 * @author Dylan Mulligan
 */
public class GoParserRegexTest extends ParseRegexTestCore {
    /**
     * Constructor initializes a given parser and assigns both the
     * regex to test it against and the source directory to test on.
     */
    public GoParserRegexTest() {
        super(new GoParser(),
                "^(?:(?!//).)*(?:import (?:(?=.*\\(\\n?)\\(([\\S\\s]*?)\\)|([\\w.]* ?\\\"[\\S]*)))(?![^\\*]*\\*\\/)",
                "Go");
    }

    ///
    /// External
    ///

    @Test
    @DisplayName("import \"fee\"")
    void importBasic() {
        Matcher m = getMatcher("import \"fee\"");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import \"bar/fee\"")
    void importMultipart() {
        Matcher m = getMatcher("import \"bar/fee\"");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import \"bar/foo/fee\"")
    void importLongMultipart() {
        Matcher m = getMatcher("import \"bar/foo/fee\"");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("bar/foo", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import \"github.com/gopherguides/greet\"")
    void import3rdParty() {
        Matcher m = getMatcher("import \"github.com/gopherguides/greet\"");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0);
        assertEquals("greet", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("github.com/gopherguides", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import f \"fee\"")
    void importAlias() {
        Matcher m = getMatcher("import f \"fee\"");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import f \"fee/fye\"")
    void importAliasMultipart() {
        Matcher m = getMatcher("import f \"fee/fye\"");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0);
        assertEquals("fye", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("fee", c.getGroup());
        // assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import . \"fee\"")
    void importAliasDot() {
        Matcher m = getMatcher("import . \"fee\"");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0);
        assertEquals("*", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("fee", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import _ \"fee\"")
    void importAliasUnderscore() {
        Matcher m = getMatcher("import _ \"fee\"");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(0, results.size());    // should not be a match
    }

    @Test
    @DisplayName("import (\n\t\"bar\"\n\t\"fee\"\n)")
    void importMultiline() {
        Matcher m = getMatcher("import (\n\t\"bar\"\n\t\"fee\"\n)");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(2, results.size());    // should only be 1 match

        // Test resulting component 1
        SVIPComponentObject c = results.get(0);
        assertEquals("bar", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 2
        c = results.get(1);
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import (\n\t\"bar\"\n\t\"fee/fye/fo\"\n)")
    void importMultilineMultipart() {
        Matcher m = getMatcher("import (\n\t\"bar\"\n\t\"fee/fye/fo\"\n)");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(2, results.size());    // should only be 1 match

        // Test resulting component 1
        SVIPComponentObject c = results.get(0);
        assertEquals("bar", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 2
        c = results.get(1);
        assertEquals("fo", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("fee/fye", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import (\n\tb \"bar\"\n\tf \"fee\"\n)")
    void importMultilineAlias() {
        Matcher m = getMatcher("import (\n\tb \"bar\"\n\tf \"fee\"\n)");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(2, results.size());    // should only be 1 match

        // Test resulting component 1
        SVIPComponentObject c = results.get(0);
        assertEquals("bar", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
//        assertEquals("b", c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 2
        c = results.get(1);
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import (\"bar\"; \"fee\")")
    void importMultilineInline() {
        Matcher m = getMatcher("import (\"bar\"; \"fee\")");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(2, results.size());    // should only be 1 match

        // Test resulting component 1
        SVIPComponentObject c = results.get(0);
        assertEquals("bar", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 2
        c = results.get(1);
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import (b \"bar\"; \"fee\")")
    void importMultilineInlineAlias() {
        Matcher m = getMatcher("import (b \"bar\"; \"fee\")");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(2, results.size());    // should only be 1 match

        // Test resulting component 1
        SVIPComponentObject c = results.get(0);
        assertEquals("bar", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
//        assertEquals("b", c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 2
        c = results.get(1);
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// INTERNAL
    ///

    @Test
    @DisplayName("import \"Go/lib/int2\"")
    void importInternal() {
        Matcher m = getMatcher("import \"Go/lib/int2\"");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0);
        assertEquals("int2", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("Go/lib", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// LANGUAGE
    ///

    @Test
    @DisplayName("import \"fmt\"")
    void importLanguage() {
        Matcher m = getMatcher("import \"fmt\"");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0);
        assertEquals("fmt", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import \"math/big\"")
    void importLanguage2() {
        Matcher m = getMatcher("import \"math/big\"");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0);
        assertEquals("big", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("math", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import . \"regexp\"")
    void importLanguage3() {
        Matcher m = getMatcher("import . \"regexp\"");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0);
        assertEquals("*", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("regexp", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// Comments
    ///

    @Test
    @DisplayName("// import \"bar/foo\"")
    void importBasicComment() {
        Matcher m = getMatcher("// import \"bar/foo\"");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("/* import \"fee\" */")
    void importBlockComment() {
        Matcher m = getMatcher("/* import \"fee\" */");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("/* import \"fee/fye\" */")
    void importBlockCommentMultipart() {
        Matcher m = getMatcher("/* import \"fee/fye\" */");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("/* import \"fee/fye\"\nimport (\"fo\" \"fum\")\n\nimport \"far/far/away\"\n*/")
    void importBlockCommentMultiline() {
        Matcher m = getMatcher("/* import \"fee/fye\"\nimport (\"fo\" \"fum\")\n\nimport \"far/far/away\"\n*/");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("/**/ import \"fee\" /**/")
    void importBetweenBlockComments() {
        Matcher m = getMatcher("/**/ import \"fee\" /**/");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("/**/ import \"fee/fye\" /**/")
    void importBetweenBlockCommentsMultipart() {
        Matcher m = getMatcher("/**/ import \"fee/fye\" /**/");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0);
        assertEquals("fye", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("fee", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import \"bar/fee\" // this imports fee")
    void importBeforeBasicComment() {
        Matcher m = getMatcher("import \"bar/fee\" // this imports fee");
        assertTrue(m.find());   // Should be a match
        ArrayList<SVIPComponentObject> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }
}
