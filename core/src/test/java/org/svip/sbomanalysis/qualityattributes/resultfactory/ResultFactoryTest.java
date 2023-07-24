package org.svip.sbomanalysis.qualityattributes.resultfactory;

import org.junit.jupiter.api.Test;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.STATUS;
import org.svip.sbomanalysis.qualityattributes.tests.enumerations.ATTRIBUTE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: ResultFactoryTest.java
 * Test class to test ResultFactory and its usages
 *
 * @author Matthew Morrison
 */
class ResultFactoryTest {

    Collection<String> testEmptyCol = new ArrayList<>();

    Collection<String> testValueCol = new ArrayList<>(List.of(
            "TestValue1", "TestValue2"
    ));
    String testTestName = "Test Name";
    ATTRIBUTE attribute1 = ATTRIBUTE.COMPLETENESS;
    ATTRIBUTE attribute2 = ATTRIBUTE.LICENSING;

    List<ATTRIBUTE> testAttributeList = new ArrayList<>(List.of(
            attribute1, attribute2
    ));

    ResultFactory resultFactory = new ResultFactory(testTestName,
            attribute1, attribute2);

    @Test
    public void result_invalid_fail_match_single_value_test() {

        Result r = resultFactory.fail("purl", INFO.INVALID, "notARealPURL", "Component");

        Result test_result = new Result(testAttributeList, testTestName,
                "notARealPURL is an invalid purl",
                "notARealPURL is an invalid purl", STATUS.FAIL);

        assertEquals(test_result.getAttributes(), r.getAttributes());
        assertEquals(test_result.getTest(), r.getTest());
        assertEquals(test_result.getMessage(), r.getMessage());
        assertEquals(test_result.getDetails(), r.getDetails());
        assertEquals(test_result.getStatus(), r.getStatus());
    }

    @Test
    public void result_missing_fail_match_single_value_test() {

        Result r = resultFactory.fail("purl", INFO.MISSING, "", "Component");

        Result test_result = new Result(testAttributeList, testTestName,
                "Component is missing purl",
                "Component is missing the following field: purl", STATUS.FAIL);

        assertEquals(test_result.getAttributes(), r.getAttributes());
        assertEquals(test_result.getTest(), r.getTest());
        assertEquals(test_result.getMessage(), r.getMessage());
        assertEquals(test_result.getDetails(), r.getDetails());
        assertEquals(test_result.getStatus(), r.getStatus());
    }


    @Test
    public void result_has_pass_match_single_value_test(){

        Result r = resultFactory.pass("purl", INFO.HAS,
                "pkg:golang/rsc.io/sampler@v1.3.0", "Component");

        Result test_result = new Result(testAttributeList, testTestName,
                "Component has a purl",
                "purl: pkg:golang/rsc.io/sampler@v1.3.0", STATUS.PASS);

        assertEquals(test_result.getAttributes(), r.getAttributes());
        assertEquals(test_result.getTest(), r.getTest());
        assertEquals(test_result.getMessage(), r.getMessage());
        assertEquals(test_result.getDetails(), r.getDetails());
        assertEquals(test_result.getStatus(), r.getStatus());
    }

    @Test
    public void result_valid_pass_match_single_value_test(){

        Result r = resultFactory.pass("purl", INFO.VALID,
                "pkg:golang/rsc.io/sampler@v1.3.0", "Component");

        Result test_result = new Result(testAttributeList, testTestName,
                "pkg:golang/rsc.io/sampler@v1.3.0 is a valid purl",
                "pkg:golang/rsc.io/sampler@v1.3.0 is a valid purl", STATUS.PASS);

        assertEquals(test_result.getAttributes(), r.getAttributes());
        assertEquals(test_result.getTest(), r.getTest());
        assertEquals(test_result.getMessage(), r.getMessage());
        assertEquals(test_result.getDetails(), r.getDetails());
        assertEquals(test_result.getStatus(), r.getStatus());
    }

