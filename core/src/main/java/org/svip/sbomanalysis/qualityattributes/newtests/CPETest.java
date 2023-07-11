package org.svip.sbomanalysis.qualityattributes.newtests;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.uids.CPE;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.ResultFactory;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Text;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.STATUS;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * file: CPETest.java
 * Series of tests for CPE strings and objects
 *
 * @author Matthew Morrison
 * @author Derek Garcia
 */
public class CPETest extends MetricTest{

    private final String TEST_NAME = "CPETest";

    private ResultFactory resultFactory;

    private SBOMPackage component;

    /**
     * Constructor to create a new MetricTest
     *
     * @param attributes the list of attributes used
     */
    public CPETest(Component component, List<ATTRIBUTE> attributes) {
        super(attributes);
        this.component = (SBOMPackage) component;
    }

    /**
     * Perform the tests for a CPE
     * @param field the field to test
     * @param value the value to test
     * @return a set of results as a result of each test
     */
    @Override
    public Set<Result> test(String field, String value) {
        Set<Result> results = new HashSet<>();
        // cpe is not a null value and does exist, tests can run
        if(value != null) {
            resultFactory = new ResultFactory(super.attributes, this.TEST_NAME);
            results.add(isValidCPE(field, value));
            results.addAll(isAccurateCPE(field, value));
        }
        // cpe is a null value and does not exist, tests cannot be run
        // return missing Result
        else {
            Text text = new Text(null, field);
            String message = text.getMessage(INFO.MISSING, field);
            String details = text.getDetails(INFO.MISSING, field);
            Result r = new Result(attributes, TEST_NAME, message, details, STATUS.ERROR);
            results.add(r);
        }

        return results;
    }

    /**
     * Test if a given CPE value is valid or not
     * @param field the field that's tested (cpe)
     * @param value the cpe value to be tested
     * @return a result of if the cpe value is valid or not
     */
    private Result isValidCPE(String field, String value){
        try{
            new CPE(value);    // throws error if given purl string is invalid
            return resultFactory.pass(field, INFO.VALID, value,
                    "Valid CPE String, CPE Object was built");
        } catch (Exception e){
            return resultFactory.fail(field, INFO.INVALID, value,
                    "Invalid CPE String, CPE Object could not build");
        }
    }

    /**
     * Test if the CPE value matches the component's stored data
     * @param field the field that's tested (cpe)
     * @param value the cpe value to be tested
     * @return a Set of Results of if the cpe matches that component's stored data
     */
    private Set<Result> isAccurateCPE(String field, String value){
        Set<Result> results = new HashSet<>();
        Result r;
        try{
            CPE cpeObj = new CPE(value);

            // test the cpe name, names need to be present
            results.add(isEqual("CPE Name", cpeObj.getProduct(), component.getName()));
            // test version information, an optional field

            // Test if CPE and/or component is missing a version
            r = hasNullValues("Version", cpeObj.getVersion(), component.getVersion());
            // both component and CPE have versions, continue to comparison test
            if(r == null){
                results.add(isEqual("CPE Version", cpeObj.getVersion(), component.getVersion()));
            }
            // CPE and/or Component is missing a version, add result to list
            else{
                results.add(r);
            }
            // test vendor information, an optional field

            // Test if CPE and/or component is missing vendor info
            r = hasNullValues("Vendor", cpeObj.getVendor(), component.getAuthor());

            // both component and CPE have vendor info, continue to comparison test
            if(r == null){
                results.add(isEqual("CPE Vendor", cpeObj.getVendor(), component.getAuthor()));
            }
            // CPE and/or Component is missing vendor info, add result to list
            else{
                results.add(r);
            }

            // TODO other elements to test? Any relevant info in CPE to test in component?

        }
        // failed to create a new CPE object, test automatically fails
        catch (Exception e){
            r = resultFactory.fail(field, INFO.INVALID, value,
                    "CPE Object could not build");
            results.add(r);
        }
        return results;
    }

    /**
     * Helper function checks if 2 fields are equal
     *
     * @param cpeValue Value stored in the CPE string
     * @param componentValue Value stored in the Component
     * @return Result with the findings
     */
    private Result isEqual(String field, String cpeValue, String componentValue){
        Result r;
        String context;
        // Check if cpe value is different, if so, test fails
        if(!CPE.isEqualWildcard(cpeValue, componentValue)){
            context = field + " value is different between CPE and Component";
            r = resultFactory.fail(field, INFO.INVALID, cpeValue, context);
            // Else they both match, test passes
        } else {
            context = field + " value is the same between CPE and Component";
            r = resultFactory.pass(field, INFO.VALID, cpeValue, context);
        }

        return r;
    }

    /**
     * For testing in optional fields, test if a field is present for both
     * the cpe and component
     * @param field the field that is being tested
     * @param cpeValue value stored in the CPE string
     * @param componentValue value stored in the Component
     * @return a result if one or both values are null OR null if both values
     * are present and not empty/null
     */
    private Result hasNullValues(String field, String cpeValue, String componentValue){
        // booleans to hold if cpe and/or component field are present or not
        // (true if empty/null, else false)
        boolean cpeValueNull = cpeValue.isEmpty();
        boolean componentValueNull = componentValue.isEmpty();

        // at least one of the fields is null
        Result r;
        String failMessage;
        // If component is missing the field info and CPE is not
        if(!cpeValueNull && componentValueNull){
            failMessage = String.format("CPE has %s info and component does " +
                    "not", field);
            r = resultFactory.fail(field, INFO.MISSING, componentValue, failMessage);
            return r;
        }
        // If CPE is missing the field info and component is not
        else if(cpeValueNull && !componentValueNull){
            failMessage = String.format("Component has %s info and CPE does " +
                    "not", field);
            r = resultFactory.fail(field, INFO.MISSING, cpeValue, failMessage);
            return r;
        }
        // If both component and CPE are missing the field's info
        else if(cpeValueNull && componentValueNull){
            failMessage = String.format("Both Component and CPE missing %s " +
                    "info", field);
            r = resultFactory.fail(field, INFO.MISSING, cpeValue, failMessage);
            return r;
        }
        // both fields are not null and have values, return null so actual
        // test for comparison can occur
        else{
            return null;
        }
    }
}
