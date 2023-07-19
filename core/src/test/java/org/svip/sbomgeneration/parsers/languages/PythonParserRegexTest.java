package org.svip.sbomgeneration.parsers.languages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.model.objects.SVIPComponentObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <b>File</b>: PythonParserRegexTest.java<br>
 * <b>Description</b>: Testing for PythonParser regex.
 *
 * @author Dylan Mulligan
 */
class PythonParserRegexTest extends ParseRegexTestCore {
    /**
     * Constructor initializes a given parser and assigns both the
     * regex to test it against and the source directory to test on.
     *
     */
    public PythonParserRegexTest() {
        super(new PythonParser(),
                "(?:(?!#))(?:(?:from ([\\w.]*) )?import (?:(?=.*\\(\\n?)\\(([\\S\\s]*?)\\)|([\\w. ,\\*]*)))",
                "Python/Absolute/ifoo");
    }

    //
    // Regex Tests
    //
    @Test
    @DisplayName("import foo")
    void importBasic() {
        Matcher m = getMatcher("import foo");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("foo", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import foo.bar")
    void importBasicTwo() {
        Matcher m = getMatcher("import foo.bar");

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
        assertEquals("foo", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import foo as f")
    void importBasicAlias() {
        Matcher m = getMatcher("import foo as f");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("foo", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("if not someVersion:\n    import fye as f\n")
    void importConditional() {
        Matcher m = getMatcher("if not someVersion:\n    import fye as f\n");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("fye", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("if not someOtherVersion:\n    from fee.fye import Fo as f\n")
    void fromImportConditional() {
        Matcher m = getMatcher("if not someOtherVersion:\n    from fee.fye import Fo as f\n");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Fo", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("fee/fye", c.getGroup());
        // assertEquals("f", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("from foo import bar")
    void fromImportBasic() {
        Matcher m = getMatcher("from foo import bar");

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
        assertEquals("foo", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("from foo import b, a")
    void fromImportTwoItems() {
        Matcher m = getMatcher("from foo import b, a");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(2, results.size());    // should 2 matches

        // Test resulting component 1
        SVIPComponentObject c1 = results.get(0).build();
        assertEquals("b", c1.getName());
        assertEquals("external", c1.getType().toLowerCase());
        assertNull(c1.getVersion());
//        // assertEquals(0, c1.getDepth());
        assertEquals("foo", c1.getGroup());
//        // assertNull(c1.getAlias());
        // assertNull(c1.getChildren());

        // Test resulting component 2
        SVIPComponentObject c2 = results.get(1).build();
        assertEquals("a", c2.getName());
        assertEquals("external", c2.getType().toLowerCase());
        assertNull(c2.getVersion());
//        // assertEquals(0, c2.getDepth());
        assertEquals("foo", c2.getGroup());
//        // assertNull(c2.getAlias());
        // assertNull(c2.getChildren());
    }

    @Test
    @DisplayName("from foo import bar as b")
    void fromImportWithAlias() {
        Matcher m = getMatcher("from foo import bar as b");

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
        assertEquals("foo", c.getGroup());
//        assertEquals("b", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("from foo import (b, a, r)")
    void fromImportWithParenthesis() {
        Matcher m = getMatcher("from foo import (b, a, r)");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(3, results.size());    // should 3 matches

        // Test resulting component 1
        SVIPComponentObject c1 = results.get(0).build();
        assertEquals("b", c1.getName());
        assertEquals("external", c1.getType().toLowerCase());
        assertNull(c1.getVersion());
//        // assertEquals(0, c1.getDepth());
        assertEquals("foo", c1.getGroup());
//        // assertNull(c1.getAlias());
        // assertNull(c1.getChildren());

        // Test resulting component 2
        SVIPComponentObject c2 = results.get(1).build();
        assertEquals("a", c2.getName());
        assertEquals("external", c2.getType().toLowerCase());
        assertNull(c2.getVersion());
//        // assertEquals(0, c2.getDepth());
        assertEquals("foo", c2.getGroup());
//        // assertNull(c2.getAlias());
        // assertNull(c2.getChildren());

        // Test resulting component 3
        SVIPComponentObject c3 = results.get(2).build();
        assertEquals("r", c3.getName());
        assertEquals("external", c3.getType().toLowerCase());
        assertNull(c3.getVersion());
//        // assertEquals(0, c3.getDepth());
        assertEquals("foo", c3.getGroup());
//        // assertNull(c3.getAlias());
        // assertNull(c3.getChildren());
    }

    @Test
    @DisplayName("from foo import (\n\tb,\n\ta,\n\tr,\n)")
    void fromImportSeveralLine() {
        /*from foo import (
	        b,
	        a,
	        r,
        )*/
        Matcher m = getMatcher("from foo import (\n\tb,\n\ta,\n\tr,\n)");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(3, results.size());    // should 3 matches

        // Test resulting component 1
        SVIPComponentObject c1 = results.get(0).build();
        assertEquals("b", c1.getName());
        assertEquals("external", c1.getType().toLowerCase());
        assertNull(c1.getVersion());
//        // assertEquals(0, c1.getDepth());
        assertEquals("foo", c1.getGroup());
//        // assertNull(c1.getAlias());
        // assertNull(c1.getChildren());

        // Test resulting component 2
        SVIPComponentObject c2 = results.get(1).build();
        assertEquals("a", c2.getName());
        assertEquals("external", c2.getType().toLowerCase());
        assertNull(c2.getVersion());
//        // assertEquals(0, c2.getDepth());
        assertEquals("foo", c2.getGroup());
//        // assertNull(c2.getAlias());
        // assertNull(c2.getChildren());

        // Test resulting component 3
        SVIPComponentObject c3 = results.get(2).build();
        assertEquals("r", c3.getName());
        assertEquals("external", c3.getType().toLowerCase());
        assertNull(c3.getVersion());
//        // assertEquals(0, c3.getDepth());
        assertEquals("foo", c3.getGroup());
//        // assertNull(c3.getAlias());
//         assertNull(c3.getChildren());
    }

    @Test
    @DisplayName("import random")
    void importExternalLanguage() {
        Matcher m = getMatcher("import random");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("random", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// Internal Tests
    ///
    @Test
    @DisplayName("import ifoo")
    void importAbsInternalBasic() {
        Matcher m = getMatcher("import ifoo");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("ifoo", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }
    @Test
    @DisplayName("import f")
    void importAbsInternalFileBasic() {
        Matcher m = getMatcher("import f");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("f", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }


    @Test
    @DisplayName("import ifoo as if")
    void importAbsInternalAlias() {
        Matcher m = getMatcher("import ifoo as if");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("ifoo", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
//        assertEquals("if", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import ifoo.bar")
    void importAbsInternalPath() {
        Matcher m = getMatcher("import ifoo.ibar");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("ibar", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("ifoo", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import ifoo.ibar as ifb")
    void importAbsInternalPathWithAlias() {
        Matcher m = getMatcher("import ifoo.ibar as ifb");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("ibar", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("ifoo", c.getGroup());
//        assertEquals("ifb", c.getAlias());
        // assertNull(c.getChildren());
    }


    @Test
    @DisplayName("from ifoo.ibar import ifoobarModule")
    void fromImportAbsInternalPath() {
        Matcher m = getMatcher("from ifoo.ibar import ifoobarModule");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("ifoobarModule", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("ifoo/ibar", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    // Instead it is a class of an internal module, there are many different solutions to this
    @Test
    @DisplayName("from ifoo.ibar.ifoobarModule import ifoobarClass")
    void fromImportAbsInternalPathModule() {
        Matcher m = getMatcher("from ifoo.ibar.ifoobarModule import ifoobarClass");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("ifoobarClass", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("ifoo/ibar/ifoobarModule", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }


    @Test
    @DisplayName("from ifoo.ifbar import ib, ia, ir")
    void fromImportAbsInternalPathWithDelimiter() {
        Matcher m = getMatcher("from ifoo.ibar import ib, ia, ir");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(3, results.size());    // should 3 matches

        // Test resulting component 1
        SVIPComponentObject c1 = results.get(0).build();
        assertEquals("ib", c1.getName());
        assertEquals("internal", c1.getType().toLowerCase());
        assertNull(c1.getVersion());
//        // assertEquals(0, c1.getDepth());
        assertEquals("ifoo/ibar", c1.getGroup());
//        // assertNull(c1.getAlias());
        // assertNull(c1.getChildren());

        // Test resulting component 2
        SVIPComponentObject c2 = results.get(1).build();
        assertEquals("ia", c2.getName());
        assertEquals("internal", c2.getType().toLowerCase());
        assertNull(c2.getVersion());
//        // assertEquals(0, c2.getDepth());
        assertEquals("ifoo/ibar", c2.getGroup());
//        // assertNull(c2.getAlias());
        // assertNull(c2.getChildren());

        // Test resulting component 3
        SVIPComponentObject c3 = results.get(2).build();
        assertEquals("ir", c3.getName());
        assertEquals("internal", c3.getType().toLowerCase());
        assertNull(c3.getVersion());
//        // assertEquals(0, c3.getDepth());
        assertEquals("ifoo/ibar", c3.getGroup());
//        // assertNull(c3.getAlias());
        // assertNull(c3.getChildren());
    }

    @Test
    @DisplayName("from ifoo.ifbar import (ib, ia, ir)")
    void fromImportAbsInternalPathWithDelimiter2() {
        Matcher m = getMatcher("from ifoo.ibar import (ib, ia, ir)");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(3, results.size());    // should 3 matches

        // Test resulting component 1
        SVIPComponentObject c1 = results.get(0).build();
        assertEquals("ib", c1.getName());
        assertEquals("internal", c1.getType().toLowerCase());
        assertNull(c1.getVersion());
        // assertEquals(0, c1.getDepth());
        assertEquals("ifoo/ibar", c1.getGroup());
        // assertNull(c1.getAlias());
        // assertNull(c1.getChildren());

        // Test resulting component 2
        SVIPComponentObject c2 = results.get(1).build();
        assertEquals("ia", c2.getName());
        assertEquals("internal", c2.getType().toLowerCase());
        assertNull(c2.getVersion());
        // assertEquals(0, c2.getDepth());
        assertEquals("ifoo/ibar", c2.getGroup());
        // assertNull(c2.getAlias());
        // assertNull(c2.getChildren());

        // Test resulting component 3
        SVIPComponentObject c3 = results.get(2).build();
        assertEquals("ir", c3.getName());
        assertEquals("internal", c3.getType().toLowerCase());
        assertNull(c3.getVersion());
        // assertEquals(0, c3.getDepth());
        assertEquals("ifoo/ibar", c3.getGroup());
        // assertNull(c3.getAlias());
        // assertNull(c3.getChildren());
    }

    @Test
    @DisplayName("from ifoo.ibar import ib, ia as IA, ir")
    void fromImportAbsInternalPathWithDelimiterAndAlias() {
        Matcher m = getMatcher("from ifoo.ibar import ib, ia as IA, ir");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(3, results.size());    // should 3 matches

        // Test resulting component 1
        SVIPComponentObject c1 = results.get(0).build();
        assertEquals("ib", c1.getName());
        assertEquals("internal", c1.getType().toLowerCase());
        assertNull(c1.getVersion());
        // assertEquals(0, c1.getDepth());
        assertEquals("ifoo/ibar", c1.getGroup());
        // assertNull(c1.getAlias());
        // assertNull(c1.getChildren());

        // Test resulting component 2
        SVIPComponentObject c2 = results.get(1).build();
        assertEquals("ia", c2.getName());
        assertEquals("internal", c2.getType().toLowerCase());
        assertNull(c2.getVersion());
        // assertEquals(0, c2.getDepth());
        assertEquals("ifoo/ibar", c2.getGroup());
//        assertEquals("IA", c2.getAlias());
        // assertNull(c2.getChildren());

        // Test resulting component 3
        SVIPComponentObject c3 = results.get(2).build();
        assertEquals("ir", c3.getName());
        assertEquals("internal", c3.getType().toLowerCase());
        assertNull(c3.getVersion());
        // assertEquals(0, c3.getDepth());
        assertEquals("ifoo/ibar", c3.getGroup());
        // assertNull(c3.getAlias());
        // assertNull(c3.getChildren());
    }

    @Test
    @DisplayName("from ifoo.ibar import (\n\tib, \n\tia, \n\tir\n)")
    void fromImportAbsInternalPathMultipleLines() {
        Matcher m = getMatcher("from ifoo.ibar import (\n\tib, \n\tia, \n\tir\n)");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(3, results.size());    // should 3 matches

        // Test resulting component 1
        SVIPComponentObject c1 = results.get(0).build();
        assertEquals("ib", c1.getName());
        assertEquals("internal", c1.getType().toLowerCase());
        assertNull(c1.getVersion());
        // assertEquals(0, c1.getDepth());
        assertEquals("ifoo/ibar", c1.getGroup());
        // assertNull(c1.getAlias());
        // assertNull(c1.getChildren());

        // Test resulting component 2
        SVIPComponentObject c2 = results.get(1).build();
        assertEquals("ia", c2.getName());
        assertEquals("internal", c2.getType().toLowerCase());
        assertNull(c2.getVersion());
        // assertEquals(0, c2.getDepth());
        assertEquals("ifoo/ibar", c2.getGroup());
        // assertNull(c2.getAlias());
        // assertNull(c2.getChildren());

        // Test resulting component 3
        SVIPComponentObject c3 = results.get(2).build();
        assertEquals("ir", c3.getName());
        assertEquals("internal", c3.getType().toLowerCase());
        assertNull(c3.getVersion());
        // assertEquals(0, c3.getDepth());
        assertEquals("ifoo/ibar", c3.getGroup());
        // assertNull(c3.getAlias());
        // assertNull(c3.getChildren());
    }

    @Test
    @DisplayName("import .")
    void importRelInternalBasic() {
        Matcher m = getMatcher("import .");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("ifoo", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import .ibar")
    void importRelInternalBasic2() {
        Matcher m = getMatcher("import .ibar");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("ibar", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("ifoo", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("from . import ibar")
    void fromImportRelInternalBasic() {
        Matcher m = getMatcher("from . import ibar");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("ibar", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("ifoo", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("from . import (f, o)")
    void fromImportRelInternalWithDelimiter() {
        Matcher m = getMatcher("from . import (f, o)");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(2, results.size());    // should 3 matches

        // Test resulting component 1
        SVIPComponentObject c1 = results.get(0).build();
        assertEquals("f", c1.getName());
        assertEquals("internal", c1.getType().toLowerCase());
        assertNull(c1.getVersion());
        // assertEquals(0, c1.getDepth());
        assertEquals("ifoo", c1.getGroup());
        // assertNull(c1.getAlias());
        // assertNull(c1.getChildren());

        // Test resulting component 2
        SVIPComponentObject c2 = results.get(1).build();
        assertEquals("o", c2.getName());
        assertEquals("internal", c2.getType().toLowerCase());
        assertNull(c2.getVersion());
        // assertEquals(0, c2.getDepth());
        assertEquals("ifoo", c2.getGroup());
        // assertNull(c2.getAlias());
        // assertNull(c2.getChildren());
    }

    @Test
    @DisplayName("from . import (\n\tf,\n\to,\n)")
    void fromImportRelInternalMultiline() {
        Matcher m = getMatcher("from . import (\n\tf,\n\to,\n)");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(2, results.size());    // should be 2 matches

        // Test resulting component 1
        SVIPComponentObject c1 = results.get(0).build();
        assertEquals("f", c1.getName());
        assertEquals("internal", c1.getType().toLowerCase());
        assertNull(c1.getVersion());
        // assertEquals(0, c1.getDepth());
        assertEquals("ifoo", c1.getGroup());
        // assertNull(c1.getAlias());
        // assertNull(c1.getChildren());

        // Test resulting component 2
        SVIPComponentObject c2 = results.get(1).build();
        assertEquals("o", c2.getName());
        assertEquals("internal", c2.getType().toLowerCase());
        assertNull(c2.getVersion());
        // assertEquals(0, c2.getDepth());
        assertEquals("ifoo", c2.getGroup());
        // assertNull(c2.getAlias());
        // assertNull(c2.getChildren());
    }

    @Test
    @DisplayName("import ..")
    void importRel2InternalBasic() {
        Matcher m = getMatcher("import ..");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Absolute", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("import ..b")
    void importRel2InternalBasic2() {
        Matcher m = getMatcher("import ..b");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("b", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("Absolute", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("from .. import r")
    void fromImportRel2InternalBasic() {
        Matcher m = getMatcher("from .. import r");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("r", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("Absolute", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("from .. import (a, r)")
    void fromImportRel2InternalWithDelimiter() {
        Matcher m = getMatcher("from .. import (a, r)");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(2, results.size());    // should 3 matches

        // Test resulting component 1
        SVIPComponentObject c1 = results.get(0).build();
        assertEquals("a", c1.getName());
        assertEquals("internal", c1.getType().toLowerCase());
        assertNull(c1.getVersion());
        // assertEquals(0, c1.getDepth());
        assertEquals("Absolute", c1.getGroup());
        // assertNull(c1.getAlias());
        // assertNull(c1.getChildren());

        // Test resulting component 2
        SVIPComponentObject c2 = results.get(1).build();
        assertEquals("r", c2.getName());
        assertEquals("internal", c2.getType().toLowerCase());
        assertNull(c2.getVersion());
        // assertEquals(0, c2.getDepth());
        assertEquals("Absolute", c2.getGroup());
        // assertNull(c2.getAlias());
        // assertNull(c2.getChildren());
    }

    @Test
    @DisplayName("from .. import (\n\tb,\n\ta,\n)")
    void fromImportRel2InternalMultiline() {
        Matcher m = getMatcher("from .. import (\n\tb,\n\ta,\n)");

        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(2, results.size());    // should 3 matches

        // Test resulting component 1
        SVIPComponentObject c1 = results.get(0).build();
        assertEquals("b", c1.getName());
        assertEquals("internal", c1.getType().toLowerCase());
        assertNull(c1.getVersion());
        // assertEquals(0, c1.getDepth());
        assertEquals("Absolute", c1.getGroup());
        // assertNull(c1.getAlias());
        // assertNull(c1.getChildren());

        // Test resulting component 2
        SVIPComponentObject c2 = results.get(1).build();
        assertEquals("a", c2.getName());
        assertEquals("internal", c2.getType().toLowerCase());
        assertNull(c2.getVersion());
        // assertEquals(0, c2.getDepth());
        assertEquals("Absolute", c2.getGroup());
        // assertNull(c2.getAlias());
        // assertNull(c2.getChildren());
    }


}