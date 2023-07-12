package org.svip.sbomanalysis.comparison.conflicts;

public class MismatchConflict extends Conflict {
    MismatchConflict(String field, String targetValue, String otherValue) {
        super(field, targetValue, otherValue);
    }
}
