package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.uids.License;

import java.util.List;

/**
 * Fixes class to generate suggested component license repairs
 */
public class LicenseFixes implements Fixes<License> {
    // Example SBOMs (https://github.com/CycloneDX/bom-examples/tree/master/SBOM)

    /**
     *
     * @param result        object from quality report
     * @param sbom          sbom from quality report
     * @param repairSubType key from quality report map most directly relating to the repair type
     * @return
     */
    @Override
    public List<Fix<License>> fix(Result result, SBOM sbom, String repairSubType) {

        /*
            The LicenseTest is missing checking for a valid URL.

            Sometimes a component only has an id or only has a name.

            Delete license if Result says fail
        */

        String details = result.getDetails();
        //String license = details.substring(0, details.indexOf("is"));

        License license;

        /*
        if (license.length() > 1) {
            return List.of(new Fix<License>(new License(license)));
        }
         */
    }
}
