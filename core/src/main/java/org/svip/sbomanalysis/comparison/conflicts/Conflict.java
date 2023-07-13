package org.svip.sbomanalysis.comparison.conflicts;
/**
 * file: Conflict.java
 * Generic class for Conflicts
 *
 * @author Thomas Roman
 */
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
