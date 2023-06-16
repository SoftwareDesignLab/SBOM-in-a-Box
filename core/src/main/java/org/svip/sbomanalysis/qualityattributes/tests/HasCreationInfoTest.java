package org.svip.sbomanalysis.qualityattributes.tests;

import org.svip.sbom.model.SBOM;

import java.util.ArrayList;
import java.util.List;

/**
 * file: HasCreationInfoTest.java
 *
 * For an SPDX SBOM, test that the SBOM Metadata contains
 * Creation Info (Creator and Created)
 * @author Matthew Morrison
 */
public class HasCreationInfoTest extends MetricTest{
    public static final String TEST_NAME = "HasCreationInfo";

    /**
     * Given an SPDX SBOM, check that it has creator and created info
     * @param sbom SBOM to test
     * @return a collection of results of if the sbom contains creator and
     * created time info
     */
    @Override
    public List<Result> test(SBOM sbom) {
        List<Result> results = new ArrayList<>() ;
        Result r;

        // test for creator info (under supplier, can be multiple elements)
        // creator info is null, so sbom did not include it, test fails
        if(isEmptyOrNull(sbom.getSupplier())){
            r = new Result(TEST_NAME, Result.STATUS.FAIL, "SBOM did " +
                    "not include creator info");
            r.updateInfo(Result.Context.STRING_VALUE,
                    "Creator Info is Missing");
        }
        // creator info has some value, test passes
        else{
            r = new Result(TEST_NAME, Result.STATUS.PASS, "SBOM " +
                    "included creator info");
            r.updateInfo(Result.Context.STRING_VALUE, sbom.getSupplier());
        }
        // add context for creator result
        r.addContext(sbom, "Creator");
        r.updateInfo(Result.Context.FIELD_NAME, "Creator");
        results.add(r);

        // test for created time info (under timestamp)
        //timestamp is null, so created info is missing, test fails
        if(isEmptyOrNull(sbom.getTimestamp())){
            r = new Result(TEST_NAME, Result.STATUS.FAIL, "SBOM did " +
                    "not include created time info");
            r.updateInfo(Result.Context.STRING_VALUE,
                    "Created Time Info is Missing");
        }
        // created time info has some value, test passes
        else{
            r = new Result(TEST_NAME, Result.STATUS.PASS, "SBOM " +
                    "included created time info");
            r.updateInfo(Result.Context.STRING_VALUE, sbom.getTimestamp());
        }
        // add context for created time result
        r.addContext(sbom, "Created");
        r.updateInfo(Result.Context.FIELD_NAME, "Created");
        results.add(r);

        return results;
    }
}
