package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbom.model.uids.Hash.Algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fixes class to generate suggested component hash repairs
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
        List<Algorithm> matchingAlgorithms = Hash.validAlgorithms(value, sbom instanceof SPDX23SBOM);
        if (matchingAlgorithms.isEmpty()) {
            // Suggest deleting the hash as a fix if hash does not match any algorithm
            return Collections.singletonList(new Fix<>(hash, null));
        }

        // Return the list of fixes of possible matching hash algorithms
        List<Fix<Hash>> fixes = new ArrayList<>();
        for (Algorithm matchingAlgorithm : matchingAlgorithms) {
            Hash fixedHash = new Hash(matchingAlgorithm, value);
            fixes.add(new Fix<>(hash, fixedHash));
        }
        return fixes;

    }

}
