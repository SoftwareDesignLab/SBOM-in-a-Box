package org.svip.sbomanalysis.qualityattributes.tests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.svip.sbom.model.old.Component;
import org.svip.sbom.model.old.SBOM;

import java.util.HashMap;

/**
 * File: Result.java
 * JSON-Friendly storage for the result of a Test
 *
 * @author Derek Garcia
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result {

    /**
     * Result test statuses
     */
    public enum STATUS {
        PASS(1),
        FAIL(0),
        ERROR(-1);

        /*
        Utilities to convert to ints
         */
        private final int code;
        STATUS(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    /**
     * Enums to update the additional info section
     */
    public enum Context{
        TYPE,           // Type of test (component, sbom, etc)
        FIELD_NAME,     // Name of value being checked EX: "publisher"
        STRING_VALUE,   // String of value being checked EX: when checking publisher string_value="google"
        IDENTIFIER      // UID of the source
    }

    @JsonIgnore
    private final String testName;
    @JsonProperty("pass")
    private final int pass;

    @JsonProperty("message")
    private final String message;

    @JsonProperty("additionalInfo")
    private HashMap<String, String> additionalInfo;     // optional field to hold any additional info


    /**
     * Create new result of a test
     *
     * @param testName name of the parent test
     * @param pass Whether the test passed or not
     * @param message test message
     */
    public Result(String testName, STATUS pass, String message){
        this.testName = testName;
        this.pass = pass.getCode();
        this.message = message;
    }

    /**
     * Update the additional Info section with data
     *
     * @param context Key context enum
     * @param value Value to set at that field
     */
    public void updateInfo(Context context, String value){
        // Create new hashmap if one doesn't exist
        if(this.additionalInfo == null)
            this.additionalInfo = new HashMap<>();
        // add value
        this.additionalInfo.put(context.toString(), value);
    }

    /**
     * Utility to add context based on the given object
     *
     * @param o Object to reference
     * @param fieldName name of field to update
     */
    public void addContext(Object o, String fieldName){
        // add SBOM context
        if(o instanceof SBOM){
            updateInfo(Context.TYPE, "SBOM");
            updateInfo(Context.IDENTIFIER, ((SBOM) o).getSerialNumber());
        }

        // add Component context
        if(o instanceof Component){
            updateInfo(Context.TYPE, "Component");
            updateInfo(Context.IDENTIFIER, ((Component) o).getName());  // todo better identifier?
        }

        // todo Other objects? ie Purl, CPE, Signatures, etc

        // add field info
        updateInfo(Context.FIELD_NAME, fieldName);
    }

    ///
    /// Getters
    ///

    public String getTestName() {
        return this.testName;
    }
}
