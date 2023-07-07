package org.svip.sbomanalysis.comparison;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;

import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.uids.CPE;

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
    public SBOM merge(Collection<SVIPSBOM> SBOMs) {
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
        Iterator<SVIPSBOM> it = SBOMs.iterator();
        SVIPSBOM a = it.next();
        SVIPSBOM b = it.next();

        // Merge it into a main SBOM
        SVIPSBOM mainBom = merge(a, b);

        // Take the remaining SBOMs and merge them into the main SBOM
        while (it.hasNext()) {
            SVIPSBOM nextBom = it.next();
            mainBom = merge(mainBom, nextBom);
        }

        // Return the main bom
        return mainBom;
    }

    public SVIPSBOM merge(SVIPSBOM A, SVIPSBOM B) {

        Set<SVIPComponentObject> componentsA = A.getSVIPComponents();

        Set<SVIPComponentObject> componentsB = B.getSVIPComponents();

        Set<Component> merged_components = mergeComponents(componentsA, componentsB);

        return new SVIPSBOM(A.getFormat(), A.getName(), A.getUID(), A.getVersion(), A.getSpecVersion(),
                A.getLicenses(), A.getCreationData(), A.getDocumentComment(), A.getRootComponent(),
                merged_components, (HashMap<String, Set<Relationship>>) A.getRelationships(),
                A.getExternalReferences(), A.getSPDXLicenseListVersion());
    }

    public Set<Component> mergeComponents(Set<SVIPComponentObject> components_A, Set<SVIPComponentObject> components_B) {
        Set<Component> merged_components = new HashSet<>();

        for(SVIPComponentObject current_A : components_A) {
            for(SVIPComponentObject current_B : components_B) {
                if(current_A.getName().equals(current_B.getName()) && current_A.getVersion().equals(current_B.getVersion())) {
                    unifyComponent(current_A, current_B);
                }
            }
        }

        return merged_components;
    }

    public Component unifyComponent(SVIPComponentObject compA, SVIPComponentObject compB) {
        Set<String> new_cpe = compA.getCPEs();
        new_cpe.addAll(compB.getCPEs());

        Set<String> new_purl = compA.getPURLs();
        new_purl.addAll(compB.getPURLs());

        Map<String, String> new_hashes = compA.getHashes();
        new_hashes.putAll(compB.getHashes());

        Set<ExternalReference> new_exRef = compA.getExternalReferences();
        new_exRef.addAll(compB.getExternalReferences());

        HashMap<String, Set<String>> new_properties = compA.getProperties();
        for(String property : compB.getProperties().keySet()) {
            if(new_properties.containsKey(property)) {

            }
        }


        SVIPComponentObject new_component = null;

        return new_component;
    }


    public HashMap<String, HashSet<String>> mergeRelationships(
            Map<String, Set<Relationship>> relationshipsA,
            Map<String, Set<Relationship>> relationshipsB
    ) {
        HashMap<String, HashSet<String>> merged_relationships = new HashMap<>();
        return merged_relationships;
    }
}
