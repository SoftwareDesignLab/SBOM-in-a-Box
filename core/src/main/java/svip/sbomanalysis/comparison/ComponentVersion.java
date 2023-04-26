package svip.sbomanalysis.comparison;

import svip.sbomanalysis.differ.UniqueIdOccurrence;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * File: ComponentVersion.java
 * A component of a specific version.
 * holds data about the component and a set of the IDs of the SBOMs it appears in.
 *
 * @author Juan Francisco Patino, Tyler Drake, Henry Orsagh
 */
public class ComponentVersion {


    /**
     * Name of the component
     */
    private String componentName;

    /**
     * version of the component
     */
    private String version;

    /**
     * a set of all CPEs associated with this component version
     */
    private Set<UniqueIdOccurrence> CPEs;

    /**
     * a set of all PURLs associated with this component version
     */
    private Set<UniqueIdOccurrence> PURLs;

    /**
     * a set of all SWIDs associated with this component version
     */
    private Set<UniqueIdOccurrence> SWIDs;

    /**
     * a set of SBOM ID numbers where this component version appears
     */
    private Set<Integer> appearances;

    /**
     * initializes component version with empty uniqueID HashSets.
     * @param componentName name of the component
     * @param version version of the component
     */
    public ComponentVersion(String componentName, String version) {
        this.componentName = componentName;
        this.version = version;
        this.CPEs = new HashSet<>();
        this.PURLs = new HashSet<>();
        this.SWIDs = new HashSet<>();
        this.appearances = new HashSet<>();
    }

    // getters and setters

    public String getComponentName() {
        return this.componentName;
    }

    public String getComponentVersion() {
        return this.version;
    }

    public Set<UniqueIdOccurrence> getCPEs() {
        return this.CPEs;
    }

    public Set<UniqueIdOccurrence> getPURLs() {
        return this.PURLs;
    }

    public Set<UniqueIdOccurrence> getSWIDs() {
        return this.SWIDs;
    }

    public Set<Integer> getAppearances() {
        return this.appearances;
    }


    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public void setComponentVersion(String version) {
        this.version = version;
    }

    public void setCPEs(Set<UniqueIdOccurrence> CPEs) {
        this.CPEs = CPEs;
    }

    public void setPURLs(Set<UniqueIdOccurrence> PURLs) {
        this.PURLs = PURLs;
    }

    public void setSWIDs(Set<UniqueIdOccurrence> SWIDs) {
        this.SWIDs = SWIDs;
    }

    public void setAppearances(Set<Integer> appearances) {
        this.appearances = appearances;
    }

    // add individual objects to respective sets

    /**
     * Adds a CPE to this componentVersion's CPE set.
     * @param CPE CPE to add.
     */
    public void addCPE(UniqueIdOccurrence CPE){
        CPEs.add(CPE);
    }


    /**
     * Adds a PURL to this componentVersion's PURL set.
     * @param PURL PURL to add.
     */
    public void addPURL(UniqueIdOccurrence PURL){
        PURLs.add(PURL);
    }

    /**
     * Adds a SWID to this componentVersion's SWID set.
     * @param SWID SWID to add.
     */
    public void addSWID(UniqueIdOccurrence SWID){
        SWIDs.add(SWID);
    }

    /**
     * adds an SBOM ID to the appearance set.
     * @param a ID of the SBOM being added.
     */
    public void addAppearance(int a){
        this.appearances.add(a);
    }

    // overrides

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComponentVersion that)) return false;
        return Objects.equals(getComponentName(), that.getComponentName()) && Objects.equals(version, that.version);
    }

    @Override
    public String toString() {
        return "ComponentVersion{" +
                "componentName='" + componentName + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(getComponentName(), version);
    }
}
