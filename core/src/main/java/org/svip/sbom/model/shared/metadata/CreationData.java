package org.svip.sbom.model.shared.metadata;

import org.svip.compare.conflicts.Comparable;
import org.svip.compare.conflicts.Conflict;
import org.svip.compare.conflicts.ConflictFactory;

import java.util.*;

/**
 * File: CreationData.java
 * Contains additional creation details about the SBOM
 *
 * @author Derek Garcia
 */
public class CreationData implements Comparable{

    // Time SBOM was created
    private String creationTime;

    // authors of SBOM data
    private final Set<Contact> authors = new HashSet<>();

    // Manufacture of the component the SBOM describes
    private Organization manufacture;

    // Supplier of the component the SBOM describes
    private Organization supplier;
    private final Set<String> licenses = new HashSet<>();
    private final Map<String, Set<String>> properties = new HashMap<>();

    // Tools used to generate the SBOM
    private final Set<CreationTool> creationTools = new HashSet<>();
    private String creatorComment;

    ///
    /// Setters
    ///

    /**
     * @param creationTime Time SBOM was created
     */
    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * @param manufacture Manufacture
     */
    public void setManufacture(Organization manufacture) {
        this.manufacture = manufacture;
    }

    /**
     * @param supplier Supplier
     */
    public void setSupplier(Organization supplier) {
        this.supplier = supplier;
    }

    /**
     * @param creatorComment Comment
     */
    public void setCreatorComment(String creatorComment) {
        this.creatorComment = creatorComment;
    }

    ///
    /// Adders
    ///

    /**
     * @param author Author of SBOM Data
     */
    public void addAuthor(Contact author){
        this.authors.add(author);
    }

    /**
     * @param license License
     */
    public void addLicense(String license){
        this.licenses.add(license);
    }

    /**
     * @param key Property key
     * @param value Property Value
     */
    public void addProperty(String key, String value){
        // Create new index if one doesn't exist
        if(!this.properties.containsKey(key))
            this.properties.put(key, new HashSet<>());

        this.properties.get(key).add(value);
    }

    public void addCreationTool(CreationTool tool) {
        creationTools.add(tool);
    }

    ///
    /// Getters
    ///

    /**
     * @return creationTime
     */
    public String getCreationTime() {
        return creationTime;
    }

    /**
     * @return authors
     */
    public Set<Contact> getAuthors() {
        return authors;
    }

    /**
     * @return manufacture
     */
    public Organization getManufacture() {
        return manufacture;
    }

    /**
     * @return supplier
     */
    public Organization getSupplier() {
        return supplier;
    }

    /**
     * @return licenses
     */
    public Set<String> getLicenses() {
        return licenses;
    }

    /**
     * @return properties
     */
    public Map<String, Set<String>> getProperties() {
        return properties;
    }

    /**
     * @return creationTools
     */
    public Set<CreationTool> getCreationTools() {
        return creationTools;
    }

    /**
     * @return creatorComment
     */
    public String getCreatorComment() {
        return creatorComment;
    }

    ///
    /// Util
    ///
    @Override
    public List<Conflict> compare(Comparable o){
        // Don't compare if not instance of same object
        if(!(o instanceof CreationData other))
            return null;

        ConflictFactory cf = new ConflictFactory();

        // Compare single String fields
        cf.addConflict("Timestamp", TIMESTAMP_MISMATCH, this.creationTime, other.getCreationTime());
        cf.addConflict("Creator Comment", MISC_MISMATCH, this.creatorComment, other.getCreatorComment());

        // Compare licenses
        // todo dif between this and sbom licenses?
        cf.compareStringSets("Creation Data: License", LICENSE_MISMATCH, this.licenses, other.getLicenses());

        // Comparable Sets
        cf.compareComparableSets("Creation Data: Author", new HashSet<>(this.authors), new HashSet<>(other.getAuthors()));
        cf.compareComparableSets("Creation Data: Tool", new HashSet<>(this.creationTools), new HashSet<>(other.getCreationTools()));

        // Compare Objects
        if(cf.comparable("Manufacture", this.manufacture, other.getManufacture()))
            cf.addConflicts(this.manufacture.compare(other.getManufacture()));
        if(cf.comparable("Supplier", this.supplier, other.getSupplier()))
            cf.addConflicts(this.supplier.compare(other.getSupplier()));

        // todo
        //  properties
        return cf.getConflicts();
    }
}
