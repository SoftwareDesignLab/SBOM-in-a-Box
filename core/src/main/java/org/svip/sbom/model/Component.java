package org.svip.sbom.model;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbomanalysis.comparison.conflicts.ComponentConflict;

import java.util.*;

/**
 * File: Component.java
 * Represents a single component inside an SBOM
 *
 * @author Matt London
 * @author Kevin Laporte
 * @author Ian Dunn
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
     * Group name of this component
     */
    private String group;

    /**
     * If the component is unpackaged (not included in SPDX notation)
     */
    private boolean unpackaged;

    /**
     * If files were analyzed to create the component.
     */
    private boolean filesAnalyzed;

    /**
     * Unique identifiers of the component (ex: CDX uses purl and/or cpe)
     */
    private Set<String> cpes;
    private Set<String> purls;
    private Set<String> swids;

    /**
     * Stored Hash values for Components
     */
    private Set<Hash> hashes;

    /**
     * Unique identifier for a component in an SBOM
     * For SPDX SBOMs this may be : SPDX_ID
     * For CDX SBOMs this may be : bom-ref
     */
    private String uniqueID;

    /**
     * UUIDs for the children of the given component
     */
    private Set<UUID> children;

    /**
     * Version of the component (version assigned by publisher)
     */
    private String version;

    /**
     * List of vulnerabilities found (created by NVIP)
     */
    private Set<Vulnerability> vulnerabilities;

    /**
     * Represent the license of the component
     */
    private Set<String> licenses;

    /**
     * All extracted licenses found in this component.
     * <ul>
     *     <li>The key of the first map is the ID of the license.</li>
     *     <li>The value of the first map is another map that contains all license data with the following keys:
     *          <ul>
     *              <li>{@code name}     - The name of the license.</li>
     *              <li>{@code text}     - The extracted text of the license (if any).</li>
     *              <li>{@code crossRef} - The cross reference of the license (if any).</li>
     *          </ul>
     *     </li>
     * </ul>
     */
    private Map<String, Map<String, String>> extractedLicenses;

    /**
     * Represent the conflicts of the component with other components
     * Note: This should ONLY be used in the master SBOM and never in individual sboms
     */
    private Set<ComponentConflict> componentConflicts;

    /**
     * Download location for the package. This field is exclusive to SPDX.
     */
    private String downloadLocation;

    /**
     * Unique verification code for the package. This field is exclusive to SPDX. See
     * <a href="https://spdx.github.io/spdx-spec/v2.3/package-information/#79-package-verification-code-field">
     *     the specification</a> for more. Note that this is not the same as a hash.
     */
    private String verificationCode;

    /**
     * Constructs a component with no attributes
     */
    public Component() {
        // This should not be a parameter passed as children will not be instantiated first
        this.uuid = UUID.randomUUID();
        this.name = null;
        this.publisher = "Unknown";
        this.group = null;
        this.unpackaged = false;
        this.filesAnalyzed = false;
        this.cpes = new HashSet<>();
        this.purls = new HashSet<>();
        this.swids = new HashSet<>();
        this.hashes = new HashSet<>();
        this.uniqueID = null;
        this.children = new HashSet<>();
        this.version = null;
        this.vulnerabilities = new HashSet<>();
        this.licenses = new HashSet<>();
        this.extractedLicenses = new HashMap<>();
        this.componentConflicts = new HashSet<>();
        this.downloadLocation = null;
        this.verificationCode = null;
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
    public Component(String name, String publisher, String version, Set<String> CPE, Set<String> PURL,
                     Set<String> SWID) {
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
        this.uuid = component.uuid;
        this.name = component.name;
        this.publisher = component.publisher;
        this.unpackaged = component.unpackaged;
        this.filesAnalyzed = component.filesAnalyzed;
        this.cpes = component.cpes;
        this.purls = component.purls;
        this.swids = component.swids;
        this.hashes = component.hashes;
        this.uniqueID = component.uniqueID;
        this.children = component.children;
        this.version = component.version;
        this.vulnerabilities = component.vulnerabilities;
        this.licenses = component.licenses;
        this.componentConflicts = component.componentConflicts;
        this.downloadLocation = component.downloadLocation;
        this.verificationCode = component.verificationCode;
    }

    ///
    /// Getters and Setters
    ///

    public UUID getUUID() { return uuid; }

    public void setUUID(UUID componentUUID) { this.uuid = componentUUID; }

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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void addLicense(String license) {
        licenses.add(license);
    }

    public Set<String> getLicenses() {
        return licenses;
    }

    public void setLicenses(HashSet<String> licenses) {
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

    public Set<String> getPurls() {
        return purls;
    }

    public void setPurls(Set<String> purls) {
        this.purls = purls;
    }

    public void addPURL(String purl) {
        this.purls.add(purl);
    }

    public Set<Hash> getHashes() {
        return this.hashes;
    }

    public void setHashes(Set<Hash> hashes) {
        this.hashes = hashes;
    }
    public void addHash(Hash hash) {
        this.hashes.add(hash);
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

    public boolean areFilesAnalyzed() {
        return filesAnalyzed;
    }

    public void setFilesAnalyzed(boolean filesAnalyzed) {
        this.filesAnalyzed = filesAnalyzed;
    }

    public String getDownloadLocation() {
        return downloadLocation;
    }

    public void setDownloadLocation(String downloadLocation) {
        this.downloadLocation = downloadLocation;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    /**
     * Add an extracted license to this component with the following properties.
     *
     * @param id The ID of the license.
     * @param name The name of the license.
     * @param text The text of the license (if any).
     * @param crossRef The cross-reference of the license (if any).
     */
    public void addExtractedLicense(@Nonnull String id, @Nonnull String name, @Nullable String text,
                                    @Nullable String crossRef) {
        Map<String, String> licenseContents = new HashMap<>() {{
            this.put("name", name);
            if(text != null) this.put("text", text);
            if(crossRef != null) this.put("crossRef", crossRef);
        }};

        extractedLicenses.put(id, licenseContents);
    }

    /**
     * Returns all extracted licenses in a {@code Map<String, Map<String, String>>}.
     * <ul>
     *     <li>The key of the first map is the ID of the license.</li>
     *     <li>The value of the first map is another map that contains all license data with the following keys:
     *          <ul>
     *              <li>{@code name}     - The name of the license.</li>
     *              <li>{@code text}     - The extracted text of the license (if any).</li>
     *              <li>{@code crossRef} - The cross reference of the license (if any).</li>
     *          </ul>
     *     </li>
     * </ul>
     *
     * @return All extracted licenses.
     */
    public Map<String, Map<String, String>> getExtractedLicenses() {
        return extractedLicenses;
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

            if (otherComponent.hashes != null) {
                retval = retval && otherComponent.hashes.equals(this.hashes);
            } else {
                retval = retval && this.hashes == null;
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
