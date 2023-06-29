package org.svip.sbom.model;

/**
 * File: Relationship.java
 * Relationship between 2 components
 *
 * @author Derek Garcia
 */
public class Relationship {
    private final String otherUID;
    private final String relationshipType;
    private String comment;

    /**
     * Create new relationship
     *
     * @param otherUID UID of other component
     * @param relationshipType SDPX style relationship type
     */
    public Relationship(String otherUID, String relationshipType){
        this.otherUID = otherUID;
        this.relationshipType = relationshipType;
    }

    /**
     * Set an optional comment
     *
     * @param comment Comment
     */
    public void setComment(String comment){
        this.comment = comment;
    }

    ///
    /// Getters
    ///

    /**
     * @return ottherUID
     */
    public String getOtherUID() {
        return otherUID;
    }

    /**
     * @return relationshipType
     */
    public String getRelationshipType() {
        return relationshipType;
    }

    /**
     * @return comment
     */
    public String getComment() {
        return comment;
    }
}
