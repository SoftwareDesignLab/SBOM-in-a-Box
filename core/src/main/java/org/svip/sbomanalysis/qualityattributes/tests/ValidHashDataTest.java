package org.svip.sbomanalysis.qualityattributes.tests;


import org.nvip.plugfest.tooling.sbom.Component;
import org.nvip.plugfest.tooling.sbom.SBOM;
import org.nvip.plugfest.tooling.sbom.uids.Hash;

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
            // hold how many hashes are valid or not
            int validHashes = 0;
            int failedHashes = 0;

            // keep a list of which hashes were valid or not
            ArrayList<String> validHashList = new ArrayList<>();
            ArrayList<String> failHashList = new ArrayList<>();

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
                    failedHashes++;
                    failHashList.add(hash.getAlgorithm().toString());
                }

                // Check if hash is valid
                if(!Hash.validateHash(hash.getAlgorithm(), hash.getValue())){
                    failedHashes++;
                    failHashList.add(hash.getAlgorithm().toString());
                } else {
                    validHashes++;
                    validHashList.add(hash.getAlgorithm().toString());
                }

            }
            String message;
            String string_value;

            if(failedHashes != 0){
                message = String.format("Component has %d invalid hashes " +
                        "and %d valid hashes", failedHashes, validHashes);
                r = new Result(TEST_NAME, Result.STATUS.FAIL, message);
                string_value = "Invalid: " + String.join(", ", failHashList)
                        + "| Valid: " + String.join(", ", validHashList);
                r.updateInfo(Result.Context.STRING_VALUE, string_value);
            }
            else{
                message = String.format("Component's %d hashes are valid",
                        validHashes);
                r = new Result(TEST_NAME, Result.STATUS.PASS, message);
                string_value = "Valid: " + String.join(", ", validHashList);
                r.updateInfo(Result.Context.STRING_VALUE, string_value);

            }
            r.addContext(c, "Hash");
            results.add(r);
        }



        // return list of results for all components
        return results;
    }
}
