package org.svip.api.entities.diff;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.svip.api.entities.SBOMFile;

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
    @JoinColumn(name = "target_sbom_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SBOMFile targetSBOMFile;

    // Other SBOM
    @ManyToOne
    @JoinColumn(name = "other_sbom_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SBOMFile otherSBOMFile;

    // Conflict collection
//    @OneToMany(mappedBy = "comparison", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OneToMany(mappedBy = "comparison")
    private Set<ConflictFile> conflicts = new HashSet<>();


    ///
    /// Setters
    ///

    /**
     * Set Target SBOMFile
     * @param targetSBOMFile target sbom file
     * @return Comparison file
     */
    public ComparisonFile setTargetSBOMFile(SBOMFile targetSBOMFile){
        this.targetSBOMFile = targetSBOMFile;
        return this;
    }

    /**
     * Set other SBOMFile
     * @param otherSBOMFile target sbom file
     * @return Comparison file
     */
    public ComparisonFile setOtherSBOMFile(SBOMFile otherSBOMFile){
        this.otherSBOMFile = otherSBOMFile;
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
     * @return Conflicts
     */
    public Set<ConflictFile> getConflicts() {
        return this.conflicts;
    }
}
