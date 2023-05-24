package org.svip.sbomfactory.generators.utils.virtualtree;

import java.util.HashSet;
import java.util.Set;

/**
 * File: VirtualNode.java
 *
 * VirtualNode represents a node in a VirtualTree This is either a file or directory. If a VirtualNode is a file, it
 * contains the contents of the file internally.
 *
 * @author Ian Dunn
 */
public class VirtualNode {

    //#region Attributes

    /**
     * The children (directories) of the VirtualNode
     */
    private final Set<VirtualNode> children;

    /**
     * The leafs (files) of the VirtualNode
     */
    private final Set<VirtualNode> leafs;

    /**
     * The file contents of the VirtualNode, if it's a file. If not, it will be initialized to null.
     */
    private final String fileContents;

    /**
     * The path of this VirtualNode. This is simply the SINGLE directory name/filename in the tree.
     */
    private VirtualPath path;

    //#endregion Attributes

    //#region Constructors

    /**
     * Constructs a new VirtualNode with a path and (optional) file contents.
     *
     * @param path The path of the VirtualNode.
     * @param fileContents The file contents of the VirtualNode (if any).
     */
    public VirtualNode(VirtualPath path, String fileContents) {
        this.children = new HashSet<>();
        this.leafs = new HashSet<>();
        this.fileContents = fileContents;
        this.path = path;
    }

    /**
     * Copy constructor for VirtualNode.
     *
     * @param old The VirtualNode to copy.
     */
    public VirtualNode(VirtualNode old) {
        this.children = old.children;
        this.leafs = old.leafs;
        this.fileContents = old.fileContents;
        this.path = old.path;
    }

    //#endregion

    //#region Core Methods

    /**
     * Add a node to this VirtualNode as either a file or directory by examining the root directory and checking if
     * it already exists in children. If so, add the path minus the root directory to that child. Otherwise, create a
     * new child and do the same thing. Once the end of the path is reached, a new node will be created and added to
     * either children or leafs, depending on if the filepath is a directory or not.
     *
     * @param filePath The FULL filepath of the file/directory to add.
     * @param fileContents The file contents of the file, if it is a file.
     */
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

    //#endregion

    //#region Getters

    /**
     * Checks if the VirtualNode is a directory by examining the file contents.
     *
     * @return True if it is a directory, false otherwise.
     */
    public boolean isDirectory() {
        return fileContents == null;
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

    //#endregion

    //#region Setters

    /**
     * Set the path of this VirtualNode to a new path. Note that this does NOT change its position in a tree, this is
     * just for internal VirtualTree display purposes.
     *
     * @param path The path to set the VirtualNode to.
     */
    public void setPath(VirtualPath path) {
        this.path = path;
    }

    //#endregion

    //#region Overrides

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

    /**
     * A private recursive toString helper method that returns the string representation of a node, and all of its
     * children/leafs indented below it.
     *
     * @param node The node to print.
     * @param indent The indent character to use.
     * @return The string representation of the node.
     */
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

    //#endregion
}
