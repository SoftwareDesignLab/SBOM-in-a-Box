package org.svip.sbomanalysis.qualityattributes.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.STATUS;
import org.svip.sbomanalysis.qualityattributes.tests.enumerations.ATTRIBUTE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: PURLTestTests.java
 * Test class to test PURLTest and its methods and usage
 * @author Kevin Laporte
 */
class PURLTestTests {

    String testRandomPURL = "pkg:random/test@2.0.0";

    String testActualPURL = "pkg:golang/rsc.io/sampler@v1.3.0";

    PURLTest purlTest;



    @BeforeEach
    public void create_purlTest(){
        SPDX23PackageObject test_component = new SPDX23PackageObject(
                null, null, null, "sampler", null, null,
                null, null, "v1.3.0", null, null, null,
                null, null, null, null, null, null, null, null, null,
                null, null, null);

        purlTest = new PURLTest(test_component, ATTRIBUTE.UNIQUENESS);
    }

    @Test
    public void test_error_test(){
        Set<Result> result =  purlTest.test("purl", null);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.ERROR, r.getStatus());
    }

    // TODO test with coverage fails, debug passes? results are being added to the set differently in each case
    // TODO refactor lots of repeat code
    @Test
    public void isValidPURL_pass_test(){
        Set<Result> result =  purlTest.test("purl", testActualPURL);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    public void isValidPURL_fail_test(){
        Set<Result> result =  purlTest.test("purl", "purl");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    public void isValidPURL_isAccuratePURL_pass_test(){
        Set<Result> result =  purlTest.test("purl", testActualPURL);

        List<Result> resultList = new ArrayList<>(result);
        for(Result r : resultList){
            assertEquals(STATUS.PASS, r.getStatus());
        }
    }

    @Test
    public void isAccuratePURL_match_getName_fail_test(){
        Set<Result> result =  purlTest.test("purl", "pkg:golang/rsc.io/incorrectName@v1.3.0");

        List<Result> resultList = new ArrayList<>(result);
        int pass = 0;
        int fail = 0;

        for(Result r : resultList){
            if(r.getStatus().equals(STATUS.PASS)){
                pass++;
            }
            else{
                fail++;
            }
        }

        assertEquals(1, pass);
        assertEquals(1, fail);
    }

    @Test
    public void isAccuratePURL_match_getVersion_fail_test(){
        Set<Result> result =  purlTest.test("purl", "pkg:golang/rsc.io/sampler@v0.0.0");

        List<Result> resultList = new ArrayList<>(result);
        int pass = 0;
        int fail = 0;

        for(Result r : resultList){
            if(r.getStatus().equals(STATUS.PASS)){
                pass++;
            }
            else{
                fail++;
            }
        }

        assertEquals(1, pass);
        assertEquals(1, fail);
    }

    @Test
    public void isValidPURL_pass_isAccuratePURL_match_fail_test(){
        Set<Result> result =  purlTest.test("purl", testRandomPURL);

        List<Result> resultList = new ArrayList<>(result);
        int pass = 0;
        int fail = 0;

        for(Result r : resultList){
            if(r.getStatus().equals(STATUS.PASS)){
                pass++;
            }
            else{
                fail++;
            }
        }

        assertEquals(1, pass);
        assertEquals(1, fail);
    }
}