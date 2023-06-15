package org.svip.sbomfactory.translators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.svip.sbom.model.Component;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.utils.Debug;

import static org.junit.jupiter.api.Assertions.*;
import static org.svip.sbomfactory.translators.Utils.*;


/**
 * File: TranslatorCDXXMLTest.java
 * Tests for TranslatorCDXXML
 *
 * @author Tyler Drake
 */
public class TranslatorCDXXMLTest extends TranslatorTestCore<TranslatorCDXXML> {

    public static final String test_small_cdx = "src/test/java/org/nvip/plugfest/tooling/sample_boms/sbom.alpine.xml";
    public static final String test_large_cdx = "src/test/java/org/nvip/plugfest/tooling/sample_boms/sbom.python.xml";
    public static final String test_no_metadata_cdx = "src/test/java/org/nvip/plugfest/tooling/sample_boms/sbom.alpine.nometadata.xml";
    public static final String test_no_metadata_cdx_two = "src/test/java/org/nvip/plugfest/tooling/sample_boms/gobom-source.xml";
    public static final String test_no_components_cdx = "src/test/java/org/nvip/plugfest/tooling/sample_boms/sbom.nocomponents.xml";
    public static final String TEST_CDX_SBOM_1_2_DEPENDENCIES = "src/test/java/org/nvip/plugfest/tooling/sample_boms/proton-bridge-v1.8.0.bom.xml";

    public static final String TEST_CDX_SBOM_1_4_DEPENDENCIES = "src/test/java/org/nvip/plugfest/tooling/sample_boms/sbom.cdxgen.1-4.xml";


    protected TranslatorCDXXMLTest() {
        super(new TranslatorCDXXML());
    }

    @BeforeEach
    public void setup() {
        this.TRANSLATOR = new TranslatorCDXXML();
    }

//    @Disabled(
//            "Possible Bug: translator doesn't reset after the first run" +
//            "As a result we end up with 18/18 components the first time, but 17/18 the second time."
//    )
    @ParameterizedTest
    @ValueSource(strings = { test_small_cdx, test_no_metadata_cdx })
    public void translatorxml_small_file_test(String pathToSBOM) throws TranslatorException {
        SBOM sbom = this.TRANSLATOR.translate(pathToSBOM);
        assertNotNull(sbom);
        assertEquals(SBOM.Type.CYCLONE_DX, sbom.getOriginFormat());
        assertEquals("1", sbom.getSbomVersion());
        assertEquals("1.4", sbom.getSpecVersion());
        assertEquals(18, sbom.getAllComponents().size());


        if(pathToSBOM.equals(test_small_cdx)) {
            assertEquals(4, sbom.getMetadata().size());
            assertEquals(1, sbom.appTools.size());
            assertFalse(checkForAppTools(sbom));
        }

    }

    @Test
    public void translatorxml_large_file_test() throws TranslatorException {
        SBOM sbom = this.TRANSLATOR.translate(test_large_cdx.toString());
        assertNotNull(sbom);
        assertEquals(SBOM.Type.CYCLONE_DX, sbom.getOriginFormat());
        assertEquals("1", sbom.getSbomVersion());
        assertEquals("1.4", sbom.getSpecVersion());
        assertEquals(434, sbom.getAllComponents().size());
        assertEquals(4, sbom.getMetadata().size());
        assertEquals(1, sbom.appTools.size());

        assertFalse(checkForAppTools(sbom));
    }

    @Test
    public void translatorxml_no_components_test() throws TranslatorException {
        SBOM sbom = this.TRANSLATOR.translate(test_no_components_cdx.toString());
        assertNotNull(sbom);
        // Should be 1 component for head component
        assertEquals(SBOM.Type.CYCLONE_DX, sbom.getOriginFormat());
        assertEquals("1", sbom.getSbomVersion());
        assertEquals("1.4", sbom.getSpecVersion());
        assertEquals(1, sbom.getAllComponents().size());
        assertEquals(4, sbom.getMetadata().size());
        assertEquals(1, sbom.appTools.size());

        assertFalse(checkForAppTools(sbom));
    }

    @Test
    public void translatorxml_v1_2_dependencies_test() throws TranslatorException {
        SBOM sbom = this.TRANSLATOR.translate(TEST_CDX_SBOM_1_2_DEPENDENCIES.toString());
        assertNotNull(sbom);
        assertEquals(202, sbom.getAllComponents().size());
        assertEquals(10, sbom.getMetadata().size());
        assertEquals(1, sbom.appTools.size());

        assertFalse(checkForAppTools(sbom));
    }

    @Test
    public void translatorxml_v1_2_dependencies_other_test() throws TranslatorException {
        SBOM sbom = this.TRANSLATOR.translate(TEST_CDX_SBOM_1_4_DEPENDENCIES);
        assertNotNull(sbom);
        assertEquals(631, sbom.getAllComponents().size());
        assertEquals(10, sbom.getMetadata().size());
        assertEquals(1, sbom.appTools.size());

        assertFalse(checkForAppTools(sbom));
    }

    @Test
    public void sbom_no_metadata_component_should_create_another_component() throws TranslatorException {
        SBOM sbom = this.TRANSLATOR.translate(test_no_metadata_cdx_two);
        assertNotNull(sbom);
        assertEquals(3, sbom.getAllComponents().size());
        assertTrue(noLicensesCheck(sbom));
    }

    @Test
    @DisplayName("Test for null UIDs")
    void nullUIDTest() throws TranslatorException {
        SBOM sbom = this.TRANSLATOR.translate(test_large_cdx);

        for (Component component : sbom.getAllComponents()) {
            Debug.logBlockTitle(component.getName());

            assertFalse(component.getCpes().contains(null));
            Debug.log(Debug.LOG_TYPE.SUMMARY, "Component does not contain null CPEs");

            assertFalse(component.getPurls().contains(null));
            Debug.log(Debug.LOG_TYPE.SUMMARY, "Component does not contain null PURLs");

            assertFalse(component.getSwids().contains(null));
            Debug.log(Debug.LOG_TYPE.SUMMARY, "Component does not contain null SWIDs");

            Debug.logBlock();
        }
    }



}
