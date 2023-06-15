package org.svip.sbomanalysis.qualityattributes.tests;

import org.svip.sbom.model.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
            Result r;
            Set<String> licenses = c.getLicenses();
            // Check if licenses exist
            // licenses are not present
            if(isEmptyOrNull(licenses)){
                r = new Result(TEST_NAME, Result.STATUS.FAIL,
                        "No Licenses Found");
                r.updateInfo(Result.Context.STRING_VALUE,
                        "No Licenses Found for Component");
            }
            // licenses are present
            else{
                r = new Result(TEST_NAME, Result.STATUS.PASS,
                        c.getLicenses().size() + " Licenses Found");
                String licenseList = String.join(", ", licenses);
                r.updateInfo(Result.Context.STRING_VALUE,
                        "Licenses: " + licenseList);
            }

            r.addContext(c, "licenses");
            r.updateInfo(Result.Context.FIELD_NAME, "licenses");
            results.add(r);
        }

        return results;
    }

}
