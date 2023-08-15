package org.svip.compare;



import com.fasterxml.jackson.annotation.JsonProperty;
import org.svip.compare.conflicts.Conflict;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File: Comparison.java
 * Collection of conflicts between 2 SBOM objects
 *
 * @author Derek Garcia
 **/
public class Comparison {
    private static final String METADATA_TAG = "metadata";

    @JsonProperty
    private final Map<String, List<Conflict>> componentConflicts = new HashMap<>();

    // todo replace with the "from" arrays
    @JsonProperty
    private final List<String> missingComponents = new ArrayList<>();

    private final List<String> missingFromTarget = new ArrayList<>();
    private final List<String> missingFromOther = new ArrayList<>();


    /**
     * Compare 2 SBOM objects
     *
     * @param target Target SBOM
     * @param other Other SBOM
     */
    public Comparison(SBOM target, SBOM other) {
        // Compare metadata
        this.componentConflicts.put(METADATA_TAG, target.compare(other));

        // Round 1: Compare target against other if equal
        for (Component targetComponent : target.getComponents()) {

            // If other doesn't have component which shares name with target component, skip
            if (other.getComponents().stream().noneMatch(o -> o.getName() != null && o.getName().equals(targetComponent.getName()))) {
                this.missingFromOther.add(targetComponent.getName());
                continue;
            }

            // Test targetValue against otherValue
            for (Component otherComponent : other.getComponents()) {
                // Compare hash codes to account for different schema representations of the same component
                if (targetComponent.hashCode() == otherComponent.hashCode())
                    this.componentConflicts.put(targetComponent.getName(), targetComponent.compare(otherComponent));
            }

        }

        // Round 2: Check for components present in other but not in target
        for (Component otherComponent : other.getComponents()) {
            // If target doesn't have component which shares name with other component, skip
            if (target.getComponents().stream().noneMatch(o -> o.getName() != null && o.getName().equals(otherComponent.getName())))
                this.missingFromTarget.add(otherComponent.getName());
        }

        // Add all missing mastering missing
        this.missingComponents.addAll(this.missingFromTarget);
        this.missingComponents.addAll(this.missingFromOther);
    }

    ///
    /// Getters
    ///

    /**
     * @return List of component conflicts
     */
    public Map<String, List<Conflict>> getComponentConflicts() {
        return this.componentConflicts;
    }

    public List<String> getMissingFromTarget() {
        return this.missingFromTarget;
    }

    public List<String> getMissingFromOther() {
        return this.missingFromOther;
    }

}