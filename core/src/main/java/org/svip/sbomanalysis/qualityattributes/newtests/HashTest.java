package org.svip.sbomanalysis.qualityattributes.newtests;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;

import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.ResultFactory;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Text;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.STATUS;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * file: HashTest.java
 * Series of tests for hash string and hash objects
 *
 * @author Matthew Morrison
 */
public class HashTest extends MetricTest{
    private final String TEST_NAME = "HashTest";
    private ResultFactory resultFactory;

    private final Component component;

    /**
     * Constructor to create a new MetricTest
     *
     * @param attributes the list of attributes used
     */
    public HashTest(List<ATTRIBUTE> attributes, Component component) {
        super(attributes);
        this.component = component;
    }

    /**
     * Test a single hash if it is valid
     * @param field the hash algorithm to test
     * @param value the hash value to test
     * @return a Result if the hash data is valid or not
     */
    @Override
    public Set<Result> test(String field, String value) {
        Set<Result> results = new HashSet<>();
        // hash  is not a null value and does exist, tests can run
        if(value != null && field != null) {
            resultFactory = new ResultFactory(super.attributes, this.TEST_NAME);
            results.add(isValidHash(field, value));

        }
        // Hash has a null algo or value, tests cannot be run
        // return missing Result
        else{
            Text text = new Text("Hash Object could not be built", field);
            String message;
            String details;
            if(value == null){
                message = text.getMessage(INFO.MISSING, "Hash Value");
                details = text.getDetails(INFO.MISSING, "Hash Value");
            }
            else{
                message = text.getMessage(INFO.MISSING, "Hash Algo");
                details = text.getDetails(INFO.MISSING, "Hash Algo");
            }
            Result r = new Result(attributes, TEST_NAME, message, details, STATUS.ERROR);
            results.add(r);
        }
        return results;
    }


    /**
     * Test the hash if it is a valid schema and type
     * @param field the hash algorithm
     * @param value the hash value
     * @return a Result if the hash is valid or not
     */
    private Result isValidHash(String field, String value){
        Result r;
        try{
            // create new hash object
            Hash hash = new Hash(field, value);

            // Check if hash algorithm is unknown
            if(hash.getAlgorithm() == Hash.Algorithm.UNKNOWN){
                r = resultFactory.fail(field, INFO.INVALID, value,
                        "Unknown Hash algorithm");
                return r;
            }

            // Check for unsupported hash in CycloneDX components
            if(component.getType().equals("CycloneDX") && Hash.isSPDXExclusive(hash.getAlgorithm())){
                r = resultFactory.fail(field, INFO.INVALID, value,
                        "Hash algorithm is not supported in CycloneDX components");
                return r;
            }

            // Check if hash is valid
            if(!Hash.validateHash(hash.getAlgorithm(), hash.getValue())){
                r = resultFactory.fail(field, INFO.INVALID, value,
                        "Invalid " + hash.getAlgorithm() + " hash");
            } else {
                r = resultFactory.pass(field, INFO.VALID, value,
                        "Valid " + hash.getAlgorithm() + " hash");
            }

        }
        // failed to create a new Hash object, test automatically fails
        catch(Exception e){
            r = resultFactory.fail(field, INFO.INVALID, value,
                    "Hash Object was unsuccessfully built");
            return r;
        }

        return r;
    }
}
