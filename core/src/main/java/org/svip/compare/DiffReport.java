package org.svip.compare;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.svip.compare.conflicts.Conflict;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class to hold results of a diff comparison between two SBOMs
 *
 * @author Matt London
 * @author Derek Garcia
 * @author Thomas Roman
 */
@JsonPropertyOrder({"target", "diffreport"})
public class DiffReport {


    @JsonProperty("target")
    private final String targetUID;
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


    public void addComparison(String otherUID, Comparison comparison){
        this.diffReport.put(otherUID, comparison);
    }

}
