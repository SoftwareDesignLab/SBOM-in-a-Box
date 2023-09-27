package org.svip.repair.fix;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.uids.License;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Fixes class to generate suggested component license repairs
 */
public class LicenseFixes implements Fixes<License> {

    private final Map<String, License> licenses;

    public LicenseFixes() {
        licenses = getAllLicenses();
    }

    /**
     *
     * @param result        object from quality report
     * @param sbom          sbom from quality report
     * @param repairSubType key from quality report map most directly relating to
     *                      the repair type
     * @return
     */
    @Override
    public List<Fix<License>> fix(Result result, SBOM sbom, String repairSubType) {

        // Get license identifier (name or id) from result message
        String details = result.getDetails();
        String identifier = details.substring(0, details.indexOf("is"));

        License license = new License(identifier);

        // Get list of valid license ids for deprecated license id or name
        List<String> validLicenseIds = validLicenseIds(identifier);

        // Suggest deleting the license as a fix if no valid license exists for the deprecated license
        if (validLicenseIds.isEmpty()) {
            return Collections.singletonList(new Fix<>(license, null));
        }

        // Return the list of fixes of possible valid licenses
        List<Fix<License>> fixes = new ArrayList<>();
        for (String licenseId : validLicenseIds) {
            License fixedLicense = licenses.get(licenseId);
            fixes.add(new Fix<>(license, fixedLicense));
        }
        return fixes;

    }

    /**
     *
     * @return
     */
    private Map<String, License> getAllLicenses() {
        try {
            URL url = new URL("https://raw.githubusercontent.com/spdx/license-list-data/main/json/licenses.json");
            ObjectMapper mapper = new ObjectMapper();

            JsonNode licensesJson = mapper.readTree(url).get("licenses");
            List<License> licenses = mapper.convertValue(licensesJson,
                    mapper.getTypeFactory().constructCollectionType(List.class, License.class));

            return licenses.stream().collect(Collectors.toMap(License::getId, Function.identity()));
        } catch (Exception e) {
            throw new RuntimeException("URL to fetch all licenses is invalid");
        }
    }

    /**
     *
     * @param deprecatedIdentifier
     * @return
     */
    private List<String> validLicenseIds(String deprecatedIdentifier) {
        switch (deprecatedIdentifier) {
            case "AGPL-1.0":
            case "Affero General Public License v1.0":
                return List.of("AGPL-1.0-only", "AGPL-1.0-or-later");
            case "AGPL-3.0":
            case "GNU Affero General Public License v3.0":
                return List.of("AGPL-3.0-only", "AGPL-3.0-or-later");
            // Grouping cases to return the same list for multiple deprecated IDs
            case "BSD-2-Clause-FreeBSD":
            case "BSD-2-Clause-NetBSD":
            case "BSD 2-Clause FreeBSD License":
            case "BSD 2-Clause NetBSD License":
                return List.of("BSD-2-Clause");
            case "bzip2-1.0.5":
            case "bzip2 and libbzip2 License v1.0.5":
                return List.of("bzip2-1.0.6");
            case "eCos-2.0":
            case "eCos license version 2.0":
                return List.of("RHeCos-1.1");
            case "GFDL-1.1":
            case "GNU Free Documentation License v1.1":
                return List.of("GFDL-1.1-only", "GFDL-1.1-or-later", "GFDL-1.1-invariants-only",
                        "GFDL-1.1-invariants-or-later", "GFDL-1.1-no-invariants-only",
                        "GFDL-1.1-no-invariants-or-later");
            case "GFDL-1.2":
            case "GNU Free Documentation License v1.2":
                return List.of("GFDL-1.2-only", "GFDL-1.2-or-later", "GFDL-1.2-invariants-only",
                        "GFDL-1.2-invariants-or-later", "GFDL-1.2-no-invariants-only",
                        "GFDL-1.2-no-invariants-or-later");
            case "GFDL-1.3":
            case "GNU Free Documentation License v1.3":
                return List.of("GFDL-1.3-only", "GFDL-1.3-or-later", "GFDL-1.3-invariants-only",
                        "GFDL-1.3-invariants-or-later", "GFDL-1.3-no-invariants-only",
                        "GFDL-1.3-no-invariants-or-later");
            case "GPL-1.0":
            case "GNU General Public License v1.0 only":
                return List.of("GPL-1.0-only");
            case "GPL-1.0+":
            case "GNU General Public License v1.0 or later":
                return List.of("GPL-1.0-or-later");
            case "GPL-2.0":
            case "GNU General Public License v2.0 only":
                return List.of("GPL-2.0-only");
            case "GPL-2.0+":
            case "GNU General Public License v2.0 or later":
                return List.of("GPL-2.0-or-later");
            // Grouping cases to return the same list for multiple deprecated IDs
            case "GPL-2.0-with-autoconf-exception":
            case "GPL-2.0-with-bison-exception":
            case "GPL-2.0-with-classpath-exception":
            case "GPL-2.0-with-font-exception":
            case "GPL-2.0-with-GCC-exception":
            case "GNU General Public License v2.0 w/Autoconf exception":
            case "GNU General Public License v2.0 w/Bison exception":
            case "GNU General Public License v2.0 w/Classpath exception":
            case "GNU General Public License v2.0 w/Font exception":
            case "GNU General Public License v2.0 w/GCC Runtime Library exception":
                return List.of("GPL-2.0-only", "GPL-2.0-or-later");
            case "GPL-3.0":
            case "GNU General Public License v3.0 only":
                return List.of("GPL-3.0-only");
            case "GPL-3.0+":
            case "GNU General Public License v3.0 or later":
                return List.of("GPL-3.0-or-later");
            // Grouping cases to return the same list for multiple deprecated IDs
            case "GPL-3.0-with-autoconf-exception":
            case "GPL-3.0-with-GCC-exception":
            case "GNU General Public License v3.0 w/Autoconf exception":
            case "GNU General Public License v3.0 w/GCC Runtime Library exception":
                return List.of("GPL-3.0-only", "GPL-3.0-or-later");
            case "LGPL-2.0":
            case "GNU Library General Public License v2 only":
                return List.of("LGPL-2.0-only");
            case "LGPL-2.0+":
            case "GNU Library General Public License v2 or later":
                return List.of("LGPL-2.0-or-later");
            case "LGPL-2.1":
            case "GNU Lesser General Public License v2.1 only":
                return List.of("LGPL-2.1-only");
            case "LGPL-2.1+":
            case "GNU Lesser General Public License v2.1 or later":
                return List.of("LGPL-2.1-or-later");
            case "LGPL-3.0":
            case "GNU Lesser General Public License v3.0 only":
                return List.of("LGPL-3.0-only");
            case "LGPL-3.0+":
            case "GNU Lesser General Public License v3.0 or later":
                return List.of("LGPL-3.0-or-later");
            // Nunit, StandardML-NJ, wxWindows could not be accurately mapped to a valid license
            default:
                // Return empty if no deprecated license could be mapped
                return Collections.emptyList();
        }
    }

}
