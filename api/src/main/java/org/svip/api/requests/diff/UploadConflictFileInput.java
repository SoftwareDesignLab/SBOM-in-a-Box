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
