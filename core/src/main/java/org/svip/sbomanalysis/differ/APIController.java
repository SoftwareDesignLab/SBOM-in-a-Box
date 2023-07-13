package org.svip.sbomanalysis.differ;

import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbomanalysis.comparison.Comparison;
/**
 * file: APIController.java
 * APIController for the diff report
 *
 * @author Thomas Roman
 */
public class APIController {
    public DiffReport compare(int targetIndex, SBOM[] sboms) {
        DiffReport diffReport = new DiffReport(sboms[targetIndex].getUID());
        for (SBOM other : sboms) {
            Comparison comparison = new Comparison(sboms[targetIndex], other);
            diffReport.addComparison(comparison);
        }
        return diffReport;
    }
}
