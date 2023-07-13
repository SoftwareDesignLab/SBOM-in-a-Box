package org.svip.sbomanalysis.qualityattributes.newtests;

import org.junit.jupiter.api.*;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.STATUS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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