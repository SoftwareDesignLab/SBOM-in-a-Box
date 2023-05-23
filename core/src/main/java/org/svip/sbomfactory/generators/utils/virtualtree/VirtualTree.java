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

    private List<VirtualNode> findLeafNodesRecursive(VirtualNode node) {
        List<VirtualNode> leafs = new ArrayList<>();

        leafs.addAll(node.getLeafs());

        for(VirtualNode child : root.getChildren()) {
            leafs.addAll(findLeafNodesRecursive(child));
        }

        return leafs;
    }
}
