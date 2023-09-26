package org.svip.repair.fix;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.uids.License;

import java.net.URL;
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

        // Suggest deleting license if license doesn't exist in
        // https://spdx.org/licenses/.

        String details = result.getDetails();
        String licenseId = details.substring(0, details.indexOf("is"));

        License license = new License(licenseId);

        /*
         * if (license.length() > 1) {
         * return List.of(new Fix<License>(new License(license)));
         * }
         */
        return null;
    }

    public static Map<String, License> getAllLicenses() {
        try {
            URL url = new URL("https://raw.githubusercontent.com/spdx/license-list-data/main/json/licenses.json");
            ObjectMapper mapper = new ObjectMapper();

            JsonNode licensesJson = mapper.readTree(url).get("licenses");
            List<License> licenses = mapper.convertValue(licensesJson,
                    mapper.getTypeFactory().constructCollectionType(List.class, License.class));

            return licenses.stream().collect(Collectors.toMap(License::getId, Function.identity()));
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    public static List<String> getValidId(String deprecatedId) {
        switch (deprecatedId) {
            case "AGPL-1.0":
                return Arrays.asList("AGPL-1.0-only", "AGPL-1.0-or-later");
            case "AGPL-3.0":
                return Arrays.asList("AGPL-3.0-only", "AGPL-3.0-or-later");
            case "BSD-2-Clause-FreeBSD":
                return Arrays.asList("BSD-2-Clause");
            case "BSD-2-Clause-NetBSD":
                return Arrays.asList("BSD-2-Clause");
            case "bzip2-1.0.5":
                return Arrays.asList("bzip2-1.0.6");
            case "eCos-2.0":
                return Arrays.asList("RHeCos-1.1");
            case "GFDL-1.1":
                return Arrays.asList("GFDL-1.1-only", "GFDL-1.1-or-later", "GFDL-1.1-invariants-only",
                        "GFDL-1.1-invariants-or-later", "GFDL-1.1-no-invariants-only",
                        "GFDL-1.1-no-invariants-or-later");
            case "GFDL-1.2":
                return Arrays.asList("GFDL-1.2-only", "GFDL-1.2-or-later", "GFDL-1.2-invariants-only",
                        "GFDL-1.2-invariants-or-later", "GFDL-1.2-no-invariants-only",
                        "GFDL-1.2-no-invariants-or-later");
            case "GFDL-1.3":
                return Arrays.asList("GFDL-1.3-only", "GFDL-1.3-or-later", "GFDL-1.3-invariants-only",
                        "GFDL-1.3-invariants-or-later", "GFDL-1.3-no-invariants-only",
                        "GFDL-1.3-no-invariants-or-later");
            case "GPL-1.0":
                return Arrays.asList("GPL-1.0-only");
            case "GPL-1.0+":
                return Arrays.asList("GPL-1.0-or-later");
            case "GPL-2.0":
                return Arrays.asList("GPL-2.0-only");
            case "GPL-2.0+":
                return Arrays.asList("GPL-2.0-or-later");
            case "GPL-2.0-with-autoconf-exception":
                return Arrays.asList("GPL-2.0-only", "GPL-2.0-or-later");
            case "GPL-2.0-with-bison-exception":
                return Arrays.asList("GPL-2.0-only", "GPL-2.0-or-later");
            case "GPL-2.0-with-classpath-exception":
                return Arrays.asList("GPL-2.0-only", "GPL-2.0-or-later");
            case "GPL-2.0-with-font-exception":
                return Arrays.asList("GPL-2.0-only", "GPL-2.0-or-later");
            case "GPL-2.0-with-GCC-exception":
                return Arrays.asList("GPL-2.0-only", "GPL-2.0-or-later");
            case "GPL-3.0":
                return Arrays.asList("GPL-3.0-only");
            case "GPL-3.0+":
                return Arrays.asList("GPL-3.0-or-later");
            case "GPL-3.0-with-autoconf-exception":
                return Arrays.asList("GPL-3.0-only", "GPL-3.0-or-later");
            case "GPL-3.0-with-GCC-exception":
                return Arrays.asList("GPL-3.0-only", "GPL-3.0-or-later");
            case "LGPL-2.0":
                return Arrays.asList("LGPL-2.0-only");
            case "LGPL-2.0+":
                return Arrays.asList("LGPL-2.0-or-later");
            case "LGPL-2.1":
                return Arrays.asList("LGPL-2.1-only");
            case "LGPL-2.1+":
                return Arrays.asList("LGPL-2.1-or-later");
            case "LGPL-3.0":
                return Arrays.asList("LGPL-3.0-only");
            case "LGPL-3.0+":
                return Arrays.asList("LGPL-3.0-or-later");
            // Licenses below could not be accurately mapped to a proper license
            case "Nunit":
            case "StandardML-NJ":
            case "wxWindows":
                return Collections.emptyList();
            // Return empty if no deprecated license could be mapped
            default:
                return Collections.emptyList();
        }
    }

}
