package org.svip.sbomanalysis.comparison.merger;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;

import java.util.Set;

public class MergerCrossSchema extends Merger{
    public MergerCrossSchema(){}

    /**
     * @param A
     * @param B
     * @return
     */
    @Override
    public SBOM mergeSBOM(SBOM A, SBOM B) {
        return null;
    }

    /**
     * @param A
     * @param B
     * @return
     */
    @Override
    protected Set<Component> mergeComponents(Set<Component> A, Set<Component> B) {
        return null;
    }

    /**
     * @param A
     * @param B
     * @return
     */
    @Override
    protected Component mergeComponent(Component A, Component B) {
        return null;
    }
}
