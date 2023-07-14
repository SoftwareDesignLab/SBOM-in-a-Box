package org.svip.sbomfactory.parsers.languages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Testing for Python Parser Regex with '#' comments
 *
 * @author Derek Garcia
 */
class PythonParserRegexLineCommentTest extends ParseRegexTestCore {
    /**
     * Constructor initializes a given parser and assigns both the
     * regex to test it against and the source directory to test on.
     *
     */
    public PythonParserRegexLineCommentTest() {
        super(new PythonParser(),
                "(?:(?=\"\"\")\"\"\"[\\S\\s]*?\"\"\"|^import (.*)$|^from (.*) import (?:(?=.*\\($)([\\S\\s]*?\\))|(.*)))",
                "Python/Absolute/ifoo");
    }

    //
    // Regex Tests
    //
    @Test
    @DisplayName("# import foo")
    void importBasic() {
        Matcher m = getMatcher("# import foo");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# import foo.bar")
    void importBasicTwo() {
        Matcher m = getMatcher("# import foo.bar");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# import foo as f")
    void importBasicAlias() {
        Matcher m = getMatcher("# import foo as f");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# from foo import bar")
    void fromImportBasic() {
        Matcher m = getMatcher("# from foo import bar");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# import foo as b, a")
    void fromImportTwoItems() {
        Matcher m = getMatcher("# from foo import b, a");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# from foo import bar as b")
    void fromImportWithAlias() {
        Matcher m = getMatcher("# from foo import bar as b");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# from foo import (b, a, r)")
    void fromImportWithParenthesis() {
        Matcher m = getMatcher("# from foo import (b, a, r)");
        assertFalse(m.find());   // Should be no matches
    }


    @Test
    @DisplayName("# import random")
    void importExternalLanguage() {
        Matcher m = getMatcher("# import random");
        assertFalse(m.find());   // Should be no matches
    }

    ///
    /// Internal Tests
    ///
    @Test
    @DisplayName("# import ifoo")
    void importAbsInternalBasic() {
        Matcher m = getMatcher("# import ifoo");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# import ifoo as if")
    void importAbsInternalAlias() {
        Matcher m = getMatcher("# import ifoo as if");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# import ifoo.bar")
    void importAbsInternalPath() {
        Matcher m = getMatcher("# import ifoo.ibar");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# import ifoo.ibar as ifb")
    void importAbsInternalPathWithAlias() {
        Matcher m = getMatcher("# import ifoo.ibar as ifb");
        assertFalse(m.find());   // Should be no matches
    }


    @Test
    @DisplayName("# from ifoo.ibar import ifoobarModule")
    void fromImportAbsInternalPath() {
        Matcher m = getMatcher("# from ifoo.ibar import ifoobarModule");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# from ifoo.ibar.ifoobarModule import ifoobarClass")
    void fromImportAbsInternalPathModule() {
        Matcher m = getMatcher("# from ifoo.ibar.ifoobarModule import ifoobarClass");
        assertFalse(m.find());   // Should be no matches
    }


    @Test
    @DisplayName("# from ifoo.ifbar import ib, ia, ir")
    void fromImportAbsInternalPathWithDelimiter() {
        Matcher m = getMatcher("# from ifoo.ibar import ib, ia, ir");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# from ifoo.ifbar import (ib, ia, ir)")
    void fromImportAbsInternalPathWithDelimiter2() {
        Matcher m = getMatcher("# from ifoo.ibar import (ib, ia, ir)");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# from ifoo.ibar import ib, ia as IA, ir")
    void fromImportAbsInternalPathWithDelimiterAndAlias() {
        Matcher m = getMatcher("# from ifoo.ibar import ib, ia as IA, ir");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# import .")
    void importRelInternalBasic() {
        Matcher m = getMatcher("# import .");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# import .ibar")
    void importRelInternalBasic2() {
        Matcher m = getMatcher("# import .ibar");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# from . import ibar")
    void fromImportRelInternalBasic() {
        Matcher m = getMatcher("# from . import ibar");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# from . import (f, o)")
    void fromImportRelInternalWithDelimiter() {
        Matcher m = getMatcher("# from . import (f, o)");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# import ..")
    void importRel2InternalBasic() {
        Matcher m = getMatcher("# import ..");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# import ..f")
    void importRel2InternalBasic2() {
        Matcher m = getMatcher("# import ..f");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# from .. import f")
    void fromImportRel2InternalBasic() {
        Matcher m = getMatcher("# from .. import f");
        assertFalse(m.find());   // Should be no matches
    }

    @Test
    @DisplayName("# from .. import (f, o)")
    void fromImportRel2InternalWithDelimiter() {
        Matcher m = getMatcher("# from .. import (f, o)");
        assertFalse(m.find());   // Should be no matches
    }
}