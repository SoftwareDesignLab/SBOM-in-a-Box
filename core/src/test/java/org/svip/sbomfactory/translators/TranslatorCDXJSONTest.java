package org.svip.sbomfactory.translators;

import org.cyclonedx.exception.ParseException;
import org.junit.jupiter.api.Test;

import org.svip.sbom.model.SBOM;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * File: TranslatorCDXJSONTest.java
 * Tests for TranslatorCDXJSON
 *
 * @author Tyler Drake
 */
public class TranslatorCDXJSONTest extends TranslatorTestCore<TranslatorCDXJSON> {

    public static final String TEST_SMALL_CDX_JSON = "src/test/java/org/svip/sbomfactory/translators/sample_boms/cdx_json/sbom.alpine.json";

    public static final String TEST_MEDIUM_CDX_JSON ="src/test/java/org/svip/sbomfactory/translators/sample_boms/cdx_json/trivy-0.39.0_celery-3.1.cdx.json";

    public static final String TEST_ANOTHER_SMALL_SYFT_CDX_JSON = "src/test/java/org/svip/sbomfactory/translators/sample_boms/cdx_json/cdx.json";

    // Create instance of translator on test initialization
    protected TranslatorCDXJSONTest() {
        super(new TranslatorCDXJSON());
    }

    @Test
    public void build_SBOM_from_small_cdx_json_test() throws IOException, ParseException, ParserConfigurationException {
        SBOM sbom = this.TRANSLATOR.translate(TEST_SMALL_CDX_JSON);
        assertNotNull(sbom);
        assertEquals("1", sbom.getSbomVersion());
        assertEquals("1.4", sbom.getSpecVersion());
        assertEquals(18, sbom.getAllComponents().size());
    }

    @Test
    public void build_SBOM_from_medium_cdx_json_test() throws IOException, ParseException, ParserConfigurationException {
        SBOM sbom = this.TRANSLATOR.translate(TEST_MEDIUM_CDX_JSON);
        assertNotNull(sbom);
        assertEquals("1", sbom.getSbomVersion());
        assertEquals("1.4", sbom.getSpecVersion());
        assertEquals(124, sbom.getAllComponents().size());
    }

    @Test
    public void build_SBOM_from_another_small_syft_json_test() throws IOException, ParseException, ParserConfigurationException {
        SBOM sbom = this.TRANSLATOR.translate(TEST_ANOTHER_SMALL_SYFT_CDX_JSON);
        assertNotNull(sbom);
        assertEquals("1", sbom.getSbomVersion());
        assertEquals("1.4", sbom.getSpecVersion());
        assertEquals(48, sbom.getAllComponents().size());
    }

}
