package org.svip.sbomanalysis.qualityattributes.tests;

import org.svip.sbom.model.old.Component;
import org.svip.sbom.model.old.SBOM;

import java.util.ArrayList;
import java.util.List;

/**
 * file: MinElementTest.java
 *
 * Tests for the 7 minimum data fields as stated by the NTIA
 * <a href="https://www.ntia.doc.gov/files/ntia/publications/sbom_minimum_elements_report.pdf">...</a>
 *
 * @author Derek Garcia
 */
public class MinElementTest extends MetricTest {
    private static final String TEST_NAME = "MinimumElements";

    /**
     * Test for minimum Elements
     *
     * @param sbom SBOM to test
     * @return List of results
     */
    @Override
    public List<Result> test(SBOM sbom) {

        // Test SBOM fields
        List<Result> results = new ArrayList<>(testSBOMFields(sbom));

        // Test Component Fields
        for(Component c : sbom.getAllComponents()){
            if(c.isUnpackaged()) continue;
            results.addAll(testComponentFields(c));
        }

        // return findings
        return results;
    }


    /**
     * Testing the data fields for the SBOM itself
     *
     * @param sbom SBOM to test
     * @return List of results
     */
    private List<Result> testSBOMFields(SBOM sbom){
        List<Result> results = new ArrayList<>();
        Result r;   // utility result for adding additional detail todo clunky solution, could be improved

        // Test supplier
        // "The name of the entity that creates the SBOM data for this component."
        r = resultEmptyOrNull(sbom.getSupplier());  // todo should be author, not supplier?
        r.addContext(sbom,"author");
        results.add(r);

        // Test timestamp
        // "Record of the date and time of the SBOM data assembly"
        r = resultEmptyOrNull(sbom.getTimestamp());
        r.addContext(sbom,"timestamp");
        results.add(r);

        return results;
    }


    /**
     * Test major component fields for content
     *
     * @param c Component to test
     * @return Collection of results
     */
    private List<Result> testComponentFields(Component c){

        List<Result> results = new ArrayList<>();
        Result r;   // utility result for adding additional detail todo clunky solution, could be improved

        // Test publisher/supplier
        // "The name of an entity that creates, defines, and identifies components."
        r = resultEmptyOrNull(c.getPublisher());
        r.addContext(c,"publisher");
        results.add(r);

        // Test name
        // "Designation assigned to a unit of software defined by the original supplier."
        r = resultEmptyOrNull(c.getName());
        r.addContext(c,"name");
        results.add(r);

        // Test version
        // "Identifier used by the supplier to specify a change in software from a previously identified version."
        r = resultEmptyOrNull(c.getVersion());
        r.addContext(c,"version");
        results.add(r);


        // Test UIDs
        // "Other identifiers that are used to identify a component, or serve as a look-up key for relevant databases."
        r = resultEmptyOrNull(c.getUniqueID());
        r.addContext(c,"uniqueID");
        results.add(r);

        r = resultEmptyOrNull(c.getCpes());
        r.addContext(c,"cpes");
        results.add(r);

        r = resultEmptyOrNull(c.getPurls());
        r.addContext(c,"purls");
        results.add(r);

        r = resultEmptyOrNull(c.getSwids());
        r.addContext(c,"swids");
        results.add(r);

        return results;
    }


    /**
     * Enhanced empty/null check that returns a result
     *
     * @param o Object to check
     * @return Result
     */
    private Result resultEmptyOrNull(Object o){
        // Null check
        if(o == null)
            return new Result(TEST_NAME, Result.STATUS.FAIL, "Value is null");

        // Test for empty string
        if(o instanceof String){
            // Not an Empty String
            if(!o.equals("")) {
                Result r = new Result(TEST_NAME, Result.STATUS.PASS, "Value is not an empty string");
                r.updateInfo(Result.Context.STRING_VALUE, o.toString());
                return r;
            }
            // String is empty
            return new Result(TEST_NAME, Result.STATUS.FAIL, "Value is an empty string");
        }
        // Not a string but isn't null
        // todo shouldn't default to true
        return new Result(TEST_NAME, Result.STATUS.PASS, "Value is not null");
    }
}
