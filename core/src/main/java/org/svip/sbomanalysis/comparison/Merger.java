package org.svip.sbomanalysis.comparison;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;

import org.svip.sbom.model.shared.Relationship;

import java.util.*;

/**
 * Merges two SBOMs of any type together into one SVIP type SBOM
 *
 * @author tyler_drake
 * @author Matt London
 */

public class Merger {
    /**
     * Merge a collection of SBOMs into one main SBOM
     *
     * @param SBOMs Collection of SBOM objects to merge together
     * @return Resulting merged bom
     */
    public SBOM merge(Collection<SBOM> SBOMs) {
        // Loop through and merge into a master SBOM
        if (SBOMs.size() == 0) {
            return null;
        } else if (SBOMs.size() == 1) {
            // Return the first element
            for (SBOM sbom : SBOMs) {
                return sbom;
            }
        }

        // Now we know there is at least two SBOMs
        Iterator<SBOM> it = SBOMs.iterator();
        SBOM a = it.next();
        SBOM b = it.next();

        // Merge it into a main SBOM
        SBOM mainBom = merge(a, b);

        // Take the remaining SBOMs and merge them into the main SBOM
        while (it.hasNext()) {
            SBOM nextBom = it.next();
            mainBom = merge(mainBom, nextBom);
        }

        // Return the main bom
        return mainBom;
    }

    public SBOM merge(SBOM A, SBOM B) {

        Set<Component> componentsA = A.getComponents();

        Set<Component> componentsB = B.getComponents();

        Set<Component> merged_components = mergeComponents(componentsA, componentsB);

        HashMap<String, HashSet<String>> merged_relationships = mergeRelationships(A.getRelationships(), B.getRelationships());

        return new SVIPSBOM(A.getFormat(), A.getName(), A.getUID(), A.getVersion(), A.getSpecVersion(),
                A.getLicenses(), A.getCreationData(), A.getDocumentComment(), (SVIPComponentObject) A.getRootComponent(),
                merged_components, (HashMap<String, Set<Relationship>>) A.getRelationships(),
                A.getExternalReferences(), "Unavailable.");
    }

    public Set<Component> mergeComponents(Set<Component> compA, Set<Component> compB) {
        Set<Component> merged_components = new HashSet<>();

        for(Component current_A : compA) {
            for(Component current_B : compB) {
                // //new ComponentConflict(current_A, current_B);
            }
        }

        return merged_components;
    }

    public HashMap<String, HashSet<String>> mergeRelationships(
            Map<String, Set<Relationship>> relationshipsA,
            Map<String, Set<Relationship>> relationshipsB
    ) {
        HashMap<String, HashSet<String>> merged_relationships = new HashMap<>();
        return merged_relationships;
    }
}
