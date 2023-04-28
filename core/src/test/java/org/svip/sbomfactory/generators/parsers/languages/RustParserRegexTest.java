package org.svip.sbomfactory.generators.parsers.languages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.util.ArrayList;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;

/**
 * file: RustParserRegexTest.java
 * Description: Testing for Rust, extends ParseRegexTestCore
 *
 * @author Dylan Mulligan
 */
public class RustParserRegexTest extends ParseRegexTestCore {
    /**
     * Constructor initializes a given parser and assigns both the
     * regex to test it against and the source directory to test on.
     *
     */
    public RustParserRegexTest() {
        super(new RustParser(),
                "^(?:(?!//).)*(?:use ([ '\\\"\\w:*]*)(?:::([\\w* ]+)|(?:(?=.*\\{\\n?)(?:\\{([\\S\\s]*?))\\};))|(?:extern ([\\w ]*)|mod ([\\w:]*));)(?![^\\/\\*]*\\*\\/)",
                "TestData/Rust");
    }

    ///
    /// External
    ///

    // Use

    @Test
    @DisplayName("use fee::fye;")
    void useBasic() {
        Matcher m = getMatcher("use fee::fye;");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fye", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("use fee::fye::fo::Fum;")
    void useLong() {
        Matcher m = getMatcher("use fee::fye::fo::Fum;");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("Fum", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee/fye/fo", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("use fee::fye as f;")
    void useAlias() {
        Matcher m = getMatcher("use fee::fye as f;");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fye", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee", c.getGroup());
        assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("use ::fee::fye;")
    void usePrefix() {
        Matcher m = getMatcher("use ::fee::fye;");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fye", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("use fee::fye::{fo};")
    void useBracketsBasic() {
        Matcher m = getMatcher("use fee::fye::{fo};");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee/fye", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("use fee::fye::{fo, fum, fair};")
    void useBracketsMultiple() {
        Matcher m = getMatcher("use fee::fye::{fo, fum, fair};");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(3, results.size());    // should be 3 matches

        // Test resulting component 1
        ParserComponent c = results.get(0);
        assertEquals("fo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee/fye", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 2
        c = results.get(1);
        assertEquals("fum", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee/fye", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 3
        c = results.get(2);
        assertEquals("fair", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee/fye", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("use fee::fye::{fo as f, fum::fro,\nfum::{grow, Grew},\n\r fair};")
    void useBracketsComplex() {
        Matcher m = getMatcher("use fee::fye::{fo as f, fum::fro,\nfum::{grow as g, Grew},\n\r fair};");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(5, results.size());    // should be 5 matches

        // Test resulting component 1
        ParserComponent c = results.get(0);
        assertEquals("fo", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee/fye", c.getGroup());
        assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 2
        c = results.get(1);
        assertEquals("fro", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee/fye/fum", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 3
        c = results.get(2);
        assertEquals("grow", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee/fye/fum", c.getGroup());
        assertEquals("g", c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 4
        c = results.get(3);
        assertEquals("Grew", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee/fye/fum", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 5
        c = results.get(4);
        assertEquals("fair", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee/fye", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    // Mod

    @Test
    @DisplayName("mod fee;")
    void modSingle() {
        Matcher m = getMatcher("mod fee;");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("mod fee::fye;")
    void modBasic() {
        Matcher m = getMatcher("mod fee::fye;");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fye", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    // Extern

    // Self

    @Test
    @DisplayName("use self::fee::fye;")
    void useSelfBasic() {
        Matcher m = getMatcher("use self::fee::fye;");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fye", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("use self::fee;")
    void useSelfShort() {
        Matcher m = getMatcher("use self::fee;");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("use self::fee::fye::Fo::fum;")
    void useSelfLong() {
        Matcher m = getMatcher("use self::fee::fye::Fo::fum;");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fum", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee/fye/Fo", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("mod self_fee;")
    void modSelfInvalid() {
        Matcher m = getMatcher("mod self_fee;");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("self_fee", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("mod self;")
    void modSelfInvalid2() {
        Matcher m = getMatcher("mod self;");

        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(0, results.size());    // should not match
    }

    @Test
    @DisplayName("use fee::fye::{self};")
    void useSelfBrackets() {
        Matcher m = getMatcher("use fee::fye::{self};");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("*", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee/fye", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// INTERNAL
    ///

    @Test
    @DisplayName("use lib::bar;")
    void useInternal() {
        Matcher m = getMatcher("use lib::bar;");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("bar", c.getName());
        assertEquals(ParserComponent.Type.INTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("lib", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("extern lib far;")
    void externInternal() {
        Matcher m = getMatcher("extern lib far;");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("far", c.getName());
        assertEquals(ParserComponent.Type.INTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("lib", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("mod tar;")
    void modInternal() {
        Matcher m = getMatcher("mod tar;");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("tar", c.getName());
        assertEquals(ParserComponent.Type.INTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// LANGUAGE
    ///

    @Test
    @DisplayName("use std::fmt::{Result, Debug, Arguments, Alignment, Display, format};")
    void useLanguage() {
        Matcher m = getMatcher("use std::fmt::{Result, Debug, Arguments, Alignment, Display, format};");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(6, results.size());    // should be 6 matches

        // Test resulting component 1
        ParserComponent c = results.get(0);
        assertEquals("Result", c.getName());
        assertEquals(ParserComponent.Type.LANGUAGE, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("std/fmt", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 2
        c = results.get(1);
        assertEquals("Debug", c.getName());
        assertEquals(ParserComponent.Type.LANGUAGE, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("std/fmt", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 3
        c = results.get(2);
        assertEquals("Arguments", c.getName());
        assertEquals(ParserComponent.Type.LANGUAGE, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("std/fmt", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 4
        c = results.get(3);
        assertEquals("Alignment", c.getName());
        assertEquals(ParserComponent.Type.LANGUAGE, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("std/fmt", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 5
        c = results.get(4);
        assertEquals("Display", c.getName());
        assertEquals(ParserComponent.Type.LANGUAGE, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("std/fmt", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());

        // Test resulting component 5
        c = results.get(5);
        assertEquals("format", c.getName());
        assertEquals(ParserComponent.Type.LANGUAGE, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("std/fmt", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// Comments
    ///

    // Single-line

    @Test
    @DisplayName("// use fee::fye;")
    void useDoubleSlash() {
        Matcher m = getMatcher("// use fee::fye;");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("//! use fee::fye;")
    void useDoubleSlashExclamation() {
        Matcher m = getMatcher("//! use fee::fye;");

        assertFalse(m.find());   // Should not be a match
    }

    // Multi-line comments

    @Test
    @DisplayName("/* use fee::fye; */")
    void useBlockComment() {
        Matcher m = getMatcher("/* use fee::fye; */");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("/*!\nuse fee::fye; \n*/")
    void useBlockComment2() {
        Matcher m = getMatcher("/*!\nuse fee::fye; \n*/");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("/*! use fee::fye; */")
    void useBlockCommentExclamation() {
        Matcher m = getMatcher("/* use fee::fye; */");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("/*! \nuse fee::fye;\n*/")
    void useBlockCommentExclamation2() {
        Matcher m = getMatcher("/*! \nuse fee::fye;\n*/");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("/** use fee::fye; */")
    void useBlockCommentStar() {
        Matcher m = getMatcher("/** use fee::fye; */");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("/**\n use fee::fye;\n */")
    void useBlockCommentStar2() {
        Matcher m = getMatcher("/**\n use fee::fye;\n */");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("/**/ use fee::fye; /**/")
    void useBetweenBlockComments() {
        Matcher m = getMatcher("/**/ use fee::fye; /**/");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fye", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("/**/ mod fee::fye; /**/")
    void modBetweenBlockComments() {
        Matcher m = getMatcher("/**/ mod fee::fye; /**/");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fye", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("/**/ extern fee fye; /**/")
    void externBetweenBlockComments3() {
        Matcher m = getMatcher("/**/ extern fee fye; /**/");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fye", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("use fee::fye // this imports fye")
    void useBeforeDoubleSlash() {
        Matcher m = getMatcher("use fee::fye // this imports fye");
        assertTrue(m.find());   // Should be a match
        ArrayList<ParserComponent> results = new ArrayList<>();
        ((LanguageParser) this.PARSER).parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        ParserComponent c = results.get(0);
        assertEquals("fye", c.getName());
        assertEquals(ParserComponent.Type.EXTERNAL, c.getType());
        assertNull(c.getVersion());
        assertEquals(0, c.getDepth());
        assertEquals("fee", c.getGroup());
        assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }
}
