package org.svip.sbomfactory.generators.utils.virtualtree;

import java.util.Iterator;
import java.util.function.Consumer;

public class VirtualTree implements Iterable<VirtualNode> {

    VirtualNode root;

    public VirtualTree(VirtualNode root) {
        this.root = root;
    }

    public void addNode(VirtualPath filePath, String fileContents) {
        root.addNode(filePath, fileContents);
    }

    @Override
    public Iterator<VirtualNode> iterator() {
        // TODO
        return null;
    }

    @Override
    public void forEach(Consumer<? super VirtualNode> action) {
        // TODO
        Iterable.super.forEach(action);
    }
}
