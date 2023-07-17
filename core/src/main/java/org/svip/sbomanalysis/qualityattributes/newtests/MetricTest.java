package org.svip.sbomanalysis.qualityattributes.newtests;

import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;

import java.util.List;
import java.util.Set;


/**
 * file: MetricTest.java
 * New template for MetricTests
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
public abstract class MetricTest {

    /**The list of attributes used for the Metric Tests*/
    List<ATTRIBUTE> attributes;

    /**
     * Constructor to create a new MetricTest
     * @param attributes the list of attributes used
     */
    public MetricTest(ATTRIBUTE... attributes){
        this.attributes = List.of(attributes);
    }


    /**
     * Test the given SBOM
     *
     * @param field the field being tested
     * @param value the value being tested
     * @return Collection of Results
     */
    public abstract Set<Result> test(String field, String value);
}
