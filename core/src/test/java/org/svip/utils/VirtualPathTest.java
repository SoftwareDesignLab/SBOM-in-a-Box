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
