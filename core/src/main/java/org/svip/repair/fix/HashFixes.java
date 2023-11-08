package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.repair.extraction.Extraction;
import org.svip.repair.extraction.MavenExtraction;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbom.model.uids.Hash.Algorithm;
import org.svip.sbom.model.uids.PURL;
import org.svip.utils.Debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.svip.sbom.model.uids.Hash.Algorithm.*;

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
     * @param componentName key from quality report map most directly relating to the repair type
     * @param componentHashCode hash code of the component
     * @return list of hash fixes
     */
    @Override
    public List<Fix<Hash>> fix(Result result, SBOM sbom, String componentName, Integer componentHashCode) {

        // Get algorithm and hash value from result message
        String[] details = result.getDetails().split(" ");
        String algorithm = details[details.length - 1];
        String value = details[0];

        // Create Hash object
        Hash hash = new Hash(algorithm, value);

        // Retrieve component from SBOM by componentName
        Component component = sbom.getComponents().stream()
                .filter(c -> c.getName().equalsIgnoreCase(componentName))
                .toList()
                .get(0);

        // Retrieve valid MD5 and SHA1 hashes from Maven Repository
        String validHashValue = hash.getValidValue(component);

        if (!validHashValue.isEmpty()) {
            Hash validHash = new Hash(hash.getAlgorithm(), hash.getValidValue(component));
            return List.of(new Fix<>(FixType.COMPONENT_HASH, hash, validHash));
        } else {
            // Get all possible algorithms that match the hash
            List<Algorithm> validAlgorithms = hash.getValidAlgorithms(sbom instanceof SPDX23SBOM);

            // Suggest deleting the hash as a fix if hash does not match any algorithm
            if (validAlgorithms.isEmpty()) {
                return List.of(new Fix<>(FixType.COMPONENT_HASH, hash, null));
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

}
