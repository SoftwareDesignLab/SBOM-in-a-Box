package org.svip.sbomanalysis.comparison.conflicts;

import java.util.ArrayList;
import java.util.List;

/**
 * File: ConflictFactory.java
 * Handles creating new Conflicts
 *
 * @author Derek Garcia
 */
public class ConflictFactory {
    private final List<Conflict> conflicts = new ArrayList<>();

    /**
     * Attempt to add a new Conflict. If there is no conflict, nothing is added
     *
     * @param field Name of the Comparison Field
     * @param mismatchType Type of conflict
     * @param target Target value
     * @param other Other value
     */
    public void addConflict(String field, MismatchType mismatchType, String target, String other){
//        // Attempt to make a conflict
//        Conflict c = Conflict.buildConflict(field, conflictType, target, other);
//
//        // Add conflict if there is one
//        if(c != null)
//            this.conflicts.add(c);
    }

    /**
     * @return List of stored conflicts
     */
    public List<Conflict> getConflicts(){
        return this.conflicts;
    }

}
