package org.svip.api.requests;

import org.svip.api.entities.SBOM;
import org.svip.api.entities.diff.ComparisonFile;
import org.svip.compare.Comparison;
import org.svip.compare.conflicts.Conflict;

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
    public ComparisonFile toComparisonFile(SBOM targetSBOM, SBOM otherSBOM) {
        ComparisonFile cf = new ComparisonFile();

        // set parent relationships
        cf.setTargetSBOM(targetSBOM)
           .setOtherSBOM(otherSBOM);

        // Add SBOM Relationships
        targetSBOM.addComparisonFileAsTarget(cf);
        otherSBOM.addComparisonFileAsOther(cf);


        return cf;
    }
}
