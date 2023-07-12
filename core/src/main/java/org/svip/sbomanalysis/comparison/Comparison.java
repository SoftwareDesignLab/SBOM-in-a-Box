package org.svip.sbomanalysis.comparison;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;

import java.util.List;
import java.util.Map;

public class Comparison {
    private Map<String, List<Conflict>> componentConflicts;
    private List<String> missingComponents;

    public Comparison(SBOM targetSBOM, SBOM otherSBOM) {

    }

    private void CompareMetadata(SBOM targetSBOM, SBOM otherSBOM) {

    }

    private void CompareComponent(Component targetComponent, Component otherComponent) {

    }

    // TODO compareOtherReleventField()
}
