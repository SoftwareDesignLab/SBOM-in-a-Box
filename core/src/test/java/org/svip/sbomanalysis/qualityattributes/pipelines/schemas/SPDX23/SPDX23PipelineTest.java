package org.svip.sbomanalysis.qualityattributes.pipelines.schemas.SPDX23;

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
 * file: SPDX23PipelineTest.java
 * Test class to test SPDX23Pipeline and its methods and usage
 *
 * @author Matthew Morrison
 */
class SPDX23PipelineTest {

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

    SPDX23Pipeline spdx23Pipeline = new SPDX23Pipeline();


    @Test
    void hasBomVersion_null_fail_test() {
        Set<Result> result = spdx23Pipeline.hasBomVersion("Bom Version", null, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasBomVersion_empty_string_fail_test() {
        Set<Result> result = spdx23Pipeline.hasBomVersion("Bom Version", "", "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasBomVersion_string_pass_test() {
        Set<Result> result = spdx23Pipeline.hasBomVersion("Bom Version", testBomVersion, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }


    @Test
    void hasDataLicense_null_fail_test() {
        Set<Result> result = spdx23Pipeline.hasDataLicense("Data License", null, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasDataLicense_incorrect_license_fail_test() {
        Set<Result> result = spdx23Pipeline.hasDataLicense("Data License", testLicensesFail, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasDataLicense_correct_license_pass_test() {
        Set<Result> result = spdx23Pipeline.hasDataLicense("Data License", testLicensesPass, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void hasSPDXID_null_fail_test() {
        Set<Result> result = spdx23Pipeline.hasSPDXID("SPDXID", null, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasSPDXID_empty_string_fail_test() {
        Set<Result> result = spdx23Pipeline.hasSPDXID("SPDXID", "", "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasSPDXID_string_pass_test() {
        Set<Result> result = spdx23Pipeline.hasSPDXID("SPDXID", testSPDXID, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void hasCreationInfo_null_fail_test() {
        Set<Result> result = spdx23Pipeline.hasCreationInfo("Creation Data", null, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.ERROR, r.getStatus());
    }

    @Test
    void hasCreationInfo_null_manufacture_fail_test() {
        testCreationData.setManufacture(null);
        Set<Result> result = spdx23Pipeline.hasCreationInfo("Creation Data", testCreationData, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasCreationInfo_empty_string_manufacture_fail_test() {
        Organization manufacture = new Organization("", "");
        testCreationData.setManufacture(manufacture);
        Set<Result> result = spdx23Pipeline.hasCreationInfo("Creation Data", testCreationData, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasCreationInfo_valid_manufacture_pass_test() {
        Organization manufacture = new Organization("Organization", "www.organization.com");
        testCreationData.setManufacture(manufacture);
        Set<Result> result = spdx23Pipeline.hasCreationInfo("Creation Data", testCreationData, "SBOM");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void hasCreationInfo_valid_manufacturer_creation_time_pass_test() {
        Organization manufacture = new Organization("Organization", "www.organization.com");
        testCreationData.setManufacture(manufacture);
        testCreationData.setCreationTime("2010-01-29T18:30:22Z");
        Set<Result> result = spdx23Pipeline.hasCreationInfo("Creation Data", testCreationData, "SBOM");

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
        Set<Result> result = spdx23Pipeline.hasCreationInfo("Creation Data", testCreationData, "SBOM");

        List<Result> resultList = new ArrayList<>(result);

        Result r = resultList.get(1);
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasDownloadLocation_null_fail_test() {
        Set<Result> result = spdx23Pipeline.hasDownloadLocation(
                "Download Location", null, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasDownloadLocation_empty_string_fail_test() {
        Set<Result> result = spdx23Pipeline.hasDownloadLocation(
                "Download Location", "", "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasDownloadLocation_string_pass_test() {
        Set<Result> result = spdx23Pipeline.hasDownloadLocation(
                "Download Location", testDownloadLocation, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void hasVerificationCode_filesAnalyzed_fail_test() {
        Set<Result> result = spdx23Pipeline.hasVerificationCode(
                "Download Location", null, true, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasVerificationCode_filesAnalyzed_pass_test() {
        Set<Result> result = spdx23Pipeline.hasVerificationCode(
                "Download Location", testVerificationCode, true, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void hasVerificationCode_filesAnalyzed_false_fail_test() {
        Set<Result> result = spdx23Pipeline.hasVerificationCode(
                "Download Location", testVerificationCode, false, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasVerificationCode_filesAnalyzed_false_pass_test() {
        Set<Result> result = spdx23Pipeline.hasVerificationCode(
                "Download Location", null, false, "Component");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

}