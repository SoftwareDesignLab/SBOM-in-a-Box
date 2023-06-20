package org.svip.sbomanalysis.qualityattributes.processors;

import org.svip.sbomanalysis.qualityattributes.tests.HasBomRefTest;
import org.svip.sbomanalysis.qualityattributes.tests.HasBomVersionTest;

/**
 * file: CDXMetricsProcessor.java
 *
 * A collection of tests that are tailored to CycloneDX SBOM specific metrics
 * @author Matthew Morrison
 */
public class CDXMetricsProcessor extends AttributeProcessor{

    /**
     * Create new preset collection of tests
     */
    public CDXMetricsProcessor(){
        this.attributeName = "CDXMetrics";
        this.metricTests.add(new HasBomRefTest());
        this.metricTests.add(new HasBomVersionTest());

        /*TODO
            hasCompositionAggregate: If compositions are present, does it contain an aggregate?
            hasExternalRefSpecs: If External References are present, do they contain a url and type?
            hasServicesName: If services are present, does each service have a name?
            hasDependenciesRef: If dependencies are present, does each dependency contain a ref?
         */

    }
}
