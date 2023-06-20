package org.svip.sbomanalysis.qualityattributes.tests;

import org.svip.sbom.model.Component;
import org.svip.sbom.model.SBOM;

import java.util.ArrayList;
import java.util.List;

/**
 * file: HasSPDXIDTest.java
 *
 * For every component in a SPDX SBOM, check that each component has a
 * valid SPDXID (SPDXID: "SPDXRef-[idstring]")
 * @author Matthew Morrison
 */
public class HasSPDXIDTest extends MetricTest{
    public static final String TEST_NAME = "HasSPDXID";

    /**
     * Test every component in a given SBOM for a valid SPDXID
     * @param sbom SBOM to test
     * @return a collection of results for every component in the SBOM
     */
    @Override
    public List<Result> test(SBOM sbom) {
        List<Result> results = new ArrayList<>();

        //TODO UID's (SPDXID) in metadata not present
        // Check that the metadata contains a valid SPDXID


        //for every component, check for SPDXID in HasSPDXID
        for(Component c : sbom.getAllComponents()){
            results.add(HasSPDXIDComponent(c));
        }
        return results;
    }

    /**
     * Check a single component for an SPDXID and test that it matches the
     * format "SPDXRef-..."
     * @param c the component to test
     * @return a result of if a component has an SPDXID and if it is valid
     */
    public Result HasSPDXIDComponent(Component c){
        // check that the component has an SPDXID
        String spdxID = c.getUniqueID();
        Result r;

        // SPDXID is not present, test fails
        if(isEmptyOrNull(spdxID)){
            r = new Result(TEST_NAME, Result.STATUS.FAIL, "Component does " +
                    "not contain a SPDXID");
        }
        // SPDXID is present, continue test
        else{
            // TODO Can we make this more thorough? Not just format?
            // check that SPDXID is a valid format
            // SPDXID starts with a valid format, test passes
            if(spdxID.startsWith("SPDXRef-")){
                r = new Result(TEST_NAME, Result.STATUS.PASS, "Component has " +
                        "a valid SPDXID");
            }
            // SPDX starts with an invalid format, test fails
            else{
                r = new Result(TEST_NAME, Result.STATUS.FAIL, "Component has " +
                        "an invalid SPDXID format");
            }
            // add context when a SPDXID is present
            r.updateInfo(Result.Context.FIELD_NAME, "SPDXID");
            r.updateInfo(Result.Context.STRING_VALUE, spdxID);
        }
        r.addContext(c, "SPDXID");
        return r;
    }
}
