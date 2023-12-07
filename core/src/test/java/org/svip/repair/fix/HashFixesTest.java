/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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
* /

package org.svip.repair.fix;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.ResultFactory;
import org.svip.metrics.resultfactory.enumerations.INFO;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.factory.objects.SVIPSBOMComponentFactory;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbom.model.uids.Hash.Algorithm;
import org.svip.serializers.deserializer.CDX14JSONDeserializer;
import org.svip.serializers.deserializer.SPDX23JSONDeserializer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * file: HashFixesTest.java
 * Test class to test HashFixes and its methods and usage
 *
 * @author Jordan Wong
 */
class HashFixesTest {

    private static HashFixes hashFixes;
    private static ResultFactory resultFactory;

    private static SBOM CDX14_SBOM;
    private static final String CDX14_SBOM_LOCATION = System.getProperty("user.dir") +
            "/src/test/resources/serializers/cdx_json/CDXMavenPlugin_build_cdx.json";
    private final String CDX14_SBOM_COMPONENT_NAME = "junit-platform-engine";

    private static SBOM SPDX23_SBOM;
    private static final String SPDX23_SBOM_LOCATION = System.getProperty("user.dir") +
            "/src/test/resources/serializers/spdx_json/syft-0.80.0-source-spdx-json.json";
    private final String SPDX23_SBOM_COMPONENT_NAME = "rsc.io/sampler";

    private final String SHA1_HASH_VALUE = "40aeef2be7b04f96bb91e8b054affc28b7c7c935";
    private final String MD5_HASH_VALUE = "5eb63bbbe01eeed093cb22bb8f5acdc3";

    @BeforeAll
    static void setup() throws Exception {
        hashFixes = new HashFixes();
        resultFactory = new ResultFactory("HashTest", ATTRIBUTE.UNIQUENESS, ATTRIBUTE.MINIMUM_ELEMENTS);
        CDX14_SBOM = new CDX14JSONDeserializer().readFromString(Files.readString(Path.of(CDX14_SBOM_LOCATION)));
        SPDX23_SBOM = new SPDX23JSONDeserializer().readFromString(Files.readString(Path.of(SPDX23_SBOM_LOCATION)));
    }

    @Test
    public void fix_unknown_hash_algorithm_test() {
        Hash hash = new Hash("Unknown", MD5_HASH_VALUE);
        testValidAlgorithms(hash, mockHashFixes(hash, SPDX23_SBOM, SPDX23_SBOM_COMPONENT_NAME),
                Algorithm.MD2, Algorithm.MD4, Algorithm.MD5, Algorithm.MD6);
    }

    @Test
    public void fix_invalid_hash_test() {
        Hash hash = new Hash("SHA1", MD5_HASH_VALUE);
        testValidAlgorithms(hash, mockHashFixes(hash, SPDX23_SBOM, SPDX23_SBOM_COMPONENT_NAME),
                Algorithm.MD2, Algorithm.MD4, Algorithm.MD5, Algorithm.MD6);
    }

    @Test
    public void fix_invalid_hash_value_with_maven_extractor_test() {
        Hash hash = new Hash("SHA1", MD5_HASH_VALUE);
        List<Fix<Hash>> fixes = mockHashFixes(hash, CDX14_SBOM, CDX14_SBOM_COMPONENT_NAME);

        assertEquals(1, fixes.size());
        assertEquals(hash, fixes.get(0).old());
        assertEquals(new Hash(Algorithm.SHA1, SHA1_HASH_VALUE), fixes.get(0).fixed());
    }

    @Test
    public void no_algorithm_or_hash_value_match_test() {
        Hash hash = new Hash("SHA1", "invalid");
        List<Fix<Hash>> fixes = mockHashFixes(hash, SPDX23_SBOM, SPDX23_SBOM_COMPONENT_NAME);

        assertEquals(1, fixes.size());
        assertEquals(hash, fixes.get(0).old());
        assertNull(fixes.get(0).fixed());
    }

    @Test
    public void cdx_hash_fix_excludes_spdx_hash_algorithms_test() {
        Hash hash = new Hash("SHA384", MD5_HASH_VALUE + MD5_HASH_VALUE + MD5_HASH_VALUE);
        testValidAlgorithms(hash, mockHashFixes(hash, CDX14_SBOM, CDX14_SBOM_COMPONENT_NAME),
                Algorithm.SHA384, Algorithm.SHA3384);
    }

    @Test
    public void invalid_purl_test() {
        SBOM sbom = buildSBOM();
        Hash hash = new Hash("SHA1", MD5_HASH_VALUE);
        testValidAlgorithms(hash, mockHashFixes(hash, sbom, CDX14_SBOM_COMPONENT_NAME),
                Algorithm.MD2, Algorithm.MD4, Algorithm.MD5, Algorithm.MD6);
    }

    private List<Fix<Hash>> mockHashFixes(Hash hash, SBOM sbom, String componentName) {
        Result result = resultFactory.fail(hash.getAlgorithm().name(), INFO.INVALID, hash.getValue(), "component");
        return hashFixes.fix(result, sbom, componentName, 0);
    }

    private SBOM buildSBOM() {
        SVIPSBOMComponentFactory packageBuilderFactory = new SVIPSBOMComponentFactory();
        SVIPComponentBuilder packageBuilder = packageBuilderFactory.createBuilder();
        packageBuilder.setName(CDX14_SBOM_COMPONENT_NAME);
        packageBuilder.addPURL("invalid");
        Component component = packageBuilder.buildAndFlush();

        SVIPSBOMBuilder svipSbomBuilder = new SVIPSBOMBuilder();
        svipSbomBuilder.addComponent(component);
        return svipSbomBuilder.Build();
    }

    private void testValidAlgorithms(Hash hash, List<Fix<Hash>> hashFixes, Algorithm... algorithm) {
        Set<Algorithm> validAlgorithms = new HashSet<>(List.of(algorithm));

        for (Fix<Hash> fix : hashFixes) {
            assertEquals(hash, fix.old());
            assertTrue(validAlgorithms.contains(fix.fixed().getAlgorithm()));
        }
    }

}
