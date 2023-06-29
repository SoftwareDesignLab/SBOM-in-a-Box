package org.svip.sbom.model.shared.metadata;

import java.util.HashMap;
import java.util.Map;

/**
 * File: CreationTool.java
 * Represent a tool used to create the SBOM Data
 *
 * @author Derek Garcia
 */
public class CreationTool {
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


}
