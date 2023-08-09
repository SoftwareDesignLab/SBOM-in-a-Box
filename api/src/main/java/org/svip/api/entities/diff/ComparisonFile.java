package org.svip.api.entities.diff;

import jakarta.persistence.*;
import org.svip.api.entities.SBOM;

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

    @ManyToOne
    @JoinColumn(name="sbom_id", nullable=false)
    private SBOM sbom;

    ///
    /// Setters
    ///

    /**
     * Set Target SBOM
     * @param sbom target sbom
     * @return Comparison file
     */
    public ComparisonFile setSBOM(SBOM sbom){
        this.sbom = sbom;
        return this;
    }
}
