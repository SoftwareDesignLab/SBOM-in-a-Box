package org.svip.metrics.tests;

import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.ResultFactory;
import org.svip.metrics.resultfactory.enumerations.INFO;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;
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
    private final String componentName;


    /**
     * Constructor to create a new MetricTest
     *
     * @param attributes the list of attributes used
     */
    public HashTest(String componentName, ATTRIBUTE... attributes) {
        super(attributes);
        String TEST_NAME = "HashTest";
        resultFactory = new ResultFactory(TEST_NAME, attributes);
        this.componentName = componentName;
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
            results.add(isValidHash(field, value));

        }
        // Hash has a null algo or value, tests cannot be run
        // return missing Result
        else {
            Result r = resultFactory.error(field, INFO.NULL,
                    value, componentName);
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
    private Result isValidHash(String field, String value) {
        var rf = new ResultFactory("Valid Hash", ATTRIBUTE.COMPLETENESS, ATTRIBUTE.UNIQUENESS, ATTRIBUTE.MINIMUM_ELEMENTS);
        try {
            // create new hash object
            Hash hash = new Hash(field, value);

            // Check if hash algorithm is unknown
            if (hash.getAlgorithm() == Hash.Algorithm.UNKNOWN) {
                return rf.fail(field, INFO.INVALID,
                        value, componentName);
            }

            // Check if hash is valid
            if (!Hash.validateHash(hash.getAlgorithm(), hash.getValue())) {
                return rf.fail(field, INFO.INVALID,
                        value, componentName);
            } else {
                return rf.pass(field, INFO.VALID,
                        value, componentName);
            }

        }
        // failed to create a new Hash object, test automatically fails
        catch (Exception e) {
            return rf.fail(field, INFO.INVALID,
                    value, componentName);
        }
    }
}
