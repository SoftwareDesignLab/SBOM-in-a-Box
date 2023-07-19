package org.svip.sbomanalysis.comparison;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbomanalysis.comparison.conflicts.Comparable;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;

import java.util.*;

import static org.svip.sbomanalysis.comparison.conflicts.MismatchType.MISSING;


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
    private static class Comparison {
        private static final String METADATA_TAG = "metadata";
        @JsonProperty
        private final Map<String, List<Conflict>> componentConflicts = new HashMap<>();
        @JsonProperty
        private final List<String> missingComponents = new ArrayList<>();

        public Comparison(SBOM target, SBOM other) {
            // Compare metadata
            this.componentConflicts.put(METADATA_TAG, target.compare(other));

            // Round 1: Compare target against other if equal
            for(Component targetComponent : target.getComponents()){
                boolean compared = false;   // track if comparison occurred

                // Test targetValue against otherValue
                for(Component otherComponent : other.getComponents()){

                    // If equal, compare
                    if(targetComponent.equals(otherComponent)){
                        this.componentConflicts.put(targetComponent.getName(), targetComponent.compare(otherComponent));
                        compared = true;
                    }
                }
                // targetValue not in other set
                if(!compared)
                    this.missingComponents.add(targetComponent.getName());
            }

            // Round 2: Don't compare other against target, just checking if present
            for(Component otherComponent : other.getComponents()){
                boolean compared = false;   // track if comparison occurred

                // Attempt to see if otherValue exists in target
                for(Component targetComponent : target.getComponents()){
                    // otherValue is in targetValue
                    if (otherComponent.equals(targetComponent)) {
                        compared = true;
                        break;
                    }
                }

                // otherValue not in target set
                if(!compared)
                    this.missingComponents.add(otherComponent.getName());
            }
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
