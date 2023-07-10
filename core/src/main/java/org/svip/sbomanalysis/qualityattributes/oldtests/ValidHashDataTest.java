package org.svip.sbomanalysis.qualityattributes.oldtests;

import org.svip.sbom.model.old.Component;
import org.svip.sbom.model.old.SBOM;
import org.svip.sbom.model.uids.Hash;

import java.util.ArrayList;
import java.util.List;

/**
 * file: ValidHashDataTest.java
 *
 * Test each component's hashes if they match schema
 * @author Matthew Morrison
 * @author Derek Garcia
 */
public class ValidHashDataTest extends MetricTest{

    private static final String TEST_NAME = "ValidHashData";

    /**
     * Test all component's hashes if they are a valid schema (if present)
     *
     * @param sbom SBOM to test
     * @return a collection of results from each component
     */
    @Override
    public List<Result> test(SBOM sbom) {
        // list to hold results for each component
        List<Result> results = new ArrayList<>();
        Result r;
        for(Component c: sbom.getAllComponents()){

            // Skip if no hashes
            if(c.getHashes().isEmpty())
                continue;

            // Check all stored hashes
            // todo switch to strings
            for(Hash hash : c.getHashes()){

                // Report error and skip if hash is unknown
                if(hash.getAlgorithm() == Hash.Algorithm.UNKNOWN){
                    r = new Result(TEST_NAME, Result.STATUS.ERROR, "Unknown Hash algorithm");
                    r.addContext(c, "Hash");
                    r.updateInfo(Result.Context.STRING_VALUE, hash.getValue());
                    results.add(r);
                    continue;
                }

                // Check for unsupported hash
                if(sbom.getOriginFormat() == SBOM.Type.CYCLONE_DX && Hash.isSPDXExclusive(hash.getAlgorithm())){
                    r = new Result(TEST_NAME, Result.STATUS.FAIL, "CycloneDX SBOM has an unsupported Hash");
                    r.addContext(c, "Hash");
                    r.updateInfo(Result.Context.STRING_VALUE, hash.getValue());
                    results.add(r);
                }

                // Check if hash is valid
                if(!Hash.validateHash(hash.getAlgorithm(), hash.getValue())){
                    r = new Result(TEST_NAME, Result.STATUS.FAIL, "Invalid " + hash.getAlgorithm() + " hash");
                } else {
                    r = new Result(TEST_NAME, Result.STATUS.PASS, "Valid " + hash.getAlgorithm() + " hash");
                }

                r.addContext(c, "Hash");
                r.updateInfo(Result.Context.STRING_VALUE, hash.getValue());
                results.add(r);
            }
        }

        // return list of results for all components
        return results;
    }
}