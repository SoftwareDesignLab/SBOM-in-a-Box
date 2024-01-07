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
import org.svip.metrics.tests.EmptyOrNullTest;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: EmptyOrNullTestTests.java
 * Test class to test EmptyOrNullTest and its methods and usage
 *
 * @author Matthew Morrison
 */
class EmptyOrNullTestTest {

    Collection<String> testEmptyCol = new ArrayList<>();
    Collection<String> testActualCol = new ArrayList<>(List.of(
            "String1", "String2"
    ));

    EmptyOrNullTest emptyOrNullTest;

    @BeforeEach
    public void create_EmptyOrNullTest(){
        emptyOrNullTest = new EmptyOrNullTest(ATTRIBUTE.COMPLETENESS);
    }

    @Test
    public void test_null_string_fail_test(){
        Result result = emptyOrNullTest.test("String", null, "field");
        assertEquals(STATUS.FAIL, result.getStatus());
    }

    @Test
    public void test_empty_string_fail_test(){
        Result result = emptyOrNullTest.test("String", "", "field");
        assertEquals(STATUS.FAIL, result.getStatus());
    }

    @Test
    public void test_string_pass_test(){
        Result result = emptyOrNullTest.test("String", "AnActualString", "field");
        assertEquals(STATUS.PASS, result.getStatus());
    }

    @Test
    public void test_null_collection_fail_test(){
        Result result = emptyOrNullTest.test("collection", null, "field");
        assertEquals(STATUS.FAIL, result.getStatus());
    }

    @Test
    public void test_empty_collection_fail_test(){
        Result result = emptyOrNullTest.test("collection", testEmptyCol, "field");
        assertEquals(STATUS.FAIL, result.getStatus());
    }

    @Test
    public void test_collection_pass_test(){
        Result result = emptyOrNullTest.test("collection", testActualCol, "field");
        assertEquals(STATUS.PASS, result.getStatus());
    }
}