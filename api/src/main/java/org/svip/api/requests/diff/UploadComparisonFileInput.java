/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

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
