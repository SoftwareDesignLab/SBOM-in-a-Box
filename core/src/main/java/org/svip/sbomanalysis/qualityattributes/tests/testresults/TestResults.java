package org.svip.sbomanalysis.qualityattributes.tests.testresults;

import org.svip.sbom.model.Component

import com.fasterxml.jackson.annotation.JsonProperty;
import org.nvip.plugfest.tooling.sbom.Component;

import java.util.ArrayList;

/**
 * File: TestResults.java
 * A class to store organized and formatted test results for an individual component.
 *
 * @author Ian Dunn
 * @author Matt London
 */
public class TestResults {
    /**
     * Component that the test results belong to
     */
    @JsonProperty("component") private final Component component;
    @JsonProperty("tests") private final ArrayList<Test> tests;

    /**
     * Initialize a new instance of TestResults
     * @param component The component that the Tests belong to
     */
    public TestResults(Component component) {
        this.component = component;
        this.tests = new ArrayList<>();
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

    ///
    /// getters and setters
    ///

    public Component getComponent() {
        return this.component;
    }

    public ArrayList<Test> getTests() {
        return this.tests;
    }

    ///
    /// Overrides
    ///

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
