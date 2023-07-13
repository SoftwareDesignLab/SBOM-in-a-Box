package org.svip.sbomanalysis.comparison;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;

import java.util.*;

public class Comparison {
    private Map<String, List<Conflict>> componentConflicts = new HashMap<>();
    private List<String> missingComponents = new ArrayList<>();
    /**
     * file: Comparison.java
     * Compares SBOMs
     *
     * @author Thomas Roman
     */
    public Comparison(SBOM targetSBOM, SBOM otherSBOM) {
        // METADATA
        CompareMetadata(targetSBOM, otherSBOM);
        // COMPONENTS
        List<Component> targetComponents = targetSBOM.getComponents().stream().toList();
        List<Component> otherComponents = otherSBOM.getComponents().stream().toList();
        int count;
        for (int i = 0; i < targetComponents.size(); i++) {
            count = 0;
            for (int j = 0; j < otherComponents.size(); j++) {
                if (Objects.equals(targetComponents.get(i).getUID(), otherComponents.get(j).getUID())) {
                    CompareComponent(targetComponents.get(i), otherComponents.get(j));
                    count += 1;
                }
            }
            // component not found in otherComponents
            if (count == 0) {
                missingComponents.add(targetComponents.get(i).getUID());
            }
        }
    }

    private void CompareMetadata(SBOM targetSBOM, SBOM otherSBOM) {
        componentConflicts.put(targetSBOM.getUID(), targetSBOM.compare(otherSBOM));
    }

    private void CompareComponent(Component targetComponent, Component otherComponent) {
        componentConflicts.put(targetComponent.getUID(), targetComponent.compare(otherComponent));
    }

    // TODO compareOtherReleventField()
}