    @Test
    public void result_null_error_match_single_value_test() {

        Result r = resultFactory.error("purl", INFO.NULL, (String) null, "Component");

        Result test_result = new Result(testAttributeList, testTestName,
                "purl was a null value",
                "purl was a null value", STATUS.ERROR);

        assertEquals(test_result.getAttributes(), r.getAttributes());
        assertEquals(test_result.getTest(), r.getTest());
        assertEquals(test_result.getMessage(), r.getMessage());
        assertEquals(test_result.getDetails(), r.getDetails());
        assertEquals(test_result.getStatus(), r.getStatus());
    }

    @Test
    public void result_error_match_single_value_test() {

        Result r = resultFactory.error("purl", INFO.ERROR, "abcd", "Component");

        Result test_result = new Result(testAttributeList, testTestName,
                "purl had an error",
                "abcd had an error producing the following object: purl", STATUS.ERROR);

        assertEquals(test_result.getAttributes(), r.getAttributes());
        assertEquals(test_result.getTest(), r.getTest());
        assertEquals(test_result.getMessage(), r.getMessage());
        assertEquals(test_result.getDetails(), r.getDetails());
        assertEquals(test_result.getStatus(), r.getStatus());
    }

    @Test
    public void result_has_pass_match_multiple_values_test(){

        Result r = resultFactory.pass("String", INFO.HAS,
                testValueCol, "Component");

        Result test_result = new Result(testAttributeList, testTestName,
                "Component has Strings",
                "Component has 2 Strings: " +
                        "TestValue1, TestValue2", STATUS.PASS);

        assertEquals(test_result.getAttributes(), r.getAttributes());
        assertEquals(test_result.getTest(), r.getTest());
        assertEquals(test_result.getMessage(), r.getMessage());
        assertEquals(test_result.getDetails(), r.getDetails());
        assertEquals(test_result.getStatus(), r.getStatus());
    }

    @Test
    public void result_missing_fail_match_multiple_values_test(){

        Result r = resultFactory.fail("String", INFO.MISSING,
                testEmptyCol, "Component");

        Result test_result = new Result(testAttributeList, testTestName,
                "Component has missing Strings",
                "Component is missing the following " +
                        "field: String", STATUS.FAIL);

        assertEquals(test_result.getAttributes(), r.getAttributes());
        assertEquals(test_result.getTest(), r.getTest());
        assertEquals(test_result.getMessage(), r.getMessage());
        assertEquals(test_result.getDetails(), r.getDetails());
        assertEquals(test_result.getStatus(), r.getStatus());
    }

    @Test
    public void result_null_error_match_multiple_values_test(){

        Result r = resultFactory.error("String", INFO.NULL,
                (Collection<String>) null, "Component");

        Result test_result = new Result(testAttributeList, testTestName,
                "String was a null value",
                "String was a null value", STATUS.ERROR);

        assertEquals(test_result.getAttributes(), r.getAttributes());
        assertEquals(test_result.getTest(), r.getTest());
        assertEquals(test_result.getMessage(), r.getMessage());
        assertEquals(test_result.getDetails(), r.getDetails());
        assertEquals(test_result.getStatus(), r.getStatus());
    }

    @Test
    public void result_matching_pass_single_value_test(){

        Result r = resultFactory.pass("Name", INFO.MATCHING,
               "Component Name", "Component Name");

        Result test_result = new Result(testAttributeList, testTestName,
                "Name matches Component",
                "Expected: Component Name\nActual: Component Name", STATUS.PASS);

        assertEquals(test_result.getAttributes(), r.getAttributes());
        assertEquals(test_result.getTest(), r.getTest());
        assertEquals(test_result.getMessage(), r.getMessage());
        assertEquals(test_result.getDetails(), r.getDetails());
        assertEquals(test_result.getStatus(), r.getStatus());
    }

}