package org.svip.sbom.model.util;

public class Description {
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
}
