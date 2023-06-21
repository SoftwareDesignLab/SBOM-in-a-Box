package org.svip.sbom.model;

import java.util.Objects;

/**
 * File: SBOM.java
 * Represents an application tool for an SBOM
 *
 * @author Juan Francisco Patino
 */
public class AppTool {

    /**
     * The vendor of the tool.
     */
    private String vendor;

    /**
     * The name of the tool.
     */
    private String name;

    /**
     * The version of the tool.
     */
    private String version;

    /**
     * Default constructor with no parameters. Initializes all attributes to null.
     */
    public AppTool() {
        this.vendor = null;
        this.name = null;
        this.version = null;
    }

    /**
     * Constructor with a complete set of attributes.
     *
     * @param vendor The vendor of the tool.
     * @param name The name of the tool.
     * @param version The version of the tool.
     */
    public AppTool(String vendor, String name, String version) {
        this.vendor = vendor;
        this.name = name;
        this.version = version;
    }

    /*
    Getters and setters
     */
    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

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

    // overrides
    @Override
    public String toString() {
        return "Tool: "
                + (this.vendor != null ? this.vendor + " " : "")
                + this.name + "-" + this.version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppTool appTool)) return false;
        return Objects.equals(getVendor(), appTool.getVendor()) && Objects.equals(getName(), appTool.getName()) && Objects.equals(getVersion(), appTool.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVendor(), getName(), getVersion());
    }
}
