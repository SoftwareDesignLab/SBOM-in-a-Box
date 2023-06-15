package org.svip.sbomanalysis.qualityattributes.tests.testresults;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * File: Test.java
 * Container to hold results of an individual test.
 *
 * @author Ian Dunn
 */
public class Result {
    /**
     * Status of the test. true=PASSED, false=FAILED
     */
    private final boolean status;

    /**
     * Message of the test.
     */
    private final String[] message;


    @JsonIgnore
    private final String testName;

    /**
     * Initialize a new Test.
     * @param status The test status - true=PASSED, false=FAILED
     * @param message Multiple string parameters with the message of the test
     */
    public Result(boolean status, String... message) {
        this.status = status;
        this.message = message;
        this.testName = "temp";
    }

    ///
    /// getters and setters
    ///

    public boolean getStatus() {
        return status;
    }

    public String getMessage() {
        return String.join("", message);
    }

    public String getTestName(){ return testName;}

    ///
    /// Overrides
    ///

    @Override
    public String toString() {
        return (status ? "PASSED: " : "FAILED: ") + getMessage();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        if (status != result.status) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return getMessage().equals(result.getMessage());
    }
}
