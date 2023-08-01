package org.svip.sbomanalysis.qualityattributes.pipelines;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * file: QualityReport.java
 * JSON-friendly object to report Metric findings
 *
 * @author Dylan Mulligan
 * @author Matt London
 * @author Ian Dunn
 * @author Derek Garcia
 * @author Matthew Morrison
 */
@JsonPropertyOrder({"uid", "componentResults" })
public class QualityReport {
    @JsonProperty
    private final String uid;

    @JsonProperty
    private Map<String, Map<String, List<Result>>> components = new HashMap<>();

    /**
     * Create a new QualityReport
     * @param uid unique identifier for the quality report
     */
    public QualityReport(String uid){
        this.uid = uid;
    }

    /**
     * add a component to the quality report
     * @param componentName the name of the component to add
     * @param results list of test results
     */
    public void addComponent(String componentName, List<Result> results){
        // init test results
        Map<String, List<Result>> testResults =
                this.components.computeIfAbsent(componentName, k -> new HashMap<>());
        testResults.put(componentName, results);
        // add the test results and the component
        components.put(uid, testResults);
    }
}
