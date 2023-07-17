package org.svip.sbomanalysis.comparison.conflicts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.svip.sbomanalysis.comparison.conflicts.MismatchType.LICENSE_MISMATCH;

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
        // Attempt to make a conflict
        Conflict c = Conflict.buildConflict(field, mismatchType, target, other);

        // Add conflict if there is one
        if(c != null)
            this.conflicts.add(c);
    }

    /**
     * Add collection of conflicts
     *
     * @param conflicts list of conflicts to add
     */
    public void addConflicts(List<Conflict> conflicts){
        this.conflicts.addAll(conflicts);
    }

    /**
     * Create conflicts for Sets of Strings
     *
     * @param field Name of the Comparison Field
     * @param mismatchType Type of conflict
     * @param target Set of target values
     * @param other Set of other values
     */
    public void compareSets(String field, MismatchType mismatchType, Set<String> target, Set<String> other){
        // Compare Strings
        for(String value : target){
            // Value in target and not in other
            if(!other.contains(value))
                addConflict(field, mismatchType, value, null);
        }
        for(String value : other){
            // Value in other and not in target
            if(!target.contains(value))
                addConflict(field, mismatchType, null, value);
        }
    }

    /**
     * @return List of stored conflicts
     */
    public List<Conflict> getConflicts(){
        return this.conflicts;
    }

}
