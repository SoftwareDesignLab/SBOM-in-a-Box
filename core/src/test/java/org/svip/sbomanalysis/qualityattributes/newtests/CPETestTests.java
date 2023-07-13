package org.svip.sbomanalysis.qualityattributes.newtests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.STATUS;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
            assertEquals(STATUS.ERROR, r.getStatus());
        }
    }

    @Test
    public void isAccurateCPE_match_name_fail_test(){
        Set<Result> result =  cpeTest.test("cpe", "cpe:2.3:a:python_software_foundation:noName:3.20.0:*:*:*:*:*:*:*");

        List<Result> resultList = new ArrayList<>(result);

        for(Result r : resultList){
            if(r.getTest().equals("AccurateCPE")){
                assertEquals(STATUS.FAIL, r.getStatus());
            }
            else{
                assertEquals(STATUS.PASS, r.getStatus());
            }
        }
    }

    @Test
    public void isAccurateCPE_match_version_fail_test(){
        Set<Result> result =  cpeTest.test("cpe", "cpe:2.3:a:python_software_foundation:python:3.20.0:*:*:*:*:*:*:*");

        List<Result> resultList = new ArrayList<>(result);

        for(Result r : resultList){
            if(r.getTest().equals("AccurateCPE")){
                assertEquals(STATUS.FAIL, r.getStatus());
            }
            else{
                assertEquals(STATUS.PASS, r.getStatus());
            }
        }
    }

    @Test
    public void isAccurateCPE_match_vendor_fail_test(){
        Set<Result> result =  cpeTest.test("cpe", "cpe:2.3:a:not_correct_vendor:python:3.20.0:*:*:*:*:*:*:*");

        List<Result> resultList = new ArrayList<>(result);

        for(Result r : resultList){
            if(r.getTest().equals("AccurateCPE")){
                assertEquals(STATUS.FAIL, r.getStatus());
            }
            else{
                assertEquals(STATUS.PASS, r.getStatus());
            }
        }
    }
}