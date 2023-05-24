package org.svip.sbomfactory.generators.utils.virtualtree;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VirtualTreeTest {
    public VirtualTreeTest() {

    }

    @Test
    void initWithNoRootTest() {
        VirtualTree tree = new VirtualTree();
        tree.addNode(new VirtualPath("/a/b/c/d.txt"), "test");
        assertEquals(1, tree.getAllFiles().size());
    }

    @Test
    void initWithFileTest() {
        VirtualTree tree = new VirtualTree(new VirtualPath("/a/b/c/d.txt"), "test");
        assertEquals(1, tree.getAllFiles().size());
        assertEquals("test", tree.getAllFiles().get(0).getFileContents());
        System.out.println(tree);
    }

    @Test
    void initWithDirectoryTest() {
        VirtualTree tree = new VirtualTree(new VirtualPath("/a/b/c/d"));
        assertEquals(0, tree.getAllFiles().size());
        System.out.println(tree);
    }

    @Test
    void addMultipleFilesTest() {
        VirtualTree tree = new VirtualTree(new VirtualPath("/a/b/c/d.txt"), "test");
        tree.addNode(new VirtualPath("a/b/README.md"), "# Test");
        System.out.println(tree);
        // TODO assertions
    }

    @Test
    void containsTest() {
        VirtualTree tree = new VirtualTree(new VirtualPath("/a/b/c/d.txt"), "test");
        tree.addNode(new VirtualPath("a/b/README.md"), "# Test");
        assertTrue(tree.contains(new VirtualPath("README.md")));
        assertTrue(tree.contains(new VirtualPath("/b/README.md")));
    }
}
