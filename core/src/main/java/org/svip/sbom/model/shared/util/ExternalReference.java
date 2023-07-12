package org.svip.sbom.model.shared.util;

import java.util.HashMap;
import java.util.Map;

/**
 * File: ExternalReference.java
 * External Reference model for references outside the SBOM
 *
 * @author Derek Garcia
 */
public class ExternalReference {

    private final String url;
    private final String type;
    private String category;
    private final Map<String, String> hashes = new HashMap<>();

    /**
     * CDX Style External Reference constructor
     *
     * @param url URL to reference
     * @param type Type of External Reference
     */
    public ExternalReference(String url, String type){
        this.url = url;
        this.type = type;
    }

    /**
     * SPDX Style External Reference constructor
     *
     * @param category Category of External Reference
     * @param url URL to reference
     * @param type Type of External Reference
     */
    public ExternalReference(String category, String url, String type){
        this.category = category;
        this.url = url;
        this.type = type;
    }


    /**
     * @param algorithm Hash Algorithm
     * @param hash Hash value
     */
    public void addHash(String algorithm, String hash){
        this.hashes.put(algorithm, hash);
    }

    ///
    /// getters
    ///

    /**
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * @return category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @return Hashes
     */
    public Map<String, String> getHashes() {
        return hashes;
    }
}
