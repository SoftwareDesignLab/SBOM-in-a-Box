package org.svip.repair.extraction;

import org.svip.sbom.model.uids.Hash.Algorithm;
import org.svip.sbom.model.uids.PURL;

import java.util.HashMap;
import java.util.Map;

/**
 * <b>File</b>: Extraction.java<br>
 * <b>Description</b>: Abstract core Class for extracting information
 * from package manager metadata
 * @author Justin Jantzi
 */
public abstract class Extraction {

    protected PURL purl;
    protected String copyright;
    protected String license;
    protected Map<Algorithm, String> hashes;

    /**
     * Creates a new object with the purl to use for extraction
     * @param purl the purl to be used
     */
    public Extraction(PURL purl) {
        this.purl = purl;
        this.copyright = "";
        this.license = "";
        this.hashes = new HashMap<>();
    }

    /**
     * Extracts the information based on the different
     * package manager implementation
     */
    public abstract void extract();

    /**
     * Gets the copyright from extracted text if exists
     * @return copyright
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * Gets the license from extracted text if exists
     * @return license
     */
    public String getLicense() {
        return license;
    }

    /**
     * Gets the hashes from maven repository if exists
     * @return hashes {algorithm : hash}
     */
    public Map<Algorithm, String> getHashes() {
        return hashes;
    }
}
