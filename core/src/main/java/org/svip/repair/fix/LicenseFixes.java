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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.uids.License;
import org.svip.utils.Debug;

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
     * Get license fix for an invalid license.
     *
     * @param result        object from quality report
     * @param sbom          sbom from quality report
     * @param componentName key from quality report map most directly relating to the repair type
     * @return singleton list of a license fix
     */
    @Override
    public List<Fix<License>> fix(Result result, SBOM sbom, String componentName, Integer componentHashCode) {
        // Get license identifier (name or id) from result message
        String details = result.getDetails();
        String identifier = details.substring(0, details.indexOf("is") - 1);

        License license = new License(identifier);

        return Collections.singletonList(new Fix<>(FixType.COMPONENT_LICENSE, license, getValidLicense(license)));
    }

    /**
     * Get a map of all licenses (valid and deprecated).
     *
     * @return map of licenses
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
            Debug.log(Debug.LOG_TYPE.ERROR, e);
            return Collections.emptyMap();
        }
    }

    /**
     * Get the valid license from the deprecated license
     *
     * @param deprecated deprecated License
     * @return valid license
     */
    private License getValidLicense(License deprecated) {
        Map<String,License> licenseMap = getAllLicenses();

        // Return null if the license id is not found in licenseMap
        if (!licenseMap.containsKey(deprecated.getIdentifier())) {
            return null;
        }
        String deprecatedId = licenseMap.get(deprecated.getIdentifier()).getId();

        // Specific deprecated license cases
        if (deprecatedId.startsWith("BSD-2-Clause-")) {
            return licenseMap.get("BSD-2-Clause");
        }
        if (deprecatedId.equals("bzip2-1.0.5")) {
            return licenseMap.get("bzip2-1.0.6");
        }
        if (deprecatedId.equals("eCos-2.0")) {
            return licenseMap.get("RHeCos-1.1");
        }
        if (deprecatedId.startsWith("GPL-2.0-with") || deprecatedId.startsWith("GPL-3.0-with")) {
            return licenseMap.get("GPL-2.0-only");
        }

        // General deprecated license cases
        if (Character.isDigit(deprecatedId.charAt(deprecatedId.length() - 1))) {
            return licenseMap.get(deprecatedId + "-only");
        }
        if (deprecatedId.endsWith("+")) {
            return licenseMap.get(deprecatedId.substring(0, deprecatedId.length() - 1) + "-or-later");
        }

        // Nunit, StandardML-NJ, wxWindows could not be accurately mapped to a valid license
        return null;
    }

}
