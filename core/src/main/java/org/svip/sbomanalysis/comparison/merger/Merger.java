package org.svip.sbomanalysis.comparison.merger;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public interface Merger {

    SBOM merge();

    Set<Component> mergeComponents();

    Component mergeComponent();

    HashMap<String, HashSet<String>> mergeRelationships();

}


