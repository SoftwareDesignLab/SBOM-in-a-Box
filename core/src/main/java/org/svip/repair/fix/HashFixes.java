package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbom.model.uids.Hash.Algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * file: HashFixes.java
 * Fixes class to generate suggested component hash repairs
 *
 * @author Jordan Wong
 */
public class HashFixes implements Fixes<Hash> {

    /**
     * Get a list of possible fixes for invalid hashes.
     *
     * @param result        object from quality report
     * @param sbom          sbom from quality report
     * @param repairSubType key from quality report map most directly relating to the repair type
     * @return list of hash fixes
     */
    @Override
    public List<Fix<Hash>> fix(Result result, SBOM sbom, String repairSubType) {

        // Get algorithm and hash value from result message
        String[] details = result.getDetails().split(" ");
        String algorithm = details[details.length - 1];
        String value = details[0];

        Hash hash = new Hash(algorithm, value);

        // Get all possible algorithms that match the hash
        List<Algorithm> validAlgorithms = Hash.validAlgorithms(value, sbom.getFormat());

        // Suggest deleting the hash as a fix if hash does not match any algorithm
        if (validAlgorithms.isEmpty()) {
            return Collections.singletonList(new Fix<>(FixType.COMPONENT_HASH, hash, null));
        }

        // Return the list of fixes of possible matching hash algorithms
        List<Fix<Hash>> fixes = new ArrayList<>();
        for (Algorithm validAlgorithm : validAlgorithms) {
            Hash fixedHash = new Hash(validAlgorithm, value);
            fixes.add(new Fix<>(FixType.COMPONENT_HASH, hash, fixedHash));
        }
        return fixes;

    }

}
