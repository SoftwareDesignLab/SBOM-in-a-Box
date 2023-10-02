package org.svip.repair.fix;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.uids.License;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        // Get the valid license id for deprecated license id or name
        String validLicenseId = validLicenseId(nameToId(identifier));

        // Suggest deleting the license as a fix if no valid license exists for the deprecated license
        if (validLicenseId.isEmpty()) {
            return Collections.singletonList(new Fix<>(license, null));
        }

        // Get a map of all licenses (valid and deprecated)
        Table<String, String, License> licenseTable = getAllLicenses();

        // Return the list of fixes of possible valid licenses
        return Collections.singletonList(new Fix<>(license, licenseTable.row(validLicenseId).get(validLicenseId)));
    }

    /**
     * Get a map of all licenses (valid and deprecated).
     *
     * @return map of all licenses
     */
    private Table<String, String, License> getAllLicenses() {
        try {
            URL url = new URL(LICENSES_URL);
            ObjectMapper mapper = new ObjectMapper();

            JsonNode licensesJson = mapper.readTree(url).get("licenses");
            List<License> licenses = mapper.convertValue(licensesJson,
                    mapper.getTypeFactory().constructCollectionType(List.class, License.class));

            return licenses.stream().collect(Tables.toTable(
                    License::getId,
                    License::getName,
                    license -> license,
                    HashBasedTable::create
            ));
        } catch (Exception e) {
            throw new RuntimeException("URL to fetch all licenses is invalid");
        }
    }

    private String nameToId(String name) {
        Table<String, String, License> licenseTable = getAllLicenses();
        return licenseTable.row(name).get(name).getName();
    }

    /**
     * Get a list of valid license ids given a deprecated license id or name.
     *
     * @param deprecatedId id of deprecated license
     * @return list of valid license ids
     */
    private String validLicenseId(String deprecatedId) {

        if (deprecatedId.matches("^BSD-2")) {
            return "BSD-2-Clause";
        }
        if (deprecatedId.matches("^bzip2")) {
            return "bzip2-1.0.6";
        }
        if (deprecatedId.matches("^eCos")) {
            return "RHeCos-1.1";
        }

        if (deprecatedId.matches("^GPL-[23].0-with") || deprecatedId.matches("\\d$")) {
            return deprecatedId + "-only";
        }
        if (deprecatedId.matches("\\+$")) {
            return deprecatedId + "or-later";
        }

        // Nunit, StandardML-NJ, wxWindows could not be accurately mapped to a valid license
        return "";
    }

}
