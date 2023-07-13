package org.svip.sbomanalysis.qualityattributes.newtests;

import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.ResultFactory;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;

import java.util.Collection;
import java.util.List;

/**
 * file: EmptyOrNullTest.java
 * File that tests if a field is empty or null
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
public class EmptyOrNullTest {

    private final String TEST_NAME = "EmptyOrNullTest";
    private ResultFactory resultFactory;

    private final List<ATTRIBUTE> attributes;

    public EmptyOrNullTest(ATTRIBUTE... attributes){
        this.attributes = List.of(attributes);
        resultFactory = new ResultFactory(TEST_NAME, attributes);
    }


    /**
     * Utility function that checks if an object is null, a string is blank or a collection is empty
     * @param field Object to test
     * @return a result if the field is empty/null or has a value
     */
    public Result test(String field, Object value, String context){
        Result r;
        boolean isEmptyorNull = false;

        // Check if value is null first
        if(value == null){
            r = resultFactory.fail(field, INFO.NULL, (String) null, context);
            return r;
        }
        // Check for empty string if value is instance of a string
        if(value instanceof String)
            isEmptyorNull = ((String) value).isEmpty();

        // Check for empty collection is value is instance of a collection
        if(value instanceof Collection<?>)
            isEmptyorNull = ((Collection<?>) value).isEmpty();

        // if value was a single string and was empty
        if(value instanceof String && isEmptyorNull)
            r = resultFactory.fail(field, INFO.MISSING, (String) value, context);

        // if value was a collection of Strings and was empty
        else if(value instanceof Collection<?> && isEmptyorNull){
            r = resultFactory.fail(field, INFO.MISSING, (Collection<String>) value, context);
        }
        // if value was a single string and had a value
        else if(!isEmptyorNull && value instanceof String ){
            r = resultFactory.pass(field, INFO.HAS, (String) value, context);
        }
        // if value was a collection of Strings and was not empty
        else{
            r = resultFactory.pass(field, INFO.HAS, (Collection<String>) value, context);
        }

        return r;

    }
}
