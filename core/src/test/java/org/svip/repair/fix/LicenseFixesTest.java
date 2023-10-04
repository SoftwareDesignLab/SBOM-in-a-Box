package org.svip.repair.fix;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.ResultFactory;
import org.svip.metrics.resultfactory.enumerations.INFO;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.uids.License;
import org.svip.serializers.deserializer.CDX14JSONDeserializer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LicenseFixesTest {

    private static LicenseFixes licenseFixes;
    private static ResultFactory resultFactory;
    private static SBOM sbom;

    private static final String CDX_14_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/resources/serializers/cdx_json/sbom.alpine.json";
    private static final String LICENSE_ID = "AGPL-1.0";
    private static final String LICENSE_NAME = "Affero General Public License v1.0";

    @BeforeAll
    static void setup() throws Exception {
        licenseFixes = new LicenseFixes();
        resultFactory = new ResultFactory("Valid SPDX License",
                ATTRIBUTE.COMPLETENESS, ATTRIBUTE.UNIQUENESS, ATTRIBUTE.MINIMUM_ELEMENTS);
        CDX14JSONDeserializer cdx14JSONDeserializer = new CDX14JSONDeserializer();
        sbom = cdx14JSONDeserializer.readFromString(Files.readString(Path.of(CDX_14_JSON_SBOM)));
    }

    @Test
    public void fix_invalid_license_id_test() {
        Result result = resultFactory.fail("license", INFO.INVALID, LICENSE_ID, "component");
        List<Fix<License>> fixes = licenseFixes.fix(result, sbom, "repairSubType");

        Fix<License> fix = fixes.get(0);
        assertEquals(new License(LICENSE_ID), fix.old());
        assertEquals("AGPL-1.0-only", fix.fixed().getId());
    }

    @Test
    public void fix_invalid_license_name_test() {
        Result result = resultFactory.fail("license", INFO.INVALID, LICENSE_NAME, "component");
        List<Fix<License>> fixes = licenseFixes.fix(result, sbom, "repairSubType");

        Fix<License> fix = fixes.get(0);
        assertEquals(new License(LICENSE_NAME), fix.old());
        assertEquals("Affero General Public License v1.0 only", fix.fixed().getName());
    }

    @Test
    public void nonexistent_license_identifier_test() {
        Result result = resultFactory.fail("license", INFO.INVALID, "Unknown", "component");
        List<Fix<License>> fixes = licenseFixes.fix(result, sbom, "repairSubType");

        Fix<License> fix = fixes.get(0);
        assertEquals(new License("Unknown"), fix.old());
        assertNull(fix.fixed());
    }

    @Test
    public void no_mapping_for_deprecated_license_test() {
        Result result = resultFactory.fail("license", INFO.INVALID, "Nunit", "component");
        List<Fix<License>> fixes = licenseFixes.fix(result, sbom, "repairSubType");

        Fix<License> fix = fixes.get(0);
        assertEquals(new License("Nunit"), fix.old());
        assertNull(fix.fixed());
    }

}
