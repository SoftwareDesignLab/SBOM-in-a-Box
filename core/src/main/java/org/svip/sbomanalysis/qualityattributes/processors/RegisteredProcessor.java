package org.svip.sbomanalysis.qualityattributes.processors;

import org.svip.sbomanalysis.qualityattributes.tests.IsRegisteredTest;


/**
 * file: RegisteredProcessor.java
 *
 * Collection of tests to ensure the SBOM's package manager are registered
 * @author Matthew Morrison
 */
public class RegisteredProcessor extends AttributeProcessor{

    /**
     * Create new preset collection of tests
     */
    public RegisteredProcessor(){
        this.attributeName = "Registered";
        this.metricTests.add(new IsRegisteredTest());
    }
}
