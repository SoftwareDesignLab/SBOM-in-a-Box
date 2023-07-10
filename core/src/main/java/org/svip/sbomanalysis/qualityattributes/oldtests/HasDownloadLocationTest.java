package org.svip.sbomanalysis.qualityattributes.oldtests;

import org.svip.sbom.model.old.Component;
import org.svip.sbom.model.old.SBOM;

import java.util.ArrayList;
import java.util.List;

/**
 * file: HasDownloadLocationTest.java
 *
 * For an SPDX SBOM, test that all components contains
 * a download location
 * @author Matthew Morrison
 */
public class HasDownloadLocationTest extends MetricTest {
    private static final String TEST_NAME = "HasDownloadLocation";

    /**
     * Test every component in the sbom for the PackageDownloadLocation field
     * and that it has a value
     * @param sbom SBOM to test
     * @return a collection of results from each component and if it contains
     * info about its download location
     */
    @Override
    public List<Result> test(SBOM sbom) {
        List<Result> results = new ArrayList<>();

        // for every component, test for its download location
        for(Component c : sbom.getAllComponents()){
            results.add(componentDownloadLocation(c));
        }
        return results;
    }

    /**
     * Test a single component for its download location and if its present
     * @param c the component to test
     * @return a result of if the component contains info about its
     * download location
     */
    private Result componentDownloadLocation(Component c){
        Result r;

        // TODO check for NOASSERTION or NONE?
        // if the downloadLocation is null, the component did not
        // include it, test fails
        if(isEmptyOrNull(c.getDownloadLocation())){
            r = new Result(TEST_NAME, Result.STATUS.FAIL, "Component did " +
                    "not include download location");
        }
        // downloadLocation has a value, test passes
        else{
            r = new Result(TEST_NAME, Result.STATUS.PASS, "Component " +
                    "included download location");
            r.updateInfo(Result.Context.STRING_VALUE, c.getDownloadLocation());
        }
        r.addContext(c, "PackageDownloadLocation");
        r.updateInfo(Result.Context.FIELD_NAME, "PackageDownloadLocation");
        return r;
    }
}
