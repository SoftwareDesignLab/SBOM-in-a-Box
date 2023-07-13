package org.svip.sbomanalysis.qualityattributes.newtests;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.uids.PURL;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.ResultFactory;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;

import java.util.HashSet;
import java.util.Set;

/**
 * file: PURLTest.java
 * Series of tests for purl strings and purl objects
 *
 * @author Matthew Morrison
 * @author Derek Garcia
 */
public class PURLTest extends MetricTest{

    private final String TEST_NAME = "PURLTest";

    private ResultFactory resultFactory;

    private final SBOMPackage component;

    /**
     * Constructor to create a new PURLTest
     *
     * @param component the component that is being tested
     * @param attributes the list of attributes used
     */
    public PURLTest(Component component, ATTRIBUTE... attributes) {
        super(attributes);
        this.component = (SBOMPackage) component;
        resultFactory = new ResultFactory(TEST_NAME, attributes);
    }

    /**
     * Conduct a series of tests for a given PURL string
     * @param field the field being tested (purl)
     * @param value the value being tested (the purl string)
     * @return a set of results from each test
     */
    @Override
    public Set<Result> test(String field, String value) {
        Set<Result> results = new HashSet<>();
        // check that purl string value is not null
        if(value != null) {
            results.addAll(isValidPURL(field, value));
            results.addAll(isAccuratePURL(field, value));
            // results.addAll(existsInRepo(field, value));
        }
        // purl string is null so no tests can be run
        // return missing Result
        else {
            Result r = resultFactory.error(field, INFO.NULL, value, component.getName());
            results.add(r);
        }
        return results;
    }

    /**
     * Test the purl string if it is valid and follows purl schema
     * @param field the field being tested (purl)
     * @param value the purl string
     * @return a result if the purl is valid or not
     */
    private Set<Result> isValidPURL(String field, String value){
        String testName = "ValidPURL";
        ResultFactory resultFactory = new ResultFactory(testName, ATTRIBUTE.UNIQUENESS);
        Set<Result> results = new HashSet<>();
        Result r;
        try{
            // create new purl object
            new PURL(value);
            r = resultFactory.pass(field, INFO.VALID, value, component.getName());
        }
        // failed to create new purl, test fails
        catch(Exception e){
            r = resultFactory.fail(field, INFO.INVALID, value, component.getName());
        }
        results.add(r);
        return results;
    }

    private Set<Result> isAccuratePURL(String field, String value){
        Set<Result> results = new HashSet<>();
        Result r;
        try{
            PURL purl = new PURL(value);
            results.add(match(purl));

        }
        // failed to create new purl, test automatically fails
        catch(Exception e){
            r = resultFactory.error(field, INFO.ERROR, value, component.getName());
            results.add(r);
        }
        return results;
    }

    /**
     * Helper function to check if PURL and Component match
     *
     * @param purl the purl to be tested
     * @return Result with the findings
     */
    private Result match(PURL purl){
        String testName = "AccuratePURL";
        ResultFactory resultFactory = new ResultFactory(testName, ATTRIBUTE.UNIQUENESS);
        Result r;

        // test purl and component name
        String purlName = purl.getName();
        if(!purlName.equals(component.getName())){
            r = resultFactory.fail("PURL Name", INFO.INVALID, purl.toString(), component.getName());
            return r;
        }
        // test purl and component version
        String purlVersion = purl.getVersion();
        if(!purlVersion.equals(component.getVersion())){
            r = resultFactory.fail("PURL Version", INFO.INVALID, purl.toString(), component.getName());
            return r;
        }
        // all fields match the component, test passes
        else {
            r = resultFactory.pass("PURL Match", INFO.VALID, purl.toString(), component.getName());
        }

        return r;
    }

    /**
     * Test if a purl string exists in its respective package manager repo
     * @param field the field being tested (purl)
     * @param value the purl string
     * @return a Set<Result> of if the purl string exists in its repo
     */
    //TODO wait for HTTPClient to implement
    private Set<Result> existsInRepo(String field, String value){
        return null;
    }
}