/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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
 */

package org.svip.api.entities.diff;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column()
    @JsonIgnore
    private String name;

    @Column(nullable = false)
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
