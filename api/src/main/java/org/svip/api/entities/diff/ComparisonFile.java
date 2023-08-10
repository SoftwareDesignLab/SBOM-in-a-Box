package org.svip.api.entities.diff;

import jakarta.persistence.*;
import org.svip.api.entities.SBOM;
import org.svip.compare.Comparison;

import java.util.HashSet;
import java.util.Set;


/**
 * File: ComparisonFile.java
 * Comparison Table for the database
 *
 * @author Derek Garcia
 **/
@Entity
@Table(name = "comparison")
public class ComparisonFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    ///
    /// Relationships
    ///

    // Target SBOM
    @ManyToOne
    @JoinColumn(name = "target_sbom", nullable = false)
    private SBOM targetSBOM;

    // Other SBOM
    @ManyToOne
    @JoinColumn(name = "other_sbom", nullable = false)
    private SBOM otherSBOM;

    // Conflict collection
//    @OneToMany(mappedBy = "comparison", orphanRemoval = true, cascade = CascadeType.ALL)
    @OneToMany(mappedBy = "comparison")
    private Set<ConflictFile> conflicts = new HashSet<>();


    public Comparison toComparison(){
        // todo use CF fields to make new comparison object
        return null;
    }

    ///
    /// Setters
    ///

    /**
     * Set Target SBOM
     * @param targetSBOM target sbom
     * @return Comparison file
     */
    public ComparisonFile setTargetSBOM(SBOM targetSBOM){
        this.targetSBOM = targetSBOM;
        return this;
    }

    /**
     * Set other SBOM
     * @param otherSBOM target sbom
     * @return Comparison file
     */
    public ComparisonFile setOtherSBOM(SBOM otherSBOM){
        this.otherSBOM = otherSBOM;
        return this;
    }

    /**
     * Add a new conflict file
     *
     * @param conflictFile file with conflict details
     * @return Comparison file
     */
    public ComparisonFile addConflictFile(ConflictFile conflictFile){
        this.conflicts.add(conflictFile);
        return this;
    }

    ///
    /// Getters
    ///

    /**
     * @return ID
     */
    public Long getID(){
        return this.id;
    }

    /**
     * @return Target SBOM
     */
    public SBOM getTargetSBOM(){
        return this.targetSBOM;
    }

    /**
     * @return Other SBOM
     */
    public SBOM getOtherSBOM(){
        return this.otherSBOM;
    }

    /**
     * @return Conflicts
     */
    public Set<ConflictFile> getConflicts() {
        return this.conflicts;
    }
}
