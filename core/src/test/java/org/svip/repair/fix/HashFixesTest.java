package org.svip.repair.fix;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.ResultFactory;
import org.svip.metrics.resultfactory.enumerations.INFO;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbom.model.uids.Hash.Algorithm;
import org.svip.serializers.deserializer.SPDX23JSONDeserializer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HashFixesTest {

    public static HashFixes hashFixes;
    private static ResultFactory resultFactory;
    private static SBOM sbom;

    private static final String CDX_14_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/resources/serializers/cdx_json/sbom.alpine.json";
    private static final String SPDX23_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/resources/serializers/spdx_json/syft-0.80.0-source-spdx-json.json";
    private static final String HASH_VALUE = "5eb63bbbe01eeed093cb22bb8f5acdc3";

    @BeforeAll
    static void setup() throws Exception {
        hashFixes = new HashFixes();
        resultFactory = new ResultFactory("Hash_Test",
                ATTRIBUTE.UNIQUENESS, ATTRIBUTE.MINIMUM_ELEMENTS);
        SPDX23JSONDeserializer spdx23JSONDeserializer = new SPDX23JSONDeserializer();
        sbom = spdx23JSONDeserializer.readFromString(Files.readString(Path.of(SPDX23_JSON_SBOM)));
    }

    @Test
    public void no_algorithm_or_hash_match_test() {
        Result result = resultFactory.fail("SHA1", INFO.INVALID, "invalid", "component");
        List<Fix<Hash>> fixes = hashFixes.fix(result, sbom, "repairSubType");

        for (Fix<Hash> fix : fixes) {
            assertEquals(new Hash(Algorithm.SHA1, "invalid"), fix.getOld());
            assertNull(fix.getFixed());
        }
    }

    // Need to test that SPDX exclusive algorithms were removed when a CDX SBOM is used
    @Test
    public void fix_unknown_hash_algorithm_test() {
        Result result = resultFactory.fail("Unknown", INFO.INVALID, HASH_VALUE, "component");
        List<Fix<Hash>> fixes = hashFixes.fix(result, sbom, "repairSubType");

        Set<Algorithm> validAlgorithms =
                new HashSet<>(Arrays.asList(Algorithm.MD2, Algorithm.MD4, Algorithm.MD5, Algorithm.MD6));

        for (Fix<Hash> fix : fixes) {
            assertEquals(new Hash(Algorithm.UNKNOWN, HASH_VALUE), fix.getOld());
            assertTrue(validAlgorithms.contains(fix.getFixed().getAlgorithm()));
        }
    }

    @Test
    public void fix_invalid_hash_value_test() {
        Result result = resultFactory.fail("SHA1", INFO.INVALID, HASH_VALUE, "component");
        List<Fix<Hash>> fixes = hashFixes.fix(result, sbom, "repairSubType");

        Set<Algorithm> validAlgorithms =
                new HashSet<>(Arrays.asList(Algorithm.MD2, Algorithm.MD4, Algorithm.MD5, Algorithm.MD6));

        for (Fix<Hash> fix : fixes) {
            assertEquals(new Hash(Algorithm.SHA1, HASH_VALUE), fix.getOld());
            assertTrue(validAlgorithms.contains(fix.getFixed().getAlgorithm()));
        }
    }

}
