package org.svip.sbomanalysis.old.differ;

import org.svip.sbomanalysis.old.comparison.conflicts.ComponentConflict;
import org.svip.sbomanalysis.old.comparison.conflicts.SBOMConflict;

import java.util.Set;

/**
 * Class to hold results of a diff comparison between two SBOMs
 *
 * @author Matt London
 */
public class DiffReport {
    /** Stores conflicts in non-trivial information within these sboms */
    private SBOMConflict sbomConflict;
    /** Stores conflicts between components in two SBOMs */
    private Set<ComponentConflict> componentConflicts;

    /**
     * Construct the report from an sbom conflict and a set of component conflicts
     * @param sbomConflict the sbom conflict
     * @param componentConflicts the set of component conflicts
     */
    public DiffReport(SBOMConflict sbomConflict, Set<ComponentConflict> componentConflicts) {
        this.sbomConflict = sbomConflict;
        this.componentConflicts = componentConflicts;
    }

    ///
    /// Getters
    ///

    public SBOMConflict getSbomConflict() {
        return sbomConflict;
    }

    public Set<ComponentConflict> getComponentConflicts() {
        return componentConflicts;
    }

    ///
    /// Overrides
    ///

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(sbomConflict.toString());
        sb.append("Component Conflicts:\n");
        for (ComponentConflict conflict : componentConflicts) {
            sb.append(conflict.toString());
        }

        return sb.toString();
    }
}
