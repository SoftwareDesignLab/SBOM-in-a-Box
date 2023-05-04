package org.svip.sbomfactory.generators.parsers.languages;

import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.util.Arrays;
import java.util.HashSet;

/**
 * file: CParser.java
 * Description: Language specific implementation of the ParserCore (C). This extends C++ parser since methods are near
 * identical
 *
 * @author Derek Garcia
 */
public class CParser extends CppParser {

    // List Header Files from C standard library
    // src: https://en.cppreference.com/w/c/header
    private final HashSet<String> C_STD_LIB = new HashSet<>(Arrays.asList(
            "assert.h",
            "complex.h",
            "ctype.h",
            "errno.h",
            "fenv.h",
            "float.h",
            "inttypes.h",
            "iso646.h",
            "limits.h",
            "locale.h",
            "math.h",
            "setjmp.h",
            "signal.h",
            "stdalign.h",
            "stdarg.h",
            "stdatomic.h",
            "stdbool.h",
            "stddef.h",
            "stdint.h",
            "stdio.h",
            "stdlib.h",
            "stdnoreturn.h",
            "string.h",
            "tgmath.h",
            "threads.h",
            "time.h",
            "uchar.h",
            "wchar.h",
            "wctype.h"
    ));

    /**
     * Check if in the ANSI C standard library
     *
     * @param component component to search for
     * @return true if language, false otherwise
     */
    @Override
    protected boolean isLanguageComponent(ParserComponent component) {
        return this.C_STD_LIB.contains(component.getName());
    }
}
