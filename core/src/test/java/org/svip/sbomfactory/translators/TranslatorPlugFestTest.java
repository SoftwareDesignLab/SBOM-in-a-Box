package org.svip.sbomfactory.translators;

import org.junit.jupiter.api.Test;

import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.translators.TranslatorPlugFest;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * File: TranslatorPlugFestTest.java
 * Tests for TranslatorPlugFest
 *
 * @author Tyler Drake
 * @author Asa Horn
 * @author Matt London
 */
public class TranslatorPlugFestTest {

    /**
     * Test Constants
     */
    private static final String TEST_JSON ="src/test/java/org/nvip/plugfest/tooling/sample_boms/cdx_json/trivy-0.39.0_celery-3.1.cdx.json";

    private static final String TEST_XML = "src/test/java/org/nvip/plugfest/tooling/sample_boms/sbom.alpine.xml";

    private static final String TEST_SPDX = "src/test/java/org/nvip/plugfest/tooling/sample_boms/sbom.docker.2-2.spdx";

    /**
     * Expected Results
     */
    private static final int EXPECTED_XML_COMPONENTS = 18;

    private static final int EXPECTED_JSON_COMPONENTS = 124;

    private static final int EXPECTED_SPDX_COMPONENTS = 137;


    @Test
    public void driver_translates_xml() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_XML);
        assertNotNull(sbom);
        assertEquals(EXPECTED_XML_COMPONENTS, sbom.getAllComponents().size());
    }

    @Test
    public void driver_translates_json() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_JSON);
        assertNotNull(sbom);
        assertEquals(EXPECTED_JSON_COMPONENTS, sbom.getAllComponents().size());
    }

    @Test
    public void driver_translates_spdx() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_SPDX);
        assertNotNull(sbom);
        assertEquals(EXPECTED_SPDX_COMPONENTS, sbom.getAllComponents().size());
    }

    @Test
    public void driver_translates_xml_content() {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(TEST_XML)));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        SBOM sbom = TranslatorPlugFest.translateContents(content, TEST_XML);
        assertNotNull(sbom);
        assertEquals(EXPECTED_XML_COMPONENTS, sbom.getAllComponents().size());
    }

    @Test
    public void driver_translates_json_content() {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(TEST_JSON)));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        SBOM sbom = TranslatorPlugFest.translateContents(content, TEST_JSON);
        assertNotNull(sbom);
        assertEquals(EXPECTED_JSON_COMPONENTS, sbom.getAllComponents().size());
    }

    @Test
    public void driver_translates_spdx_content() {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(TEST_SPDX)));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        SBOM sbom = TranslatorPlugFest.translateContents(content, TEST_SPDX);
        assertNotNull(sbom);
        assertEquals(EXPECTED_SPDX_COMPONENTS, sbom.getAllComponents().size());
    }


    @Test
    public void driver_translates_xml_supplier() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_XML);
        assertNotNull(sbom);
        assertEquals("anchore", sbom.getSupplier());
    }

    @Test
    public void driver_translates_json_supplier() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_JSON);
        assertNotNull(sbom);
        assertEquals("[org.cyclonedx.model.Tool@9e23bc53]", sbom.getSupplier());
    }

    @Test
    public void driver_translates_spdx_supplier() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_SPDX);
        assertNotNull(sbom);
        assertEquals("spdx-sbom-generator-source-code", sbom.getSupplier());
    }

    @Test
    public void driver_translates_xml_timestamp() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_XML);
        assertNotNull(sbom);
        assertEquals("2023-02-21T08:50:33-05:00", sbom.getTimestamp());
    }

    @Test
    public void driver_translates_json_timestamp() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_JSON);
        assertNotNull(sbom);
        assertEquals("Wed Apr 05 12:49:04 EDT 2023", sbom.getTimestamp());
    }

    @Test
    public void driver_translates_spdx_timestamp() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_SPDX);
        assertNotNull(sbom);
        assertEquals("2023-03-10T18:48:20Z", sbom.getTimestamp());
    }

    @Test
    public void driver_translates_xml_format() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_XML);
        assertNotNull(sbom);
        assertEquals("CYCLONE_DX", sbom.getOriginFormat().toString());
    }

    @Test
    public void driver_translates_json_format() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_JSON);
        assertNotNull(sbom);
        assertEquals("CYCLONE_DX", sbom.getOriginFormat().toString());
    }

    @Test
    public void driver_translates_spdx_format() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_SPDX);
        assertNotNull(sbom);
        assertEquals("SPDX", sbom.getOriginFormat().toString());
    }

    @Test
    public void driver_translates_xml_SBOM_version() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_XML);
        assertNotNull(sbom);
        assertEquals("1", sbom.getSbomVersion());
    }

    @Test
    public void driver_translates_json_SBOM_version() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_JSON);
        assertNotNull(sbom);
        assertEquals("1", sbom.getSbomVersion());
    }

    @Test
    public void driver_translates_spdx_SBOM_version() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_SPDX);
        assertNotNull(sbom);
        assertEquals("1", sbom.getSbomVersion());
    }

    @Test
    public void driver_translates_xml_spec_version() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_XML);
        assertNotNull(sbom);
        assertEquals("http://cyclonedx.org/schema/bom/1.4", sbom.getSpecVersion());
    }

    @Test
    public void driver_translates_json_spec_version() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_JSON);
        assertNotNull(sbom);
        assertEquals("1.4", sbom.getSpecVersion());
    }

    @Test
    public void driver_translates_spdx_spec_version() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_SPDX);
        assertNotNull(sbom);
        assertEquals("SPDX-2.2", sbom.getSpecVersion());
    }

    @Test
    public void driver_translates_xml_children() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_SPDX);
        assertNotNull(sbom);
        assertNotNull(sbom.getHeadUUID());
        assertEquals(135, sbom.getChildrenUUIDs(sbom.getHeadUUID()).size());
    }

    @Test
    public void driver_translates_json_spec_children() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_SPDX);
        assertNotNull(sbom);
        assertNotNull(sbom.getHeadUUID());
        assertEquals(135, sbom.getChildrenUUIDs(sbom.getHeadUUID()).size());
    }

    @Test
    public void driver_translates_spdx_children() {
        SBOM sbom = TranslatorPlugFest.translate(TEST_SPDX);
        assertNotNull(sbom);
        assertNotNull(sbom.getHeadUUID());
        assertEquals(135, sbom.getChildrenUUIDs(sbom.getHeadUUID()).size());
    }
}
