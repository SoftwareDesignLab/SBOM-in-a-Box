package org.svip.sbom.model;

import org.svip.sbomanalysis.old.comparison.conflicts.ComponentConflict;
import org.svip.sbomvex.model.Vulnerability;

import java.util.*;

/**
 * File: Component.java
 * Represents a single component inside an SBOM
 *
 * @author Matt London
 * @author Kevin Laporte
 */
public class Component {

    /**
     * UUID assigned to Component upon being added to the dependency tree
     *
     * Note: only used for assembling Dependency Tree
     */
    private UUID uuid = null;

    /**
     * Name of the component
     */
    private String name;

    /**
     * Publisher of this component
     */
    private String publisher;

    /**
     * If the component is unpackaged (not included in SPDX notation)
     */
    private boolean unpackaged;

    /**
     * Unique identifiers of the component (ex: CDX uses purl and/or cpe)
     */
    private Set<String> cpes;
    private Set<PURL> purls;
    private Set<String> swids;

    /**
     * Unique identifier for a component in an SBOM
     * For SPDX SBOMs this may be : SPDX_ID
     * For CDX SBOMs this may be : bom-ref
     */
    private String uniqueID;

    /**
     * UUIDs for the children of the given component
     */
    private final Set<UUID> children;

    /**
     * Version of the component (version assigned by publisher)
     */
    private String version;

    /**
     * List of vulnerabilities found (created by NVIP)
     */
    private final Set<Vulnerability> vulnerabilities;

    /**
     * Represent the license of the component
     */
    private Set<String> licenses;

    /**
     * Represent the conflicts of the component with other components
     * Note: This should ONLY be used in the master SBOM and never in individual sboms
     */
    private final Set<ComponentConflict> componentConflicts;

    /**
     * Constructs a component with no attributes
     */
    public Component() {
        // This should not be a parameter passed as children will not be instantiated first
        this.children = new HashSet<>();
        this.vulnerabilities = new HashSet<>();
        this.cpes = new HashSet<>();
        this.purls = new HashSet<>();
        this.swids = new HashSet<>();
        this.componentConflicts = new HashSet<>();
        this.unpackaged = false;
    }

    /**
     * Constructs a component with the name field and version field
     * In order to search for vulnerabilities name and version are required
     *
     * @param name    Name of the component
     * @param version Version of the component
     */
    public Component(String name, String version) {
        this();
        this.name = name;
        this.version = version;
        this.publisher = "Unknown";
    }

    /**
     * Constructs a component with all attributes
     *
     * @param name      Name of the component
     * @param publisher Publisher of the component
     * @param version   Version number of the component
     */
    public Component(String name, String publisher, String version) {
        this(name, version);
        this.publisher = publisher;
    }

    /**
     * Constructs a component unique to packages in SPDX files without cpe/purl/swid
     *
     * @param name      Name of the component
     * @param publisher Publisher of the component
     * @param version   Version number of the component
     * @param uniqueID  SPDX ID
     */
    public Component(String name, String publisher, String version, String uniqueID) {
        this(name, publisher, version);
        this.uniqueID = uniqueID;
    }

    /**
     * Constructs a component with all attributes
     *
     * @param name      Name of the component
     * @param publisher Publisher of the component
     * @param version   Version number of the component
     * @param CPE       Set of CPEs of the component
     * @param PURL      Set of PURLs of the component
     * @param SWID      SWID of the component
     */
    public Component(String name, String publisher, String version, Set<String> CPE, Set<PURL> PURL, Set<String> SWID) {
        this(name, publisher, version);
        this.cpes = CPE;
        this.purls = PURL;
        this.swids = SWID;
    }

    /**
     * Copy a component's attributes to this component
     *
     * @param component Component to copy from
     */
    public void copyFrom(Component component) {
        this.name = component.name;
        this.publisher = component.publisher;
        this.uuid = component.uuid;
        this.cpes = new HashSet<>(component.cpes);
        this.purls = new HashSet<>(component.purls);
        this.swids = new HashSet<>(component.swids);
        this.children.addAll(component.children);
        this.version = component.version;
        this.vulnerabilities.addAll(component.vulnerabilities);
        this.licenses = component.licenses;
    }

    ///
    /// Getters and Setters
    ///

    public UUID getUUID() { return uuid; }

