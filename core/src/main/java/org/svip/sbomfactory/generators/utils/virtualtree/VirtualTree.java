package org.svip.sbomfactory.generators.utils.virtualtree;

import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class VirtualTree implements Iterable<VirtualNode> {

    VirtualNode root;

    public VirtualTree(VirtualNode root) {
        this.root = root;
    }

    // Initialize with a file
    public VirtualTree(VirtualPath filePath, String fileContents) {
        // TODO do we support a single file being added?
        this.root = new VirtualNode(filePath.getRoot(), null);
        this.addNode(filePath.removeRoot(), fileContents);
    }

    public VirtualTree(VirtualPath directoryPath) {
        this(directoryPath, null);
    }

    public void addNode(VirtualPath filePath, String fileContents) {
        if(filePath.getRoot().equals(root.getPath())) {
            root.addNode(filePath.removeRoot(), fileContents);
            return;
        }

        root.addNode(filePath, fileContents);
    }

    public VirtualNode getRoot() {
        return root;
    }

    @Override
    public Iterator<VirtualNode> iterator() {
        return findLeafNodesRecursive(root).iterator(); // TODO remove will not work, does it matter?
    }

    public int getTotalFiles() {
        return findLeafNodesRecursive(root).size();
    }

    private List<VirtualNode> findLeafNodesRecursive(VirtualNode node) {
        List<VirtualNode> leafs = new ArrayList<>(node.getLeafs());

        for(VirtualNode child : node.getChildren()) {
            leafs.addAll(findLeafNodesRecursive(child));
        }

        return leafs;
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
