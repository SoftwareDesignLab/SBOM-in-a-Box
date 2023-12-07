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

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    public void testSPDXLicense_deprecated_license_name_fail_test(){
        Set<Result> result = licenseTest.test("license", testDeprecatedLicenseName);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }


}