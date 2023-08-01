package org.svip.sbomanalysis.comparison.conflicts;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Text;
import static org.svip.sbomanalysis.comparison.conflicts.MismatchType.HASH_MISMATCH;
import static org.svip.sbomanalysis.comparison.conflicts.MismatchType.MISSING;
import java.util.Set;

/**
 * File: ConflictFactory.java
 * Handles creating new Conflicts
 *
 * @author Derek Garcia
 * @author Thomas Roman
 */
public class ConflictFactory {
    private final List<Conflict> conflicts = new ArrayList<>();
    // for writing objects as strings
    private final ObjectMapper objectMapper = new ObjectMapper();

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
     *              TODO Doesn't seem to check sets properly. See CDX14ComponentObejctTest.java
     */
    public void compareComparableSets(String field, Set<Comparable> target, Set<Comparable> other) throws JsonProcessingException {

        // Null check
        if(!comparable(field, target, other))
            return;

        // Construct Text to use for diff report conflict messages
        Text text = new Text("Conflict", field);

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
                addConflict(field, MISSING, objectMapper.writeValueAsString(targetValue), text.getNullItemInSetResponse());
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
                addConflict(field, MISSING, text.getNullItemInSetResponse(), objectMapper.writeValueAsString(otherValue));
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
    public void compareStringSets(String field, MismatchType mismatchType, Set<String> target, Set<String> other) throws JsonProcessingException {
        // Null check
        if(!comparable(field, target, other))
            return;

        // Construct Text to use for diff report conflict messages
        Text text = new Text("Conflict", field);

        // Compare Strings
        for(String value : target){
            // Value in target and not in other
            if(!other.contains(value))
                addConflict(field, mismatchType, value, text.getNullItemInSetResponse());
        }
        for(String value : other){
            // Value in other and not in target
            if(!target.contains(value))
                addConflict(field, mismatchType, text.getNullItemInSetResponse(), value);
        }
    }

    /**
     * Compare two sets of hashes
     *
     * @param field Name of the Comparison Field
     * @param target Set of target values
     * @param other Set of other values
     */
    public void compareHashes(String field, Map<String, String> target, Map<String, String> other) throws JsonProcessingException {
        // Null check
        if(!comparable(field, target, other))
            return;

        // Construct Text to use for diff report conflict messages
        Text text = new Text("Conflict", field);

        // Round 1: Compare target against other if equal
        for(String targetAlg : target.keySet()){
            // If other doesn't contain hash, add as missing
            if(!other.containsKey(targetAlg)){
                addConflict(field, MISSING, targetAlg + ", " + target.get(targetAlg), text.getNullResponse());
                continue;
            }
            // Compare hash values
            addConflict(field + " " + targetAlg + " Hash", HASH_MISMATCH, target.get(targetAlg), other.get(targetAlg));
        }

        // Round 2: Don't compare other against target, just checking if present
        for(String otherAlg : other.keySet()){
            // If target doesn't contain hash, add as missing
            if(!target.containsKey(otherAlg))
                addConflict(field, MISSING, text.getNullResponse(), otherAlg + ", " + other.get(otherAlg));
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
    public boolean comparable(String field, Object target, Object other) throws JsonProcessingException {

        // Both are missing, no conflict
        if(target == null && other == null)
            return false;

        // Construct Text to use for diff report conflict messages
        Text text = new Text("Conflict", field);

        // One is missing from the other
        // TODO Better way to handle this case
        if(target == null || other == null){
            addConflict(field, MISSING, (target == null ? objectMapper.writeValueAsString(other) : text.getNullResponse()), (other == null ? objectMapper.writeValueAsString(target) : text.getNullResponse()));
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
