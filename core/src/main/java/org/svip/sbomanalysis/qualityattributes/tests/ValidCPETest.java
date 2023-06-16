package org.svip.sbomanalysis.qualityattributes.tests;

import org.svip.sbom.model.Component;
import org.svip.sbom.model.SBOM;
import org.svip.sbom.model.uids.CPE;


import java.util.ArrayList;
import java.util.List;

/**
 * file: ValidCPETest.java
 *
 * Tests if the cpes are valid
 *
 * @author Derek Garcia
 */
public class ValidCPETest extends MetricTest {
    private static final String TEST_NAME = "ValidCPE";

    /**
     * Validates the PURL
     *
     * @param sbom SBOM to test
     * @return Collection of results
     */
    @Override
    public List<Result> test(SBOM sbom) {
        List<Result> results = new ArrayList<>();

        for (Component c : sbom.getAllComponents()) {
            // Skip if no CPEs
            if(isEmptyOrNull(c.getCpes()))
                continue;
            Result r;

            // Else attempt to make object
            for (String cpe : c.getCpes()) {
                // Attempt to make new Purl Object
                try{
                    new CPE(cpe);    // throws error if given purl string is invalid
                    r = new Result(TEST_NAME, Result.STATUS.PASS, "Valid CPE String");
                } catch (Exception e){
                    //Debug disabled for now
                    //Debug.log(Debug.LOG_TYPE.WARN, "Failed to parse CPE \"" + cpe +"\" | " + e.getMessage());    // log incase regex fails
                    r = new Result(TEST_NAME, Result.STATUS.FAIL, "Invalid CPE String");
                }

                r.addContext(c, "cpe");
                results.add(r);
            }
        }

        // return findings
        return results;
    }
}
