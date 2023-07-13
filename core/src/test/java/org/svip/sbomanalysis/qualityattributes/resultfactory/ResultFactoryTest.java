package org.svip.sbomanalysis.qualityattributes.resultfactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResultFactoryTest {

    String testField = "Test Field";
    String testValue = "TestValue";
    Collection<String> testValueCol = new ArrayList<>(List.of(
            "TestValue1", "TestValue2"
    ));
    String testTestName = "Test Name";
    ATTRIBUTE attribute1 = ATTRIBUTE.COMPLETENESS;
    ATTRIBUTE attribute2 = ATTRIBUTE.LICENSING;

    List<ATTRIBUTE> testAttributeList = new ArrayList<>(List.of(
            attribute1, attribute2
    ));

    ResultFactory resultFactory;


    @BeforeEach
    public void new_ResultFactory(){
        resultFactory = new ResultFactory(testTestName, attribute1, attribute2);
    }



}