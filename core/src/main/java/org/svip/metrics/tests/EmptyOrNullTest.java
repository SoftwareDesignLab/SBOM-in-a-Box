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
