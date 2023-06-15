package org.svip.sbomanalysis.qualityattributes.processors;


import org.svip.sbomanalysis.qualityattributes.tests.AccurateCPETest;
import org.svip.sbomanalysis.qualityattributes.tests.AccuratePURLTest;
import org.svip.sbomanalysis.qualityattributes.tests.HasHashDataTest;
import org.svip.sbomanalysis.qualityattributes.tests.ValidHashDataTest;

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
