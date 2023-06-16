package org.svip.sbomanalysis.qualityattributes.tests;

import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbom.model.Component;
import org.svip.sbom.model.CPE;
import org.svip.sbom.model.SBOM;

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
     * Validates the CPE
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
                    Debug.log(Debug.LOG_TYPE.WARN, "Failed to parse CPE \"" + cpe +"\" | " + e.getMessage());    // log incase regex fails
                    r = new Result(TEST_NAME, Result.STATUS.FAIL, "Invalid CPE String");
                }
                r.addContext(c, "Valid CPE String");
                r.updateInfo(Result.Context.FIELD_NAME, "cpe");
                r.updateInfo(Result.Context.STRING_VALUE, cpe);
                results.add(r);
            }
        }

        // return findings
        return results;
    }
}
