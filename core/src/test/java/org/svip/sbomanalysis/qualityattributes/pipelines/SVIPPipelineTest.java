package org.svip.sbomanalysis.qualityattributes.pipelines;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.STATUS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * file: SVIPPipelineTest.java
 * Test class to test SVIPPipeline and its methods and usage
 *
 * @author Matthew Morrison
 */
class SVIPPipelineTest {

    String testUID = "urn:uuid:1b53623d-b96b-4660-8d25-f84b7f617c54";
    String testBomRef = "pkg:maven/com.google.guava/guava@24.1.1-jre?type=jar";
    String testSupportedHash = "SHA1";
    String testSPDXExclusiveHash = "SHA224";
    String testBomVersion = "1.0";
    Set<String> testLicensesPass = new HashSet<>(List.of(
            "CC0-1.0"
    ));

    Set<String> testLicensesFail = new HashSet<>(List.of(
            "Not a Real License"
    ));

    String testSPDXID = "SPDXRef-1";

    String testDownloadLocation = "http://ftp.gnu.org/gnu/glibc/glibc-ports-2.15.tar.gz";

    CreationData testCreationData = new CreationData();

    String testVerificationCode = "1";

    SVIPPipeline svipPipeline = new SVIPPipeline();


    @Test
    void hasBomVersion_null_fail_test() {
        Set<Result> result = svipPipeline.hasBomVersion("Bom Version", null, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasBomVersion_empty_string_fail_test() {
        Set<Result> result = svipPipeline.hasBomVersion("Bom Version", "", "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasBomVersion_string_pass_test() {
        Set<Result> result = svipPipeline.hasBomVersion("Bom Version", testBomVersion, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void validSerialNumber_null_fail_test() {
        Set<Result> result = svipPipeline.validSerialNumber("Serial Number", null, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void validSerialNumber_empty_string_fail_test() {
        Set<Result> result = svipPipeline.validSerialNumber("Serial Number", "", "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void validSerialNumber_string_pass_test() {
        Set<Result> result = svipPipeline.validSerialNumber("Serial Number", testUID, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void hasBomRef_null_fail_test() {
        Set<Result> result = svipPipeline.hasBomRef("Bom Ref", null, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasBomRef_empty_string_fail_test() {
        Set<Result> result = svipPipeline.hasBomRef("Bom Ref", "", "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasBomRef_string_pass_test() {
        Set<Result> result = svipPipeline.hasBomRef("Bom Ref", testBomRef, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void supportedHash_unsupported_fail_test() {
        Set<Result> result = svipPipeline.supportedHash("Hash Algorithm", testSPDXExclusiveHash, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void supportedHash_supported_pass_test() {
        Set<Result> result = svipPipeline.supportedHash("Hash Algorithm", testSupportedHash, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void hasDataLicense_null_fail_test() {
        Set<Result> result = svipPipeline.hasDataLicense("Data License", null, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasDataLicense_incorrect_license_fail_test() {
        Set<Result> result = svipPipeline.hasDataLicense("Data License", testLicensesFail, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasDataLicense_correct_license_pass_test() {
        Set<Result> result = svipPipeline.hasDataLicense("Data License", testLicensesPass, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void hasSPDXID_null_fail_test() {
        Set<Result> result = svipPipeline.hasSPDXID("SPDXID", null, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasSPDXID_empty_string_fail_test() {
        Set<Result> result = svipPipeline.hasSPDXID("SPDXID", "", "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasSPDXID_string_pass_test() {
        Set<Result> result = svipPipeline.hasSPDXID("SPDXID", testSPDXID, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void hasCreationInfo_null_fail_test() {
        Set<Result> result = svipPipeline.hasCreationInfo("Creation Data", null, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.ERROR, r.getStatus());
    }

    @Test
    void hasCreationInfo_null_manufacture_fail_test() {
        testCreationData.setManufacture(null);
        Set<Result> result = svipPipeline.hasCreationInfo("Creation Data", testCreationData, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasCreationInfo_empty_string_manufacture_fail_test() {
        Organization manufacture = new Organization("", "");
        testCreationData.setManufacture(manufacture);
        Set<Result> result = svipPipeline.hasCreationInfo("Creation Data", testCreationData, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasCreationInfo_valid_manufacture_pass_test() {
        Organization manufacture = new Organization("Organization", "www.organization.com");
        testCreationData.setManufacture(manufacture);
        Set<Result> result = svipPipeline.hasCreationInfo("Creation Data", testCreationData, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void hasCreationInfo_valid_manufacturer_creation_time_pass_test() {
        Organization manufacture = new Organization("Organization", "www.organization.com");
        testCreationData.setManufacture(manufacture);
        testCreationData.setCreationTime("2010-01-29T18:30:22Z");
        Set<Result> result = svipPipeline.hasCreationInfo("Creation Data", testCreationData, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);
        assertEquals(STATUS.PASS, r.getStatus());

        r = resultList.get(1);
        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void hasCreationInfo_invalid_creation_time_fail_test() {
        Organization manufacture = new Organization("Organization", "www.organization.com");
        testCreationData.setManufacture(manufacture);
        testCreationData.setCreationTime("");
        Set<Result> result = svipPipeline.hasCreationInfo("Creation Data", testCreationData, "SBOM");

        List<Result> resultList = new ArrayList<>(result);

        Result r = resultList.get(1);
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasDownloadLocation_null_fail_test() {
        Set<Result> result = svipPipeline.hasDownloadLocation(
                "Download Location", null, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasDownloadLocation_empty_string_fail_test() {
        Set<Result> result = svipPipeline.hasDownloadLocation(
                "Download Location", "", "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasDownloadLocation_string_pass_test() {
        Set<Result> result = svipPipeline.hasDownloadLocation(
                "Download Location", testDownloadLocation, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void hasVerificationCode_filesAnalyzed_fail_test() {
        Set<Result> result = svipPipeline.hasVerificationCode(
                "Download Location", null, true, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasVerificationCode_filesAnalyzed_pass_test() {
        Set<Result> result = svipPipeline.hasVerificationCode(
                "Download Location", testVerificationCode, true, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void hasVerificationCode_filesAnalyzed_false_fail_test() {
        Set<Result> result = svipPipeline.hasVerificationCode(
                "Download Location", testVerificationCode, false, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasVerificationCode_filesAnalyzed_false_pass_test() {
        Set<Result> result = svipPipeline.hasVerificationCode(
                "Download Location", null, false, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }
}