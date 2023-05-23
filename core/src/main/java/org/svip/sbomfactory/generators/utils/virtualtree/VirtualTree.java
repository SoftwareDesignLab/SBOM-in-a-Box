package org.svip.sbomfactory.generators.utils.virtualtree;

import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class VirtualTree {

    VirtualNode root;

    public VirtualTree() {
        this.root = null;
    }

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
        if(this.root == null) {
            root = new VirtualNode(filePath.getRoot(), fileContents);
        }

        if(filePath.getRoot().equals(root.getPath())) {
            root.addNode(filePath.removeRoot(), fileContents);
            return;
        }

        root.addNode(filePath, fileContents);
    }

    public VirtualNode getRoot() {
        return root;
    }

    public boolean contains(VirtualPath fileName) {
        for(VirtualPath file : getAllFiles()) {
            if(file.endsWith(fileName)) return true;
        }

        return false;
    }

    public List<VirtualPath> getAllFiles() {
        return findLeafNodesRecursive(root, root.getPath());
    }

    private List<VirtualPath> findLeafNodesRecursive(VirtualNode node, VirtualPath previousPath) {
        List<VirtualPath> leafs = new ArrayList<>(
                node.getLeafs().stream().map(n -> previousPath.concatenate(n.getPath())).toList()
        );

        for(VirtualNode child : node.getChildren()) {
            leafs.addAll(findLeafNodesRecursive(child, previousPath.concatenate(child.getPath())));
        }

        return leafs;
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
