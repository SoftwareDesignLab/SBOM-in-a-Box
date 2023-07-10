package org.svip.sbomanalysis.qualityattributes.newtests;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;

import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.ResultFactory;

import java.util.List;
import java.util.Set;

public class HashTest extends MetricTest{
    private final String TEST_NAME = "HashTest";
    private ResultFactory resultFactory;

    /**
     * Constructor to create a new MetricTest
     *
     * @param attributes the list of attributes used
     */
    public HashTest(List<ATTRIBUTE> attributes) {
        super(attributes);
    }

    @Override
    public Set<Result> test(String field, String value) {
        return null;
    }


    private Set<Result> isValidHash(Component component, List<ATTRIBUTE> attributes){

        return null;
    }
}
