package org.svip.sbomanalysis.metrics.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.enumerations.STATUS;
import org.svip.metrics.tests.LicenseTest;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: LicenseTestTests.java
 * Test class to test LicenseTest and its methods and usage
 *
 * @author Matthew Morrison
 */
class LicenseTestTests {

    LicenseTest licenseTest;



    String testValidLicenseID = "Apache-2.0";
    String testValidLicenseName = "Apache License 2.0";

    String testDeprecatedLicenseID = "AGPL-1.0";
    String testDeprecatedLicenseName = "Affero General Public License v1.0";


    @BeforeEach
    public void create_LicenseTest(){
        licenseTest = new LicenseTest("Component", ATTRIBUTE.LICENSING, ATTRIBUTE.COMPLETENESS);
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
    public void testSPDXLicense_deprecated_license_id_fail_test(){
        Set<Result> result = licenseTest.test("license", testDeprecatedLicenseID);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);
        System.out.println(r.getDetails());
        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    public void testSPDXLicense_deprecated_license_name_fail_test(){
        Set<Result> result = licenseTest.test("license", testDeprecatedLicenseName);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);
        System.out.println(r.getDetails());
        assertEquals(STATUS.FAIL, r.getStatus());
    }


}