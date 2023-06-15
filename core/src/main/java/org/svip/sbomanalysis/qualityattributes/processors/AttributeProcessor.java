package org.svip.sbomanalysis.qualityattributes.processors;

import org.nvip.plugfest.tooling.qa.tests.MetricTest;
import org.nvip.plugfest.tooling.qa.tests.Result;
import org.nvip.plugfest.tooling.sbom.SBOM;

import java.util.ArrayList;
import java.util.List;

/**
 * file: AttributeProcessor.java
 *
 * Template for AttributeProcessors
 *
 * @author Derek Garcia
 */
public abstract class AttributeProcessor {

    protected List<MetricTest> metricTests = new ArrayList<>();
    protected String attributeName;

    /**
     * Default constructor for prebuilt attribute processors
     */
    protected AttributeProcessor(){}

    /**
     * Constructor for custom processors
     *
     * @param attributeName Name of processor
     * @param metricTests Collection of tests to run
     */
    protected AttributeProcessor(String attributeName, List<MetricTest> metricTests){
        this.attributeName = attributeName;
        this.metricTests = metricTests;
    }


    /**
     * Run tests against given SBOM
     *
     * @param sbom sbom to test
     * @return Collection of test results
     */
    public List<Result> process(SBOM sbom){
        List<Result> results = new ArrayList<>();
        // run each test
        for(MetricTest test : this.metricTests)
            results.addAll(test.test(sbom));

        return results;
    }

    ///
    /// Getters
    ///

    public String getAttributeName() {
        return this.attributeName;
    }
}
