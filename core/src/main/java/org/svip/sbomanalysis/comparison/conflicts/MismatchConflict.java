package org.svip.sbomanalysis.comparison.conflicts;

public class MismatchConflict extends Conflict {
    MismatchConflict(String field, String targetValue, String otherValue, ConflictType type) {
        super(field, targetValue, otherValue);
        this.type = ConflictType.MISSING;
        this.message = field + " doesn't match";
    }
}
