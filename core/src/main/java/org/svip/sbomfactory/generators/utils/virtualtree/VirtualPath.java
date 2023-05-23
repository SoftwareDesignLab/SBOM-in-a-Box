package org.svip.sbomfactory.generators.utils.virtualtree;

import org.svip.sbomfactory.generators.utils.Debug;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;

public class VirtualPath {
    private final String[] pathParts;

    public VirtualPath(Path path) {
        this(path.toString());
    }

    public VirtualPath(String path) {
        // This statement first splits the path by the \ character, then joins it with / and separates it by /.
        // This achieves the goal of splitting the path into an OS-independent representation.
        this(String.join("/", path.split("\\\\")).split("/"));
    }

    private VirtualPath(String[] pathParts) {
        if(pathParts.length == 0) throw new IllegalArgumentException("Invalid path string provided");
        if(pathParts.length == 1 && pathParts[0].equals(""))
            throw new IllegalArgumentException("Invalid path string provided");

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
        // TODO move this into test
        Debug.enableDebug();
//        final VirtualPath v1 = new VirtualPath("");
//        final String v1Constructor = "new VirtualPath(\"\")";
//        Debug.log(Debug.LOG_TYPE.DEBUG, String.format("%s.getParent(); %s", v1Constructor, v1.getParent()));
//        Debug.log(Debug.LOG_TYPE.DEBUG, String.format("%s.getFileName(); %s", v1Constructor, v1.getFileName()));

//        final VirtualPath v2 = new VirtualPath(new String[]{});
//        final String v2Constructor = "new VirtualPath(new String[]{})";
//        Debug.log(Debug.LOG_TYPE.DEBUG, String.format("%s.getParent(); %s", v2Constructor, v2.getParent()));
//        Debug.log(Debug.LOG_TYPE.DEBUG, String.format("%s.getFileName(); %s", v2Constructor, v2.getFileName()));

        final VirtualPath v3 = new VirtualPath("a/b/c");
        final String v3Constructor = "new VirtualPath(\"a/b/c\")";
        Debug.log(Debug.LOG_TYPE.DEBUG, String.format("%s.getParent(); %s", v3Constructor, v3.getParent()));
        Debug.log(Debug.LOG_TYPE.DEBUG, String.format("%s.getFileName(); %s", v3Constructor, v3.getFileName()));

        final VirtualPath v4 = new VirtualPath("a\\b\\c/d");
        final String v4Constructor = "new VirtualPath(\"a\\b\\c/d\")";
        Debug.log(Debug.LOG_TYPE.DEBUG, String.format("%s.getParent(); %s", v4Constructor, v4.getParent()));
        Debug.log(Debug.LOG_TYPE.DEBUG, String.format("%s.getFileName(); %s", v4Constructor, v4.getFileName()));

//        final VirtualPath v5 = new VirtualPath(new String[]{"", ""});
//        final String v5Constructor = "new VirtualPath(new String[]{\"\", \"\"})";
//        Debug.log(Debug.LOG_TYPE.DEBUG, String.format("%s.getParent(); %s", v5Constructor, v5.getParent()));
//        Debug.log(Debug.LOG_TYPE.DEBUG, String.format("%s.getFileName(); %s", v5Constructor, v5.getFileName()));
    }
}
