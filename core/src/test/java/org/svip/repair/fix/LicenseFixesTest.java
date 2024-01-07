/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
 */

package org.svip.repair.fix;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
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
        List<Fix<License>> fixes = licenseFixes.fix(result, sbom, "repairSubType", 0);

        Fix<License> fix = fixes.get(0);
        assertEquals(new License(LICENSE_ID), fix.old());
        assertEquals("AGPL-1.0-only", fix.fixed().getId());
    }

    @Test
    public void fix_invalid_license_name_test() {
        Result result = resultFactory.fail("license", INFO.INVALID, LICENSE_NAME, "component");
        List<Fix<License>> fixes = licenseFixes.fix(result, sbom, "repairSubType", 0);

        Fix<License> fix = fixes.get(0);
        assertEquals(new License(LICENSE_NAME), fix.old());
        assertEquals("Affero General Public License v1.0 only", fix.fixed().getName());
    }

    @Test
    public void fix_invalid_licenses_for_specific_cases_test() {
        List<String> deprecatedLicenseIds =
                List.of("BSD-2-Clause-FreeBSD", "bzip2-1.0.5", "eCos-2.0", "GPL-2.0-with-autoconf-exception");
        List<String> validLicenseIds =
                List.of("BSD-2-Clause", "bzip2-1.0.6", "RHeCos-1.1", "GPL-2.0-only");

        for (int i = 0; i < deprecatedLicenseIds.size(); i++) {
            Result result = resultFactory.fail("license", INFO.INVALID, deprecatedLicenseIds.get(i), "component");
            Fix<License> fix = licenseFixes.fix(result, sbom, "repairSubType", 0).get(0);
            assertEquals(deprecatedLicenseIds.get(i), fix.old().getId());
            assertEquals(validLicenseIds.get(i), fix.fixed().getId());
        }
    }

    @Test
    public void fix_invalid_licenses_for_general_cases_test() {
        List<String> deprecatedLicenseIds =
                List.of("LGPL-2.0", "LGPL-2.0+", "LGPL-3.0", "LGPL-3.0+", "GPL-2.0", "GPL-2.0+", "GPL-3.0", "GPL-3.0+");
        List<String> validLicenseIds =
                List.of("LGPL-2.0-only", "LGPL-2.0-or-later", "LGPL-3.0-only", "LGPL-3.0-or-later",
                        "GPL-2.0-only", "GPL-2.0-or-later", "GPL-3.0-only", "GPL-3.0-or-later");

        for (int i = 0; i < deprecatedLicenseIds.size(); i++) {
            Result result = resultFactory.fail("license", INFO.INVALID, deprecatedLicenseIds.get(i), "component");
            Fix<License> fix = licenseFixes.fix(result, sbom, "repairSubType", 0).get(0);
            assertEquals(deprecatedLicenseIds.get(i), fix.old().getId());
            assertEquals(validLicenseIds.get(i), fix.fixed().getId());
        }
    }

    @Test
    public void nonexistent_license_identifier_test() {
        Result result = resultFactory.fail("license", INFO.INVALID, "Unknown", "component");
        List<Fix<License>> fixes = licenseFixes.fix(result, sbom, "repairSubType", 0);

        Fix<License> fix = fixes.get(0);
        assertEquals(new License("Unknown"), fix.old());
        assertNull(fix.fixed());
    }

    @Test
    public void no_mapping_for_deprecated_license_test() {
        Result result = resultFactory.fail("license", INFO.INVALID, "Nunit", "component");
        List<Fix<License>> fixes = licenseFixes.fix(result, sbom, "repairSubType", 0);

        Fix<License> fix = fixes.get(0);
        assertEquals(new License("Nunit"), fix.old());
        assertNull(fix.fixed());
    }

    @Test
    public void fix_valid_license_test() {
        List<String> validLicenseIds =
                List.of("BSD-2-Clause", "bzip2-1.0.6", "RHeCos-1.1", "GPL-2.0-only",
                        "LGPL-2.0-only", "LGPL-2.0-or-later", "LGPL-3.0-only", "LGPL-3.0-or-later",
                        "GPL-2.0-only", "GPL-2.0-or-later", "GPL-3.0-only", "GPL-3.0-or-later");

        for (String validLicenseId : validLicenseIds) {
            Result result = resultFactory.pass("license", INFO.VALID, validLicenseId, "component");
            Fix<License> fix = licenseFixes.fix(result, sbom, "repairSubType", 0).get(0);
            assertEquals(validLicenseId, fix.old().getId());
            assertNull(fix.fixed());
        }
    }

}
