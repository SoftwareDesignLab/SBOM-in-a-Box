package org.svip.api.entities.diff;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.svip.compare.conflicts.Conflict;
import org.svip.compare.conflicts.MismatchType;

/**
 * File: ConflictFile.java
 * Conflicts to be stored in the database
 *
 * @author Derek Garcia
 **/
@Entity
@Table(name = "conflict")
public class ConflictFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column
    private String name;

    @Column
    @JsonProperty("message")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "mismatch_type")
    @JsonProperty("type")
    private MismatchType mismatchType;

    @Column(name = "target_value")
    @JsonProperty("target")
    private String targetValue;

    @Column(name = "other_value")
    @JsonProperty("other")
    private String otherValue;

    ///
    /// Relationships
    ///

    // source comparison
    @ManyToOne
    @JoinColumn(name = "comparison_id", nullable = false)
    private ComparisonFile comparison;

    
    ///
    /// Setters
    ///

    /**
     * Set name that the conflict occurred in
     *
     * @param name name
     * @return ConflictFile
     */
    public ConflictFile setName(String name){
        this.name = name;
        return this;
    }

    /**
     * Set the sbom / component conflict message
     * @param message message
     * @return ConflictFile
     */
    public ConflictFile setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Set mismatchType
     * @param mismatchType type of mismatch
     * @return Conflict File
     */
    public ConflictFile setMismatchType(MismatchType mismatchType) {
        this.mismatchType = mismatchType;
        return this;
    }

    /**
     * Set the value stored in the target
     * @param targetValue value in target
     * @return Conflict File
     */
    public ConflictFile setTargetValue(String targetValue) {
        this.targetValue = targetValue;
        return this;
    }


    /**
     * Set the value stored in the other
     * @param otherValue value in other
     * @return Conflict File
     */
    public ConflictFile setOtherValue(String otherValue) {
        this.otherValue = otherValue;
        return this;
    }

    /**
     * Set the parent comparison file
     *
     * @param cf parent comparison file
     * @return Conflict File
     */
    public ConflictFile setComparison(ComparisonFile cf) {
        this.comparison = cf;
        return this;
    }

    ///
    /// Getters
    ///

    /**
     * @return Name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return Mismatch type
     */
    public MismatchType getMismatchType(){
        return this.mismatchType;
    }
}
