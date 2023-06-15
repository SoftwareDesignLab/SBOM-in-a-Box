package org.svip.sbomfactory.translators;

import org.cyclonedx.exception.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.SBOM;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * File: SPDXParser.java
 * Tests for SPDXParser class
 *
 * @author Tyler Drake
 */
public class TranslatorSPDXTest extends TranslatorTestCore<TranslatorSPDX> {

    /**
     * Test Constants
     */

    private static final String TEST_SPDX_v2_3_SBOM = "src/test/java/org/svip/sbomfactory/translators/sample_boms/sbom.alpine.2-3.spdx";

    private static final String TEST_SPDX_v2_2_SBOM = "src/test/java/org/svip/sbomfactory/translators/sample_boms/sbom.docker.2-2.spdx";

    private static final String TEST_SPDX_SMALL_v2_2_SBOM = "src/test/java/org/svip/sbomfactory/translators/sample_boms/sbom.docker.2-2_small.spdx";

    private static final String TEST_SPDX_LARGE_v2_3_SBOM = "src/test/java/org/svip/sbomfactory/translators/sample_boms/sbom.python.2-3.spdx";

    private static final String TEST_SBOM_DOESNT_EXIST = "src/test/java/org/svip/sbomfactory/translators/sample_boms/sbom.idontexist.spdx";

    private static final String TEST_SBOM_SPDX_NO_COMPONENTS = "src/test/java/org/svip/sbomfactory/translators/sample_boms/sbom.nocomponents.2-3.spdx";

    private static final String TEST_SBOM_SPDX_EMPTY = "src/test/java/org/svip/sbomfactory/translators/sample_boms/sbom.empty.2-3.spdx";

    protected TranslatorSPDXTest() {
        super(new TranslatorSPDX());
    }

    /**
     * Tests
     */

    @Test
    public void builder_makes_SBOM_test() throws IOException, ParseException, ParserConfigurationException {
        SBOM test = this.TRANSLATOR.translate(TEST_SPDX_v2_3_SBOM);
        assertNotNull(test);
        Assertions.assertEquals(SBOM.Type.SPDX, test.getOriginFormat());
        assertEquals("1", test.getSbomVersion());
        assertEquals("2.3", test.getSpecVersion());
        assertEquals(17, test.getAllComponents().size());
    }

    @Test
    public void builder_makes_SBOM_from_SPDX_2_2_test() throws IOException, ParseException, ParserConfigurationException {
        SBOM test = this.TRANSLATOR.translate(TEST_SPDX_v2_2_SBOM);
        assertNotNull(test);
        assertEquals(SBOM.Type.SPDX, test.getOriginFormat());
        assertEquals("1", test.getSbomVersion());
        assertEquals("2.2", test.getSpecVersion());
        assertEquals(137, test.getAllComponents().size());
    }

//    @Test
//    public void builder_makes_SBOM_from_small_SPDX_2_2_test() throws IOException, ParseException, ParserConfigurationException {
//        SBOM test = this.TRANSLATOR.translate(TEST_SPDX_SMALL_v2_2_SBOM); TODO this file doesn't exist??
//        assertNotNull(test);
//        assertEquals("1", test.getSbomVersion());
//        assertEquals("2.2", test.getSpecVersion());
//        assertEquals(28, test.getAllComponents().size());
//    }


    @Test
    public void builder_makes_large_SBOM_test() throws IOException, ParseException, ParserConfigurationException {
        SBOM test = this.TRANSLATOR.translate(TEST_SPDX_LARGE_v2_3_SBOM);
        assertNotNull(test);
        assertEquals(SBOM.Type.SPDX, test.getOriginFormat());
        assertEquals("1", test.getSbomVersion());
        assertEquals("2.3", test.getSpecVersion());
        assertEquals(433, test.getAllComponents().size());
    }


    @Test
    public void builder_does_not_make_SBOM_from_blank_path() throws IOException, ParseException, ParserConfigurationException {
        SBOM test = this.TRANSLATOR.translate("");
        assertNull(test);
    }

    @Test
    public void builder_does_not_make_SBOM_from_non_existing_file() throws IOException, ParseException, ParserConfigurationException {
        SBOM test = this.TRANSLATOR.translate(TEST_SBOM_DOESNT_EXIST);
        assertNull(test);
    }

    @Test
    public void builder_parses_SBOM_with_no_components() throws IOException, ParseException, ParserConfigurationException {
        SBOM test = this.TRANSLATOR.translate(TEST_SBOM_SPDX_NO_COMPONENTS);
        assertNotNull(test);
        assertEquals(SBOM.Type.SPDX, test.getOriginFormat());
        assertEquals("1", test.getSbomVersion());
        assertEquals("2.3", test.getSpecVersion());
        assertEquals(1, test.getAllComponents().size());
    }

    @Test
    public void builder_parses_SBOM_that_is_empty() throws IOException, ParseException, ParserConfigurationException {
        SBOM test = this.TRANSLATOR.translate(TEST_SBOM_SPDX_EMPTY);
        assertNotNull(test);
        assertEquals(SBOM.Type.SPDX, test.getOriginFormat());
        assertEquals("1", test.getSbomVersion());
        assertEquals(null, test.getSpecVersion());
        assertEquals(1, test.getAllComponents().size());
    }
}
