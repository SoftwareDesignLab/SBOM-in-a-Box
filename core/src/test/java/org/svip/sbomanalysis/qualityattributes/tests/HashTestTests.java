package org.svip.sbomanalysis.qualityattributes.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.sbomanalysis.qualityattributes.pipelines.schemas.CycloneDX14.CDX14Pipeline;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.STATUS;
import org.svip.sbomanalysis.qualityattributes.tests.enumerations.ATTRIBUTE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: HashTestTests.java
 * Test class to test HashTest and its methods and usage
 *
 * @author Matthew Morrison
 */
class HashTestTests {

    String testHashAlgo1 = "MD5";
    String testMD5Hash = "5eb63bbbe01eeed093cb22bb8f5acdc3";

    String testHashAlgo2 = "SHA1";
    String testNotSHA1Hash = "2f05477fc24bb4faefd86517156dafdecec45b8ad3cf2522a563582b";

    String testSPDXExclusiveHash = "SHA224";

    HashTest hashTest;

    @BeforeEach
    public void create_HashTest(){
        hashTest = new HashTest("Component", ATTRIBUTE.UNIQUENESS);
    }

    @Test
    public void test_null_error_test(){
        Set<Result> result = hashTest.test("hash", null);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.ERROR, r.getStatus());
    }

    @Test
    public void isValidHash_pass_test(){
        Set<Result> result =  hashTest.test(testHashAlgo1, testMD5Hash);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    public void isValidHash_Unknown_Algo_fail_test(){
        Set<Result> result =  hashTest.test("Unknown", testMD5Hash);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    public void isValidHash_invalid_hash_fail_test(){
        Set<Result> result =  hashTest.test(testHashAlgo2, testNotSHA1Hash);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    public void supportedCDXHash_fail_test(){
        CDX14Pipeline cdx14Pipeline = new CDX14Pipeline();
        Result r = cdx14Pipeline.supportedHash("Supported Hash", testSPDXExclusiveHash, "Component");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    public void supportedCDXHash_pass_test(){
        CDX14Pipeline cdx14Pipeline = new CDX14Pipeline();
        Result r = cdx14Pipeline.supportedHash("Supported Hash", testHashAlgo1, "Component");
        assertEquals(STATUS.PASS, r.getStatus());
    }

}