package org.svip.api.requests.diff;

import org.svip.api.entities.SBOMFile;
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
     * @param targetSBOMFile Target SBOM file for comparison
     * @param otherSBOMFile Other SBOM file for comparison
     * @return ComparisonFile
     */
    public ComparisonFile toComparisonFile(SBOMFile targetSBOMFile, SBOMFile otherSBOMFile) {
        ComparisonFile cf = new ComparisonFile();

        // add all conflicts
        for(String key : this.comparison.getComponentConflicts().keySet()){
            for(Conflict c : this.comparison.getComponentConflicts().get(key)){
                // Convert to ConflictFile
                cf.addConflictFile(new UploadConflictFileInput(key).toConflictFile(cf, c));
            }
        }

        // Add missing conflicts
        for(String component : this.comparison().getMissingFromTarget()){
            /* TODO HOTFIX
                Handles edge case where an SBOM object has only null values, guess is regex failure and not enough
                info to build in the deserialization stage. UPLOAD SHOULD FAIL IF NAME IS NULL, this just prevents
                that from happening
            */
            if(component == null)
                continue;
            // end hotfix
            cf.addConflictFile(new UploadConflictFileInput(component).toMissingConflictFile(cf, false));
        }


        for(String component : this.comparison().getMissingFromOther()){
            /* TODO HOTFIX
                Handles edge case where an SBOM object has only null values, guess is regex failure and not enough
                info to build in the deserialization stage. UPLOAD SHOULD FAIL IF NAME IS NULL, this just prevents
                that from happening
            */
            if(component == null)
                continue;
            // end hotfix
            cf.addConflictFile(new UploadConflictFileInput(component).toMissingConflictFile(cf, true));
        }


        // set parent relationships
        cf.setTargetSBOMFile(targetSBOMFile)
           .setOtherSBOMFile(otherSBOMFile);

        // Add SBOM Relationships
        targetSBOMFile.addComparisonFileAsTarget(cf);
        otherSBOMFile.addComparisonFileAsOther(cf);

        return cf;
    }
}
