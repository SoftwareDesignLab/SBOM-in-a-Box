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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        Set<String> validLicenseIds = new HashSet<>(Arrays.asList("AGPL-1.0-only", "AGPL-1.0-or-later"));

        for (Fix<License> fix : fixes) {
            assertEquals(new License(LICENSE_ID), fix.old());
            assertTrue(validLicenseIds.contains(fix.fixed().getId()));
        }
    }

    @Test
    public void fix_invalid_license_name_test() {
        Result result = resultFactory.fail("license", INFO.INVALID, LICENSE_NAME, "component");
        List<Fix<License>> fixes = licenseFixes.fix(result, sbom, "repairSubType");

        Set<String> validLicenseIds = new HashSet<>(Arrays.asList("AGPL-1.0-only", "AGPL-1.0-or-later"));

        for (Fix<License> fix : fixes) {
            assertEquals(new License(LICENSE_NAME), fix.old());
            assertTrue(validLicenseIds.contains(fix.fixed().getId()));
        }
    }

    @Test
    public void no_valid_license_fix_test() {
        Result result = resultFactory.fail("license", INFO.INVALID, "Unknown", "component");
        List<Fix<License>> fixes = licenseFixes.fix(result, sbom, "repairSubType");

        assertEquals(1, fixes.size());
        assertEquals(new License("Unknown"), fixes.get(0).old());
        assertNull(fixes.get(0).fixed());
    }

    @Test
    public void fix_all_possible_licenses_test() {
        Map<String, License> licenses = licenseFixes.getAllLicenses();

        for (String licenseId : licenses.keySet()) {
            Result result = resultFactory.fail("license", INFO.INVALID, licenseId, "component");
            assertNotNull(licenseFixes.fix(result, sbom, "repairSubType"));
        }
    }

}
