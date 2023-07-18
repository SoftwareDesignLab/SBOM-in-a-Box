package org.svip.sbomanalysis.comparison.conflicts;

import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.svip.sbomanalysis.comparison.conflicts.MismatchType.LICENSE_MISMATCH;
import static org.svip.sbomanalysis.comparison.conflicts.MismatchType.MISSING;

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
     * Compare Sets of Comparable Objects
     *
     * @param field Name of the Comparison Field
     * @param target Set of target values
     * @param other Set of other values
     */
    public void compareComparableSets(String field, Set<Comparable> target, Set<Comparable> other){

        // Null check
        if(!comparable(field, target, other))
            return;

        // Round 1: Compare target against other if equal
        for(Comparable targetValue : target){
            boolean compared = false;   // track if comparison occurred

            // Test targetValue against otherValue
            for(Comparable otherValue : other){

                // If equal, compare
                if(targetValue.equals(otherValue)){
                    addConflicts(targetValue.compare(otherValue));
                    compared = true;
                }
            }
            // targetValue not in other set
            if(!compared)
                addConflict(field, MISSING, "Contains " + field + " Data", null);
        }

        // Round 2: Don't compare other against target, just checking if present
        for(Comparable otherValue : other){
            boolean compared = false;   // track if comparison occurred

            // Attempt to see if otherValue exists in target
            for(Comparable targetValue : target){
                // otherValue is in targetValue
                if (otherValue.equals(targetValue)) {
                    compared = true;
                    break;
                }
            }

            // otherValue not in target set
            if(!compared)
                addConflict(field, MISSING, null, "Contains " + field + " Data");
        }
    }

    /**
     * Create conflicts for Sets of Strings
     *
     * @param field Name of the Comparison Field
     * @param mismatchType Type of conflict
     * @param target Set of target values
     * @param other Set of other values
     */
    public void compareStringSets(String field, MismatchType mismatchType, Set<String> target, Set<String> other){
        // Null check
        if(!comparable(field, target, other))
            return;

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
     * Utility method to test to see if 2 objects can be compared
     *
     * @param field Name of the Comparison Field
     * @param target Target value
     * @param other Other value
     * @return True if they can be compared, false otherwise
     */
    private boolean comparable(String field, Object target, Object other){

        // Both are missing, no conflict
        if(target == null && other == null)
            return false;

        // One is missing from the other
        // TODO Better way to handle this case
        if(target == null || other == null){
            addConflict(field, MISSING, (target == null ? "Present" : null), (other == null ? "Present" : null));
            return false;
        }

        // False: Skip, not comparable classes
        // True: Objects are of the same class to be compared
        return target.getClass().equals(other.getClass());

    }

    /**
     * @return List of stored conflicts
     */
    public List<Conflict> getConflicts(){
        return this.conflicts;
    }

}
