/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

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
