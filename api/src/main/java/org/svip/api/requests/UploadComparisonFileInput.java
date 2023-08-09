package org.svip.api.requests;

import org.svip.api.entities.SBOM;
import org.svip.api.entities.diff.ComparisonFile;
import org.svip.compare.Comparison;

/**
 * file: UploadComparisonFileInput.java
 * Input request to create a new Comparison File
 *
 * @author Derek Garcia
 **/
public record UploadComparisonFileInput(Comparison comparison) {

    /**
     * Create a new Comparison File Object
     *
     * @param targetSBOM Target SBOM for comparison
     * @param otherSBOM otherSBOM for comparison
     * @return ComparisonFile
     */
    public ComparisonFile toQualityReportFile(SBOM targetSBOM, SBOM otherSBOM) {
        ComparisonFile qf = new ComparisonFile();
        // todo requests to convert missing and conflicts in the Comparison
        qf.setTargetSBOM(targetSBOM)
           .setOtherSBOM(otherSBOM);

        // Add SBOM Relationships
        targetSBOM.addComparisonFileAsTarget(qf);
        otherSBOM.addComparisonFileAsOther(qf);


        return qf;
    }
}
