package org.svip.repair.fix;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.uids.License;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File: LicenseFixes.java
 * Fixes class to generate suggested component license repairs
 *
 * @author Jordan Wong
 * @author Hubert Liang
 */
public class LicenseFixes implements Fixes<License> {

    private static String LICENSES_URL = "https://raw.githubusercontent.com/spdx/license-list-data/main/json/licenses.json";

    /**
     * Get a list of possible license fixes for invalid licenses.
     *
     * @param result        object from quality report
     * @param sbom          sbom from quality report
     * @param repairSubType key from quality report map most directly relating to the repair type
     * @return list of license fixes
     */
    @Override
    public List<Fix<License>> fix(Result result, SBOM sbom, String repairSubType) {
        // Get license identifier (name or id) from result message
        String details = result.getDetails();
        String identifier = details.substring(0, details.indexOf("is") - 1);

        License license = new License(identifier);

        return Collections.singletonList(new Fix<>(license, getValidLicense(license)));
    }

    /**
     * Get a map of all licenses (valid and deprecated).
     *
     * @return map of all licenses
     */
    private Map<String, License> getAllLicenses() {
        try {
            URL url = new URL(LICENSES_URL);
            ObjectMapper mapper = new ObjectMapper();

            JsonNode licensesJson = mapper.readTree(url).get("licenses");
            List<License> licenseList = mapper.convertValue(licensesJson,
                    mapper.getTypeFactory().constructCollectionType(List.class, License.class));

            Map<String, License> licenseMap = new HashMap<>();
            for (License license : licenseList) {
                licenseMap.put(license.getId(), license);
                licenseMap.put(license.getName(), license);
            }

            return licenseMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a list of valid license ids given a deprecated license id or name.
     *
     * @param deprecated the deprecated License
     * @return list of valid license ids
     */
    private License getValidLicense(License deprecated) {
        Map<String,License> licenseMap = getAllLicenses();

        // Return null if the license id is not found in licenseMap
        if (!licenseMap.containsKey(deprecated.getIdentifier())) {
            return null;
        }
        String deprecatedId = licenseMap.get(deprecated.getIdentifier()).getId();

        // Specific deprecated license cases
        if (deprecatedId.startsWith("BSD-2")) {
            return licenseMap.get("BSD-2-Clause");
        }
        if (deprecatedId.startsWith("bzip2")) {
            return licenseMap.get("bzip2-1.0.6");
        }
        if (deprecatedId.startsWith("eCos")) {
            return licenseMap.get("RHeCos-1.1");
        }
        if (deprecatedId.startsWith("GPL-2.0-with") || deprecatedId.startsWith("GPL-3.0-with")) {
            return licenseMap.get(deprecatedId + "-only");
        }

        // General deprecated license cases
        if (Character.isDigit(deprecatedId.charAt(deprecatedId.length() - 1))) {
            return licenseMap.get(deprecatedId + "-only");
        }
        if (deprecatedId.endsWith("+")) {
            return licenseMap.get(deprecatedId + "-or-later");
        }

        // Nunit, StandardML-NJ, wxWindows could not be accurately mapped to a valid license
        return null;
    }

}
