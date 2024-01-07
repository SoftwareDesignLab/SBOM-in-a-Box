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
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.uids.Hash;

import java.util.HashSet;
import java.util.Set;

/**
 * file: HashTest.java
 * Series of tests for hash string and hash objects
 *
 * @author Matthew Morrison
 */
public class HashTest extends MetricTest {
    private final ResultFactory resultFactory;
    private final Component component;


    /**
     * Constructor to create a new MetricTest
     *
     * @param attributes the list of attributes used
     */
    public HashTest(Component component, ATTRIBUTE... attributes) {
        super(attributes);
        String TEST_NAME = "HashTest";
        resultFactory = new ResultFactory(TEST_NAME, attributes);
        this.component = component;
    }

    /**
     * Test a single hash if it is valid
     *
     * @param field the hash algorithm to test
     * @param value the hash value to test
     * @return a Result if the hash data is valid or not
     */
    @Override
    public Set<Result> test(String field, String value) {
        Set<Result> results = new HashSet<>();
        // hash  is not a null value and does exist, tests can run
        if (value != null && field != null) {
            results.add(validHashResult(field, value));
        }
        // Hash has a null algo or value, tests cannot be run
        // return missing Result
        else {
            Result r = resultFactory.error(field, INFO.NULL, value, component.getName());
            results.add(r);
        }
        return results;
    }


    /**
     * Test the hash if it is a valid schema and type
     *
     * @param field the hash algorithm
     * @param value the hash value
     * @return a Result if the hash is valid or not
     */
    private Result validHashResult(String field, String value) {
        ResultFactory rf = new ResultFactory("Valid Hash", ATTRIBUTE.COMPLETENESS, ATTRIBUTE.UNIQUENESS,
                ATTRIBUTE.MINIMUM_ELEMENTS);
        // create new hash object
        Hash hash = new Hash(field, value);

        // Check if hash algorithm is unknown
        if (hash.getAlgorithm().equals(Hash.Algorithm.UNKNOWN))
            return rf.fail(field, INFO.INVALID, value, component.getName());

        // Check if hash is valid
        if (!hash.isValid(component))
            return rf.fail(field, INFO.INVALID, value, component.getName());
        else
            return rf.pass(field, INFO.VALID, value, component.getName());
    }
}
