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
 * file: PerlParserRegexTest.java
 * Description: Testing for Perl import regex edge cases, extends ParseRegexTestCore
 *
 * @author Dylan Mulligan
 */
public class PerlParserRegexTest extends ParseRegexTestCore {
    /**
     * Constructor initializes a given parser and assigns both the
     * regex to test it against and the source directory to test on.
     *
     */
    public PerlParserRegexTest() {
        super(new PerlParser(),
                "^(?:(?!#).)*(?:(?:use (?:(?=if).*, [ '\\\"]*(\\w+)[ '\\\"]|(?:(?=.*alias.*)[ '\\\"]*(.+)[ '\\\"]*|[ '\\\"]*(.+)[ '\\\"]*));)|require [ '\\\"]*([\\$\\w./:]+)[ '\\\"]*(?:.*->VERSION\\(([\\w.]*)|.*->import\\((.*)\\)|))(?![^\\=]*\\=(?:cut|end))",
                "Perl");
    }

    ///
    /// External
    ///

    @Test
    @DisplayName("use foo;")
    void useBasic() {
        Matcher m = getMatcher("use foo;");
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
    @DisplayName("use bar::foo;")
    void useMultipart() {
        Matcher m = getMatcher("use bar::foo;");
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
        assertEquals("bar", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("use if $var < 5.008, \"foo\";")
    void useConditional() {
        Matcher m = getMatcher("use if $var < 5.008, \"foo\";");
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
    @DisplayName("use package::alias 'Fbbq' => 'Foo::Barista::Bazoo::Qux';")
    void useAlias1() {
        Matcher m = getMatcher("use package::alias 'Fbbq' => 'Foo::Barista::Bazoo::Qux';");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Qux", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("Foo/Barista/Bazoo", c.getGroup());
//        assertEquals("Fbbq", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("use aliased 'Foo::Barista::Bazoo::Qux' => 'Fbbq';")
    void useAlias2() {
        Matcher m = getMatcher("use aliased 'Foo::Barista::Bazoo::Qux' => 'Fbbq';");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Qux", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("Foo/Barista/Bazoo", c.getGroup());
//        assertEquals("Fbbq", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("use namespace::alias 'Foo::Barista::Bazoo::Qux' => 'Fbbq';")
    void useAlias3() {
        Matcher m = getMatcher("use namespace::alias 'Foo::Barista::Bazoo::Qux' => 'Fbbq';");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("Qux", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
//         assertEquals(0, c.getDepth());
        assertEquals("Foo/Barista/Bazoo", c.getGroup());
//        assertEquals("Fbbq", c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("use 5.24.1;")
    void useVersion1() {
        Matcher m = getMatcher("use 5.24.1;");
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(0, results.size());    // should not find any matches
    }

    @Test
    @DisplayName("use v5.24.1;")
    void useVersion2() {
        Matcher m = getMatcher("use v5.24.1;");
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(0, results.size());    // should not find any matches
    }

    @Test
    @DisplayName("BEGIN { require foo}")
    void beginRequireBasic() {
        Matcher m = getMatcher("BEGIN { require foo}");
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
    @DisplayName("BEGIN { require foo; foo->VERSION(v12.34) }")
    void beginRequireVersion() {
        Matcher m = getMatcher("BEGIN { require foo; foo->VERSION(v12.34) }");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("foo", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertEquals("v12.34", c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("BEGIN { require foo; foo->import( free ); }")
    void beginRequireFrom() {
        Matcher m = getMatcher("BEGIN { require foo; foo->import( free ); }");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("free", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("foo", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require bar::foo;")
    void requireBasic() {
        Matcher m = getMatcher("require bar::foo;");
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
        assertEquals("bar", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require $var;")
    void requireVariable() {
        Matcher m = getMatcher("require $var;");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("$var", c.getName());
        assertEquals("external", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertNull(c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require 5.24.1;")
    void requireVersion1() {
        Matcher m = getMatcher("use 5.24.1;");
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(0, results.size());    // should not find any matches
    }

    @Test
    @DisplayName("require v5.24.1;")
    void requireVersion2() {
        Matcher m = getMatcher("require v5.24.1;");
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(0, results.size());    // should not find any matches
    }

    @Test
    @DisplayName("no foo;")
    void noBasic() {
        Matcher m = getMatcher("no foo;");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("no 6;")
    void noVersion() {
        Matcher m = getMatcher("no 6;");

        assertFalse(m.find());   // Should not be a match
    }

    ///
    /// DEFAULT (Should not create any components)
    ///

    @Test
    @DisplayName("use strict;")
    void useStrict() {
        Matcher m = getMatcher("use strict;");
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(0, results.size());    // should not find any matches
    }

    @Test
    @DisplayName("use warning;")
    void useWarning() {
        Matcher m = getMatcher("use warning;");
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(0, results.size());    // should not find any matches
    }

    @Test
    @DisplayName("use integer;")
    void useInteger() {
        Matcher m = getMatcher("use integer;");
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(0, results.size());    // should not find any matches
    }

    @Test
    @DisplayName("use bytes;")
    void useBytes() {
        Matcher m = getMatcher("use bytes;");
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(0, results.size());    // should not find any matches
    }

    @Test
    @DisplayName("use constant;")
    void useConstant() {
        Matcher m = getMatcher("use constant;");
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(0, results.size());    // should not find any matches
    }

    ///
    /// INTERNAL
    ///

    @Test
    @DisplayName("use lib::bar;")
    void useInternal() {
        Matcher m = getMatcher("use lib::bar;");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("bar", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("lib", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require lib::bar;")
    void requireInternal() {
        Matcher m = getMatcher("require lib::bar;");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("bar", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("lib", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    @Test
    @DisplayName("require \"lib/fee.pm\";")
    void requireInternal2() {
        Matcher m = getMatcher("require \"lib/fee.pm\";");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("fee", c.getName());
        assertEquals("internal", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("lib", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// LANGUAGE
    ///

    @Test
    @DisplayName("use autodie::exception::system;")
    void useLanguage() {
        Matcher m = getMatcher("use autodie::exception::system;");
        assertTrue(m.find());   // Should be a match
        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);

        assertEquals(1, results.size());    // should only be 1 match

        // Test resulting component
        SVIPComponentObject c = results.get(0).build();
        assertEquals("system", c.getName());
        assertEquals("language", c.getType().toLowerCase());
        assertNull(c.getVersion());
        // assertEquals(0, c.getDepth());
        assertEquals("autodie/exception", c.getGroup());
        // assertNull(c.getAlias());
        // assertNull(c.getChildren());
    }

    ///
    /// Comments
    ///

    @Test
    @DisplayName("# use foo;")
    void useHashtag() {
        Matcher m = getMatcher("# use foo;");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("=begin\nuse foo;\n=cut;")
    void useBlockComment() {
        Matcher m = getMatcher("=begin\nuse foo;\n=cut;");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("=begin\nrequire foo;\n=cut;")
    void requireBlockComment() {
        Matcher m = getMatcher("=begin\nrequire foo;\n=cut;");

        assertFalse(m.find());   // Should not be a match
    }

    @Test
    @DisplayName("=a\nuse foo ();\n=cut;\nuse foo;\n=begin\nuse foo;=cut;")
    void useBetweenBlockComments() {
        Matcher m = getMatcher("=a\nuse foo ();\n=cut;\nuse foo;\n=begin\nuse foo;=cut;");
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
    @DisplayName("use foo; # this imports foo")
    void useBeforeHashtag() {
        Matcher m = getMatcher("use foo; // this imports foo");
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

}
