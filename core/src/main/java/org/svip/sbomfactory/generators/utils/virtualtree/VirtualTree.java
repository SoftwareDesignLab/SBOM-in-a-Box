package org.svip.sbomfactory.generators.utils.virtualtree;

import org.svip.sbomfactory.generators.utils.Debug;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * File VirtualTree.java
 *
 * VirtualTree is a complete, internal, in-memory representation of any file tree. A VirtualTree is constructed by
 * adding a list of file paths, and an internal structure of VirtualNodes is created to represent each directory and
 * file. It also stores the file contents, and can return a list of all files in the file tree.
 *
 * @author Ian Dunn
 */
public class VirtualTree {

    //#region Attributes

    /**
     * The root node of the tree. This is usually the working directory of the generator.
     */
    VirtualNode root;

    //#endregion

    //#region Constructors

    /**
     * Constructor to initialize an empty VirtualTree. TODO If a root is not initialized before tree methods are used,
     * a NullPointerException may be thrown.
     */
    public VirtualTree() {
        this.root = null;
    }

    /**
     * Constructor to initialize a VirtualTree with a root node.
     *
     * @param root The root node of the VirtualTree.
     */
    public VirtualTree(VirtualNode root) {
        this.root = root;
    }

    /**
     * Initialize a VirtualTree with a filepath and the contents of that file.
     *
     * @param filePath The filepath to initialize the VirtualTree with. The root directory of this filepath will be the
     *                 root node of the tree.
     * @param fileContents The file contents to initialize the VirtualNode that holds the file with.
     */
    public VirtualTree(VirtualPath filePath, String fileContents) {
        this.root = new VirtualNode(filePath.getRoot(), fileContents);
    }

    /**
     * Initialize a VirtualTree with a directory path.
     *
     * @param directoryPath The directory path to initialize the VirtualTree with. The root directory of this directory
     *                      path will be the root node of the tree.
     */
    public VirtualTree(VirtualPath directoryPath) {
        this(directoryPath, null);
    }

    //#endregion

    //#region Static Methods

    /**
     * Builds a VirtualTree from an existing file tree located in the user's filesystem given a source path. This loops
     * through all files and directories using Files.walk() over the source path, and adds each file with its contents
     * to the VirtualTree.
     *
     * @param src The source path to build the file tree representation from.
     * @return A new VirtualTree that represents all files and directories in the source path.
     */
    public static VirtualTree buildVirtualTree(VirtualPath src) {
        VirtualTree tree = new VirtualTree(src); // New VirtualTree with root node of source path.

        final long buildT1 = System.currentTimeMillis();

        // Build the tree by finding each file and adding to the virtual tree
        try (Stream<Path> stream = Files.walk(src.getPath())) {
            stream.forEach(filepath -> {
                // Only add the directory + files of the path if the file is found - no empty directories
                if (!Files.isDirectory(filepath)) {
                    try {
                        tree.addNode(new VirtualPath(filepath), Files.readString(filepath));
                    } catch (IOException e) {
                        Debug.log(Debug.LOG_TYPE.WARN, "Unable to read file contents of: " + filepath);
                    }
                }
            });
        } catch (Exception e) {
            Debug.log(Debug.LOG_TYPE.ERROR, "Unable to access file");
            Debug.log(Debug.LOG_TYPE.EXCEPTION, e.getMessage());
        }

        final long buildT2 = System.currentTimeMillis();

        // Report stats
        log(Debug.LOG_TYPE.SUMMARY, String.format("VirtualTree construction complete. " +
                        "Found %s Directories and %s Files in %.2f seconds",
                tree.getNumDirectories(),
                tree.getAllFiles().size(),
                (float)(buildT2 - buildT1) / 1000));

        return tree;
    }

    //#endregion

    //#region Core Methods

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

    public boolean contains(VirtualPath fileName) {
        for(VirtualNode file : getAllFiles()) {
            if(file.getPath().endsWith(fileName)) return true;
        }

        return false;
    }

    //#endregion

    //#region Getters

    public VirtualNode getRoot() {
        return root;
    }

    /**
     * Gets the first "common directory" of the file tree. This is the first directory that occurs with either multiple
     * children or at least one file. For example, this would turn a project with /src/main/java/mypackage/... into just
     * /mypackage/...
     *
     * @return The common directory node of this file tree.
     */
    public VirtualNode getCommonDirectory() {
        VirtualNode ptr = root;
        while(ptr.getChildren().size() == 1 && ptr.getLeafs().size() == 0) {
            ptr = ptr.getChildren().toArray(new VirtualNode[0])[0];
        }

        return ptr;
    }

    /**
     * Return a recursive list of all file nodes (including file contents) in the VirtualTree.
     *
     * @return A recursive list of all file nodes.
     */
    public List<VirtualNode> getAllFiles() {
        return findLeafNodesRecursive(root, root.getPath());
    }

    /**
     * Get the total number of directories in the VirtualTree.
     *
     * @return The total number of directories in the VirtualTree.
     */
    public int getNumDirectories() {
        return getNumDirectoriesRecursive(root);
    }

    //#endregion

    //#region Helper Methods

    /**
     * A private, recursive helper method to return a flat list of ALL leaf nodes with full filepaths from the given
     * node.
     *
     * @param node The node to return all leaf nodes of, as well as the leaf nodes of its children recursively.
     * @param previousPath The previous path from the root node.
     * @return A flat list of ALL leaf nodes found recursively with full filepaths.
     */
    private List<VirtualNode> findLeafNodesRecursive(VirtualNode node, VirtualPath previousPath) {
        // Duplicates all leafs and then appends their path to the previous node's path
        List<VirtualNode> leafs = new ArrayList<>(node.getLeafs().stream().map(n -> {
            // TODO is there an easier way to do this?
            VirtualNode newPath = new VirtualNode(n);
            newPath.setPath(previousPath.concatenate(n.getPath()));
            return newPath;
        }).toList());

        for(VirtualNode child : node.getChildren()) {
            leafs.addAll(findLeafNodesRecursive(child, previousPath.concatenate(child.getPath())));
        }

        return leafs;
    }

    /**
     * A private, recursive helper method to return a total of ALL directories underneath the given node.
     *
     * @param node The node to get all directories under.
     * @return The number of directories underneath this node (non-inclusive).
     */
    private int getNumDirectoriesRecursive(VirtualNode node) {
        int total = node.getChildren().size();

        for(VirtualNode child : node.getChildren()) {
            total += getNumDirectoriesRecursive(child);
        }

        return total;
    }

    //#endregion

    //#region Overrides

    @Override
    public String toString() {
        return root.toString(); // Return the root node toString, which recursively lists all other nodes beneath it
    }

    //#endregion
}
