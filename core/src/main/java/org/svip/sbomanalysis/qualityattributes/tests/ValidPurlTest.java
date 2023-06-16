package org.svip.sbomanalysis.qualityattributes.tests;

import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbom.model.Component;
import org.svip.sbom.model.uids.PURL;
import org.svip.sbom.model.SBOM;


import java.util.ArrayList;
import java.util.List;

/**
 * file: ValidPurlTest.java
 *
 * Tests if the purls are valid
 *
 * @author Derek Garcia
 */
public abstract class ValidPurlTest extends MetricTest {
    private static final String TEST_NAME = "ValidPurl";

    protected ValidPurlTest() {
        super("Valid Purl Test");
    }
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
            // Skip if no PURLs
            if(isEmptyOrNull(c.getPurls()))
                continue;
            Result r;
            // Else attempt to make object
            for (String p : c.getPurls()) {
                // Attempt to make new Purl Object
                try{
                    new PURL(p);    // throws error if given purl string is invalid
                    r = new Result(TEST_NAME, Result.STATUS.PASS, "Valid Purl String");
                } catch (Exception e){
                    Debug.log(Debug.LOG_TYPE.WARN, "Failed to parse PURL \"" + p +"\" | " + e.getMessage());    // log incase regex fails
                    r = new Result(TEST_NAME, Result.STATUS.FAIL, "Invalid Purl String");
                }
                r.addContext(c, "Valid PURL String");
                r.updateInfo(Result.Context.FIELD_NAME, "purl");
                r.updateInfo(Result.Context.STRING_VALUE, p);
                results.add(r);
            }
        }

        // return findings
        return results;
    }
}
