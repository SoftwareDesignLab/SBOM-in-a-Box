package org.svip.sbom.model.shared.util;

import org.svip.sbomanalysis.comparison.conflicts.Comparable;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;

import java.util.List;

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
        // todo
        return null;
    }
}
