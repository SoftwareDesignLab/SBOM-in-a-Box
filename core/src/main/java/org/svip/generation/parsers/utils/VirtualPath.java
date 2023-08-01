package org.svip.generation.parsers.utils;

import org.svip.utils.Debug;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * File: VirtualPath.java
 *
 * VirtualPath is a representation of a filepath, similar to Java's Path implementation. It contains functionality to
 * auto-remove . and .., as well as finding the root and file/extension. It also contains traditional string
 * manipulation functions, such as concatenation and endsWith to make path construction and comparison easier. It is
 * also faster than Path, due to the fact that it is more lightweight and is string-based rather than filesystem-based.
 *
 * @author Ian Dunn
 * @author Dylan Mulligan
 * @author Asa Horn
 */
public class VirtualPath {

    //#region Attributes

    /**
     * A broken down list of "parts" of the path, a.k.a. a list of all directories and the last directory or file at the
     * end.
     */
    private List<String> pathParts;

    //#endregion

    //#region Constructors

    /**
     * Construct a VirtualPath from an existing Path.
     *
     * @param path The path to construct the VirtualPath from. If preceded or appended with a /, it will remove them. If
     *             the path contains any . or .. characters, it will modify the final VirtualPath instance to remove
     *             these and represent the same directory/file.
     */
    public VirtualPath(Path path) {
        this(path.toString());
    }

    /**
     * Construct a VirtualPath from a filepath string.
     *
     * @param path The non-null, non-empty filepath string. If preceded or appended with a /, it will remove them. If
     *             the path contains any . or .. characters, it will modify the final VirtualPath instance to remove
     *             these and represent the same directory/file.
     */
    public VirtualPath(String path) {
        // This statement first splits the path by the \ character, then joins it with / and separates it by /.
        // This achieves the goal of splitting the path into an OS-independent representation.
        this(Arrays.stream(String.join("/", path.split("\\\\")).split("/")).toList());
    }

    /**
     * Construct a VirtualPath from a list of path parts. Used to construct from a split array of strings or a split
     * path.
     *
     * @param pathParts The list of path parts.
     */
    protected VirtualPath(List<String> pathParts) {
        if(pathParts.size() == 0) throw new IllegalArgumentException("Empty path string provided");
        if(pathParts.size() == 1 && pathParts.get(0).equals(""))
            throw new IllegalArgumentException("Empty path string provided");

        List<String> tempPathParts = new ArrayList<>(pathParts);
        // Remove / at beginning if something like /SVIP/...
        if(tempPathParts.get(0).equals("")) tempPathParts.remove(0);
        // Remove / at end if something like SVIP/core/
        if(tempPathParts.get(tempPathParts.size() - 1).equals("")) tempPathParts.remove(tempPathParts.size() - 1);

        if(String.join("", tempPathParts).equals(""))
            throw new IllegalArgumentException("Invalid path string provided");

        this.pathParts = new ArrayList<>();

        // Handle any . or .. path elements passed in and modify the path accordingly
        for(int i = 0; i < tempPathParts.size(); i++) {
            switch(tempPathParts.get(i)) {
                case "." -> { continue; }
                case ".." -> { this.pathParts.remove(i); } // TODO make sure this works
                default -> { this.pathParts.add(tempPathParts.get(i)); }
            }
        }
    }

    //#endregion

    //#region Getters

    /**
     * Gets the parent (everything before the file name) of the current VirtualPath.
     *
     * @return The parent of the current VirtualPath.
     */
    public VirtualPath getParent() {
        if(this.pathParts.size() > 1)
            return new VirtualPath(this.pathParts.subList(0, this.pathParts.size() - 1));
        else
            return this;
    }

    /**
     * Gets the root (the first element) of the current VirtualPath.
     *
     * @return The root of the current VirtualPath.
     */
    public VirtualPath getRoot() {
        return new VirtualPath(this.pathParts.get(0));
    }

    /**
     * Get the number of parts in the VirtualPath.
     *
     * @return The number of parts in the VirtualPath.
     */
    public int getLength() {
        return pathParts.size();
    }

    /**
     * Get the filename of the VirtualPath.
     *
     * @return The filename of the VirtualPath. If it is a path to a directory, return the last directory name.
     */
    public VirtualPath getFileName() {
        return new VirtualPath(this.pathParts.get(pathParts.size() - 1));
    }

    /**
     * Get the corresponding Path instance of the VirtualPath.
     *
     * @return The corresponding Path instance of the VirtualPath.
     */
    public Path getPath() {
        return Path.of(this.toString());
    }

    /**
     * Get the file extension of the VirtualPath.
     *
     * @return The file extension of the VirtualPath. If the VirtualPath is a directory, return null.
     */
    public String getFileExtension() {
        if(!this.isFile()) return null;

        String fileName = this.getFileName().toString();
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    /**
     * Check if this VirtualPath is a file.
     *
     * @return True if it is a file, false if it is a directory.
     */
    public boolean isFile() {
        return this.getFileName().toString().contains(".");
    }

    //#endregion

    //#region Filepath Modification

    /**
     * Removes the root (top-level) directory from this VirtualPath.
     *
     * @return A new VirtualPath with the root directory removed. If removing the root directory results in an empty
     * path (i.e. the previous path length was 1), this returns null.
     */
    public VirtualPath removeRoot() {
        List<String> newPathParts = this.pathParts.subList(1, this.pathParts.size());
        if(newPathParts.size() == 0) return null;

        return new VirtualPath(newPathParts);
    }

    /**
     * Removes the file extension from this VirtualPath.
     *
     * @return A new VirtualPath with the file extension removed.
     */
    public VirtualPath removeFileExtension() {
        if(!this.isFile()) return this;

        return new VirtualPath(this.toString().substring(0, this.toString().lastIndexOf('.')));
    }

    /**
     * Concatenates another VirtualPath to this instance.
     *
     * @param path The VirtualPath to append to the end of this instance.
     * @return A new VirtualPath with the concatenated path.
     */
    public VirtualPath concatenate(VirtualPath path) {
        List<String> concatenatedPath = new ArrayList<>(pathParts);
        concatenatedPath.addAll(path.pathParts);
        return new VirtualPath(concatenatedPath);
    }

    //#endregion

    /**
     * Checks if a path ends with another path. The path being passed in must be a subpath of this path. Not the other
     * way around.
     *
     * @param other the path to be checked
     * @return True if this path ends with other, false otherwise
     */
    public boolean endsWith(VirtualPath other){
        try{
            if(this.toString().endsWith(other.toString())) {
                return this.getFileName().equals(other.getFileName()); // Ensure last part of path is fully complete
            };
        } catch (InvalidPathException e){
            Debug.log(Debug.LOG_TYPE.ERROR, "Invalid Path");
            Debug.log(Debug.LOG_TYPE.EXCEPTION, e.getMessage());
        }
        return false;
    }

    //#region Overrides
    @Override
    public String toString() {
        return String.join("/", this.pathParts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VirtualPath that)) return false;

        return Objects.equals(pathParts, that.pathParts);
    }

    @Override
    public int hashCode() {
        return pathParts != null ? pathParts.hashCode() : 0;
    }

    //#endregion
}
