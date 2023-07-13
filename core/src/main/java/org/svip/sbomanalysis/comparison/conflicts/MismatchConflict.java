package org.svip.sbomanalysis.comparison.conflicts;
/**
 * file: MismatchConflict.java
 * Class for mismatch conflicts
 *
 * @author Thomas Roman
 */
public class MismatchConflict extends Conflict {
    public MismatchConflict(String field, String targetValue, String otherValue, ConflictType type) {
        super(field, targetValue, otherValue);
        this.type = ConflictType.MISSING;
        this.message = field + " doesn't match";
    }
}
