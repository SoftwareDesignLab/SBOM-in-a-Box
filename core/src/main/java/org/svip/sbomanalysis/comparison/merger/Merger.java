package org.svip.sbomanalysis.comparison.merger;

import org.checkerframework.checker.units.qual.C;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Merger {

    SBOM A;

    SBOM B;

    public Merger(SBOM A, SBOM B) {
        this.A = A;
        this.B = B;
    }

    protected abstract SBOM mergeSBOM();

    protected abstract Set<Component> mergeComponents(Set<Component> A, Set<Component> B);

    protected abstract Component mergeComponent(Component A, Component B);

}
