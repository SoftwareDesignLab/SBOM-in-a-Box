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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing for Python Parser Regex with block comments
 *
 * @author Derek Garcia
 */
class PythonParserRegexBlockCommentTest extends ParseRegexTestCore {
    /**
     * Constructor initializes a given parser and assigns both the
     * regex to test it against and the source directory to test on.
     *
     */
    public PythonParserRegexBlockCommentTest() {
        super(new PythonParser(),
                "(?:(?=\"\"\")\"\"\"[\\S\\s]*?\"\"\"|^import (.*)$|^from (.*) import (?:(?=.*\\($)([\\S\\s]*?\\))|(.*)))",
                "Python/Absolute/ifoo");
    }

    final String openBlockComment = "\"\"\"\n";
    final String closeBlockComment = "\n\"\"\"";
    
    //
    // Regex Tests
    //
    @Test
    @DisplayName(openBlockComment + "import foo" + closeBlockComment)
    void importBasic() {
        Matcher m = getMatcher(openBlockComment + "import foo" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components
    }

    @Test
    @DisplayName(openBlockComment + "import foo.bar" + closeBlockComment)
    void importBasicTwo() {
        Matcher m = getMatcher(openBlockComment + "import foo.bar" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "import foo as f" + closeBlockComment)
    void importBasicAlias() {
        Matcher m = getMatcher(openBlockComment + "import foo as f" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "from foo import bar" + closeBlockComment)
    void fromImportBasic() {
        Matcher m = getMatcher(openBlockComment + "from foo import bar" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "import foo as b, a" + closeBlockComment)
    void fromImportTwoItems() {
        Matcher m = getMatcher(openBlockComment + "from foo import b, a" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "from foo import bar as b" + closeBlockComment)
    void fromImportWithAlias() {
        Matcher m = getMatcher(openBlockComment + "from foo import bar as b" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "from foo import (b, a, r)" + closeBlockComment)
    void fromImportWithParenthesis() {
        Matcher m = getMatcher(openBlockComment + "from foo import (b, a, r)" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "from foo import (\n" +
            "\tb,\n" +
            "\ta,\n" +
            "\tr,\n" +
            ")" + closeBlockComment)
    void fromImportSeveralLine() {
        /*from foo import (
	        b,
	        a,
	        r,
        )*/
        Matcher m = getMatcher(openBlockComment + "from foo import (\n" +
                "\tb,\n" +
                "\ta,\n" +
                "\tr,\n" +
                ")" + closeBlockComment);

        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "import random" + closeBlockComment)
    void importExternalLanguage() {
        Matcher m = getMatcher(openBlockComment + "import random" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    ///
    /// Internal Tests
    ///
    @Test
    @DisplayName(openBlockComment + "import ifoo" + closeBlockComment)
    void importAbsInternalBasic() {
        Matcher m = getMatcher(openBlockComment + "import ifoo" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "import ifoo as if" + closeBlockComment)
    void importAbsInternalAlias() {
        Matcher m = getMatcher(openBlockComment + "import ifoo as if" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "import ifoo.bar" + closeBlockComment)
    void importAbsInternalPath() {
        Matcher m = getMatcher(openBlockComment + "import ifoo.ibar" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "import ifoo.ibar as ifb" + closeBlockComment)
    void importAbsInternalPathWithAlias() {
        Matcher m = getMatcher(openBlockComment + "import ifoo.ibar as ifb" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }


    @Test
    @DisplayName(openBlockComment + "from ifoo.ibar import ifoobarModule" + closeBlockComment)
    void fromImportAbsInternalPath() {
        Matcher m = getMatcher(openBlockComment + "from ifoo.ibar import ifoobarModule" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "from ifoo.ibar.ifoobarModule import ifoobarClass" + closeBlockComment)
    void fromImportAbsInternalPathModule() {
        Matcher m = getMatcher(openBlockComment + "from ifoo.ibar.ifoobarModule import ifoobarClass" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }


    @Test
    @DisplayName(openBlockComment + "from ifoo.ifbar import ib, ia, ir" + closeBlockComment)
    void fromImportAbsInternalPathWithDelimiter() {
        Matcher m = getMatcher(openBlockComment + "from ifoo.ibar import ib, ia, ir" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "from ifoo.ifbar import (ib, ia, ir)" + closeBlockComment)
    void fromImportAbsInternalPathWithDelimiter2() {
        Matcher m = getMatcher(openBlockComment + "from ifoo.ibar import (ib, ia, ir)" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "from ifoo.ibar import ib, ia as IA, ir" + closeBlockComment)
    void fromImportAbsInternalPathWithDelimiterAndAlias() {
        Matcher m = getMatcher(openBlockComment + "from ifoo.ibar import ib, ia as IA, ir" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "from ifoo.ibar import (\n" +
            "\tib, \n" +
            "\tia, \n" +
            "\tir\n" +
            ")" + closeBlockComment)
    void fromImportAbsInternalPathMultipleLines() {
        Matcher m = getMatcher(openBlockComment + "from ifoo.ibar import (\n" +
                "\tib, \n" +
                "\tia, \n" +
                "\tir\n" +
                ")" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "import ." + closeBlockComment)
    void importRelInternalBasic() {
        Matcher m = getMatcher(openBlockComment + "import ." + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "import .ibar" + closeBlockComment)
    void importRelInternalBasic2() {
        Matcher m = getMatcher(openBlockComment + "import .ibar" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "from . import ibar" + closeBlockComment)
    void fromImportRelInternalBasic() {
        Matcher m = getMatcher(openBlockComment + "from . import ibar" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "from . import (f, o)" + closeBlockComment)
    void fromImportRelInternalWithDelimiter() {
        Matcher m = getMatcher(openBlockComment + "from . import (f, o)" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "from . import (\n" +
            "\tf,\n" +
            "\to,\n" +
            ")" + closeBlockComment)
    void fromImportRelInternalMultiline() {
        Matcher m = getMatcher(openBlockComment + "from . import (\n" +
                "\tf,\n" +
                "\to,\n" +
                "))" + closeBlockComment);

        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "import .." + closeBlockComment)
    void importRel2InternalBasic() {
        Matcher m = getMatcher(openBlockComment + "import .." + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "import ..f" + closeBlockComment)
    void importRel2InternalBasic2() {
        Matcher m = getMatcher(openBlockComment + "import ..f" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "from .. import f" + closeBlockComment)
    void fromImportRel2InternalBasic() {
        Matcher m = getMatcher(openBlockComment + "from .. import f" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "from .. import (f, o)" + closeBlockComment)
    void fromImportRel2InternalWithDelimiter() {
        Matcher m = getMatcher(openBlockComment + "from .. import (f, o)" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }

    @Test
    @DisplayName(openBlockComment + "from .. import (\n" +
            "\tf,\n" +
            "\to,\n" +
            ")" + closeBlockComment)
    void fromImportRel2InternalMultiline() {
        Matcher m = getMatcher(openBlockComment + "from .. import (\n" +
                "\tf,\n" +
                "\to,\n" +
                "))" + closeBlockComment);
        assertTrue(m.find());   // Should be a match

        List<SVIPComponentBuilder> results = new ArrayList<>();
        this.PARSER.parseRegexMatch(results, m);
        assertEquals(0, results.size());    // should be no components   // Should be a match
    }


}