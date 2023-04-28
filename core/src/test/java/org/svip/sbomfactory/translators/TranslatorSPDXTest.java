package org.svip.sbomfactory.translators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.svip.sbom.model.SBOM;
import org.svip.sbom.model.SBOMType;
import org.svip.sbomfactory.translators.TranslatorSPDX;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * File: SPDXParser.java
 * Tests for SPDXParser class
 *
 * @author Tyler Drake
 */
public class TranslatorSPDXTest {

    /**
     * Test Constants
     */

    private static final String TEST_SPDX_v2_3_SBOM = "src/test/java/org/nvip/plugfest/tooling/sample_boms/sbom.alpine.2-3.spdx";

    private static final String TEST_SPDX_v2_2_SBOM = "src/test/java/org/nvip/plugfest/tooling/sample_boms/sbom.docker.2-2.spdx";

    private static final String TEST_SPDX_LARGE_v2_3_SBOM = "src/test/java/org/nvip/plugfest/tooling/sample_boms/sbom.python.2-3.spdx";

    private static final String TEST_SBOM_DOESNT_EXIST = "src/test/java/org/nvip/plugfest/tooling/sample_boms/sbom.idontexist.spdx";

    private static final String TEST_SBOM_SPDX_NO_COMPONENTS = "src/test/java/org/nvip/plugfest/tooling/sample_boms/sbom.nocomponents.2-3.spdx";

    private static final String TEST_SBOM_SPDX_EMPTY = "src/test/java/org/nvip/plugfest/tooling/sample_boms/sbom.empty.2-3.spdx";

    /**
     * Tests
     */

    @Test
    public void builder_makes_SBOM_test() throws IOException {
        SBOM test = TranslatorSPDX.translatorSPDX(TEST_SPDX_v2_3_SBOM);
        assertNotNull(test);
        Assertions.assertEquals(SBOMType.SPDX, test.getOriginFormat());
        assertEquals("1", test.getSbomVersion());
        assertEquals("SPDX-2.3", test.getSpecVersion());
        assertEquals(94, test.getAllComponents().size());
    }

    @Test
    public void builder_makes_SBOM_from_SPDX_2_2_test() throws IOException {
        SBOM test = TranslatorSPDX.translatorSPDX(TEST_SPDX_v2_2_SBOM);
        assertNotNull(test);
        assertEquals(SBOMType.SPDX, test.getOriginFormat());
        assertEquals("1", test.getSbomVersion());
        assertEquals("SPDX-2.2", test.getSpecVersion());
        assertEquals(137, test.getAllComponents().size());
    }


    @Test
    public void builder_makes_large_SBOM_test() throws IOException {
        SBOM test = TranslatorSPDX.translatorSPDX(TEST_SPDX_LARGE_v2_3_SBOM);
        assertNotNull(test);
        assertEquals(SBOMType.SPDX, test.getOriginFormat());
        assertEquals("1", test.getSbomVersion());
        assertEquals("SPDX-2.3", test.getSpecVersion());
        assertEquals(19037, test.getAllComponents().size());
    }


    @Test
    public void builder_does_not_make_SBOM_from_blank_path() throws IOException {
        SBOM test = TranslatorSPDX.translatorSPDX("");
        assertNull(test);
    }

    @Test
    public void builder_does_not_make_SBOM_from_non_existing_file() throws IOException {
        SBOM test = TranslatorSPDX.translatorSPDX(TEST_SBOM_DOESNT_EXIST);
        assertNull(test);
    }

    @Test
    public void builder_parses_SBOM_with_no_components() throws IOException {
        SBOM test = TranslatorSPDX.translatorSPDX(TEST_SBOM_SPDX_NO_COMPONENTS);
        assertNotNull(test);
        assertEquals(SBOMType.SPDX, test.getOriginFormat());
        assertEquals("1", test.getSbomVersion());
        assertEquals("SPDX-2.3", test.getSpecVersion());
        assertEquals(1, test.getAllComponents().size());
    }

    @Test
    public void builder_parses_SBOM_that_is_empty() throws IOException {
        SBOM test = TranslatorSPDX.translatorSPDX(TEST_SBOM_SPDX_EMPTY);
        assertNotNull(test);
        assertEquals(SBOMType.SPDX, test.getOriginFormat());
        assertEquals("1", test.getSbomVersion());
        assertEquals(null, test.getSpecVersion());
        assertEquals(0, test.getAllComponents().size());
    }
}
