package org.svip.sbomanalysis.comparison.conflicts;

import org.svip.sbomfactory.generators.utils.Debug;

/**
 * file: MissingConflict.java
 * Class for missing conflict
 *
 * @author Thomas Roman
 */
public class MissingConflict extends Conflict {
    public MissingConflict(String field, String targetValue, String otherValue) {
        super(field, targetValue, otherValue);
        this.type = ConflictType.MISSING;
        this.message = field + " is missing";
    }
}
