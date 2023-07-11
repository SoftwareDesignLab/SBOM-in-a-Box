package org.svip.sbomanalysis.comparison.merger;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.shared.Relationship;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MergerCDX extends Merger {

    public MergerCDX(CDX14SBOM A, CDX14SBOM B) {
        super(A, B);
    }

    @Override
    protected SBOM mergeSBOM(SBOM A, SBOM B) {

        Set<CDX14ComponentObject> componentsA = Collections.singleton((CDX14ComponentObject) A.getComponents());
        Set<CDX14ComponentObject> componentsB = Collections.singleton((CDX14ComponentObject) A.getComponents());

        CDX14SBOM mainSBOM = (CDX14SBOM) A;



    }

    @Override
    protected Set<Component> mergeComponents(Set<Component> A, Set<Component> B) {
        return null;
    }

    @Override
    protected Component mergeComponent(Component A, Component B) {
        return null;
    }
}
