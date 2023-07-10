package org.svip.sbomanalysis.comparison.merger;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.shared.Relationship;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MergerCDX extends Merger {

    public MergerCDX(CDX14SBOM A, CDX14SBOM B) {
        super(A, B);
    }

    protected SBOM mergeSBOM(CDX14SBOM A, CDX14SBOM B) {

        Set<Component> componentsA = A.getComponents();

        Set<Component> componentsB = B.getComponents();

        Set<Component> merged_components = mergeComponents(componentsA, componentsB);

        return new CDX14SBOM(
                A.getFormat(), A.getName(), A.getUID(), A.getVersion(), A.getSpecVersion(),
                A.getLicenses(), A.getCreationData(), A.getDocumentComment(), A.getRootComponent(),
                merged_components, (HashMap<String, Set<Relationship>>) A.getRelationships(), A.getExternalReferences()
        );
    }

    @Override
    protected SBOM mergeSBOM(SBOM A, SBOM B) {
        return null;
    }
}
