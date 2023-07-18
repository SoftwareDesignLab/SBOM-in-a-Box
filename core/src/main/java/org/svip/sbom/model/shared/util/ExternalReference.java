package org.svip.sbom.model.shared.util;

import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbomanalysis.comparison.conflicts.Comparable;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;
import org.svip.sbomanalysis.comparison.conflicts.ConflictFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.svip.sbomanalysis.comparison.conflicts.MismatchType.MISC_MISMATCH;
import static org.svip.sbomanalysis.comparison.conflicts.MismatchType.TIMESTAMP_MISMATCH;

/**
 * File: ExternalReference.java
 * External Reference model for references outside the SBOM
 *
 * @author Derek Garcia
 */
public class ExternalReference implements Comparable {

    private final String url;
    private String comment;
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

    /**
     * @param comment Reference comment
     */
    public void setComment(String comment){
        this.comment = comment;
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


    @Override
    public List<Conflict> compare(Comparable o) {
        // Don't compare if not instance of same object
        if(!(o instanceof ExternalReference other))
            return null;

        ConflictFactory cf = new ConflictFactory();

        // Compare single String fields
        return cf.getConflicts();
    }
}
