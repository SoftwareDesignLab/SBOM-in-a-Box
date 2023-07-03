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
    SPDX23SBOMBuilder setRootComponent(SPDX23Component rootComponent);

    SPDX23SBOMBuilder addComponent(SPDX23Component component);

    /**
     * add a license list version to the SPDX 2.3 SBOM builder
     * @param licenseListVersion the SPDX 2.3 package
     * @return a SPDX 2.3 SBOM builder
     */
    SPDX23SBOMBuilder setSPDXLicenseListVersion(String licenseListVersion);

    /**
     * @return a SPDX 2.3 SBOM
     */
    SPDX23SBOM buildSPDX23SBOM();

}
