package org.svip.sbomfactory.generators.utils.virtualtree;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class VirtualNode {

    private final Set<VirtualNode> children;
    private final Set<VirtualNode> leafs;
    private final String fileContents;
    private VirtualPath path;

    public VirtualNode(VirtualPath path, String fileContents) {
        this.children = new HashSet<>();
        this.leafs = new HashSet<>();
        this.fileContents = fileContents;
        this.path = path;
    }

    public boolean isDirectory() {
        return fileContents == null;
    }

    public void addNode(VirtualPath filePath, String fileContents) {
        VirtualPath current = filePath.getRoot();
        VirtualPath remaining = filePath.removeRoot();

        if(remaining == null) { // If no more remaining parts, we have reached either a file or end directory
            if(current.isFile())
                this.leafs.add(new VirtualNode(current, fileContents));
            else
                this.children.add(new VirtualNode(current, fileContents));
            return;
        }

        children.add(new VirtualNode(current, null)); // Since this is a set, we're fine adding duplicates
        for(VirtualNode subDir : children) {
            if(subDir.path.equals(current)) { // Find the current directory and add node
                subDir.addNode(remaining, fileContents);
            }
        }
    }

    protected Set<VirtualNode> getChildren() {
        return children;
    }

    protected Set<VirtualNode> getLeafs() {
        return leafs;
    }

    public String getFileContents() {
        return fileContents;
    }

    public VirtualPath getPath() {
        return path;
    }

    public void setPath(VirtualPath path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VirtualNode that)) return false;

        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        int result = children.hashCode();
        result = 31 * result + leafs.hashCode();
        result = 31 * result + (fileContents != null ? fileContents.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return toString(this, "-");
    }

    private String toString(VirtualNode node, String indent) {
        StringBuilder out = new StringBuilder(node.path + "\n");

        if(node.leafs.size() > 0) {
            for(VirtualNode file : node.leafs) {
                out.append(indent).append(file.path)/*.append(": ").append(file.getFileContents())*/.append("\n");
            }
        }

        if(node.children.size() > 0) {
            for(VirtualNode child : node.children) {
                out.append(indent).append(toString(child, indent + "-")).append("\n");
            }
        }

        return out.toString();
    }
}
