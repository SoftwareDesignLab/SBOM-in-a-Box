package org.svip.sbomanalysis.qualityattributes.tests;

import org.svip.sbom.model.SBOM;

import java.util.ArrayList;
import java.util.List;

/**
 * file: HasBomVersionTest.java
 *
 * For a given SBOM, check if it has a version number declared
 * @author Matthew Morrison
 */
public class HasBomVersionTest extends MetricTest{
    private static final String TEST_NAME = "HasBomVersion";

    /**
     * Given an SBOM, test if is it contains a version number
     * @param sbom SBOM to test
     * @return the result of checking the sbom's version number
     */
    @Override
    public List<Result> test(SBOM sbom) {
        List<Result> result = new ArrayList<>();
        Result r;

        // if sbom version is empty or null, the sbom is missing this
        // info, test fails
        if(isEmptyOrNull(sbom.getSbomVersion())){
            r = new Result(TEST_NAME, Result.STATUS.FAIL, "SBOM " +
                    "does not have version number declared");
            r.updateInfo(Result.Context.STRING_VALUE, "SBOM Version Number " +
                    "is Missing");
        }
        // sbom version is present, test passes
        else{
            r = new Result(TEST_NAME, Result.STATUS.PASS, "SBOM " +
                    "has a version number declared");
            r.updateInfo(Result.Context.STRING_VALUE, sbom.getSbomVersion());
        }
        r.addContext(sbom,"SBOM Version Number");
        r.updateInfo(Result.Context.FIELD_NAME, "version");
        result.add(r);

        return result;
    }
}
