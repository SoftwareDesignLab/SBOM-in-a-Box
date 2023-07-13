package org.svip.sbomanalysis.qualityattributes.tests;


import org.svip.sbom.model.old.Component;
import org.svip.sbom.model.old.SBOM;
import org.svip.sbom.model.uids.CPE;
import org.svip.utils.Debug;

import java.util.ArrayList;
import java.util.List;

/**
 * file: AccurateCPETest.java
 *
 * Test purls for if they match stored component data

 * @author Derek Garcia
 * @author Matthew Morrison
 */
public class AccurateCPETest extends MetricTest{
    // the test name for the results
    private static final String TEST_NAME = "AccurateCPE";

    /**
     * Test every component for CPEs. If they are present, test if
     * CPEs match the component's stored data
     * @param sbom SBOM to test
     * @return a collection of results for each component and their CPE(s)
     */
    @Override
    public List<Result> test(SBOM sbom) {
        // list to hold results for each component
        List<Result> results = new ArrayList<>();

        // Test each component
        for(Component c: sbom.getAllComponents())
            results.addAll(matchingCPEs(c));


        return results;
    }


    /**
     * For every CPE in the component, test that the CPEs information
     * matches the stored component data
     *
     * @param c the component to test
     * @return a list of results for each CPE in the component
     */
    private List<Result> matchingCPEs(Component c){
        List<Result> results = new ArrayList<>();
        Result r;

        // Test each stored cpe
        for(String cpe: c.getCpes()){
            CPE cpeObj;
            // Try to parse CPE string
            try{
                cpeObj = new CPE(cpe);

                // test name
                // both at minimum should have a name
                results.add(isEqual(c, "component name", cpeObj.getProduct(), c.getName()));


                // test version

                // Test if CPE and/or component is missing a version
                r = hasNullValues(c, cpeObj.getVersion(), c.getVersion(), "Version");

                // both component and CPE have versions, continue to comparison test
                if(r == null){
                    results.add(isEqual(c, "version", cpeObj.getVersion(), c.getVersion()));
                }
                // CPE and/or Component is missing a version, add result to list
                else{
                    results.add(r);
                }

                // test Vendor

                // Test if CPE and/or component is missing vendor info
                r = hasNullValues(c, cpeObj.getVendor(), c.getPublisher(), "Vendor");

                // both component and CPE have vendor info, continue to comparison test
                if(r == null){
                    results.add(isEqual(c, "Vendor", cpeObj.getVendor(), c.getPublisher()));
                }
                // CPE and/or Component is missing vendor info, add result to list
                else{
                    results.add(r);
                }

                // TODO other elements to test? Any relevant info in CPE to test in component?

            } catch (Exception e){
                // Failed to parse cpeObj string
                Debug.log(Debug.LOG_TYPE.DEBUG, "Unable to parse CPE \"" + cpe + "\"");
                r = new Result(TEST_NAME, Result.STATUS.FAIL, e.getMessage());

                r.updateInfo(Result.Context.FIELD_NAME, "CPE");
                r.updateInfo(Result.Context.STRING_VALUE, cpe);

                results.add(r);
            }
        }


        return results;
    }

    /**
     * Checks if 2 fields are equal
     *
     * @param c Component
     * @param field name of field that is being checked
     * @param cpeValue Value stored in the PURL string
     * @param componentValue Value stored in the Component
     * @return Result with the findings
     */
    private Result isEqual(Component c, String field, String cpeValue, String componentValue){
        Result r;
        // Check if cpe value is different, if so, test fails
        if(!CPE.isEqualWildcard(cpeValue, componentValue)){
            r = new Result(TEST_NAME, Result.STATUS.FAIL,
                    "CPE does not match " + field);
            r.updateInfo(Result.Context.STRING_VALUE, componentValue);

            // Else they both match, test passes
        } else {
            r = new Result(TEST_NAME, Result.STATUS.PASS, "CPE matches " + field);
        }

        // Add context and return
        r.addContext(c, "CPE:" + field);
        return r;
    }

    /**
     * For testing in optional fields, test if a field is present for both
     * the cpe and component
     * @param cpeValue value stored in the CPE string
     * @param componentValue value stored in the Component
     * @return a result if one or both values are null OR null if both values
     * are present and not empty/null
     */
    private Result hasNullValues(Component c, String cpeValue, String componentValue, String fieldName){
        // booleans to hold if cpe and/or component field are present or not
        // (true if empty/null, else false)
        boolean cpeValueNull = isEmptyOrNull(cpeValue);
        boolean componentValueNull = isEmptyOrNull(componentValue);

        // at least one of the fields is null
        Result r;
        String failMessage;
        String contextMessage = String.format("CPE %s", fieldName);
        // If component is missing the field info and CPE is not
        if(!cpeValueNull && componentValueNull){
            failMessage = String.format("CPE has %s info and component does " +
                    "not", fieldName);
            r = new Result(TEST_NAME, Result.STATUS.FAIL, failMessage);
            r.addContext(c, contextMessage);
            r.updateInfo(Result.Context.STRING_VALUE, cpeValue);
            return r;
        }
        // If CPE is missing the field info and component is not
        else if(cpeValueNull && !componentValueNull){
            failMessage = String.format("Component has %s info and CPE does " +
                    "not", fieldName);
            r = new Result(TEST_NAME, Result.STATUS.FAIL, failMessage);
            r.addContext(c, contextMessage);
            return r;
        }
        // If both component and CPE are missing the field's info
        else if(cpeValueNull && componentValueNull){
            failMessage = String.format("Both Component and CPE missing %s " +
                    "info", fieldName);
            r = new Result(TEST_NAME, Result.STATUS.FAIL, failMessage);
            r.addContext(c, contextMessage);
            return r;
        }
        // both fields are not null and have values, return null so actual
        // test for comparison can occur
        else{
            return null;
        }
    }
}
