package org.svip.sbomanalysis.comparison;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.svip.sbom.model.old.SBOM;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;

import java.util.*;


/**
 * Class to hold results of a diff comparison between two SBOMs
 *
 * @author Matt London
 * @author Derek Garcia
 */
@JsonPropertyOrder({"target", "diffreport"})
public class DiffReport {


    /**
     * Utility class for organizing conflict data
     */
    private class Comparison {
        private static final String METADATA_TAG = "metadata";
        @JsonProperty
        private final Map<String, List<Conflict>> componentConflicts = new HashMap<>();
        @JsonProperty
        private final List<String> missingComponents = new ArrayList<>();

        public Comparison(SBOM target, SBOM other) {

        }

    }


    @JsonProperty("target")
    private String targetUID;
    @JsonProperty
    private final HashMap<String, Comparison> diffReport = new HashMap<>();
    private final SBOM targetSBOM;

    /**
     * Create a new DiffReport of a target SBOM
     *
     * @param targetUID  UID of target SBOM
     * @param targetSBOM Target SBOM to reference
     */
    public DiffReport(String targetUID, SBOM targetSBOM) {
        this.targetUID = targetUID;
        this.targetSBOM = targetSBOM;
    }


    /**
     * Generate a report of the differences between two SBOMs and store the results
     *
     * @param otherUID  UID of other SBOM
     * @param otherSBOM other SBOM to compare against
     */
    public void compare(String otherUID, SBOM otherSBOM) {
        this.diffReport.put(otherUID, new Comparison(this.targetSBOM, otherSBOM));
    }

}
