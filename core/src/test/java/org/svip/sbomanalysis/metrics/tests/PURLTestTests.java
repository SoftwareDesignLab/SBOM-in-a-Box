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

package org.svip.sbomanalysis.metrics.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.metrics.tests.PURLTest;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.enumerations.STATUS;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: PURLTestTests.java
 * Test class to test PURLTest and its methods and usage
 * @author Kevin Laporte
 */
class PURLTestTests {

    String testRandomPURL = "pkg:random/test@2.0.0";

    String testActualPURL = "pkg:golang/rsc.io/sampler@v1.3.0";

    PURLTest purlTest;



    @BeforeEach
    public void create_purlTest(){
        SPDX23PackageObject test_component = new SPDX23PackageObject(
                null, null, null, "sampler", null, null,
                null, null, "v1.3.0", null, null, null,
                null, null, null, null, null, null, null, null, null,
                null, null, null);

        purlTest = new PURLTest(test_component, ATTRIBUTE.UNIQUENESS);
    }

    @Test
    public void test_error_test(){
        Set<Result> result =  purlTest.test("purl", null);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.ERROR, r.getStatus());
    }

    // TODO test with coverage fails, debug passes? results are being added to the set differently in each case
    // TODO refactor lots of repeat code
    @Test
    public void validPURLResult_pass_test(){
        Set<Result> result =  purlTest.test("purl", testActualPURL);

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.PASS, r.getStatus());
    }

    @Test
    public void validPURLResult_fail_test(){
        Set<Result> result =  purlTest.test("purl", "purl");

        List<Result> resultList = new ArrayList<>(result);
        Result r = resultList.get(0);

        assertEquals(STATUS.FAIL, r.getStatus());
    }

    @Test
    public void validPURLResult_isAccuratePURL_pass_test(){
        Set<Result> result =  purlTest.test("purl", testActualPURL);

        List<Result> resultList = new ArrayList<>(result);
        for(Result r : resultList){
            assertEquals(STATUS.PASS, r.getStatus());
        }
    }

    @Test
    public void accuratePURLResults_match_getName_fail_test(){
        Set<Result> result =  purlTest.test("purl", "pkg:golang/rsc.io/incorrectName@v1.3.0");

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
        assertEquals(1, fail);
    }

    @Test
    public void accuratePURLResults_match_getVersion_fail_test(){
        Set<Result> result =  purlTest.test("purl", "pkg:golang/rsc.io/sampler@v0.0.0");

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
        assertEquals(1, fail);
    }
}