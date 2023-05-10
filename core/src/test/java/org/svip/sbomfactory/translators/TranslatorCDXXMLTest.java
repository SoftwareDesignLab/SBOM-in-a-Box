/**
 * @file TranslatorCDXTest.java
 *
 * Test set for TranslatorCDX class
 *
 * @author Tyler Drake
 */

package org.svip.sbomfactory.translators;

import org.cyclonedx.exception.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.svip.sbom.model.SBOM;
import org.svip.sbom.model.SBOMType;

import static org.junit.jupiter.api.Assertions.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * File: TranslatorCDXXMLTest.java
 * Tests for TranslatorCDXXML
 *
 * @author Tyler Drake
 */
public class TranslatorCDXXMLTest extends TranslatorTestCore<TranslatorCDXXML> {

    public static final String TEST_SMALL_CDX = "src/test/java/org/svip/sbomfactory/translators/sample_boms/sbom.alpine.xml";
    public static final String TEST_LARGE_CDX = "src/test/java/org/svip/sbomfactory/translators/sample_boms/sbom.python.xml";
    public static final String TEST_NO_METADATA_CDX = "src/test/java/org/svip/sbomfactory/translators/sample_boms/sbom.nometadata.xml";
    public static final String TEST_NO_COMPONENTS_CDX = "src/test/java/org/svip/sbomfactory/translators/sample_boms/sbom.nocomponents.xml";

    protected TranslatorCDXXMLTest() {
        super(new TranslatorCDXXML());
    }


    @Test
    public void translatorcdx_small_file_test() throws ParserConfigurationException, IOException, ParseException {
        SBOM sbom = this.TRANSLATOR.translate(TEST_SMALL_CDX);
        assertNotNull(sbom);
        Assertions.assertEquals(SBOMType.CYCLONE_DX, sbom.getOriginFormat());
        assertEquals("1", sbom.getSbomVersion());
        assertEquals("http://cyclonedx.org/schema/bom/1.4", sbom.getSpecVersion());
        assertEquals(18, sbom.getAllComponents().size());
    }

    @Test
    public void translatorcdx_large_file_test() throws ParserConfigurationException, IOException, ParseException {
        SBOM sbom = this.TRANSLATOR.translate(TEST_LARGE_CDX);
        assertNotNull(sbom);
        assertEquals(SBOMType.CYCLONE_DX, sbom.getOriginFormat());
        assertEquals("1", sbom.getSbomVersion());
        assertEquals("http://cyclonedx.org/schema/bom/1.4", sbom.getSpecVersion());
        assertEquals(434, sbom.getAllComponents().size());
    }

    @Test
    public void translatorcdx_no_metadata_test() throws ParserConfigurationException, IOException, ParseException {
        SBOM sbom = this.TRANSLATOR.translate(TEST_NO_METADATA_CDX);
        assertNull(sbom);
    }

    @Test
    public void translatorcdx_no_components_test() throws ParserConfigurationException, IOException, ParseException {
        SBOM sbom = this.TRANSLATOR.translate(TEST_NO_COMPONENTS_CDX);
        assertNotNull(sbom);
        // Should be 1 component for head component
        assertEquals(SBOMType.CYCLONE_DX, sbom.getOriginFormat());
        assertEquals("1", sbom.getSbomVersion());
        assertEquals("http://cyclonedx.org/schema/bom/1.4", sbom.getSpecVersion());
        assertEquals(1, sbom.getAllComponents().size());
    }

}
