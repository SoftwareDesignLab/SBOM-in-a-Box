package org.svip.sbom.model.uids;

import java.util.EnumSet;
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
