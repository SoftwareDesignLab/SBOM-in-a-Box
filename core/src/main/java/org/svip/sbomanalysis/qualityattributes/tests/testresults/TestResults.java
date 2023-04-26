package org.svip.sbomanalysis.qualityattributes.tests.testresults;

import org.svip.sbom.model.Component;

import java.util.ArrayList;

/**
 * File: TestResults.java
 * A class to store organized and formatted test results for an individual component.
 *
 * @author Ian Dunn
 */
public class TestResults {
    /**
     * Component that the test results belong to
     */
    private final Component component;
    private final ArrayList<Test> tests;

    /**
     * Initialize a new instance of TestResults
     * @param c The component that the Tests belong to
     */

    public TestResults(Component c) {
        this.component = c;
        this.tests = new ArrayList<>();
    }

    /**
     * Get the component that the tests belong to.
     *
     * @return The component that the tests belong to
     */
    public Component getComponent() {
        return this.component;
    }

    /**
     * Get a list of all tests, regardless of passing or failing.
     *
     * @return An ArrayList of all tests performed
     */
    public ArrayList<Test> getTests() {
        return this.tests;
    }

    /**
     * Add a single test
     *
     * @param t Test to add
     */
    public void addTest(Test t) {
        tests.add(t);
    }

    /**
     * Add multiple tests from another TestResults instance
     *
     * @param r The TestResults to add to this instance
     */
    public void addTests(TestResults r) {
        tests.addAll(r.getTests());
    }

    /**
     * Get the amount of successful tests ran on the component.
     *
     * @return The number of successful tests ran on the component
     */
    public int getSuccessfulTests() {
        int success = 0;
        for(Test t : tests) {
            if(t.getStatus()) {
                success++;
            }
        }

        return success;
    }

    /**
     * Get the final status of the component based on all tests.
     *
     * @return true if the component passed all tests, false otherwise
     */
    public boolean isSuccessful() {
        return getSuccessfulTests() == tests.size();
    }

    /**
     * Prints the component name, final status, and number of tests passed vs total tests.
     *  Underneath the header is printed the status and message of each individual Test.
     *
     * @return A String representation of all test results.
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(String.format("Component '%s' %s with %d/%d Tests Passed:\n",
                component.getName(),
                isSuccessful() ? "PASSED" : "FAILED", // If isSuccessful is true, set component to PASSED
                getSuccessfulTests(),
                tests.size()));

        for(Test t : tests) {
            out.append(String.format("  %s\n", t.toString()));
        }
        return out.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestResults results = (TestResults) o;

        if (!component.equals(results.getComponent())) return false;
        return tests.equals(results.getTests());
    }
}
