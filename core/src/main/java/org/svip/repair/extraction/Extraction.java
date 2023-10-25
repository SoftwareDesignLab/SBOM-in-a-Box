package org.svip.repair.extraction;

import org.svip.sbom.model.uids.PURL;

import java.util.HashMap;
import java.util.List;

/**
 * <b>File</b>: Extraction.java<br>
 * <b>Description</b>: Abstract core Class for extracting information
 * from package manager metadata
 * @author Justin Jantzi
 */
public abstract class Extraction {

    protected PURL purl;
    protected HashMap<String, String> results;

    /**
     * Creates a new object with the purl to use for extraction
     * @param purl the purl to be used
     */
    public Extraction(PURL purl) {
        this.purl = purl;
        this.results = new HashMap<String, String>();
    }

    /**
     * Extracts the information based on the different
     * package manager implementation
     */
    public abstract void extract();

    /**
     * Checks to see if the results from extracted text contains the key
     * and if so return the value
     * @param key what to check (ie: copyright)
     * @return value or null
     */
    private String getValue(String key) {
        if(!results.containsKey(key))
            return null;

        return results.get(key);
    }

    /**
     * Gets the copyright from extracted text if exists
     * @return copyright
     */
    public String getCopyright() {
        return getValue("copyright");
    }

    /**
     * Gets the license from extracted text if exists
     * @return license
     */
    public String getLicense() {
        return getValue("license");
    }

    /**
     * Gets the hashes from maven repository if exists
     * @return hashes {algorithm : hash}
     */
    public HashMap<String, String> getHashes() {
        HashMap<String, String> hashes = new HashMap<>(results);
        hashes.keySet().retainAll(List.of("md5", "sha1"));
        return hashes;
    }
}
