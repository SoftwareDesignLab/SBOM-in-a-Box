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


    public void compareComparableSets(String field, MismatchType mismatchType, List<Comparable> target, List<Comparable> other){
        // Null check
        if(!comparable(field, target, other))
            return;

        for(Comparable targetValue : target){
            boolean compared = false;   // track if comparison occurred
            for(Comparable otherValue : other){

                if(targetValue.equals(otherValue)){
                    addConflicts(targetValue.compare(otherValue));
                    compared = true;
                }
            }
            if(!compared)
                addConflict(field, MISSING, targetValue.toString(), null);
        }

        for(Comparable otherValue : other){
            boolean compared = false;   // track if comparison occurred
            for(Comparable targetValue : target){
                if (otherValue.equals(targetValue)) {
                    compared = true;
                    break;
                }
            }
            if(!compared)
                addConflict(field, MISSING, null, otherValue.toString());
        }

//        while(!target.isEmpty()){
//            Comparable targetValue = target.remove(0);
//
//            boolean compared = false;   // track if comparison occurred
//
//            for(Comparable otherValue : other){
//                if(targetValue.equals(otherValue)){
//                    addConflicts(targetValue.compare(otherValue));
//                    compared = true;
//                }
//            }
//
//            if(!compared)
//                addConflict(field, MISSING, targetValue.toString(), null);
//        }
//
//        for(Comparable otherValue : other)
//            addConflict(field, MISSING, null, otherValue.toString());

    }

    private boolean comparable(String field, Object target, Object other){

        // Both are missing, no conflict
        if(target == null && other == null)
            return false;

        // One is missing from the other
        // TODO Better way to handle this case
        if(target == null || other == null){
            addConflict(field, MISSING, "FOO", "BAR");
            return false;
        }

        // False: Skip, not comparable classes
        // True: Objects are of the same class to be compared
        return target.getClass().equals(other.getClass());

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


    public void compareContacts(String field, Set<Contact> target, Set<Contact> other){
        // Null check
        if(!comparable(field, target, other))
            return;

        List<Contact> targetList = new ArrayList<>(target);
        List<Contact> otherList = new ArrayList<>(other);

        while(!targetList.isEmpty()){
            Contact targetValue = targetList.remove(0);

            boolean compared = false;   // track if comparison occurred
            for(Contact otherValue : otherList){
                if(targetValue.equals(otherValue)){
                    addConflicts(targetValue.compare(otherValue));
                    otherList.remove(otherValue);
                    compared = true;
                }
            }

            if(!compared)
                addConflict(field, MISSING, target.toString(), null);
        }

        for(Contact otherValue : otherList)
            addConflict(field, MISSING, null, otherValue.toString());

    }

    public void compareCreationData(String field, CreationData target, CreationData other){
        // Null check
        if(!comparable(field, target, other))
            return;

        // add conflicts
        addConflicts(target.compare(other));
    }

    public void compareLicenseCollections(String field, MismatchType mismatchType, LicenseCollection target, LicenseCollection other){
        if (target == null && other == null) {
            return;
        }
        if (target == null) {
            addConflict(field, mismatchType, null, other.getConcluded().toString() + other.getDeclared().toString() + other.getInfoFromFiles().toString());
            return;
        }
        if (other == null) {
            addConflict(field, mismatchType, target.getConcluded().toString() + target.getDeclared().toString() + target.getInfoFromFiles().toString(), null);
            return;
        }
        // CONCLUDED
        compareStringSets("License", LICENSE_MISMATCH, target.getConcluded(), other.getConcluded());
        // DECLARED
        compareStringSets("License", LICENSE_MISMATCH, target.getDeclared(), other.getDeclared());
        // INFO FROM FILES
        compareStringSets("License", LICENSE_MISMATCH, target.getInfoFromFiles(), other.getInfoFromFiles());
    }

    public void compareContacts(String field, MismatchType mismatchType, Contact target, Contact other) {
        // Both are missing, no conflict
        if(target == null && other == null)
            return;

        // Target is missing
        if(target == null) {
            addConflict(field, mismatchType, null, other.getName());
            return;
        }

        // Other is missing
        if(other == null) {
            addConflict(field, mismatchType, target.getName(), null);
            return;
        }

        // ensure that these are not missing before their respective mismatch checks
        boolean nameMissing = false;
        boolean emailMissing = false;
        boolean phoneMissing = false;

        // Target Name is missing
        if(target.getName() == null || target.getName().isEmpty()) {
            addConflict(field, mismatchType, null, other.getName());
            nameMissing = true;
        }
        // Other Name is missing
        if(other.getName() == null || other.getName().isEmpty()) {
            addConflict(field, mismatchType, target.getName(), null);
            nameMissing = true;
        }
        // Target Email is missing
        if(target.getEmail() == null || target.getEmail().isEmpty()) {
            addConflict(field, mismatchType, null, other.getName());
            emailMissing = true;
        }
        // Other Email is missing
        if(other.getEmail() == null || other.getEmail().isEmpty()) {
            addConflict(field, mismatchType, target.getName(), null);
            emailMissing = true;
        }
        // Target Phone is missing
        if(target.getPhone() == null || target.getPhone().isEmpty()) {
            addConflict(field, mismatchType, null, other.getName());
            phoneMissing = true;
        }
        // Other Phone is missing
        if(other.getPhone() == null || other.getPhone().isEmpty()) {
            addConflict(field, mismatchType, target.getName(), null);
            phoneMissing = true;
        }
        // Mismatch
        if(!nameMissing && !target.getName().equals(other.getName())) {
            addConflict(field, mismatchType, target.getName(), other.getName());
        }
        if(!phoneMissing && !target.getPhone().equals(other.getPhone())) {
            addConflict(field, mismatchType, target.getName(), other.getName());
        }
        if(!emailMissing && !target.getEmail().equals(other.getEmail())) {
            addConflict(field, mismatchType, target.getName(), other.getName());
        }
    }

    /**
     * @return List of stored conflicts
     */
    public List<Conflict> getConflicts(){
        return this.conflicts;
    }

}
