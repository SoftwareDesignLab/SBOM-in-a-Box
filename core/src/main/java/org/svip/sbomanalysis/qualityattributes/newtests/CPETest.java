package org.svip.sbomanalysis.qualityattributes.newtests;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.uids.CPE;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.ResultFactory;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;

import java.util.HashSet;
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

    private final ResultFactory resultFactory;

    private final SBOMPackage component;

    /**
     * Constructor to create a new MetricTest
     *
     * @param attributes the list of attributes used
     */
    public CPETest(Component component, ATTRIBUTE... attributes) {
        super(attributes);
        this.component = (SBOMPackage) component;
        resultFactory = new ResultFactory(TEST_NAME, attributes);
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
            results.add(isValidCPE(field, value));
            results.addAll(isAccurateCPE(field, value));
        }
        // cpe is a null value and does not exist, tests cannot be run
        // return missing Result
        else {
            Result r = resultFactory.error(field, INFO.MISSING, value);
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
            return resultFactory.pass(field, INFO.VALID, value);
        } catch (Exception e){
            return resultFactory.fail(field, INFO.INVALID, value);
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
            results.add(match(cpeObj));
        }
        // failed to create a new CPE object, test automatically fails
        catch (Exception e){
            r = resultFactory.fail(field, INFO.INVALID, value);
            results.add(r);
        }
        return results;
    }

    /**
     * Helper function checks if CPE and Component match
     *
     * @param cpe the cpe to be tested against
     * @return Result with the findings
     */
    private Result match(CPE cpe){
        Result r;

        // test cpe and component name
        String cpeName = cpe.getProduct();
        if(!cpeName.equals(component.getName())){
            r = resultFactory.fail("CPE Name", INFO.INVALID, cpe.toString());
            return r;
        }

        // test cpe and component version
        String cpeVersion = cpe.getVersion();
        if(!cpeVersion.equals(component.getVersion())){
            r = resultFactory.fail("CPE Vendor", INFO.INVALID, cpe.toString());
            return r;
        }

        // test cpe vendor to component author
        String cpeVendor = cpe.getVendor();
        if(!cpeVendor.equals(component.getAuthor())){
            r = resultFactory.fail("CPE Vendor", INFO.INVALID, cpe.toString());
            return r;
        }
        // all fields match the component, test passes
       else {
            r = resultFactory.pass("CPE Match", INFO.VALID, cpe.toString());
        }

        return r;
    }

}
