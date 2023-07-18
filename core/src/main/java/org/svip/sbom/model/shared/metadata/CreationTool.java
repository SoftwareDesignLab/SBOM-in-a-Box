package org.svip.sbom.model.shared.metadata;

import org.svip.sbomanalysis.comparison.conflicts.Comparable;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;
import org.svip.sbomanalysis.comparison.conflicts.ConflictFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.svip.sbomanalysis.comparison.conflicts.MismatchType.MISC_MISMATCH;

/**
 * File: CreationTool.java
 * Represent a tool used to create the SBOM Data
 *
 * @author Derek Garcia
 */
public class CreationTool implements Comparable {
    private String vendor;
    private String name;
    private String version;
    private final Map<String, String> hashes = new HashMap<>();

    //
    // Setters
    //

    /**
     * @param vendor vendor name
     */
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }


    /**
     * @param name tool name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param version tool version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    //
    // Adders
    //

    /**
     * @param algorithm Hash Algorithm
     * @param hash Hash value
     */
    public void addHash(String algorithm, String hash){
        this.hashes.put(algorithm, hash);
    }

    ///
    /// Getters
    ///

    /**
     * @return Vendor name
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * @return Tool name
     */
    public String getName() {
        return name;
    }

    /**
     * @return Tool Version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return Hashes
     */
    public Map<String, String> getHashes() {
        return hashes;
    }


    @Override
    public List<Conflict> compare(Comparable o) {
        // Don't compare if not instance of same object
        if(!(o instanceof CreationTool other))
            return null;

        ConflictFactory cf = new ConflictFactory();
        cf.addConflict("Tool Vendor", MISC_MISMATCH, this.vendor, other.getVendor());
        cf.addConflict("Tool Name", MISC_MISMATCH, this.name, other.getName());
        cf.addConflict("Tool Version", MISC_MISMATCH, this.version, other.getVendor());

        return cf.getConflicts();
    }

    @Override
    public boolean equals(Object o) {
        // Test if correct class
        if (o == null || getClass() != o.getClass()) return false;
        CreationTool other = (CreationTool) o;

        // todo more lax comparison like Contact?

        // Check if vendor equivalent
        if(!this.vendor.equals(other.getVendor()))
            return false;

        // Check if name equivalent
        if(!this.name.equals(other.getName()))
            return false;

        // Check if version equivalent
        if(!this.version.equals(other.getVersion()))
            return false;

        // Compare hashes
        for(String alg : this.hashes.keySet()){
            // Missing hash
            if(!other.getHashes().containsKey(alg))
                return false;
            // Same hash alg, different values
            if(!other.getHashes().get(alg).equals(this.hashes.get(alg)))
                return false;
        }

        // All checks pass
        return true;
    }
}
