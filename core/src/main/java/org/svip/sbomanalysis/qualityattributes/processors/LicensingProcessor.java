package org.svip.sbomanalysis.qualityattributes.processors;


import org.svip.sbomanalysis.qualityattributes.tests.HasLicenseDataTest;
import org.svip.sbomanalysis.qualityattributes.tests.ValidSPDXLicenseTest;

/**
 * file: LicensingProcessor.java
 *
 * Collection of tests for components' licenses
 * @author Matthew Morrison
 */
public class LicensingProcessor extends AttributeProcessor{

    /**
     * Create new preset collection of tests
     */
    public LicensingProcessor(){
        this.attributeName = "Licensing";
        this.metricTests.add(new HasLicenseDataTest());
        this.metricTests.add(new ValidSPDXLicenseTest());
    }
}
