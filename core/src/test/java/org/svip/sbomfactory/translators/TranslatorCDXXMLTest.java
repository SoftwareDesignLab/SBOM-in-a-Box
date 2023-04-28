/**
 * @file TranslatorCDXTest.java
 *
 * Test set for TranslatorCDX class
 *
 * @author Tyler Drake
 */

package org.svip.sbomfactory.translators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.svip.sbom.model.SBOM;
import org.svip.sbom.model.SBOMType;
import org.svip.sbomfactory.translators.TranslatorCDXXML;

import static org.junit.jupiter.api.Assertions.*;

import javax.xml.parsers.ParserConfigurationException;

/**
 * File: TranslatorCDXXMLTest.java
 * Tests for TranslatorCDXXML
 *
 * @author Tyler Drake
 */
public class TranslatorCDXXMLTest {

    public static final String test_small_cdx = "src/test/java/org/nvip/plugfest/tooling/sample_boms/sbom.alpine.xml";
    public static final String test_large_cdx = "src/test/java/org/nvip/plugfest/tooling/sample_boms/sbom.python.xml";
    public static final String test_no_metadata_cdx = "src/test/java/org/nvip/plugfest/tooling/sample_boms/sbom.nometadata.xml";
    public static final String test_no_components_cdx = "src/test/java/org/nvip/plugfest/tooling/sample_boms/sbom.nocomponents.xml";


    @Test
    public void translatorcdx_small_file_test() throws ParserConfigurationException {
        SBOM sbom = TranslatorCDXXML.translatorCDXXML(test_small_cdx.toString());
        assertNotNull(sbom);
        Assertions.assertEquals(SBOMType.CYCLONE_DX, sbom.getOriginFormat());
        assertEquals("1", sbom.getSbomVersion());
        assertEquals("http://cyclonedx.org/schema/bom/1.4", sbom.getSpecVersion());
        assertEquals(18, sbom.getAllComponents().size());
    }

    @Test
    public void translatorcdx_large_file_test() throws ParserConfigurationException {
        SBOM sbom = TranslatorCDXXML.translatorCDXXML(test_large_cdx.toString());
        assertNotNull(sbom);
        assertEquals(SBOMType.CYCLONE_DX, sbom.getOriginFormat());
        assertEquals("1", sbom.getSbomVersion());
        assertEquals("http://cyclonedx.org/schema/bom/1.4", sbom.getSpecVersion());
        assertEquals(434, sbom.getAllComponents().size());
    }

    @Test
    public void translatorcdx_no_metadata_test() throws ParserConfigurationException {
        SBOM sbom = TranslatorCDXXML.translatorCDXXML(test_no_metadata_cdx.toString());
        assertNull(sbom);
    }

    @Test
    public void translatorcdx_no_components_test() throws ParserConfigurationException {
        SBOM sbom = TranslatorCDXXML.translatorCDXXML(test_no_components_cdx.toString());
        assertNotNull(sbom);
        // Should be 1 component for head component
        assertEquals(SBOMType.CYCLONE_DX, sbom.getOriginFormat());
        assertEquals("1", sbom.getSbomVersion());
        assertEquals("http://cyclonedx.org/schema/bom/1.4", sbom.getSpecVersion());
        assertEquals(1, sbom.getAllComponents().size());
    }

}
