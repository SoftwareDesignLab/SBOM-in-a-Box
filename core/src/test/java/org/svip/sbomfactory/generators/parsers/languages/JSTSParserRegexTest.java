package org.svip.sbomfactory.generators.parsers.languages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.util.ArrayList;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;

/**
 * file: JSTSParserRegexTest.java
 * Description: Testing for JS/TS import regex edge cases, extends ParseRegexTestCore
 *
 * @author Dylan Mulligan
 */
public class JSTSParserRegexTest extends ParseRegexTestCore {
    /**
     * Constructor initializes a given parser and assigns both the
     * regex to test it against and the source directory to test on.
     *
     */
    public JSTSParserRegexTest() {
        super(new JSTSParser(),
                "^(?:(?!//).)*(?:import (?:(?:(?=.*\\{\\n?)([\\S\\s]*?\\})|(.*))\\s*from [ '\\\"]*([\\w\\.\\/]+)[ '\\\"]*|[ '\\\"]*([\\w\\.\\/]+)[ '\\\"]*)|require[ \\('\\\"]*([\\w\\.\\/]+)[ \\)'\\\"]*)(?![^\\/\\*]*\\*\\/)",
                "TestData/JS");
    }

    ///
    /// External
    ///

    @Test
    @DisplayName("import bar")
    void importBasic() {
        Matcher m = getMatcher("import bar");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("bar", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import 'bar'")
    void importSingleQuotes() {
        Matcher m = getMatcher("import 'bar'");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("bar", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("\"import \"bar\"")
    void importDoubleQuotes() {
        Matcher m = getMatcher("import \"bar\"");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("bar", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import foo from bar")
    void importFrom() {
        Matcher m = getMatcher("import foo from bar");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("foo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import foo, fee from bar")
    void importMultipleFrom() {
        Matcher m = getMatcher("import foo, fee from bar");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(2, results.size());    // should be 2 matches

        // Test resulting components
        ParserComponent c = results.get(0); // first match

        assertEquals("foo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());

        c = results.get(1); // second match

        assertEquals("fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import foo as f from bar")
    void importAsAliasFrom() {
        Matcher m = getMatcher("import foo as f from bar");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("foo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import foo as f, fee from bar")
    void importMultipleAsAliasFrom() {
        Matcher m = getMatcher("import foo as f, fee from bar");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(2, results.size());    // should be 2 matches

        // Test resulting components
        ParserComponent c = results.get(0); // first match

        assertEquals("foo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());

        c = results.get(1); // second match

        assertEquals("fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import { foo } from bar")
    void importBracesFrom() {
        Matcher m = getMatcher("import { foo } from bar");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("foo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import { foo as f } from bar")
    void importBracesAsAliasFrom() {
        Matcher m = getMatcher("import { foo as f } from bar");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("foo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import { foo as f, fee } from bar")
    void importBracesMultipleAsAliasFrom() {
        Matcher m = getMatcher("import { foo as f, fee } from bar");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(2, results.size());    // should be 2 matches

        // Test resulting component
        ParserComponent c = results.get(0); // first match

        assertEquals("foo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());

        c = results.get(1); // second match

        assertEquals("fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import foo as f, { goo as g } from bar")
    void importBracesSomeAsAliasFrom() {
        Matcher m = getMatcher("import foo as f, { goo as g } from bar");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(2, results.size());    // should be 2 matches

        // Test resulting component
        ParserComponent c = results.get(0); // first match

        assertEquals("foo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());

        c = results.get(1); // second match

        assertEquals("goo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("bar", c.getGroup());
        assertEquals("g", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require(bar)")
    void requireBasic() {
        Matcher m = getMatcher("require(bar)");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("bar", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require('bar')")
    void requireSingleQuotes() {
        Matcher m = getMatcher("require('bar')");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("bar", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require(\"bar\")")
    void requireDoubleQuotes() {
        Matcher m = getMatcher("require(\"bar\")");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("bar", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// INTERNAL
    ///

    @Test
    @DisplayName("import './lib/bar.js'")
    void importInternal() {
        Matcher m = getMatcher("import './lib/bar.js'");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("./lib/bar.js", c.getName());
        assertEquals(ParserComponent.Type.INTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("./lib/bar.js", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import foo from './lib/bar.js'")
    void importInternalFrom() {
        Matcher m = getMatcher("import foo from './lib/bar.js'");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("foo", c.getName());
        assertEquals(ParserComponent.Type.INTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("./lib/bar.js", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// LANGUAGE
    ///

    // No language tests, as JS has no language components

    ///
    /// Comments
    ///

    @Test
    @DisplayName("// import bar")
    void importDoubleSlash() {
        Matcher m = getMatcher("// import bar");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("/*\nimport bar\n*/")
    void importBlockComment() {
        Matcher m = getMatcher("/*\nimport bar\n*/");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("import {fee, /*...*/ } from bar")
    void importInlineBlockComment() {
        Matcher m = getMatcher("import {fee, /*...*/ } from bar");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size()); // should only find fee
    }

    @Test
    @DisplayName("/**/ import bar /**/")
    void importBetweenBlockComments() {
        Matcher m = getMatcher("/**/ import bar /**/");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("bar", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import bar // this imports bar")
    void importBeforeDoubleSlash() {
        Matcher m = getMatcher("import bar // this imports bar");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("bar", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

}
