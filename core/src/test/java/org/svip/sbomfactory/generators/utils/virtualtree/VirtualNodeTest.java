package org.svip.sbomfactory.generators.utils.virtualtree;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class VirtualNodeTest {
    public VirtualNodeTest() {

    }

    @Test
    void addFileTest() {
        VirtualNode root = new VirtualNode(new VirtualPath("root"), null);

        root.addNode(new VirtualPath("/test.txt"), "test\n");
        assertEquals(1, root.getLeafs().size());
        assertEquals(0, root.getChildren().size());
        assertNull(root.getFileContents());
        assertEquals("test\n", root.getLeafs().toArray(new VirtualNode[0])[0].getFileContents());
    }

    @Test
    void addDirectoryTest() {
        VirtualNode root = new VirtualNode(new VirtualPath("root"), null);

        root.addNode(new VirtualPath("/testDir"), null);
        assertEquals(0, root.getLeafs().size());
        assertEquals(1, root.getChildren().size());
        assertNull(root.getFileContents());
        assertNull(root.getChildren().toArray(new VirtualNode[0])[0].getFileContents());
    }

    @Test
    void addMultiplePathsTest() {
        VirtualNode root = new VirtualNode(new VirtualPath("root"), null);

        root.addNode(new VirtualPath("/testDir/test.txt"), "test\n");
        assertEquals(0, root.getLeafs().size());
        assertEquals(1, root.getChildren().size());

        VirtualNode testDir = root.getChildren().toArray(new VirtualNode[0])[0];
        assertEquals(0, testDir.getChildren().size());
        assertEquals(1, testDir.getLeafs().size());
        assertNull(testDir.getFileContents());
        assertEquals("test\n", testDir.getLeafs().toArray(new VirtualNode[0])[0].getFileContents());
    }
}
