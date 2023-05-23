package org.svip.sbomfactory.generators.utils.virtualtree;

import org.svip.sbomfactory.generators.utils.Debug;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class VirtualPath {
    private final List<String> pathParts;

    public VirtualPath(Path path) {
        this(path.toString());
    }

    public VirtualPath(String path) {
        // This statement first splits the path by the \ character, then joins it with / and separates it by /.
        // This achieves the goal of splitting the path into an OS-independent representation.
        this(Arrays.stream(String.join("/", path.split("\\\\")).split("/")).toList());
    }

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

        this.pathParts = tempPathParts;
    }

    public VirtualPath getParent() {
        if(this.pathParts.size() > 1)
            return new VirtualPath(this.pathParts.subList(0, this.pathParts.size() - 1));
        else
            return this;
    }

    public VirtualPath getRoot() {
        return new VirtualPath(this.pathParts.get(0));
    }

    public VirtualPath removeRoot() {
        List<String> newPathParts = this.pathParts.subList(1, this.pathParts.size());
        if(newPathParts.size() == 0) return null;

        return new VirtualPath(newPathParts);
    }

    public VirtualPath concatenate(VirtualPath path) {
        List<String> concatenatedPath = new ArrayList<>(pathParts);
        concatenatedPath.addAll(path.pathParts);
        return new VirtualPath(concatenatedPath);
    }

    public int getLength() {
        return pathParts.size();
    }

    @Override
    public String toString() {
        return String.join("/", this.pathParts);
    }

    public VirtualPath getFileName() {
        return new VirtualPath(this.pathParts.get(pathParts.size() - 1));
    }

    public Path getPath() {
        return Path.of(this.toString());
    }

    public boolean isFile() {
        return this.getFileName().toString().contains(".");
    }

    /**
     * Mirrors the Path.endsWith() method. Note the path being passed in must be a subpath of this path. Not the
     * other way around.
     *
     * @param other - the path to be checked
     * @return - true if this path ends with other, false otherwise
     */
    public boolean endsWith(VirtualPath other){
        try{
            return this.getPath().endsWith(other.getPath());
        } catch (InvalidPathException e){
            System.err.println("note: invalid path" + other);
        }
        return false;
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
}
