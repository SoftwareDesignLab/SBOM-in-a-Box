package org.svip.sbomfactory.generators.utils;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;

public class VirtualPath {
    private final String[] pathParts;

    public VirtualPath(String path) {
        this.pathParts = path.split("\\\\");
        if(this.pathParts.length == 0) throw new IllegalArgumentException("Invalid path string provided");
    }

    public VirtualPath(Path path) {
        this(path.toString());
    }

    private VirtualPath(String[] pathParts) {
        if(pathParts.length == 0) throw new IllegalArgumentException("Invalid path string provided");
        this.pathParts = pathParts;
    }

    public VirtualPath getParent() {
        if(this.pathParts.length > 1)
            return new VirtualPath(Arrays.copyOfRange(this.pathParts, 0, this.pathParts.length - 1));
        else
            return this;
    }

    @Override
    public String toString() {
        return String.join("\\\\", this.pathParts);
    }

    public VirtualPath getFileName() {
        return new VirtualPath(this.pathParts[pathParts.length - 1]);
    }

    public Path getPath() {
        return Path.of(this.toString());
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

    public static void main(String[] args) {
        final VirtualPath v1 = new VirtualPath("");
//        final VirtualPath v2 = new VirtualPath(new String[]{});
        final VirtualPath v3 = new VirtualPath("a/b/c");
        final VirtualPath v4 = new VirtualPath("a\\b\\c");
        final VirtualPath v5 = new VirtualPath(new String[]{"", ""});
        v1.getParent();
        v1.getFileName();
        v3.getParent();
        v3.getFileName();
        v4.getParent();
        v4.getFileName();
        v5.getParent();
        v5.getFileName();
        final String a = "";
    }
}
