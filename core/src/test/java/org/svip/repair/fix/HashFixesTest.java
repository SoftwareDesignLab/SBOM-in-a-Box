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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.svip.sbom.model.uids.Hash.Algorithm.SHA3384;

class HashFixesTest {

    private static HashFixes hashFixes;
    private static ResultFactory resultFactory;
    private static SBOM cdxSbom;
    private static SBOM spdxSbom;

    private static final String CDX14_SBOM = System.getProperty("user.dir") +
            "/src/test/resources/serializers/cdx_json/CDXMavenPlugin_build_cdx.json";
    private final String CDX14_SBOM_COMPONENT_NAME = "junit-platform-engine";
    private final String CDX14_SBOM_COMPONENT_SHA1_HASH_VALUE = "40aeef2be7b04f96bb91e8b054affc28b7c7c935";

    private static final String SPDX23_SBOM = System.getProperty("user.dir") +
            "/src/test/resources/serializers/spdx_json/syft-0.80.0-source-spdx-json.json";
    private final String SPDX23_SBOM_COMPONENT_NAME = "rsc.io/sampler";

    private static final String HASH_VALUE = "5eb63bbbe01eeed093cb22bb8f5acdc3";

    @BeforeAll
    static void setup() throws Exception {
        hashFixes = new HashFixes();
        resultFactory = new ResultFactory("Hash_Test",
                ATTRIBUTE.UNIQUENESS, ATTRIBUTE.MINIMUM_ELEMENTS);
        CDX14JSONDeserializer cdx14JSONDeserializer = new CDX14JSONDeserializer();
        SPDX23JSONDeserializer spdx23JSONDeserializer = new SPDX23JSONDeserializer();
        cdxSbom = cdx14JSONDeserializer.readFromString(Files.readString(Path.of(CDX14_SBOM)));
        spdxSbom = spdx23JSONDeserializer.readFromString(Files.readString(Path.of(SPDX23_SBOM)));
    }

    @Test
    public void fix_unknown_hash_algorithm_test() {
        Result result = resultFactory.fail("Unknown", INFO.INVALID, HASH_VALUE, "component");
        List<Fix<Hash>> fixes = hashFixes.fix(result, spdxSbom, "repairSubType", 0);

        Set<Algorithm> validAlgorithms =
                new HashSet<>(Arrays.asList(Algorithm.MD2, Algorithm.MD4, Algorithm.MD5, Algorithm.MD6));

        for (Fix<Hash> fix : fixes) {
            assertEquals(new Hash(Algorithm.UNKNOWN, HASH_VALUE), fix.old());
            assertTrue(validAlgorithms.contains(fix.fixed().getAlgorithm()));
        }
    }

    @Test
    public void fix_invalid_hash_test() {
        Result result = resultFactory.fail("SHA1", INFO.INVALID, HASH_VALUE, "component");
        List<Fix<Hash>> fixes = hashFixes.fix(result, spdxSbom, "repairSubType", 0);

        Set<Algorithm> validAlgorithms =
                new HashSet<>(Arrays.asList(Algorithm.MD2, Algorithm.MD4, Algorithm.MD5, Algorithm.MD6));

        for (Fix<Hash> fix : fixes) {
            assertEquals(new Hash(Algorithm.SHA1, HASH_VALUE), fix.old());
            assertTrue(validAlgorithms.contains(fix.fixed().getAlgorithm()));
        }
    }

    @Test
    public void fix_invalid_hash_value_with_maven_extractor_test() {
        Result result = resultFactory.fail("SHA1", INFO.INVALID, HASH_VALUE, "component");
        List<Fix<Hash>> fixes = hashFixes.fix(result, cdxSbom, CDX14_SBOM_COMPONENT_NAME, 0);

        assertEquals(1, fixes.size());
        assertEquals(new Hash(Algorithm.SHA1, HASH_VALUE), fixes.get(0).old());
        assertEquals(new Hash(Algorithm.SHA1, CDX14_SBOM_COMPONENT_SHA1_HASH_VALUE), fixes.get(0).fixed());
    }

    @Test
    public void no_algorithm_or_hash_value_match_test() {
        Result result = resultFactory.fail("SHA1", INFO.INVALID, "invalid", "component");
        List<Fix<Hash>> fixes = hashFixes.fix(result, spdxSbom, "repairSubType", 0);

        assertEquals(1, fixes.size());
        assertEquals(new Hash(Algorithm.SHA1, "invalid"), fixes.get(0).old());
        assertNull(fixes.get(0).fixed());
    }

    @Test
    public void cdx_hash_fix_excludes_spdx_hash_algorithms_test() {
        String hashValueOfLength96 = HASH_VALUE + HASH_VALUE + HASH_VALUE;
        Result result = resultFactory.fail("SHA384", INFO.INVALID, hashValueOfLength96, "component");
        List<Fix<Hash>> fixes = hashFixes.fix(result, cdxSbom, CDX14_SBOM_COMPONENT_NAME, 0);

        Set<Algorithm> validAlgorithms =
                new HashSet<>(Arrays.asList(Algorithm.SHA384, Algorithm.SHA3384));

        for (Fix<Hash> fix : fixes) {
            assertEquals(new Hash(Algorithm.SHA384, hashValueOfLength96), fix.old());
            assertTrue(validAlgorithms.contains(fix.fixed().getAlgorithm()));
        }
    }

    @Test
    public void invalid_purl_test() {
        SVIPSBOMComponentFactory packageBuilderFactory = new SVIPSBOMComponentFactory();
        SVIPComponentBuilder packageBuilder = packageBuilderFactory.createBuilder();
        packageBuilder.setName(CDX14_SBOM_COMPONENT_NAME);
        packageBuilder.addPURL("invalid");
        Component component = packageBuilder.buildAndFlush();

        SVIPSBOMBuilder svipSbomBuilder = new SVIPSBOMBuilder();
        svipSbomBuilder.addComponent(component);
        SBOM sbom = svipSbomBuilder.Build();

        Result result = resultFactory.fail("SHA1", INFO.INVALID, HASH_VALUE, "component");
        List<Fix<Hash>> fixes = hashFixes.fix(result, sbom, CDX14_SBOM_COMPONENT_NAME, 0);

        Set<Algorithm> validAlgorithms =
                new HashSet<>(Arrays.asList(Algorithm.MD2, Algorithm.MD4, Algorithm.MD5, Algorithm.MD6));

        for (Fix<Hash> fix : fixes) {
            assertEquals(new Hash(Algorithm.SHA1, HASH_VALUE), fix.old());
            assertTrue(validAlgorithms.contains(fix.fixed().getAlgorithm()));
        }
    }

    @Test
    public void valid_algorithms_test() {
        mockHashes().forEach((key, value) -> {
            Result result = resultFactory.fail(key.name(), INFO.INVALID, value, "component");
            List<Fix<Hash>> fixes = hashFixes.fix(result, spdxSbom, SPDX23_SBOM_COMPONENT_NAME, 0);
            assertTrue(fixes.stream().allMatch(fix -> fix.old().equals(new Hash(key, value))));
        });
    }

    private Map<Algorithm, String> mockHashes() {
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
