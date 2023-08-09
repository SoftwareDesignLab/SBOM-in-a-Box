package org.svip.api.entities.diff;

import jakarta.persistence.*;
import org.svip.api.entities.SBOM;
import org.svip.compare.Comparison;

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

}
