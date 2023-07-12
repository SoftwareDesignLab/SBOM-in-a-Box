package org.svip.sbomanalysis.differ;

import org.svip.sbomanalysis.comparison.Comparison;

import java.util.Map;

public class DiffReport {
    private String targetUID;
    private Map<String, Comparison> comparisons;

    DiffReport(String targetUID) {
        this.targetUID = targetUID;
    }
    public void addComparison(Comparison comparison) {
        this.comparisons.put(this.targetUID, comparison);
    }
}
