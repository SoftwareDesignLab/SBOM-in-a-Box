package org.svip.sbomanalysis.qualityattributes.tests;

import org.svip.sbom.model.Component;
import org.svip.sbom.model.SBOM;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * file: HasBomRefTest.java
 *
 * For every component in a given CDX SBOM, test if the component has a
 * unique identifier to reference inside the BOM (bom-ref for CDX)
 * @author Matthew Morrison
 */
public abstract class HasBomRefTest extends MetricTest{
    private static final String TEST_NAME = "HasBomRef";

    protected HasBomRefTest() {
        super("Has Bom Ref Test");
    }
    /**
     * Test every component for a bom-ref
     * @param sbom SBOM to test
     * @return a collection of results from each component in the SBOM
     */
    @Override
    public List<Result> test(SBOM sbom) {
        List<Result> results = new ArrayList<>();

        // check to make sure components are present, if not return an
        // error result that no components are present
        Set<Component> components = sbom.getAllComponents();

        if(components.isEmpty()){
            Result r = new Result(TEST_NAME, Result.STATUS.ERROR, "SBOM " +
                    "does not have any components to test");
            r.addContext(sbom, "Component bom-refs");
            r.updateInfo(Result.Context.STRING_VALUE, "No components present");
            results.add(r);
            return results;
        }

        // if components are present
        // loop through each component and test
        for(Component c: components){
            results.add(checkBomRef(c));
        }
        return results;
    }

    /**
     * For a given component, check if a unique id (bom-ref for CycloneDX)
     * is present
     * @param c the component to test
     * @return the result of the component and if a bom-ref is present
     */
    private Result checkBomRef(Component c){
        Result r;
        // if unique id is empty/null, then there is no bom-ref
        if(isEmptyOrNull(c.getUniqueID())){
            r = new Result(TEST_NAME, Result.STATUS.FAIL, "Component " +
                    "does not have bom-ref identifier");
            r.updateInfo(Result.Context.STRING_VALUE, "Bom-Ref is Missing");
        }
        // a bom-ref is present
        else{
            r = new Result(TEST_NAME, Result.STATUS.PASS, "Component " +
                    "has bom-ref identifier");
            r.updateInfo(Result.Context.STRING_VALUE, c.getUniqueID());
        }
        r.addContext(c,"Bom-Ref");
        r.updateInfo(Result.Context.FIELD_NAME, "bom-ref");
        return r;
    }
}
