package org.svip.sbomanalysis.comparison.conflicts;

public abstract class Conflict {
    protected ConflictType type;
    protected String message;
    protected String target;
    protected String other;

    Conflict(String field, String targetValue, String otherValue) {
        target = targetValue;
        other = otherValue;
    }
}
