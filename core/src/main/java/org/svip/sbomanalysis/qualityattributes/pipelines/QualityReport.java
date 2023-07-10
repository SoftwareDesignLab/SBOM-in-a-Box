package org.svip.sbomanalysis.qualityattributes.pipelines;

import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * file: QualityReport.java
 * Object to report Metric findings
 *
 * @author Matthew Morrison
 */
public class QualityReport {

    private final String uid;
    private Map<String, Map<String, List<Result>>> components;

    public QualityReport(String uid){
        this.uid = uid;
    }

    public void addComponent(String componentName, List<Result> results){
        // init test results
        Map<String, List<Result>> testResults =
                this.components.computeIfAbsent(componentName, k -> new HashMap<>());
        testResults.put(componentName, results);
        // add the test results and the component
        components.put(uid, testResults);
    }
}
