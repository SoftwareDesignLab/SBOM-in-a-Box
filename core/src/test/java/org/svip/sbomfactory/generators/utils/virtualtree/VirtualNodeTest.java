package org.svip.sbomfactory.generators.utils.virtualtree;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.utils.Debug;
import org.svip.utils.VirtualPath;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class VirtualNodeTest {
    private static final VirtualPath ROOT_PATH = new VirtualPath("root");
    private static final VirtualPath SINGLE_FILE_PATH = new VirtualPath("/test.txt");
    private static final VirtualPath DIR_PATH = new VirtualPath("/testDir");
    private static final VirtualPath DIR_FILE_PATH = new VirtualPath("/testDir/test.txt");

    private static final String FILE_CONTENTS = "test\n";

    private VirtualNode root;

    @BeforeEach
    void constructRoot() {
        root = new VirtualNode(ROOT_PATH, null);
    }

    @AfterEach
    void printNode() {
        assertNull(root.getFileContents());
        Debug.log(Debug.LOG_TYPE.SUMMARY, "Resulting Root VirtualNode:\n" + root);
    }

    @Test
    void addFileTest() {
        root.addNode(SINGLE_FILE_PATH, FILE_CONTENTS);
        assertChildrenAndLeafsSize(root, 1, 0);
        assertEquals(FILE_CONTENTS, getFirstInSet(root.getLeafs()).getFileContents());
    }

    @Test
    void addDirectoryTest() {
        root.addNode(DIR_PATH, null);
        assertChildrenAndLeafsSize(root, 0, 1);
        assertNull(getFirstInSet(root.getChildren()).getFileContents());
    }

    @Test
    void addMultiplePathsTest() {
        root.addNode(DIR_FILE_PATH, FILE_CONTENTS);
        assertChildrenAndLeafsSize(root, 0, 1);

        VirtualNode testDir = getFirstInSet(root.getChildren());
        assertChildrenAndLeafsSize(testDir, 1, 0);
        assertNull(testDir.getFileContents());
        assertEquals(FILE_CONTENTS, getFirstInSet(testDir.getLeafs()).getFileContents());
    }

    // Helper methods to decrease re-use of code
    // Check the given node to ensure its leafs and children are the specified sizes
    void assertChildrenAndLeafsSize(VirtualNode node, int leafs, int children) {
        assertEquals(leafs, node.getLeafs().size());
        assertEquals(children, node.getChildren().size());
    }

    // Get the "first" (usually only) element in a set of VirtualNodes
    VirtualNode getFirstInSet(Set<VirtualNode> children) {
        return children.toArray(new VirtualNode[0])[0];
    }
}
