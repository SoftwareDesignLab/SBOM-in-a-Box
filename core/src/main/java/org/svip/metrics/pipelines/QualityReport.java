package org.svip.metrics.pipelines;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.svip.metrics.resultfactory.Result;
import org.svip.repair.fix.Fix;

import java.util.*;

/**
 * file: QualityReport.java
 * JSON-friendly object to report Metric findings
 *
 * @author Dylan Mulligan
 * @author Matt London
 * @author Ian Dunn
 * @author Derek Garcia
 * @author Matthew Morrison
 * @author Justin Jantzi
 */
@JsonPropertyOrder({"uid", "componentResults"})
public class QualityReport {
    @JsonProperty
    private final String uid;

    private final Map<Integer, List<Result>> components = new HashMap<>();

    private final Map<Integer, String> componentHashCodeMapping = new HashMap<>();

    /**
     * Create a new QualityReport
     *
     * @param uid unique identifier for the quality report
     */
    public QualityReport(String uid) {
        this.uid = uid;
    }

    /**
     * add a component to the quality report
     *
     * @param componentName the name of the component to add
     * @param results       list of test results
     */
    public void addComponent(String componentName, int hashCode, List<Result> results) {
        // init test results
        List<Result> testResults = this.components.computeIfAbsent(hashCode, k -> new ArrayList<>());
        testResults.addAll(results);
        // add the test results and the component
        components.put(hashCode, testResults);
        componentHashCodeMapping.put(hashCode, componentName);
    }


    ///
    /// Getters
    ///

    /**
     * @return QA UID
     */
    public String getUid() {
        return this.uid;
    }

    /**
     * returns the results of the quality report
     *
     * @return A nested map which holds the results for each component
     */
    public Map<Integer, List<Result>> getResults() {
        return components;
    }

    /**
     * Gets how many fixes are appended to the quality report
     * @return fix amount
     */
    public long getFixAmount() {
        return getResults().values().stream().filter(x -> x.stream().filter(y -> y.getFixes().size() > 0).count() > 0).count();
    }

    /**
     * Gets all fixes appended
     * @return fixes
     */
    public Map<Integer, Set<Fix<?>>> getFixes() {
        Map<Integer, Set<Fix<?>>> fixes = new HashMap<>();
        Map<Integer, List<Result>> results = getResults();

        for(Integer component : results.keySet()) {
            List<Result> compResults = results.get(component);
            Set<Fix<?>> compFixes = new HashSet<>();

            for(Result result : compResults) {
                compFixes.addAll(result.getFixes());
            }

            if(compFixes.size() > 0)
                fixes.put(component, compFixes);
        }

        return fixes;
    }

    public Map<Integer, String> getHashCodeMapping() { return this.componentHashCodeMapping; }
}
