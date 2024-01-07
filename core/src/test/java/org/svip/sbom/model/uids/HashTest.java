/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
 */

package org.svip.sbom.model.uids;

import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.factory.objects.SVIPSBOMComponentFactory;
import org.svip.sbom.model.interfaces.generics.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private static final String HASH_VALUE = "743a64546ababa69c8af34e057722cd2";
    private final String PURL = "pkg:maven/org.junit.platform/junit-platform-engine@1.9.2?type=jar";

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
    public void isSPDXExclusive_test() {
        assertFalse(Hash.isSPDXExclusive(Algorithm.UNKNOWN));
        assertTrue(Hash.isSPDXExclusive(Algorithm.SHA224));
        assertTrue(Hash.isSPDXExclusive(Algorithm.BLAKE2b512));
        assertTrue(Hash.isSPDXExclusive(Algorithm.MD2));
        assertTrue(Hash.isSPDXExclusive(Algorithm.MD4));
        assertTrue(Hash.isSPDXExclusive(Algorithm.MD6));
        assertTrue(Hash.isSPDXExclusive(Algorithm.ADLER32));
    }

    @Test
    public void isValid_test() {
        Hash invalidHash = new Hash(Algorithm.SHA1.name(), HASH_VALUE);
        Hash validHash = new Hash(Algorithm.MD5.name(), HASH_VALUE);

        Component component = buildComponent(PURL);
        assertTrue(validHash.isValid(component));
        assertFalse(invalidHash.isValid(component));

        assertTrue(Arrays.stream(Algorithm.values())
                .anyMatch(algorithm -> new Hash(algorithm.name(), HASH_VALUE).isValid(null)));

        invalidHash = new Hash(Algorithm.UNKNOWN.name(), HASH_VALUE);
        assertFalse(invalidHash.isValid(component));
    }

    @Test
    public void getValidAlgorithms_test() {
        for (Hash hash : mockHashes()) {
            assertNotNull(hash.getValidAlgorithms(true));
            assertNotNull(hash.getValidAlgorithms(false));
        }
    }

    @Test
    public void getValidValue_test() {
        Component component = buildComponent(PURL);
        Hash hash = new Hash(Algorithm.MD5.name(), HASH_VALUE);
        assertEquals(HASH_VALUE, hash.getValidValue(component));

        component = buildComponent("pkg:maven:invalidPURL");
        assertEquals("", hash.getValidValue(component));
        assertEquals("", hash.getValidValue(null));
    }

    private Component buildComponent(String purl) {
        SVIPSBOMComponentFactory packageBuilderFactory = new SVIPSBOMComponentFactory();
        SVIPComponentBuilder packageBuilder = packageBuilderFactory.createBuilder();
        packageBuilder.addHash(Algorithm.MD5.name(), HASH_VALUE);
        packageBuilder.addPURL(purl);
        return packageBuilder.buildAndFlush();
    }

    private List<Hash> mockHashes() {
        List<Hash> hashes = new ArrayList<>();

        hashes.add(new Hash(Algorithm.UNKNOWN, ""));
        hashes.add(new Hash(Algorithm.ADLER32, HASH_VALUE.substring(0, 8)));
        hashes.add(new Hash(Algorithm.MD2, HASH_VALUE));
        hashes.add(new Hash(Algorithm.SHA1, (HASH_VALUE + HASH_VALUE).substring(0, 40)));
        hashes.add(new Hash(Algorithm.SHA224, (HASH_VALUE + HASH_VALUE).substring(0, 56)));
        hashes.add(new Hash(Algorithm.SHA256, HASH_VALUE + HASH_VALUE));
        hashes.add(new Hash(Algorithm.SHA384, HASH_VALUE + HASH_VALUE + HASH_VALUE));
        hashes.add(new Hash(Algorithm.SHA512, HASH_VALUE + HASH_VALUE + HASH_VALUE + HASH_VALUE));

        return hashes;
    }
}
