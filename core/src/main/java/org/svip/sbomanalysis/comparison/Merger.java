package org.svip.sbomanalysis.comparison;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;

import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

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
        return new SVIPSBOM(A.getFormat(), A.getName(), A.getUID(), A.getVersion(), A.getSpecVersion(),
                A.getLicenses(), A.getCreationData(), A.getDocumentComment(), (SVIPComponentObject) A.getRootComponent(),
                A.getComponents(), (HashMap<String, Set<Relationship>>) A.getRelationships(),
                A.getExternalReferences(), "");
    }
}
