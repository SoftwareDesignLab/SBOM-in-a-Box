package org.svip.sbomfactory.generators.utils.virtualtree;

import java.util.HashSet;
import java.util.Set;

public class VirtualNode {

    Set<VirtualNode> children;
    Set<VirtualNode> leafs;
    String fileContents;
    VirtualPath path;

    public VirtualNode(String fileContents, VirtualPath path) {
        this.children = new HashSet<>();
        this.leafs = new HashSet<>();
        this.fileContents = fileContents;
        this.path = path;
    }

    public void addNode(VirtualPath filePath, String fileContents) {
        // TODO
    }
}
