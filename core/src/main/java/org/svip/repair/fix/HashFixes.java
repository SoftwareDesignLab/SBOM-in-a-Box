package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.repair.extraction.Extraction;
import org.svip.repair.extraction.MavenExtraction;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbom.model.uids.Hash.Algorithm;
import org.svip.sbom.model.uids.PURL;
import org.svip.utils.Debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        if (component instanceof SBOMPackage sbomPackage
                && (hash.getAlgorithm().equals(MD5) || hash.getAlgorithm().equals(SHA1))) {

            // Create PURL object
            PURL purl = null;
            try {
                purl = new PURL(sbomPackage.getPURLs().stream().findFirst().get());
            } catch (Exception e) {
                Debug.log(Debug.LOG_TYPE.WARN, e.getMessage());
            }

            // Extract valid MD5 or SHA1 hash from Maven Repository and return Fix
            if (purl != null && purl.getType().equals("maven")) {
                Extraction extraction = new MavenExtraction(purl);
                extraction.extract();

                Map<Algorithm, String> validHashes = extraction.getHashes();
                Hash validHash = new Hash(hash.getAlgorithm(), validHashes.get(hash.getAlgorithm()));

                return List.of(new Fix<>(FixType.COMPONENT_HASH, hash, validHash));
            }
        }

        // Get all possible algorithms that match the hash
        List<Algorithm> validAlgorithms = validAlgorithms(value, sbom.getFormat());

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

    /**
     * Get a list of matching algorithms using the length of hashValue.
     * @param hashValue  the hash value
     * @param sbomFormat the format of the SBOM (i.e., CycloneDX, SPDX)
     * @return list of algorithms
     */
    private List<Algorithm> validAlgorithms(String hashValue, String sbomFormat) {
        List<Algorithm> algorithms = switch(hashValue.length()) {
            case 8 -> Arrays.asList(ADLER32);
            case 32 -> Arrays.asList(MD2, MD4, MD5, MD6);
            case 40 -> Arrays.asList(SHA1);
            case 56 -> Arrays.asList(SHA224);
            case 64 -> Arrays.asList(SHA256, SHA3256, BLAKE2b256, BLAKE3, MD6);
            case 96 -> Arrays.asList(SHA384, SHA3384, BLAKE2b512);
            case 128 -> Arrays.asList(SHA512, SHA3512, BLAKE2b512, MD6);
            default -> Collections.emptyList();
        };

        if (sbomFormat.equals("CycloneDX")) {
            algorithms.removeIf(Hash::isSPDXExclusive);
        }

        return algorithms;
    }

}
