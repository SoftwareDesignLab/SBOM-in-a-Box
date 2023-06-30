package org.svip.sbombuilder;

import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;

public interface SPDX23SBOMBuilder {
    /**
     * add a license list version to the SPDX 2.3 SBOM builder
     * @param licenseListVersion the SPDX 2.3 package
     * @return a SPDX 2.3 SBOM builder
     */
    SPDX23SBOMBuilder setSPDXLicenseListVersion(String licenseListVersion);
    /**
     * add a SPDX 2.3 package to the SPDX 2.3 SBOM builder
     * @param spdx23Package the SPDX 2.3 package
     * @return a SPDX 2.3 SBOM builder
     */
    SPDX23SBOMBuilder addSPDX23Package(SPDX23Package spdx23Package);
    /**
     * add a SPDX 2.3 file to the SPDX 2.3 SBOM builder
     * @param spdx23File the SPDX 2.3 file
     * @return a SPDX 2.3 SBOM builder
     */
    SPDX23SBOMBuilder addSPDX23File(SPDX23File spdx23File);
    /**
     * @return a SPDX 2.3 SBOM
     */
    SPDX23SBOM buildSPDX23SBOM();

}
