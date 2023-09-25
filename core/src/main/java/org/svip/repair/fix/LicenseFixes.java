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
     * @param repairSubType key from quality report map most directly relating to the repair type
     * @return
     */
    @Override
    public List<Fix<License>> fix(Result result, SBOM sbom, String repairSubType) {

        // Suggest deleting license if license doesn't exist in https://spdx.org/licenses/.

        String details = result.getDetails();
        String licenseId = details.substring(0, details.indexOf("is"));

        License license = new License(licenseId);

        /*
        if (license.length() > 1) {
            return List.of(new Fix<License>(new License(license)));
        }
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

    public static String getValidId(String deprecatedId) {
        /*
        switch(deprecatedId) {
            case
        }
         */
        return "";
    }

}
