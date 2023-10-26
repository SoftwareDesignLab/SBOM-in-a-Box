package org.svip.sbom.model.uids;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.Component;

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

/**
 * file: HashTest.java
 * Test class to test Hash and its methods and usage
 *
 * @author Jordan Wong
 */
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
        assertFalse(Hash.validateHash(null, new Hash(Algorithm.UNKNOWN, HASH_VALUE)));
        assertFalse(Hash.validateHash(null, new Hash(Algorithm.ADLER32, HASH_VALUE)));
        assertFalse(Hash.validateHash(null, new Hash(Algorithm.MD6, HASH_VALUE + HASH_VALUE + HASH_VALUE)));

        assertTrue(Hash.validateHash(null, new Hash(Algorithm.MD5, HASH_VALUE)));
        assertTrue(Hash.validateHash(null, new Hash(Algorithm.MD6, HASH_VALUE)));
        assertTrue(Hash.validateHash(null, new Hash(Algorithm.MD6, HASH_VALUE + HASH_VALUE)));
        assertTrue(Arrays.stream(Algorithm.values()).anyMatch(algorithm -> Hash.validateHash(null, new Hash(algorithm, HASH_VALUE))));
    }

    @Test
    public void validate_md5_hash_test() {

    }

    @Test
    public void validate_sha1_hash_test() {

    }

}
