package org.svip.sbomanalysis.qualityattributes.pipelines.schemas.CycloneDX14;

import org.junit.jupiter.api.Test;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.STATUS;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
        Set<Result> result = cdx14Pipeline.hasBomVersion("Bom Version", null, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasBomVersion_empty_string_fail_test() {
        Set<Result> result = cdx14Pipeline.hasBomVersion("Bom Version", "", "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasBomVersion_string_pass_test() {
        Set<Result> result = cdx14Pipeline.hasBomVersion("Bom Version", testBomVersion, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void validSerialNumber_null_fail_test() {
        Set<Result> result = cdx14Pipeline.validSerialNumber("Serial Number", null, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void validSerialNumber_empty_string_fail_test() {
        Set<Result> result = cdx14Pipeline.validSerialNumber("Serial Number", "", "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void validSerialNumber_string_pass_test() {
        Set<Result> result = cdx14Pipeline.validSerialNumber("Serial Number", testUID, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void hasBomRef_null_fail_test() {
        Set<Result> result = cdx14Pipeline.hasBomRef("Bom Ref", null, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasBomRef_empty_string_fail_test() {
        Set<Result> result = cdx14Pipeline.hasBomRef("Bom Ref", "", "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasBomRef_string_pass_test() {
        Set<Result> result = cdx14Pipeline.hasBomRef("Bom Ref", testBomRef, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void supportedHash_unsupported_fail_test() {
        Set<Result> result = cdx14Pipeline.supportedHash("Hash Algorithm", testSPDXExclusiveHash, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void supportedHash_supported_pass_test() {
        Set<Result> result = cdx14Pipeline.supportedHash("Hash Algorithm", testSupportedHash, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }
}