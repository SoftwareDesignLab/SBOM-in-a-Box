package org.svip.sbomanalysis.qualityattributes.newtests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.STATUS;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LicenseTestTests {

    LicenseTest licenseTest;



    String testValidLicenseID = "Apache-2.0";
    String testValidLicenseName = "Apache License 2.0";

    String testDeprecatedLicenseID = "AGPL-1.0";
    String testDeprecatedLicenseName = "Affero General Public License v1.0";


    @BeforeEach
    public void create_LicenseTest(){
        licenseTest = new LicenseTest(ATTRIBUTE.LICENSING, ATTRIBUTE.COMPLETENESS);
    }


    @Test
    public void test_null_error_test(){
        Set<Result> result = licenseTest.test("license", null);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.ERROR, r.getStatus());
    }

    @Test
    public void test_empty_string_error_test(){
        Set<Result> result = licenseTest.test("license", "");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.ERROR, r.getStatus());
    }

    @Test
    public void testSPDXLicense_valid_license_id_pass_test(){
        Set<Result> result = licenseTest.test("license", testValidLicenseID);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    public void testSPDXLicense_valid_license_name_pass_test(){
        Set<Result> result = licenseTest.test("license", testValidLicenseName);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    public void testSPDXLicense_deprecated_license_id_pass_test(){
        Set<Result> result = licenseTest.test("license", testDeprecatedLicenseID);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    public void testSPDXLicense_deprecated_license_name_pass_test(){
        Set<Result> result = licenseTest.test("license", testDeprecatedLicenseName);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }


}