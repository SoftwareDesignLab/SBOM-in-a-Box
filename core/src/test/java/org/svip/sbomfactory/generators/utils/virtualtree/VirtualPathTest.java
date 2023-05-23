package org.svip.sbomfactory.generators.utils.virtualtree;

import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.generators.utils.Debug;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VirtualPathTest {
    public VirtualPathTest() {
        Debug.enableDebug();
    }

    @Test
    void emptyStringTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            new VirtualPath("");
        });
    }

    @Test
    void srcDirectoryTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            new VirtualPath("/");
        });
    }

    @Test
    void forwardSlashPathTest() {
        VirtualPath forward = new VirtualPath("a/b/c.java");
        assertEquals("a/b", forward.getParent().toString());
        assertEquals("c.java", forward.getFileName().toString());
    }

    @Test
    void backSlashPathTest() {
        VirtualPath back = new VirtualPath("a\\b\\c.java");
        assertEquals("a/b", back.getParent().toString());
        assertEquals("c.java", back.getFileName().toString());
    }

    @Test
    void mixedSlashPathTest() {
        VirtualPath mixed = new VirtualPath("a/b\\c/d");
        assertEquals("a/b/c", mixed.getParent().toString());
        assertEquals("d", mixed.getFileName().toString());
    }
}
