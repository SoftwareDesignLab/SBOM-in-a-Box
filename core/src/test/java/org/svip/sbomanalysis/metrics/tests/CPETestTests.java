package org.svip.sbomanalysis.metrics.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.metrics.tests.CPETest;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.enumerations.STATUS;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: CPETestTests.java
 * Test class to test CPETest and its methods and usage
 *
 * @author Matthew Morrison
 */
class CPETestTests {


    String testActualCPE = "cpe:2.3:a:python_software_foundation:python:3.11.2:*:*:*:*:*:*:*";

    CPETest cpeTest;

    @BeforeEach
    public void create_CPETest(){
        SPDX23PackageObject test_component = new SPDX23PackageObject(
                null, null, "python_software_foundation", "python", null, null,
                null, null, "3.11.2", null, null, null,
                null, null, null, null, null, null, null, null, null,
                null, null, null);
        cpeTest = new CPETest(test_component, ATTRIBUTE.SPDX23, ATTRIBUTE.UNIQUENESS);
    }

    @Test
    public void test_error_test(){
        Set<Result> result = cpeTest.test("cpe", null);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.ERROR, r.getStatus());
    }

    @Test
    public void isValidCPE_isAccurateCPE_pass_test(){
        Set<Result> result =  cpeTest.test("cpe", testActualCPE);

        List<Result> resultList = new ArrayList<>(result);
        for(Result r : resultList){
            assertEquals(STATUS.PASS, r.getStatus());
        }
    }

    @Test
    public void isValidCPE_isAccurateCPE_fail_test(){
        Set<Result> result =  cpeTest.test("cpe", "cpe:cpefail");

        List<Result> resultList = new ArrayList<>(result);
        for(Result r : resultList){
            assertEquals(STATUS.FAIL, r.getStatus());
        }
    }

    @Test
    public void isAccurateCPE_match_name_fail_test(){
        Set<Result> result =  cpeTest.test("cpe", "cpe:2.3:a:python_software_foundation:nohtyp:3.11.2:*:*:*:*:*:*:*");

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

        assertEquals(3, pass);
        assertEquals(1, fail);
    }

    @Test
    public void isAccurateCPE_match_version_fail_test(){
        Set<Result> result =  cpeTest.test("cpe", "cpe:2.3:a:python_software_foundation:python:3.20.0:*:*:*:*:*:*:*");

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

        assertEquals(3, pass);
        assertEquals(1, fail);
    }

    @Test
    public void isAccurateCPE_match_vendor_fail_test(){
        Set<Result> result =  cpeTest.test("cpe", "cpe:2.3:a:not_correct_vendor:python:3.11.2:*:*:*:*:*:*:*");

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

        assertEquals(3, pass);
        assertEquals(1, fail);
    }

    @Test
    public void isAccurateCPE_all_pass_test(){
        Set<Result> result =  cpeTest.test("cpe", testActualCPE);

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

        assertEquals(4, pass);
        assertEquals(0, fail);
    }
}