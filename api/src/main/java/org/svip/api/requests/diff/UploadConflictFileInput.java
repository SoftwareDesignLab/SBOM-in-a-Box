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

import org.svip.api.entities.diff.ComparisonFile;
import org.svip.api.entities.diff.ConflictFile;
import org.svip.compare.conflicts.Conflict;
import org.svip.compare.conflicts.MismatchType;

/**
 * file: UploadConflictFileInput.java
 * Input request to create a new Conflict File
 *
 * @author Derek Garcia
 **/
public record UploadConflictFileInput(String name) {

    /**
     * Create a new Conflict File Object using a Conflict
     *
     * @param cf Comparison File the conflict belongs to
     * @param conflict Conflict to get details from
     * @return ComparisonFile
     */
    public ConflictFile toConflictFile(ComparisonFile cf, Conflict conflict) {
        ConflictFile conflictFile = new ConflictFile();

        // Set content
        conflictFile.setName(this.name)
                    .setMessage(conflict.getMessage())
                    .setMismatchType(conflict.getType())
                    .setTargetValue(conflict.getTarget())
                    .setOtherValue(conflict.getOther())
                    .setComparison(cf);     // set parent relationship

        // set relationship to comparison
        cf.addConflictFile(conflictFile);

        return conflictFile;
    }

    /**
     * Special Case to add a missing component
     *
     * @param cf Comparison File the conflict belongs to
     * @param inTarget is the missing component in the target
     * @return ComparisonFile
     */
    public ConflictFile toMissingConflictFile(ComparisonFile cf, boolean inTarget) {
        ConflictFile conflictFile = new ConflictFile();

        // Set content
        conflictFile.setName(this.name)
                .setMessage("Component is missing")
                .setMismatchType(MismatchType.MISSING_COMPONENT)
                .setComparison(cf);     // set parent relationship

        // Set the name in the value that it's present in
        if(inTarget){
            conflictFile.setTargetValue(this.name);
        } else {
            conflictFile.setOtherValue(this.name);
        }

        // set relationship to comparison
        cf.addConflictFile(conflictFile);

        return conflictFile;
    }
}
