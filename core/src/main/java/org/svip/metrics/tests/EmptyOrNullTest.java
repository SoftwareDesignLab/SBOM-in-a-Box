package org.svip.metrics.tests;

import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.ResultFactory;
import org.svip.metrics.resultfactory.enumerations.INFO;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;

import java.util.Collection;

/**
 * file: EmptyOrNullTest.java
 * File that tests if a field is empty or null
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
public class EmptyOrNullTest {

    private final ResultFactory resultFactory;


    /**
     * Constructor class to create a new EmptyOrNullTest
     *
     * @param attributes a list of attributes associates with the test
     */
    public EmptyOrNullTest(ATTRIBUTE... attributes) {
        String TEST_NAME = "EmptyOrNullTest";
        resultFactory = new ResultFactory(TEST_NAME, attributes);
    }


    /**
     * Utility function that checks if an object is null, a string is blank or a collection is empty
     *
     * @param field Object to test
     * @return a result if the field is empty/null or has a value
     */
    public Result test(String field, Object value, String context) {
        // Check if value is null first
        if (value == null) {
            return resultFactory.fail(field, INFO.NULL,
                    (String) null, context);
        }

        // value is a string
        // Check for empty string if value is instance of a string
        else if (value instanceof String) {
            if (((String) value).isEmpty()) {
                return resultFactory.fail(field, INFO.MISSING,
                        (String) value, context);
            } else {
                return resultFactory.pass(field, INFO.HAS,
                        (String) value, context);
            }
        }

        // value is a collection of value
        // Check for empty collection is value is instance of a collection
        else {
            // if value is an empty collection, test fails
            if (((Collection<?>) value).isEmpty()) {
                return resultFactory.fail(field, INFO.MISSING,
                        (Collection<String>) value, context);
            }
            // collection is not empty, test passes
            else {
                return resultFactory.pass(field, INFO.HAS,
                        (Collection<String>) value, context);
            }
        }
    }
}
