package org.svip.sbomanalysis.qualityattributes.tests;


import org.svip.sbom.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * file: HasCreationInfoTest.java
 *
 * For an SPDX SBOM, test components to see if any contain extracted
 * licenses not on the SPDX license list and list them
 * @author Matthew Morrison
 */
public class HasExtractedLicensesTest extends MetricTest{
    public static final String TEST_NAME = "HasExtractedLicenses";

    /**
     * Check all components in a given SBOM for extracted licenses not on
     * the SPDX license list
     * @param sbom SBOM to test
     * @return a collection of results if any extracted licenses exist
     * in the SBOM
     */
    @Override
    public List<Result> test(SBOM sbom) {
        List<Result> results = new ArrayList<>();
        Result r;

        // for every component, check for any extracted licenses
        for(Component c : sbom.getAllComponents()){
            Map<String, Map<String, String>>  extractedLicenses = c.getExtractedLicenses();
            // skip if no extracted licenses are found for the component
            if(!extractedLicenses.isEmpty()){
                ArrayList<String> licenseStrings = new ArrayList<>();
                for(String licenseRef : extractedLicenses.keySet()){{
                    licenseStrings.add(licenseRef);
                }}
                    String message = "Extracted Licenses Found:  "
                            + String.join(", ", licenseStrings);
                    r = new Result(TEST_NAME, Result.STATUS.PASS, "Extracted licenses found for component "
                            + c.getName());
                    r.addContext(c, "Extracted Licenses");
                    r.updateInfo(Result.Context.FIELD_NAME, "ExtractedLicenses");
                    r.updateInfo(Result.Context.STRING_VALUE, message);
                    results.add(r);

            }
        }

        if(results.isEmpty()){
            r = new Result(TEST_NAME, Result.STATUS.PASS, "No Extracted " +
                    "Licenses found in SBOM");
            r.addContext(sbom, "Extracted Licenses");
            r.updateInfo(Result.Context.FIELD_NAME, "ExtractedLicenses");
            r.updateInfo(Result.Context.STRING_VALUE,
                    "No Extracted Licenses Found in SBOM");
            results.add(r);
        }

        return results;
    }
}
