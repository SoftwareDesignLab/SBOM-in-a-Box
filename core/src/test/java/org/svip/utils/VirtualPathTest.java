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

package org.svip.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.svip.generation.parsers.utils.VirtualPath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VirtualPathTest {
    private static final String EMPTY_PATH = "";
    private static final String SRC_PATH = "/";

    // Small test path
    private static final String SMALL_FORWARD_SLASH_PATH = "a/b/c.java";
    private static final String SMALL_BACK_SLASH_PATH = "a\\b\\c.java";
    private static final String SMALL_PARENT_DIR = "a/b";
    private static final String SMALL_FILENAME = "c.java";

    // Large test path
    private static final String LARGE_MIXED_SLASH_PATH = "a/b\\c/d";

    private static final String LARGE_PARENT_DIR = "a/b/c";
    private static final String LARGE_FILENAME = "d";


    public VirtualPathTest() {
        Debug.enableDebug();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {EMPTY_PATH, SRC_PATH})
    void invalidPathTest(String empty) {
        assertThrows(IllegalArgumentException.class, () -> {
            new VirtualPath(EMPTY_PATH);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {SMALL_FORWARD_SLASH_PATH, SMALL_BACK_SLASH_PATH})
    void smallPathTest() {
        VirtualPath forward = new VirtualPath(SMALL_FORWARD_SLASH_PATH);
        assertEquals(SMALL_PARENT_DIR, forward.getParent().toString());
        assertEquals(SMALL_FILENAME, forward.getFileName().toString());
    }

    @Test
    void largeMixedPathTest() {
        VirtualPath mixed = new VirtualPath(LARGE_MIXED_SLASH_PATH);
        assertEquals(LARGE_PARENT_DIR, mixed.getParent().toString());
        assertEquals(LARGE_FILENAME, mixed.getFileName().toString());
    }
}