    protected void setUUID(UUID componentUUID) { this.uuid = componentUUID; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void addLicense(String license) {
        if (licenses == null) {
            licenses = new HashSet<>();
        }
        licenses.add(license);
    }

    public Set<String> getLicenses() {
        return licenses;
    }

    public void setLicenses(Set<String> licenses) {
        this.licenses = licenses;
    }

    public String getLicense(String license) {
        return licenses.stream()
                .filter(license::equals)
                .findAny()
                .orElse(null);
    }

    public void setUnpackaged(boolean unpackaged) {
        this.unpackaged = unpackaged;
    }

    public boolean isUnpackaged() {
        return unpackaged;
    }

    public void addChild(UUID child) {
        children.add(child);
    }

    public Set<UUID> getChildren() {
        return children;
    }

    public void removeChild(UUID child) {
        children.remove(child);
    }

    public Set<String> getCpes() {
        return cpes;
    }

    public void setCpes(Set<String> cpe) {
        this.cpes = cpe;
    }

    public void addCPE(String cpe) {
        this.cpes.add(cpe);
    }

    public Set<PURL> getPurls() {
        return purls;
    }

    public void setPurls(Set<PURL> purls) {
        this.purls = purls;
    }

    public void addPURL(PURL purl) {
        this.purls.add(purl);
    }

    public Set<String> getSwids() {
        return swids;
    }

    public void setSwids(Set<String> swid) {
        this.swids = swid;
    }

    public void addSWID(String swid) {
        this.swids.add(swid);
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public Set<ComponentConflict> getConflicts() {
        return componentConflicts;
    }

    public void addConflict(ComponentConflict componentConflict) {
        componentConflicts.add(componentConflict);
    }

    public void addVulnerability(Vulnerability vulnerability) {
        vulnerabilities.add(vulnerability);
    }

    public Set<Vulnerability> getVulnerabilities() {
        return vulnerabilities;
    }

    ///
    /// Overrides
    ///

    @Override
    public String toString() {
        // Only add what is not null
        StringBuilder sb = new StringBuilder();
        if (this.publisher != null) {
            sb.append(this.publisher).append(" ");
        }
        if (this.name != null) {
            // This should never happen, but may as well guard against it
            sb.append(this.name);
        }
        if (this.version != null) {
            sb.append(":").append(this.version);
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        //multiply the hash of every vulnerability together
        int vulnerabilitiesHash = 1;
        for (Vulnerability vulnerability : vulnerabilities) {
            vulnerabilitiesHash *= vulnerability.hashCode();
        }
        return Objects.hash(name, publisher, cpes, purls, swids, children, version, licenses) * vulnerabilitiesHash;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Component) {
            Component otherComponent = (Component) other;
            // Check each field for null
            boolean retval = true;

            if (otherComponent.name != null) {
                retval = retval && otherComponent.name.equals(this.name);
            } else {
                retval = retval && this.name == null;
            }

            if (otherComponent.publisher != null) {
                retval = retval && otherComponent.publisher.equals(this.publisher);
            } else {
                retval = retval && this.publisher == null;
            }

            if (otherComponent.cpes != null) {
                retval = retval && otherComponent.cpes.equals(this.cpes);
            } else {
                retval = retval && this.cpes == null;
            }

            if (otherComponent.purls != null) {
                retval = retval && otherComponent.purls.equals(this.purls);
            } else {
                retval = retval && this.purls == null;
            }

            if (otherComponent.swids != null) {
                retval = retval && otherComponent.swids.equals(this.swids);
            } else {
                retval = retval && this.swids == null;
            }

            if (otherComponent.children != null) {
                retval = retval && otherComponent.children.equals(this.children);
            } else {
                retval = retval && this.children == null;
            }

            if (otherComponent.version != null) {
                retval = retval && otherComponent.version.equals(this.version);
            } else {
                retval = retval && this.version == null;
            }

            if (otherComponent.licenses != null) {
                retval = retval && otherComponent.licenses.equals(this.licenses);
            } else {
                retval = retval && this.licenses == null;
            }

            if (otherComponent.vulnerabilities != null) {
                retval = retval && otherComponent.vulnerabilities.equals(this.vulnerabilities);
            } else {
                retval = retval && this.vulnerabilities == null;
            }

            return retval;
        }
        return false;
    }
}
