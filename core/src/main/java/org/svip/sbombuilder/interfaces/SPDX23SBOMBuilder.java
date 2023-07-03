package org.svip.sbombuilder.interfaces;

import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Component;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;

/**
 * file: SPDX23SBOMBuilder.java
 * Interface for SPDX 2.3 SBOM Builder
 *
 * @author Thomas Roman
 */
public interface SPDX23SBOMBuilder extends SBOMBuilder {
    /**
     * add a component to the SPDX 2.3 SBOM builder
     * @param component the SPDX 2.3 component
     * @return a SPDX 2.3 SBOM builder
     */
    SPDX23SBOMBuilder addSPDX23Component(SPDX23Component component);

    /**
     * add a license list version to the SPDX 2.3 SBOM builder
     * @param licenseListVersion
     * @return a SPDX 2.3 SBOM builder
     */
    SPDX23SBOMBuilder setSPDXLicenseListVersion(String licenseListVersion);

    /**
     * @return a SPDX 2.3 SBOM
     */
    SPDX23SBOM buildSPDX23SBOM();

}
