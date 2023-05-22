package org.svip.sbom.model;

import java.util.Objects;

/**
 * <b>File</b>: PURL.java<br>
 * <b>Description</b>: Object representation of the Package URl for a component
 *
 * @author Juan Francisco Patino
 */
public class PURL {

    private String name;
    private String version;
    private ComponentPackageManager pm;
    private String PURLString;

    public PURL(String PURL){
        this.PURLString = PURL;
        addFromString();
    }

    public PURL(){}

    /**
     * Helper function to convert the string representation to a class
     * given the object already contains a PURL String
     */
    public void addFromString(){
        assert PURLString != null;
        addFromString(PURLString);
    }

    /**
     * Helper function to convert the string representation to a class
     * @param purl the PURL String
     */
    public void addFromString(String purl){
        String p = purl.toLowerCase();
        if(p.contains("alpine"))
            setPackageManager(ComponentPackageManager.ALPINE);
        else if(p.contains("debian"))
            setPackageManager(ComponentPackageManager.DEBIAN);
        else if(p.contains("python"))
            setPackageManager(ComponentPackageManager.PYTHON);
        else
            setPackageManager(ComponentPackageManager.NUGET); // add cases here as PMs are added


        try{
            String[] purlSplit = p.split("[/@]");
            this.name = purlSplit[2];
            purlSplit = p.split("[@?]");
            this.version = purlSplit[1];
        }catch (IndexOutOfBoundsException e){
            // some PURLS don't have version / this is just for convenience
        }


    }

    ///
    /// Getters and Setters
    ///

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ComponentPackageManager getPackageManager() {
        return pm;
    }

    public void setPackageManager(ComponentPackageManager pm) {
        this.pm = pm;
    }

    public void setPURLString(String PURLString) {
        this.PURLString = PURLString;
    }

    ///
    /// Overrides
    ///

    @Override
    public String toString() {
        return PURLString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PURL purl)) return false;
        return Objects.equals(getName(), purl.getName()) && Objects.equals(getVersion(), purl.getVersion()) && pm == purl.pm && Objects.equals(PURLString, purl.PURLString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getVersion(), pm, PURLString);
    }
}