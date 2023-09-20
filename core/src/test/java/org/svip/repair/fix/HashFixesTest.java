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
import org.svip.serializers.deserializer.CDX14JSONDeserializer;
import org.svip.serializers.deserializer.SPDX23JSONDeserializer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

class HashFixesTest {

    private static HashFixes hashFixes;
    private static ResultFactory resultFactory;
    private static SBOM cdxSbom;
    private static SBOM spdxSbom;

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
        CDX14JSONDeserializer cdx14JSONDeserializer = new CDX14JSONDeserializer();
        SPDX23JSONDeserializer spdx23JSONDeserializer = new SPDX23JSONDeserializer();
        cdxSbom = cdx14JSONDeserializer.readFromString(Files.readString(Path.of(CDX_14_JSON_SBOM)));
        spdxSbom = spdx23JSONDeserializer.readFromString(Files.readString(Path.of(SPDX23_JSON_SBOM)));
    }

    @Test
    public void fixUnknownAlgorithmTest() {
        Result result = resultFactory.fail("Unknown", INFO.INVALID, HASH_VALUE, "component");
        List<Fix<Hash>> fixes = hashFixes.fix(result, spdxSbom, "repairSubType");

        Set<Algorithm> validAlgorithms =
                new HashSet<>(Arrays.asList(Algorithm.MD2, Algorithm.MD4, Algorithm.MD5, Algorithm.MD6));

        for (Fix<Hash> fix : fixes) {
            assertEquals(new Hash(Algorithm.UNKNOWN, HASH_VALUE), fix.getOld());
            assertTrue(validAlgorithms.contains(fix.getFixed().getAlgorithm()));
        }
    }

    @Test
    public void fixInvalidHashTest() {
        Result result = resultFactory.fail("SHA1", INFO.INVALID, HASH_VALUE, "component");
        List<Fix<Hash>> fixes = hashFixes.fix(result, spdxSbom, "repairSubType");

        Set<Algorithm> validAlgorithms =
                new HashSet<>(Arrays.asList(Algorithm.MD2, Algorithm.MD4, Algorithm.MD5, Algorithm.MD6));

        for (Fix<Hash> fix : fixes) {
            assertEquals(new Hash(Algorithm.SHA1, HASH_VALUE), fix.getOld());
            assertTrue(validAlgorithms.contains(fix.getFixed().getAlgorithm()));
        }
    }

    @Test
    public void noAlgorithmOrHashMatchTest() {
        Result result = resultFactory.fail("SHA1", INFO.INVALID, "invalid", "component");
        List<Fix<Hash>> fixes = hashFixes.fix(result, spdxSbom, "repairSubType");

        assertEquals(1, fixes.size());
        assertEquals(new Hash(Algorithm.SHA1, "invalid"), fixes.get(0).getOld());
        assertNull(fixes.get(0).getFixed());
    }

    @Test
    public void cdxSbomTest() {
        Result result = resultFactory.fail("SHA1", INFO.INVALID, HASH_VALUE, "component");
        List<Fix<Hash>> fixes = hashFixes.fix(result, cdxSbom, "repairSubType");

        assertEquals(1, fixes.size());
        assertEquals(new Hash(Algorithm.SHA1, HASH_VALUE), fixes.get(0).getOld());
        assertEquals(new Hash(Algorithm.MD5, HASH_VALUE), fixes.get(0).getFixed());
    }

}
