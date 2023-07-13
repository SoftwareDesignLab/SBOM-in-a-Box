package org.svip.sbomfactory.generators.utils.virtualtree;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.svip.utils.Debug;
import org.svip.utils.VirtualPath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VirtualTreeTest {
    private static final VirtualPath FILE_PATH_1 = new VirtualPath("/a/b/c/d.txt");
    private static final VirtualPath FILE_PATH_2 = new VirtualPath("a/b/README.md");
    private static final VirtualPath FILE_2 = new VirtualPath("README.md");
    private static final VirtualPath SUB_FILE_2 = new VirtualPath("/b/README.md");

    private static final VirtualPath DIR_PATH = new VirtualPath("/a/b/c/d");
    private static final String FILE_CONTENTS_1 = "test";
    private static final String FILE_CONTENTS_2 = "# Test";

    private VirtualTree tree;

    @AfterEach
    void printTree() {
        Debug.log(Debug.LOG_TYPE.SUMMARY, "Resulting VirtualTree:\n" + tree);
    }

    @Test
    void initWithNoRootTest() {
        tree = new VirtualTree();
        tree.addNode(FILE_PATH_1, FILE_CONTENTS_1);
        assertEquals(1, tree.getAllFiles().size());
    }

    @Test
    void initWithFileTest() {
        tree = new VirtualTree(FILE_PATH_1, FILE_CONTENTS_1);
        assertEquals(1, tree.getAllFiles().size());
        assertEquals(FILE_CONTENTS_1, tree.getAllFiles().get(0).getFileContents());
    }

    @Test
    void initWithDirectoryTest() {
        tree = new VirtualTree(DIR_PATH);
        assertEquals(0, tree.getAllFiles().size());
    }

    @Test
    void addMultipleFilesTest() {
        tree = new VirtualTree(FILE_PATH_1, FILE_CONTENTS_1);
        tree.addNode(FILE_PATH_2, FILE_CONTENTS_2);
        assertEquals(2, tree.getAllFiles().size());
    }

    @Test
    void containsTest() {
        tree = new VirtualTree(FILE_PATH_1, FILE_CONTENTS_1);
        tree.addNode(FILE_PATH_2, FILE_CONTENTS_2);
        assertTrue(tree.contains(FILE_2));
        assertTrue(tree.contains(SUB_FILE_2));
    }
}
