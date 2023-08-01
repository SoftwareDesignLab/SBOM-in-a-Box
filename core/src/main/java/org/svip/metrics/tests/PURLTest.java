package org.svip.metrics.tests;

import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.ResultFactory;
import org.svip.metrics.resultfactory.enumerations.INFO;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.uids.PURL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * file: PURLTest.java
 * Series of tests for purl strings and purl objects
 *
 * @author Matthew Morrison
 * @author Derek Garcia
 */
public class PURLTest extends MetricTest {

    private final ResultFactory resultFactory;

    private final SBOMPackage component;


    /**
     * Constructor to create a new PURLTest
     *
     * @param component  the component that is being tested
     * @param attributes the list of attributes used
     */
    public PURLTest(Component component, ATTRIBUTE... attributes) {
        super(attributes);
        this.component = (SBOMPackage) component;
        String TEST_NAME = "PURLTest";
        resultFactory = new ResultFactory(TEST_NAME, attributes);
    }

    /**
     * Conduct a series of tests for a given PURL string
     *
     * @param field the field being tested (purl)
     * @param value the value being tested (the purl string)
     * @return a set of results from each test
     */
    @Override
    public Set<Result> test(String field, String value) {
        Set<Result> results = new HashSet<>();
        // check that purl string value is not null
        if (value != null) {
            results.add(isValidPURL(field, value));
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
     *
     * @param field the field being tested (purl)
     * @param value the purl string
     * @return a result if the purl is valid or not
     */
    private Result isValidPURL(String field, String value) {
        var rf = new ResultFactory("Valid PURL", ATTRIBUTE.COMPLETENESS, ATTRIBUTE.UNIQUENESS, ATTRIBUTE.MINIMUM_ELEMENTS);
        try {
            // create new purl object
            new PURL(value);
            return rf.pass(field, INFO.VALID,
                    value, component.getName());
        }
        // failed to create new purl, test fails
        catch (Exception e) {
            return rf.fail(field, INFO.INVALID,
                    value, component.getName());
        }
    }

    /**
     * Test the purl string for its accuracy against the component's fields
     *
     * @param field the field to be tested
     * @param value the purl string
     * @return the result of if the purl's fields matches the
     * component's fields
     */
    private List<Result> isAccuratePURL(String field, String value) {
        var rf = new ResultFactory("Accurate PURL", ATTRIBUTE.COMPLETENESS, ATTRIBUTE.UNIQUENESS, ATTRIBUTE.MINIMUM_ELEMENTS);
        try {
            PURL purl = new PURL(value);
            return match(purl);

        }
        // failed to create new purl, test automatically fails
        catch (Exception e) {
            List<Result> result = new ArrayList<>();
            result.add(rf.fail(field, INFO.ERROR,
                    value, component.getName()));
            return result;
        }
    }

    /**
     * Helper function to check if PURL and Component match
     *
     * @param purl the purl to be tested
     * @return list of results with the findings
     */
    private List<Result> match(PURL purl) {
        var rf = new ResultFactory("Matching PURL", ATTRIBUTE.COMPLETENESS, ATTRIBUTE.UNIQUENESS, ATTRIBUTE.MINIMUM_ELEMENTS);
        List<Result> results = new ArrayList<>();
        // test purl and component name
        String purlName = purl.getName();
        if (!purlName.equals(component.getName())) {
            results.add(rf.fail("PURL Name", INFO.NOT_MATCHING,
                    purl.getName(), component.getName()));
        } else {
            results.add(rf.pass("PURL Name", INFO.MATCHING,
                    purl.getName(), component.getName()));
        }
        // test purl and component version
        String purlVersion = purl.getVersion();
        if (!purlVersion.equals(component.getVersion())) {
            results.add(rf.fail("PURL Version", INFO.NOT_MATCHING,
                    purl.getVersion(), component.getVersion()));
        } else {
            results.add(rf.pass("PURL Version", INFO.MATCHING,
                    purl.getVersion(), component.getVersion()));
        }
        return results;
    }

    /**
     * Test if a purl string exists in its respective package manager repo
     *
     * @param field the field being tested (purl)
     * @param value the purl string
     * @return a Set<Result> of if the purl string exists in its repo
     */
    //TODO wait for HTTPClient to implement
    private Set<Result> existsInRepo(String field, String value) {
        return null;
    }
}