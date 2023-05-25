package org.svip.sbomfactory.translators;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.SBOM;
import org.svip.sbom.model.SBOMType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TranslatorControllerTest {

    public static final String TEST_SMALL_CDX_JSON = "src/test/java/org/svip/sbomfactory/translators/sample_boms/cdx_json/sbom.alpine.json";

    public static final String TEST_SMALL_CDX_XML = "src/test/java/org/svip/sbomfactory/translators/sample_boms/sbom.alpine.xml";

    private static final String TEST_SMALL_SPDX_TAG = "src/test/java/org/svip/sbomfactory/translators/sample_boms/sbom.alpine.2-3.spdx";

    @Test
    public void controller_builds_cdx_json_test() {
        SBOM sbom = TranslatorController.toSBOM("", TEST_SMALL_CDX_JSON);
        assertNotNull(sbom);
        assertEquals(SBOMType.CYCLONE_DX, sbom.getOriginFormat());
        assertEquals("1", sbom.getSbomVersion());
        assertEquals("1.4", sbom.getSpecVersion());
        assertEquals(18, sbom.getAllComponents().size());
    }

    @Test
    public void controller_builds_cdx_xml_test() {
        SBOM sbom = TranslatorController.toSBOM("", TEST_SMALL_CDX_XML);
        assertNotNull(sbom);
        assertEquals(SBOMType.CYCLONE_DX, sbom.getOriginFormat());
        assertEquals("1", sbom.getSbomVersion());
        assertEquals("1.4", sbom.getSpecVersion());
        assertEquals(18, sbom.getAllComponents().size());
    }

    @Test
    public void controller_builds_spdx_tag_test() {
        SBOM sbom = TranslatorController.toSBOM("", TEST_SMALL_SPDX_TAG);
        assertNotNull(sbom);
        assertEquals(SBOMType.SPDX, sbom.getOriginFormat());
        assertEquals("1", sbom.getSbomVersion());
        assertEquals("2.3", sbom.getSpecVersion());
        assertEquals(17, sbom.getAllComponents().size());
    }

}
