package org.svip.sbomfactory.generators.utils.virtualtree;

import org.svip.sbomfactory.generators.utils.Debug;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.svip.sbomfactory.generators.utils.Debug.log;

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

    public VirtualNode getCommonDirectory() {
        VirtualNode ptr = root;
        while(ptr.getChildren().size() == 1 && ptr.getLeafs().size() == 0) {
            ptr = ptr.getChildren().toArray(new VirtualNode[0])[0];
        }

        return ptr;
    }

    public boolean contains(VirtualPath fileName) {
        for(VirtualNode file : getAllFiles()) {
            if(file.getPath().endsWith(fileName)) return true;
        }

        return false;
    }

    public List<VirtualNode> getAllFiles() {
        return findLeafNodesRecursive(root, root.getPath());
    }

    public int getNumDirectories() {
        return getNumDirectoriesRecursive(root);
    }

    private List<VirtualNode> findLeafNodesRecursive(VirtualNode node, VirtualPath previousPath) {
        // Duplicates all leafs and then appends their path to the previous node's path
        List<VirtualNode> leafs = new ArrayList<>(node.getLeafs().stream().map(n -> {
            // TODO is there an easier way to do this?
            VirtualNode newPath = new VirtualNode(n);
            newPath.setPath(previousPath.concatenate(n.getPath()));
            return newPath;
        }).toList());
//        leafs.stream().map(n -> n.setPath(previousPath.concatenate(n.getPath())));

        for(VirtualNode child : node.getChildren()) {
            leafs.addAll(findLeafNodesRecursive(child, previousPath.concatenate(child.getPath())));
        }

        return leafs;
    }

    private int getNumDirectoriesRecursive(VirtualNode node) {
        int total = node.getChildren().size();

        for(VirtualNode child : node.getChildren()) {
            total += getNumDirectoriesRecursive(child);
        }

        return total;
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public static VirtualTree buildVirtualTree(VirtualPath src) {
        VirtualTree tree = new VirtualTree(src);

        final long buildT1 = System.currentTimeMillis();

        // Build the tree by finding each file and adding to the virtual tree
        try (Stream<Path> stream = Files.walk(src.getPath())) {
            stream.forEach(filepath -> {
                // Only add the directory + files of the path if the file is found - no empty directories
                if (!Files.isDirectory(filepath)) {
                    try {
                        tree.addNode(new VirtualPath(filepath), Files.readString(filepath));
                    } catch (IOException e) {
                        Debug.log(Debug.LOG_TYPE.ERROR, "Unable to read file contents of: " + filepath);
                        Debug.log(Debug.LOG_TYPE.EXCEPTION, e.getMessage());
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
}
