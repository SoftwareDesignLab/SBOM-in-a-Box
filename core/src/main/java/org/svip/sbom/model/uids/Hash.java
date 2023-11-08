package org.svip.sbom.model.uids;

import jregex.Pattern;
import org.svip.repair.extraction.Extraction;
import org.svip.repair.extraction.MavenExtraction;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.utils.Debug;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.svip.sbom.model.uids.Hash.Algorithm.*;

/**
 * Hash Object to hold Hash values
 *
 * @author Derek Garcia
 */
public class Hash {

    // List of supported hashing algorithms for SPDX and CDX
    public enum Algorithm {
        SHA1,
        SHA224,
        SHA256,
        SHA384,
        SHA512,
        SHA3256,
        SHA3384,
        SHA3512,
        BLAKE2b256,
        BLAKE2b384,
        BLAKE2b512,
        BLAKE3,
        MD2,
        MD4,
        MD5,
        MD6,
        ADLER32,
        UNKNOWN
    }

    // SPDX only Hashes
    public static final Set<Algorithm> SPDXAlgorithms = EnumSet.of(
        SHA224,
        BLAKE2b512,
        MD2,
        MD4,
        MD6,
        ADLER32
    );


    private final Algorithm algorithm;
    private final String value;


    /**
     * Create a new hash object
     *
     * @param algorithm hash algorithm used
     * @param value     value of hash
     */
    public Hash(String algorithm, String value) {
        this.algorithm = toAlgorithmEnum(algorithm);
        this.value = value;
    }

    /**
     * Create a new hash object
     *
     * @param algorithm hash algorithm used
     * @param value     value of hash
     */
    public Hash(Algorithm algorithm, String value) {
        this.algorithm = algorithm;
        this.value = value;
    }

    /**
     * Convert given string into hash algoritm. "Other" if doesn't match any given
     *
     * @param algorithm string of algorithm name
     * @return Enum of algorithm
     */
    private Algorithm toAlgorithmEnum(String algorithm) {
        // sanitize input
        algorithm = algorithm.toLowerCase().replaceAll("-", "");

        // check each algo to find match
        for (Algorithm algEnum : Algorithm.values()) {
            // return matching algo
            if (algEnum.toString().toLowerCase().equals(algorithm))
                return algEnum;
        }

        return Algorithm.UNKNOWN;   // unknown or unsupported algo
    }

    /**
     * Check if algorithm is exclusive to SPDX
     *
     * @param algorithm Algorithm to check
     * @return true if exclusive, false otherwise
     */
    public static boolean isSPDXExclusive(Algorithm algorithm) {
        return SPDXAlgorithms.contains(algorithm);
    }

    /**
     * Validates a hash function to make sure the length and content are correct
     *
     * @param component SBOMFile component
     * @return True if hash passes, false otherwise
     */
    public boolean isValid(Component component) {
        // Validate MD5 and SHA1 hashes against Maven Repository
        String validHashValue = getValidValue(component);
        if (!validHashValue.isEmpty()) {
            SBOMPackage sbomPackage = (SBOMPackage) component;
            String componentHash = sbomPackage.getHashes().getOrDefault(algorithm.name(), "");
            return componentHash.equals(validHashValue);
        } else {
            // If component is not from Maven Repository, then test against supported hashes using regex
            Pattern p = new Pattern(
                    switch (algorithm) {
                        case ADLER32 -> "^[a-fA-F0-9]{8}$";
                        case SHA1 -> "^[a-fA-F0-9]{40}$";
                        case SHA224 -> "^[a-fA-F0-9]{56}$";
                        case SHA256, SHA3256, BLAKE2b256, BLAKE3 -> "^[a-fA-F0-9]{64}$";
                        case SHA384, SHA3384, BLAKE2b384 -> "^[a-fA-F0-9]{96}$";
                        case SHA512, SHA3512, BLAKE2b512 -> "^[a-fA-F0-9]{128}$";
                        case MD2, MD4, MD5 -> "^[a-fA-F0-9]{32}$";
                        case MD6 -> "^([a-fA-F0-9]{32}|[a-fA-F0-9]{64}|[a-fA-F0-9]{128})$";
                        default -> "(?!)"; // Regex will always fail
                    }, Pattern.MULTILINE);
            return p.matches(value);
        }
    }

    /**
     * Get a list of possible matching algorithms based on the length of the hash value.
     * @param isSPDX true if getting valid algorithms for SPDX file
     * @return list of algorithms
     */
    public List<Algorithm> getValidAlgorithms(boolean isSPDX) {
        List<Algorithm> algorithms = new LinkedList<Algorithm>(switch(value.length()) {
            case 8 -> Arrays.asList(ADLER32);
            case 32 -> Arrays.asList(MD2, MD4, MD5, MD6);
            case 40 -> Arrays.asList(SHA1);
            case 56 -> Arrays.asList(SHA224);
            case 64 -> Arrays.asList(SHA256, SHA3256, BLAKE2b256, BLAKE3, MD6);
            case 96 -> Arrays.asList(SHA384, SHA3384, BLAKE2b512);
            case 128 -> Arrays.asList(SHA512, SHA3512, BLAKE2b512, MD6);
            default -> Collections.emptyList();
        });

        if (!isSPDX) {
            algorithms.removeIf(Hash::isSPDXExclusive);
        }

        return algorithms;
    }

    public String getValidValue(Component component) {
        if (!(component instanceof SBOMPackage sbomPackage
                && (algorithm.equals(MD5) || algorithm.equals(SHA1))
                && (sbomPackage.getPURLs().stream().findFirst().get()).startsWith("pkg:maven"))) {
            return "";
        }

        // Create PURL object
        PURL purl;
        try {
            String purlString = sbomPackage.getPURLs().stream().findFirst().get();
            purl = new PURL(purlString);
        } catch (Exception e) {
            Debug.log(Debug.LOG_TYPE.WARN, e.getMessage());
            return "";
        }

        // Perform extraction of MD5 and SHA1 hashes from Maven Repository
        Extraction extraction = new MavenExtraction(purl);
        extraction.extract();

        return extraction.getHashes().get(algorithm);
    }

    ///
    /// getters
    ///

    /**
     * @return Algorithm type
     */
    public Algorithm getAlgorithm() {
        return algorithm;
    }

    /**
     * @return hashed value
     */
    public String getValue() {
        return value;
    }

    ///
    /// Overrides
    ///

    /**
     * @return type:value
     */
    @Override
    public String toString() {
        return this.algorithm.toString() + ":" + this.value;
    }

    /**
     * Test for object equivalence
     *
     * @param o Other Object
     * @return true if same, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hash hash = (Hash) o;

        if (algorithm != hash.algorithm) return false;
        return Objects.equals(value, hash.value);
    }

    /**
     * Generate hashcode for this object
     *
     * @return hashcode
     */
    @Override
    public int hashCode() {
        int result = algorithm != null ? algorithm.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
