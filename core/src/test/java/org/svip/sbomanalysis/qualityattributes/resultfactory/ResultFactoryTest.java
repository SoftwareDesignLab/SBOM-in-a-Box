package org.svip.sbomanalysis.qualityattributes.resultfactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.STATUS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResultFactoryTest {

    String testField = "Test Field";
    String testValue = "TestValue";
    Collection<String> testValueCol = new ArrayList<>(List.of(
            "TestValue1", "TestValue2"
    ));
    String testTestName = "Test Name";
    ATTRIBUTE attribute1 = ATTRIBUTE.COMPLETENESS;
    ATTRIBUTE attribute2 = ATTRIBUTE.LICENSING;

    List<ATTRIBUTE> testAttributeList = new ArrayList<>(List.of(
            attribute1, attribute2
    ));

//    ResultFactory resultFactory = new ResultFactory(testTestName,
//            attribute1, attribute2);
//
//    @Test
//    public void result_invalid_fail_match_single_value_test(){
//
//        Result r = resultFactory.fail("purl", INFO.INVALID, "notARealPURL", "Component");
//
//        Result test_result = new Result(testAttributeList, testTestName,
//                "notARealPURL is an invalid purl",
//                "notARealPURL is an invalid purl", STATUS.FAIL);
//
//        assertEquals(test_result.getAttributes(), r.getAttributes());
//        assertEquals(test_result.getTest(), r.getTest());
//        assertEquals(test_result.getMessage(), r.getMessage());
//        assertEquals(test_result.getDetails(), r.getDetails());
//        assertEquals(test_result.getStatus(), r.getStatus());
//    }
//
//    @Test
//    public void result_missing_fail_match_single_value_test(){
//
//        Result r = resultFactory.fail("purl", INFO.MISSING, "");
//
//        Result test_result = new Result(testAttributeList, testTestName,
//                " is a missing purl",
//                "TODO", STATUS.FAIL);
//
//        assertEquals(test_result.getAttributes(), r.getAttributes());
//        assertEquals(test_result.getTest(), r.getTest());
//        assertEquals(test_result.getMessage(), r.getMessage());
//        assertEquals(test_result.getDetails(), r.getDetails());
//        assertEquals(test_result.getStatus(), r.getStatus());
//    }
//
//    @Test
//    public void result_valid_pass_match_single_value_test(){
//
//        Result r = resultFactory.pass("purl", INFO.VALID,
//                "pkg:golang/rsc.io/sampler@v1.3.0");
//
//        Result test_result = new Result(testAttributeList, testTestName,
//                "pkg:golang/rsc.io/sampler@v1.3.0 is a valid purl",
//                "pkg:golang/rsc.io/sampler@v1.3.0 is a valid purl", STATUS.PASS);
//
//        assertEquals(test_result.getAttributes(), r.getAttributes());
//        assertEquals(test_result.getTest(), r.getTest());
//        assertEquals(test_result.getMessage(), r.getMessage());
//        assertEquals(test_result.getDetails(), r.getDetails());
//        assertEquals(test_result.getStatus(), r.getStatus());
//    }
//
//    @Test
//    public void result_has_pass_match_single_value_test(){
//
//        Result r = resultFactory.pass("purl", INFO.HAS,
//                "pkg:golang/rsc.io/sampler@v1.3.0");
//
//        Result test_result = new Result(testAttributeList, testTestName,
//                "pkg:golang/rsc.io/sampler@v1.3.0 has a purl",
//                "purl: pkg:golang/rsc.io/sampler@v1.3.0", STATUS.PASS);
//
//        assertEquals(test_result.getAttributes(), r.getAttributes());
//        assertEquals(test_result.getTest(), r.getTest());
//        assertEquals(test_result.getMessage(), r.getMessage());
//        assertEquals(test_result.getDetails(), r.getDetails());
//        assertEquals(test_result.getStatus(), r.getStatus());
//    }

}