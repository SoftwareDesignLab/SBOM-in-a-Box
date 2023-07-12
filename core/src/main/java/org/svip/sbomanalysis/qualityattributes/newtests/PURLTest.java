package org.svip.sbomanalysis.qualityattributes.newtests;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.uids.PURL;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.ResultFactory;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Text;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.STATUS;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
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
            results.addAll(existsInRepo(field, value));
        }
        // purl string is null so no tests can be run
        // return missing Result
        else {
            Result r = resultFactory.error(field, INFO.MISSING, value);
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
        Set<Result> results = new HashSet<>();
        Result r;
        try{
            // create new purl object
            new PURL(value);
            r = resultFactory.pass(field, INFO.VALID, value);
        }
        // failed to create new purl, test fails
        catch(Exception e){
            r = resultFactory.fail(field, INFO.INVALID, value);
        }
        results.add(r);
        return results;
    }

    private Set<Result> isAccuratePURL(String field, String value){
        Set<Result> results = new HashSet<>();
        Result r;
        try{
            PURL purl = new PURL(value);
            results.add(isEqual(field, purl.getName(), component.getName()));

            results.add(isEqual(field, purl.getVersion(), component.getVersion()));

        }
        // failed to create new purl, test automatically fails
        catch(Exception e){
            r = resultFactory.fail(field, INFO.INVALID, value);
            results.add(r);
        }
        return results;
    }

    /**
     * Helper function to check if 2 fields are equal
     *
     * @param field name of field that is being checked
     * @param purlValue Value stored in the PURL string
     * @param componentValue Value stored in the Component
     * @return Result with the findings
     */
    private Result isEqual(String field, String purlValue, String componentValue){
        Result r;
        // Check if purl value is different
        if(!purlValue.equals(componentValue)){
            r = resultFactory.fail(field, INFO.INVALID, purlValue);
        }
        // Else they both match
        else {
            r = resultFactory.pass(field, INFO.VALID, purlValue);
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