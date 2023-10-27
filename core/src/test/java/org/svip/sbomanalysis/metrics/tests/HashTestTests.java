package org.svip.sbomanalysis.metrics.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.metrics.tests.HashTest;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.factory.objects.SVIPSBOMComponentFactory;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbom.model.uids.Hash.Algorithm;
import org.svip.metrics.pipelines.schemas.CycloneDX14.CDX14Pipeline;
import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.enumerations.STATUS;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * file: HashTestTests.java
 * Test class to test HashTest and its methods and usage
 *
 * @author Matthew Morrison
 */
class HashTestTests {

    private static HashTest hashTest;

    private final String MD5_HASH_ALGORITHM = "MD5";
    private final String MD5_HASH_VALUE = "743a64546ababa69c8af34e057722cd2";

    private final String SHA1_HASH_ALGORITHM = "SHA1";
    private final String NOT_SHA1_HASH_VALUE = "2f05477fc24bb4faefd86517156dafdecec45b8ad3cf2522a563582b";

    private final String SPDX_EXCLUSIVE_HASH_ALGORITHM = "SHA224";

    private final String HASH_VALUE_LENGTH_32 = "5eb63bbbe01eeed093cb22bb8f5acdc3";

    private final String PURL = "pkg:maven/org.junit.platform/junit-platform-engine@1.9.2?type=jar";



    @BeforeEach
    public void beforeEach() {
        SVIPSBOMComponentFactory packageBuilderFactory = new SVIPSBOMComponentFactory();
        SVIPComponentBuilder packageBuilder = packageBuilderFactory.createBuilder();
        packageBuilder.addHash(MD5_HASH_ALGORITHM, MD5_HASH_VALUE);
        packageBuilder.addPURL(PURL);
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
    public void validHashResult_pass_test() {
        Set<Result> result =  hashTest.test(MD5_HASH_ALGORITHM, MD5_HASH_VALUE);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    public void validHashResult_Unknown_Algo_fail_test() {
        Set<Result> result =  hashTest.test("Unknown", MD5_HASH_VALUE);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    public void validHashResult_invalid_hash_fail_test() {
        Set<Result> result =  hashTest.test(SHA1_HASH_ALGORITHM, NOT_SHA1_HASH_VALUE);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    public void isValidHash_not_maven_purl_test() {
        SVIPSBOMComponentFactory packageBuilderFactory = new SVIPSBOMComponentFactory();
        SVIPComponentBuilder packageBuilder = packageBuilderFactory.createBuilder();
        packageBuilder.addPURL("");
        SVIPComponentObject component = packageBuilder.buildAndFlush();
        hashTest = new HashTest(component, ATTRIBUTE.UNIQUENESS);

        // FAIL cases
        Set<Result> result = hashTest.test(Algorithm.UNKNOWN.toString(), HASH_VALUE_LENGTH_32);
        assertEquals(STATUS.FAIL, result.iterator().next().getStatus());

        result =  hashTest.test(Algorithm.ADLER32.toString(), HASH_VALUE_LENGTH_32);
        assertEquals(STATUS.FAIL, result.iterator().next().getStatus());

        result =  hashTest.test(Algorithm.MD6.toString(),
                HASH_VALUE_LENGTH_32 + HASH_VALUE_LENGTH_32 + HASH_VALUE_LENGTH_32);
        assertEquals(STATUS.FAIL, result.iterator().next().getStatus());

        // PASS cases
        result =  hashTest.test(Algorithm.MD5.toString(), HASH_VALUE_LENGTH_32);
        assertEquals(STATUS.PASS, result.iterator().next().getStatus());

        result =  hashTest.test(Algorithm.MD6.toString(), HASH_VALUE_LENGTH_32);
        assertEquals(STATUS.PASS, result.iterator().next().getStatus());

        result =  hashTest.test(Algorithm.MD6.toString(), HASH_VALUE_LENGTH_32 + HASH_VALUE_LENGTH_32);
        assertEquals(STATUS.PASS, result.iterator().next().getStatus());

        result =  hashTest.test(Algorithm.MD6.toString(), HASH_VALUE_LENGTH_32 + HASH_VALUE_LENGTH_32
                + HASH_VALUE_LENGTH_32 + HASH_VALUE_LENGTH_32);
        assertEquals(STATUS.PASS, result.iterator().next().getStatus());

        // Test all remaining Hash Algorithms
        assertTrue(Arrays.stream(Algorithm.values()).anyMatch(algorithm -> {
            Set<Result> r = hashTest.test(algorithm.toString(), HASH_VALUE_LENGTH_32);
            return r.iterator().next().getStatus().equals(STATUS.PASS);
        }));
    }

    @Test
    public void isValidHash_invalid_purl_test() {
        SVIPSBOMComponentFactory packageBuilderFactory = new SVIPSBOMComponentFactory();
        SVIPComponentBuilder packageBuilder = packageBuilderFactory.createBuilder();
        packageBuilder.addPURL("pkg:maven");
        SVIPComponentObject component = packageBuilder.buildAndFlush();
        hashTest = new HashTest(component, ATTRIBUTE.UNIQUENESS);

        Set<Result> result = hashTest.test(MD5_HASH_ALGORITHM, MD5_HASH_VALUE);
        assertEquals(STATUS.FAIL, result.iterator().next().getStatus());
    }

    @Test
    public void supportedCDXHash_unsupported_fail_test() {
        CDX14Pipeline cdx14Pipeline = new CDX14Pipeline();
        Hash hash = new Hash(SPDX_EXCLUSIVE_HASH_ALGORITHM, "asdfasdfasdfasdfa");
        Result r = cdx14Pipeline.supportedHash("Hash Algorithm", hash, "Component");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    public void supportedCDXHash_supported_pass_test() {
        CDX14Pipeline cdx14Pipeline = new CDX14Pipeline();
        Hash hash = new Hash(MD5_HASH_ALGORITHM, "asdfasdfasdfasdfa");
        Result r = cdx14Pipeline.supportedHash("Hash Algorithm", hash, "Component");
        assertEquals(STATUS.PASS, r.getStatus());
    }
}