package org.svip.sbomanalysis.metrics.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.metrics.tests.HashTest;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.factory.objects.SVIPSBOMComponentFactory;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.uids.Hash;
import org.svip.metrics.pipelines.schemas.CycloneDX14.CDX14Pipeline;
import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.enumerations.STATUS;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;

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

    private final String testHashAlgo1 = "MD5";
    private final String testMD5Hash = "743a64546ababa69c8af34e057722cd2";

    private final String testHashAlgo2 = "SHA1";
    private final String testNotSHA1Hash = "2f05477fc24bb4faefd86517156dafdecec45b8ad3cf2522a563582b";

    private final String testSPDXExclusiveHash = "SHA224";

    private static HashTest hashTest;
    private static SVIPSBOMComponentFactory packageBuilderFactory;
    private static SVIPComponentBuilder packageBuilder;

    @BeforeAll
    static void setup() {
        packageBuilderFactory = new SVIPSBOMComponentFactory();
        packageBuilder = packageBuilderFactory.createBuilder();
        packageBuilder.addPURL("pkg:maven/org.junit.platform/junit-platform-engine@1.9.2?type=jar");
        SVIPComponentObject component = packageBuilder.buildAndFlush();
        hashTest = new HashTest(component, ATTRIBUTE.UNIQUENESS);
    }

    @Test
    public void test_null_error_test() {
        Set<Result> result = hashTest.test("hash", null);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.ERROR, r.getStatus());
    }

    @Test
    public void isValidHash_pass_test() {
        Set<Result> result =  hashTest.test(testHashAlgo1, testMD5Hash);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    public void isValidHash_Unknown_Algo_fail_test() {
        Set<Result> result =  hashTest.test("Unknown", testMD5Hash);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    public void isValidHash_invalid_hash_fail_test() {
        Set<Result> result =  hashTest.test(testHashAlgo2, testNotSHA1Hash);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void supportedCDXHash_unsupported_fail_test() {
        CDX14Pipeline cdx14Pipeline = new CDX14Pipeline();
        Hash hash = new Hash(testSPDXExclusiveHash, "asdfasdfasdfasdfa");
        Result r = cdx14Pipeline.supportedHash("Hash Algorithm", hash, "Component");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void supportedCDXHash_supported_pass_test() {
        CDX14Pipeline cdx14Pipeline = new CDX14Pipeline();
        Hash hash = new Hash(testHashAlgo1, "asdfasdfasdfasdfa");
        Result r = cdx14Pipeline.supportedHash("Hash Algorithm", hash, "Component");
        assertEquals(STATUS.PASS, r.getStatus());
    }

}