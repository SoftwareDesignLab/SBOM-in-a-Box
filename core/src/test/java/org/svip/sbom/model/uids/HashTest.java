package org.svip.sbom.model.uids;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.svip.sbom.model.uids.Hash.Algorithm;

class HashTest {

    private static final String HASH_VALUE = "5eb63bbbe01eeed093cb22bb8f5acdc3";

    @Test
    public void construction_test() {
        Hash invalidHash = new Hash("invalidAlgorithm", HASH_VALUE);
        assertNotNull(invalidHash);
        Hash hashFromString = new Hash("MD5", HASH_VALUE);
        assertNotNull(hashFromString);
        Hash hashFromAlgorithm = new Hash(Algorithm.MD5, HASH_VALUE);
        assertNotNull(hashFromAlgorithm);

        assertEquals(hashFromString, hashFromAlgorithm);
        assertEquals(hashFromString.hashCode(), hashFromAlgorithm.hashCode());
        assertEquals("MD5:" + HASH_VALUE, hashFromString.toString());
    }

    @Test
    public void getters_test() {
        Hash hash = new Hash(Algorithm.MD5, HASH_VALUE);
        assertEquals(Algorithm.MD5, hash.getAlgorithm());
        assertEquals(HASH_VALUE, hash.getValue());
    }

    @Test
    public void is_spdx_exclusive_test() {
        assertFalse(Hash.isSPDXExclusive(Algorithm.UNKNOWN));
        assertTrue(Hash.isSPDXExclusive(Algorithm.SHA224));
        assertTrue(Hash.isSPDXExclusive(Algorithm.BLAKE2b512));
        assertTrue(Hash.isSPDXExclusive(Algorithm.MD2));
        assertTrue(Hash.isSPDXExclusive(Algorithm.MD4));
        assertTrue(Hash.isSPDXExclusive(Algorithm.MD6));
        assertTrue(Hash.isSPDXExclusive(Algorithm.ADLER32));
    }

    @Test
    public void validate_hash_test() {
        assertFalse(Hash.validateHash(Algorithm.UNKNOWN, HASH_VALUE));
        assertFalse(Hash.validateHash(Algorithm.ADLER32, HASH_VALUE));
        assertFalse(Hash.validateHash(Algorithm.MD6, HASH_VALUE + HASH_VALUE + HASH_VALUE));

        assertTrue(Hash.validateHash(Algorithm.MD5, HASH_VALUE));
        assertTrue(Hash.validateHash(Algorithm.MD6, HASH_VALUE));
        assertTrue(Hash.validateHash(Algorithm.MD6, HASH_VALUE + HASH_VALUE));
        assertTrue(Arrays.stream(Algorithm.values()).anyMatch(algorithm -> Hash.validateHash(algorithm, HASH_VALUE)));
    }

    @Test
    public void valid_algorithms_test() {
        assertEquals(Collections.emptyList(), Hash.validAlgorithms("invalid", true));
        assertEquals(List.of(Algorithm.MD5), Hash.validAlgorithms(HASH_VALUE, false));
        assertEquals(List.of(Algorithm.MD2, Algorithm.MD4, Algorithm.MD5, Algorithm.MD6), Hash.validAlgorithms(HASH_VALUE, true));

        Map<Algorithm, String> hashes = generateHashes();
        assertEquals(Collections.emptyList(), Hash.validAlgorithms(hashes.get(Algorithm.ADLER32), false));
        assertEquals(Collections.emptyList(), Hash.validAlgorithms(hashes.get(Algorithm.SHA224), false));
        for (String hash : hashes.values()) {
            assertNotNull(Hash.validAlgorithms(hash, true));
        }

    }

    private Map<Algorithm, String> generateHashes() {
        Map<Algorithm, String> hashes = new HashMap<>();

        hashes.put(Algorithm.ADLER32, HASH_VALUE.substring(0, 8));
        hashes.put(Algorithm.MD2, HASH_VALUE);
        hashes.put(Algorithm.SHA1, (HASH_VALUE + HASH_VALUE).substring(0, 40));
        hashes.put(Algorithm.SHA224, (HASH_VALUE + HASH_VALUE).substring(0, 56));
        hashes.put(Algorithm.SHA256, HASH_VALUE + HASH_VALUE);
        hashes.put(Algorithm.SHA384, HASH_VALUE + HASH_VALUE + HASH_VALUE);
        hashes.put(Algorithm.SHA512, HASH_VALUE + HASH_VALUE + HASH_VALUE + HASH_VALUE);

        return hashes;
    }

}
