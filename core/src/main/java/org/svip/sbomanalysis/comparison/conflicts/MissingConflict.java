package org.svip.sbomanalysis.comparison.conflicts;

public class MissingConflict extends Conflict {
    public MissingConflict(String field, String targetValue, String otherValue) {
        super(field, targetValue, otherValue);
        this.type = type;
        this.message = field + " is missing";
    }
}
