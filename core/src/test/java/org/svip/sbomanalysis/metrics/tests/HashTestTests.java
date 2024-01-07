/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

package org.svip.sbomanalysis.metrics.tests;

import org.junit.jupiter.api.BeforeAll;
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

    private static HashTest hashTest;

    private static final String MD5_HASH_ALGORITHM = "MD5";
    private static final String MD5_HASH_VALUE = "743a64546ababa69c8af34e057722cd2";

    private static final String SHA1_HASH_ALGORITHM = "SHA1";
    private static final String NOT_SHA1_HASH_VALUE = "2f05477fc24bb4faefd86517156dafdecec45b8ad3cf2522a563582b";

    private static final String SPDX_EXCLUSIVE_HASH_ALGORITHM = "SHA224";

    private static final String PURL = "pkg:maven/org.junit.platform/junit-platform-engine@1.9.2?type=jar";


    @BeforeAll
    static void setup() {
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
