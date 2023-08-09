package org.svip.api.entities.diff;

import jakarta.persistence.*;
import org.svip.api.entities.SBOM;
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

    @Column(nullable = false)
    private String field;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "mismatch_type")
    private MismatchType mismatchType;

    @Column(nullable = false, name = "target_value")
    private String targetValue;

    @Column(nullable = false, name = "other_value")
    private String otherValue;

    ///
    /// Relationships
    ///

    // source comparison
    @ManyToOne
    @JoinColumn(name = "comparison", nullable = false)
    private ComparisonFile comparison;
    
    
    ///
    /// Setters
    ///

    /**
     * Set the sbom / component field name
     * @param field name of field
     * @return ConflictFile
     */
    public ConflictFile setField(String field) {
        this.field = field;
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
}
