package org.svip.sbomanalysis.qualityattributes.newtests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.STATUS;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PURLTestTests {

    String nullPURL = null;

    String testRandomPURL = "pkg:random/test@2.0.0";

    String testActualPURL = "pkg:golang/rsc.io/sampler@v1.3.0";

    PURLTest purlTest;

    String test_name = "Python";


    @BeforeEach
    public void create_purlTest(){
        SPDX23PackageObject test_component = new SPDX23PackageObject(
                null, null, null, test_name, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null,
                null, null, null);

        purlTest = new PURLTest(test_component, ATTRIBUTE.SPDX23, ATTRIBUTE.UNIQUENESS);
    }

    @Test
    public void isValidPURL_assert_Error_status_test(){
       Set<Result> result =  purlTest.test("purl", nullPURL);

       List<Result> resultList = new ArrayList<Result>(result);
       Result r = resultList.get(0);


       List<ATTRIBUTE> test_attributes = new ArrayList<>(List.of(
               ATTRIBUTE.SPDX23, ATTRIBUTE.UNIQUENESS
       ));
       //TODO change details once implemented differently
       Result test_result = new Result(test_attributes, "PURLTest",
               "null is a missing purl", "TODO", STATUS.ERROR);

       assertEquals(test_result.getAttributes(), r.getAttributes());
       assertEquals(test_result.getTest(), r.getTest());
       assertEquals(test_result.getMessage(), r.getMessage());
       assertEquals(test_result.getDetails(), r.getDetails());
       assertEquals(test_result.getStatus(), r.getStatus());
    }

}