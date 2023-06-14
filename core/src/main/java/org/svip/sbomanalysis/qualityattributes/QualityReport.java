package org.svip.sbomanalysis.qualityattributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.svip.sbomanalysis.qualityattributes.tests.testresults.Result;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;




/**
 * JSON-friendly object to report Metric findings
 *
 * @author Dylan Mulligan
 * @author Matt London
 * @author Ian Dunn
 * @author Derek Garcia
 */
@JsonPropertyOrder({"uid", "attributeResults" })
public class QualityReport {
    @JsonProperty
    private final String uid;
    @JsonProperty
    private final HashMap<String, HashMap<String, ArrayList<Result>>> attributeResults = new HashMap<>();


    /**
     * Create new QualityReport object with a form of UID
     *
     * @param uid unique identifier for the quality report
     */
    public QualityReport(String uid){
        this.uid = uid;
    }

    /**
     * Update an attribute with result details
     *
     * @param attributeName Name of attribute
     * @param results List of test results
     */
    public void updateAttribute(String attributeName, List<Result> results){
        // init test results
        HashMap<String, ArrayList<Result>> testResults =
                this.attributeResults.computeIfAbsent(attributeName, k -> new HashMap<>());

        // Add each test and result to the attribute
        for(Result r : results){
            testResults.computeIfAbsent(r.getTestName(), k -> new ArrayList<>());
            testResults.get(r.getTestName()).add(r);
        }
    }
}

