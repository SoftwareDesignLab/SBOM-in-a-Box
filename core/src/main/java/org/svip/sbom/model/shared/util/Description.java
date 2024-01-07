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

package org.svip.sbom.model.shared.util;

import org.svip.compare.conflicts.Comparable;
import org.svip.compare.conflicts.Conflict;
import org.svip.compare.conflicts.ConflictFactory;

import java.util.List;

import static org.svip.compare.conflicts.MismatchType.MISC_MISMATCH;

/**
 * File: Description.java
 * Utility object to hold description information
 *
 * @author Derek Garcia
 * @author Thomas Roman
 */
public class Description implements Comparable {
    private final String summary;
    private String description;

    /**
     * Create a new description with a brief
     *
     * @param summary short summary
     */
    public Description(String summary) {
        this.summary = summary;
    }

    /**
     * Set a more detailed description
     *
     * @param description detailed description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    ///
    /// Getters
    ///

    /**
     * @return Summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @return Detailed description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public List<Conflict> compare(Comparable o) {
        // Don't compare if not instance of same object
        if (!(o instanceof Description other))
            return null;

        ConflictFactory cf = new ConflictFactory();

        cf.addConflict("Summary", MISC_MISMATCH, this.summary, other.getSummary());
        cf.addConflict("Description", MISC_MISMATCH, this.description, other.getDescription());

        return cf.getConflicts();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Description other = (Description) o;

        // compare summary
        if (!this.summary.equals(other.getSummary()))
            return false;

        // compare descriptions
        return this.description.equals(other.getDescription());
    }

    public String toString() {
        return "Summary: " + this.summary + ", Description: " + this.description;
    }
}
