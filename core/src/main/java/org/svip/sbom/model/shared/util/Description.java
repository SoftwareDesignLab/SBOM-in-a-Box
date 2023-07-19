package org.svip.sbom.model.shared.util;

import org.checkerframework.checker.units.qual.C;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbomanalysis.comparison.conflicts.Comparable;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;
import org.svip.sbomanalysis.comparison.conflicts.ConflictFactory;
import org.svip.sbomanalysis.comparison.conflicts.MismatchType;

import java.util.List;

import static org.svip.sbomanalysis.comparison.conflicts.MismatchType.MISC_MISMATCH;

/**
 * File: Description.java
 * Utility object to hold description information
 *
 * @author Derek Garcia
 */
public class Description implements Comparable {
    private final String summary;
    private String description;

    /**
     * Create a new description with a brief
     * @param summary short summary
     */
    public Description(String summary){
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
        if(!(o instanceof Description other))
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
        if(!this.summary.equals(other.getSummary()))
            return false;

        // compare descriptions
        return this.description.equals(other.getDescription());
    }
}
