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
 * @author Thomas Roman
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

                // If other doesn't have component which shares name with target component, skip
                if(other.getComponents().stream().noneMatch(o->o.getName() != null && o.getName().equals(targetComponent.getName()))){
                    this.missingComponents.add(targetComponent.getName());
                    continue;
                }

                // Test targetValue against otherValue
                for(Component otherComponent : other.getComponents()){
                    // Compare hash codes to account for different schema representations of the same component
                    if(targetComponent.hashCode() == otherComponent.hashCode())
                        this.componentConflicts.put(targetComponent.getName(), targetComponent.compare(otherComponent));
                }

            }

            // Round 2: Check for components present in other but not in target
            for(Component otherComponent : other.getComponents()){
                // If target doesn't have component which shares name with other component, skip
                if(target.getComponents().stream().noneMatch(o->o.getName() != null && o.getName().equals(otherComponent.getName())))
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
