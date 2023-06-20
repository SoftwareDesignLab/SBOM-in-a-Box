package org.svip.sbomanalysis.qualityattributes.processors;

import org.svip.sbomanalysis.qualityattributes.tests.MinElementTest;
import org.svip.sbomanalysis.qualityattributes.tests.ValidPurlTest;
import org.svip.sbomanalysis.qualityattributes.tests.ValidCPETest;

/**
 * file: CompletenessProcessor.java
 *
 * Collection of tests to ensure the SBOM's data fields are complete
 * @author Derek Garcia
 */
public class CompletenessProcessor extends AttributeProcessor{

    /**
     * Create new preset collection of tests
     */
    public CompletenessProcessor(){
       this.attributeName = "Completeness";
       this.metricTests.add(new MinElementTest());
       this.metricTests.add(new ValidPurlTest());
       this.metricTests.add(new ValidCPETest());
       //TODO
       // ValidSWID Test
       // HasRelationships test
   }
}
