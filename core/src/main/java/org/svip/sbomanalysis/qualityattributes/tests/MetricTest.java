package org.svip.sbomanalysis.qualityattributes.tests;

import org.svip.sbomanalysis.qualityattributes.tests.testresults.TestResults;
import org.svip.sbom.model.*;

/**
 * Abstract class to be extended by all metric tests
 */
public abstract class MetricTest {
    //#region Attributes

    private final String name;

    //#endregion

    //#region Constructors

    protected MetricTest(String name) {
        this.name = name;
    }

    //#endregion

    //#region Abstract Methods

    public abstract TestResults test(Component c);

    //#endregion

    //#region Getters

    public String getName() { return this.name; }

    //#endregion

}
