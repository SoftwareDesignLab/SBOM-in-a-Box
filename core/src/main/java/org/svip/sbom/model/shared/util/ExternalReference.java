package org.svip.sbom.model.shared.util;

import org.svip.compare.conflicts.Comparable;
import org.svip.compare.conflicts.Conflict;
import org.svip.compare.conflicts.ConflictFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.svip.compare.conflicts.MismatchType.MISC_MISMATCH;

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
     * @return comment
     */
    public String getComment(){
        return this.comment;
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
        cf.addConflict("URL", MISC_MISMATCH, this.url, other.getUrl());
        cf.addConflict("Type", MISC_MISMATCH, this.type, other.getType());
        cf.addConflict("Category", MISC_MISMATCH, this.category, other.getCategory());
        cf.addConflict("Comment", MISC_MISMATCH, this.comment, other.getComment());

        // Compare Hashes
        cf.compareHashes("External Reference Hash", this.hashes, other.getHashes());

        return cf.getConflicts();
    }

    @Override
    public boolean equals(Object o) {
        // Test if correct class
        if (o == null || getClass() != o.getClass()) return false;
        ExternalReference other = (ExternalReference) o;

        // If urls don't match then not same
        if((this.url != null && other.getUrl() != null && !this.url.equals(other.getUrl())))
            return false;

        // Check if category is equivalent
        if(!this.category.equals(other.getCategory()))
            return false;

        // Check if type is equivalent
        if(!this.type.equals(other.getType()))
            return false;

        // Compare hashes
        for(String alg : this.hashes.keySet()){
            // Missing hash, missing doesn't imply different
            if(!other.getHashes().containsKey(alg))
                continue;
            // Same hash alg, different values
            if(!other.getHashes().get(alg).equals(this.hashes.get(alg)))
                return false;
        }

        // Don't compare free text comment, pass all tests
        return true;
    }
}
