package org.svip.sbom.model.uids;

import jregex.Pattern;
import org.svip.repair.extraction.Extraction;
import org.svip.repair.extraction.MavenExtraction;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
    public enum SPDXAlgorithm {
        SHA224,
        BLAKE2b512,
        MD2,
        MD4,
        MD6,
        ADLER32
    }

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
     * @param a Algorithm to check
     * @return true if exclusive, false otherwise
     */
    public static boolean isSPDXExclusive(Algorithm a) {
        return a == SHA224 ||
                a == BLAKE2b512 ||
                a == MD2 ||
                a == MD4 ||
                a == MD6 ||
                a == ADLER32;
    }

    /**
     * Validates a hash function to make sure the length and content are correct
     *
     * @param a    Hash algorithm
     * @param hash Hash string
     * @return True if hash passes, false otherwise
     */
    public static boolean validateHash(Component component, Hash hash) throws Exception {
        String hashRegex;
        // Test against supported hashes
        switch (hash.algorithm) {
            case ADLER32:
                hashRegex = "^[a-fA-F0-9]{8}$";
                break;
            case SHA1:
                hashRegex = "^[a-fA-F0-9]{40}$";
                break;
            case SHA224:
                hashRegex = "^[a-fA-F0-9]{56}$";
                break;
            case SHA256:
            case SHA3256:
            case BLAKE2b256:
            case BLAKE3:
                hashRegex = "^[a-fA-F0-9]{64}$";
                break;
            case SHA384:
            case SHA3384:
            case BLAKE2b384:
                hashRegex = "^[a-fA-F0-9]{96}$";
                break;
            case SHA512:
            case SHA3512:
            case BLAKE2b512:
                hashRegex = "^[a-fA-F0-9]{128}$";
                break;
            case MD2:
            case MD4:
            case MD5:
                hashRegex = "^[a-fA-F0-9]{32}$";
                break;
            // MD6 can be 128, 256, or 512 bits
            // todo better way to distinguish?
            case MD6:
                Pattern p = new Pattern("^[a-fA-F0-9]{32}$", Pattern.MULTILINE);
                if (p.matches(hash.getValue())) return true;
                p = new Pattern("^[a-fA-F0-9]{64}$", Pattern.MULTILINE);
                if (p.matches(hash.getValue())) return true;
                p = new Pattern("^[a-fA-F0-9]{128}$", Pattern.MULTILINE);
                return p.matches(hash.getValue());
            default:
                return false;
        }

        // Test regex
        Pattern p = new Pattern(hashRegex, Pattern.MULTILINE);
        if (component instanceof SBOMPackage sbomPackage) {
            if (!p.matches(hash.getValue())) {
                return false;
            }
            // Don't know why there are multiple PURLs for a single component
            Extraction ex;
            for (String purl : sbomPackage.getPURLs()) {
                ex = new MavenExtraction(new PURL(purl));
                ex.extract();
                HashMap<Algorithm, String> hashes = ex.getHashes();
                // enumerate through all hashes
            }
        }
        return p.matches(hash.getValue());
    }

    /**
     * Get a list of matching algorithms given a hash value.
     * @param hash   Hash string
     * @param sbomFormat the format of the SBOM (i.e., CycloneDX, SPDX)
     * @return list of algorithms
     */
    public static List<Algorithm> validAlgorithms(String hash, String sbomFormat) {
        List<Algorithm> algorithms = new ArrayList<>();

        switch(hash.length()) {
            case 8:
                algorithms.add(ADLER32);
                break;
            case 32:
                algorithms.addAll(List.of(MD2, MD4, MD5, MD6));
                break;
            case 40:
                algorithms.add(SHA1);
                break;
            case 56:
                algorithms.add(SHA224);
                break;
            case 64:
                algorithms.addAll(List.of(SHA256, SHA3256, BLAKE2b256, BLAKE3, MD6));
                break;
            case 96:
                algorithms.addAll(List.of(SHA384, SHA3384, BLAKE2b512));
                break;
            case 128:
                algorithms.addAll(List.of(SHA512, SHA3512, BLAKE2b512, MD6));
                break;
            default:
                return Collections.emptyList();
        }

        if (sbomFormat.equals("CycloneDX")) {
            algorithms.removeIf(Hash::isSPDXExclusive);
        }

        if (!algorithms.isEmpty() && validateHash(algorithms.get(0), hash)) {
            return algorithms;
        }
        return Collections.emptyList();
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
