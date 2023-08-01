package org.svip.sbomanalysis.comparison.conflicts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Text;

/**
 * file: Conflict.java
 * Representation for conflicts
 *
 * @author Thomas Roman
 * @author Derek Garcia
 */
public class Conflict {

    @JsonProperty
    private final MismatchType type;

    @JsonProperty
    private final String message;

    @JsonProperty
    private final String target;

    @JsonProperty
    private final String other;

    public MismatchType GetType(){
        return this.type;
    }

    public String GetMessage(){
        return this.message;
    }
    public String GetTarget(){return this.target;}
    public String GetOther(){return this.other;}

    /**
     * Mismatch Conflict Constructor
     *
     * @param field Name of the conflict field
     * @param mismatchType Type of mismatch
     * @param target Target Value
     * @param other Other Value
     */
    private Conflict(String field, MismatchType mismatchType, String target, String other){
        this.type = mismatchType;
        this.message = getMismatchMessage(field);
        this.target = target;
        this.other = other;
    }

    /**
     * Missing Conflict Constructor
     *
     * @param field Name of the conflict field
     * @param target Target Value
     * @param other Other Value
     */
    private Conflict(String field, String target, String other){
        this.type = MismatchType.MISSING;
        this.message = getMissingMessage(field);
        this.target = target;
        this.other = other;
    }

    ///
    /// Helper Methods
    ///

    /**
     * Get the present Missing message
     *
     * @param field Name of the field that is missing
     * @return Missing message
     */
    private String getMissingMessage(String field){
        return field + " is missing";
    }

    /**
     * Get the preset Mismatch message
     *
     * @param field Name of the field that is mismatched
     * @return Mismatch message
     */
    private String getMismatchMessage(String field){
        return field + " doesn't match";
    }

    /**
     * Creates a new conflict if one exists, otherwise returns null
     *
     * @param field Name of the conflict field
     * @param mismatchType Type of mismatch
     * @param target Target Value
     * @param other Other Value
     * @return New conflict object
     */
    public static Conflict buildConflict(String field, MismatchType mismatchType, String target, String other){

        // Both are missing, no conflict
        if(target == null && other == null)
            return null;

        Text text = new Text("Conflict", field);

        // added more extensive null checks to account for the different null responses
        // Target is missing
        if(target == null || target.isEmpty() || target.equals(text.getNullResponse()) || target.equals(text.getNullItemInSetResponse()))
            return new Conflict(field, target, other);

        // Other is missing
        if(other == null || other.isEmpty() || other.equals(text.getNullResponse()) || other.equals(text.getNullItemInSetResponse()))
            return new Conflict(field, target, other);

        // Mismatch
        if(!target.equals(other))
            return new Conflict(field, mismatchType, target, other);

        // Default to no conflict
        return null;
    }



}
