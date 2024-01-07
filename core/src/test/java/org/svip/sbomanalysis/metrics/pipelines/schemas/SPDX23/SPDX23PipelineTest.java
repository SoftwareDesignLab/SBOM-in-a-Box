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

package org.svip.sbomanalysis.metrics.pipelines.schemas.SPDX23;

import org.junit.jupiter.api.Test;
import org.svip.metrics.pipelines.schemas.SPDX23.SPDX23Pipeline;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.enumerations.STATUS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Result r = spdx23Pipeline.hasBomVersion("Bom Version", null, "SBOM");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasBomVersion_empty_string_fail_test() {
        Result r = spdx23Pipeline.hasBomVersion("Bom Version", "", "SBOM");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasBomVersion_string_pass_test() {
        Result r = spdx23Pipeline.hasBomVersion("Bom Version", testBomVersion, "SBOM");
        assertEquals(STATUS.PASS, r.getStatus());
    }


    @Test
    void hasDataLicense_null_fail_test() {
        Result r = spdx23Pipeline.hasDataLicense("Data License", null, "SBOM");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasDataLicense_incorrect_license_fail_test() {
        Result r = spdx23Pipeline.hasDataLicense("Data License", testLicensesFail, "SBOM");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasDataLicense_correct_license_pass_test() {
        Result r = spdx23Pipeline.hasDataLicense("Data License", testLicensesPass, "SBOM");
        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void hasSPDXID_null_fail_test() {
        Result r = spdx23Pipeline.hasSPDXID("SPDXID", null, "Component");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasSPDXID_empty_string_fail_test() {
        Result r = spdx23Pipeline.hasSPDXID("SPDXID", "", "Component");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasSPDXID_string_pass_test() {
        Result r = spdx23Pipeline.hasSPDXID("SPDXID", testSPDXID, "Component");
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
    void hasCreationInfo_empty_string_manufacture_fail_time_pass_test() {
        Organization manufacture = new Organization("", "");
        testCreationData.setManufacture(manufacture);
        testCreationData.setCreationTime("2010-01-29T18:30:22Z");
        Set<Result> result = spdx23Pipeline.hasCreationInfo("Creation Data", testCreationData, "SBOM");

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

        assertEquals(1, pass);
        assertEquals(1, fail);
    }

    @Test
    void hasCreationInfo_empty_string_manufacture_fail_time_fail_test() {
        Organization manufacture = new Organization("", "");
        testCreationData.setManufacture(manufacture);
        testCreationData.setCreationTime("");
        Set<Result> result = spdx23Pipeline.hasCreationInfo("Creation Data", testCreationData, "SBOM");

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

        assertEquals(0, pass);
        assertEquals(2, fail);
    }

    @Test
    void hasCreationInfo_valid_manufacturer_creation_time_pass_test() {
        Organization manufacture = new Organization("Organization", "www.organization.com");
        testCreationData.setManufacture(manufacture);
        testCreationData.setCreationTime("2010-01-29T18:30:22Z");
        Set<Result> result = spdx23Pipeline.hasCreationInfo("Creation Data", testCreationData, "SBOM");

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

        assertEquals(2, pass);
        assertEquals(0, fail);
    }

    @Test
    void hasCreationInfo_invalid_creation_time_fail_test() {
        Organization manufacture = new Organization("Organization", "www.organization.com");
        testCreationData.setManufacture(manufacture);
        testCreationData.setCreationTime("");
        Set<Result> result = spdx23Pipeline.hasCreationInfo("Creation Data", testCreationData, "SBOM");

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

        assertEquals(1, pass);
        assertEquals(1, fail);
    }

    @Test
    void hasDownloadLocation_null_fail_test() {
        Result r = spdx23Pipeline.hasDownloadLocation(
                "Download Location", null, "Component");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasDownloadLocation_empty_string_fail_test() {
        Result r = spdx23Pipeline.hasDownloadLocation(
                "Download Location", "", "Component");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasDownloadLocation_string_pass_test() {
        Result r = spdx23Pipeline.hasDownloadLocation(
                "Download Location", testDownloadLocation, "Component");
        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void hasVerificationCode_filesAnalyzed_fail_test() {
        Result r = spdx23Pipeline.hasVerificationCode(
                "Download Location", null, true, "Component");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasVerificationCode_filesAnalyzed_pass_test() {
        Result r = spdx23Pipeline.hasVerificationCode(
                "Download Location", testVerificationCode, true, "Component");
        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    void hasVerificationCode_filesAnalyzed_false_fail_test() {
        Result r = spdx23Pipeline.hasVerificationCode(
                "Download Location", testVerificationCode, false, "Component");
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    void hasVerificationCode_filesAnalyzed_false_pass_test() {
        Result r = spdx23Pipeline.hasVerificationCode(
                "Download Location", null, false, "Component");
        assertEquals(STATUS.PASS, r.getStatus());
    }

}