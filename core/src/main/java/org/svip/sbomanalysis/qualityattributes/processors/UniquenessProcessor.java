package org.svip.sbomanalysis.qualityattributes.processors;

import org.nvip.plugfest.tooling.qa.tests.AccurateCPETest;
import org.nvip.plugfest.tooling.qa.tests.AccuratePURLTest;
import org.nvip.plugfest.tooling.qa.tests.HasHashDataTest;
import org.nvip.plugfest.tooling.qa.tests.ValidHashDataTest;

/**
 * file: UniquenessProcessor.java
 *
 * Collection of tests to ensure component's UIDs are accurate
 * @author Matthew Morrison
 */
public class UniquenessProcessor extends AttributeProcessor {

    /**
     * Create a new preset collection of tests
     */
    public UniquenessProcessor(){
        this.attributeName = "Uniqueness";
        this.metricTests.add(new HasHashDataTest());
        this.metricTests.add(new ValidHashDataTest());
        this.metricTests.add(new AccuratePURLTest());
        this.metricTests.add(new AccurateCPETest());
        /*
        TODO
         accurateSWID: SWID matches stored swid data
         */
    }
}
