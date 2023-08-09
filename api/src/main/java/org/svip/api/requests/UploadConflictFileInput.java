package org.svip.api.requests;

import org.svip.api.entities.diff.ComparisonFile;
import org.svip.api.entities.diff.ConflictFile;
import org.svip.compare.conflicts.Conflict;

/**
 * file: UploadConflictFileInput.java
 * Input request to create a new Conflict File
 *
 * @author Derek Garcia
 **/
public record UploadConflictFileInput(Conflict conflict) {

    /**
     * Create a new Conflict File Object
     *
     * @param cf Comparison File the conflict belongs to
     * @return ComparisonFile
     */
    public ConflictFile toConflictFile(ComparisonFile cf) {
        ConflictFile conflictFile = new ConflictFile();

        // Set content
        conflictFile.setMessage(conflict.getMessage())
                    .setMismatchType(conflict.getType())
                    .setTargetValue(conflict.getTarget())
                    .setOtherValue(conflict.getOther())
                    .setComparison(cf);     // set parent relationship

        // set relationship to comparison
        cf.addConflictFile(conflictFile);

        return conflictFile;
    }
}
