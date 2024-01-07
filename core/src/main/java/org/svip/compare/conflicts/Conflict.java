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

package org.svip.compare.conflicts;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * file: Conflict.java
 * Representation for conflicts
 *
 * @author Thomas Roman
 * @author Derek Garcia
 */
public class Conflict {

    @JsonProperty
    private final MismatchType type;

    @JsonProperty
    private final String message;

    @JsonProperty
    private final String target;

    @JsonProperty
    private final String other;

    /**
     * Mismatch Conflict Constructor
     *
     * @param field        Name of the conflict field
     * @param mismatchType Type of mismatch
     * @param target       Target Value
     * @param other        Other Value
     */
    private Conflict(String field, MismatchType mismatchType, String target, String other) {
        this.type = mismatchType;
        this.message = getMismatchMessage(field);
        this.target = target;
        this.other = other;
    }

    /**
     * Missing Conflict Constructor
     *
     * @param field  Name of the conflict field
     * @param target Target Value
     * @param other  Other Value
     */
    private Conflict(String field, String target, String other) {
        this.type = MismatchType.MISSING;
        this.message = getMissingMessage(field);
        this.target = target;
        this.other = other;
    }

    ///
    /// Helper Methods
    ///

    /**
     * Get the present Missing message
     *
     * @param field Name of the field that is missing
     * @return Missing message
     */
    private String getMissingMessage(String field) {
        return field + " is missing";
    }

    /**
     * Get the preset Mismatch message
     *
     * @param field Name of the field that is mismatched
     * @return Mismatch message
     */
    private String getMismatchMessage(String field) {
        return field + " doesn't match";
    }

    /**
     * Creates a new conflict if one exists, otherwise returns null
     *
     * @param field        Name of the conflict field
     * @param mismatchType Type of mismatch
     * @param target       Target Value
     * @param other        Other Value
     * @return New conflict object
     */
    public static Conflict buildConflict(String field, MismatchType mismatchType, String target, String other) {

        // Both are missing, no conflict
        if (target == null && other == null)
            return null;

        // Target is missing
        if (target == null || target.isEmpty())
            return new Conflict(field, target, other);

        // Other is missing
        if (other == null || other.isEmpty())
            return new Conflict(field, target, other);

        // Mismatch
        if (!target.equals(other))
            return new Conflict(field, mismatchType, target, other);

        // Default to no conflict
        return null;
    }

    ///
    /// Getters
    ///

    public MismatchType getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    public String getTarget() {
        return this.target;
    }

    public String getOther() {
        return this.other;
    }


}
