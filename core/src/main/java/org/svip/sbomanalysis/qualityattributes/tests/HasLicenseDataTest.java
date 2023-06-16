package org.svip.sbomanalysis.qualityattributes.tests;

import org.svip.sbom.model.Component;
import org.svip.sbom.model.SBOM;

import java.util.ArrayList;
import java.util.List;

/**
 * file: HasLicenseDataTest.java
 *
 * Test each component in the given SBOM if they have license data
 * @author Matthew Morrison
 * @author Derek Garcia
 */
public class HasLicenseDataTest extends MetricTest{

    private static final String TEST_NAME = "HasLicenseData";

    /**
     * Test all SBOM components for their licenses and if their data is present
     * (url, id, name, etc)
     * @param sbom SBOM to test
     * @return Collection of results for each component
     */
    @Override
    public List<Result> test(SBOM sbom) {
        // create a list to hold each result of sbom components
        List<Result> results = new ArrayList<>();

        // Check for components
        for(Component c : sbom.getAllComponents()){
            // Check if licenses exist
            Result r = isEmptyOrNull(c.getLicenses())
                    ? new Result(TEST_NAME, Result.STATUS.FAIL, "No Licenses Found")
                    : new Result(TEST_NAME, Result.STATUS.PASS, c.getLicenses().size() + " Licenses Found");

            r.addContext(c, "licenses");

            results.add(r);
        }

        return results;
    }

}
