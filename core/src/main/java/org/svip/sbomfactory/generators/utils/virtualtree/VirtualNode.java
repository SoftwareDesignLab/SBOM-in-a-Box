package org.svip.sbomfactory.generators.utils.virtualtree;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class VirtualNode {

    private final Set<VirtualNode> children;
    private final Set<VirtualNode> leafs;
    private final String fileContents;
    private final VirtualPath path;

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
        List<String> pathParts = filePath.getPathParts();
        VirtualPath current = new VirtualPath(pathParts.remove(0));

        if(pathParts.size() == 0) { // If no more remaining parts, we have reached either a file or end directory
            if(current.isFile())
                this.leafs.add(new VirtualNode(current, fileContents));
            else
                this.children.add(new VirtualNode(current, fileContents));
            return;
        }

        children.add(new VirtualNode(current, null)); // Since this is a set, we're fine adding duplicates
        for(VirtualNode subDir : children) {
            if(subDir.path.equals(current)) { // Find the current directory and add node
                subDir.addNode(new VirtualPath(pathParts), fileContents);
            }
        }
    }

    public Set<VirtualNode> getChildren() {
        return children;
    }

    public Set<VirtualNode> getLeafs() {
        return leafs;
    }

    public String getFileContents() {
        return fileContents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VirtualNode that)) return false;

        if (!children.equals(that.children)) return false;
        if (!leafs.equals(that.leafs)) return false;
        if (!Objects.equals(fileContents, that.fileContents)) return false;
        return Objects.equals(path, that.path);
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
        return "VirtualNode{" +
                "children=" + children +
                ", leafs=" + leafs +
                ", fileContents='" + fileContents + '\'' +
                ", path=" + path +
                '}';
    }
}
