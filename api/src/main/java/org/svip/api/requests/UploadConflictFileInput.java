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

        // set relationship


        return conflictFile;
    }
}
