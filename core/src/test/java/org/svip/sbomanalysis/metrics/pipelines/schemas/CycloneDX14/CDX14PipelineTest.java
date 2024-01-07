/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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
 */

package org.svip.sbomanalysis.metrics.pipelines.schemas.CycloneDX14;

import org.junit.jupiter.api.Test;
import org.svip.metrics.pipelines.schemas.CycloneDX14.CDX14Pipeline;
import org.svip.sbom.model.uids.Hash;
import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.enumerations.STATUS;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: CDX14PipelineTest.java
 * Test class to test CDX14Pipeline and its methods and usage
 *
 * @author Matthew Morrison
 */
class CDX14PipelineTest {

    String testBomVersion = "1.0";
    String testUID = "urn:uuid:1b53623d-b96b-4660-8d25-f84b7f617c54";
    String testBomRef = "pkg:maven/com.google.guava/guava@24.1.1-jre?type=jar";
    String testSupportedHash = "SHA1";
    String testSPDXExclusiveHash = "SHA224";


    CDX14Pipeline cdx14Pipeline = new CDX14Pipeline();

    @Test
    void hasBomVersion_null_fail_test() {
        Result r = cdx14Pipeline.hasBomVersion("Bom Version", null, "SBOM");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasBomVersion_empty_string_fail_test() {
        Result r = cdx14Pipeline.hasBomVersion("Bom Version", "", "SBOM");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasBomVersion_string_pass_test() {
        Result r = cdx14Pipeline.hasBomVersion("Bom Version", testBomVersion, "SBOM");
        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void validSerialNumber_null_fail_test() {
        Result r = cdx14Pipeline.validSerialNumber("Serial Number", null, "SBOM");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void validSerialNumber_empty_string_fail_test() {
        Result r = cdx14Pipeline.validSerialNumber("Serial Number", "", "SBOM");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void validSerialNumber_string_pass_test() {
        Result r = cdx14Pipeline.validSerialNumber("Serial Number", testUID, "SBOM");
        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void hasBomRef_null_fail_test() {
        Result r = cdx14Pipeline.hasBomRef("Bom Ref", null, "Component");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasBomRef_empty_string_fail_test() {
        Result r = cdx14Pipeline.hasBomRef("Bom Ref", "", "Component");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasBomRef_string_pass_test() {
        Result r = cdx14Pipeline.hasBomRef("Bom Ref", testBomRef, "Component");
        assertEquals(STATUS.PASS, r.getStatus());
    }


    @Test
    void supportedHash_unsupported_fail_test() {
        Hash hash = new Hash(testSPDXExclusiveHash, "asdfasdfasdfasdfa");
        Result r = cdx14Pipeline.supportedHash("Hash Algorithm", hash, "Component");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void supportedHash_supported_pass_test() {
        Hash hash = new Hash(testSupportedHash, "asdfasdfasdfasdfa");
        Result r = cdx14Pipeline.supportedHash("Hash Algorithm", hash, "Component");
        assertEquals(STATUS.PASS, r.getStatus());
    }
}