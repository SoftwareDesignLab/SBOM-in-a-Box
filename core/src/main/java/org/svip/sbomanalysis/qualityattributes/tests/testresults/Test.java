package org.svip.sbomanalysis.qualityattributes.tests.testresults;

/**
 * File: Test.java
 * Container to hold results of an individual test.
 *
 * @author Ian Dunn
 */
public class Test {
    /**
     * Status of the test. true=PASSED, false=FAILED
     */
    private final boolean status;

    /**
     * Message of the test.
     */
    private final String[] message;

    /**
     * Initialize a new Test.
     * @param status The test status - true=PASSED, false=FAILED
     * @param message Multiple string parameters with the message of the test
     */
    public Test(boolean status, String... message) {
        this.status = status;
        this.message = message;
    }

    /**
     * Get status of the test
     *
     * @return The status of the test - true=PASSED, false=FAILED
     */
    public boolean getStatus() {
        return status;
    }

    /**
     * Get the test output message.
     *
     * @return The message of the test output.
     */
    public String getMessage() {
        return String.join("", message);
    }

    @Override
    public String toString() {
        return (status ? "PASSED: " : "FAILED: ") + getMessage();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Test test = (Test) o;

        if (status != test.status) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return getMessage().equals(test.getMessage());
    }
}
