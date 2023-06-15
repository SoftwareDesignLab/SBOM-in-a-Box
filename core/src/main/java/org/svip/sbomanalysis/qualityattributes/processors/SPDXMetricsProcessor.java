package org.svip.sbomanalysis.qualityattributes.processors;

import org.nvip.plugfest.tooling.qa.tests.*;

/**
 * file: SPDXMetricsProcessor.java
 *
 * Collection of tests to ensure SPDX SBOM specific metrics are included
 * and accurate
 * @author Matthew Morrison
 */
public class SPDXMetricsProcessor extends AttributeProcessor{

    /**
     * Create a new preset collection of tests
     */
    public SPDXMetricsProcessor(){
        this.attributeName = "SPDXMetrics";

        this.metricTests.add(new HasDataLicenseSPDXTest());
        this.metricTests.add(new HasSPDXIDTest());
        this.metricTests.add(new HasDocumentNamespaceTest());
        this.metricTests.add(new HasDownloadLocationTest());
        this.metricTests.add(new HasCreationInfoTest());
        this.metricTests.add(new HasVerificationCodeTest());
        this.metricTests.add(new HasExtractedLicensesTest());
        this.metricTests.add(new ExtractedLicenseMinElementTest());
        /*
        TODO
           hasBomVersion
           hasDocumentName
         */
    }
}
