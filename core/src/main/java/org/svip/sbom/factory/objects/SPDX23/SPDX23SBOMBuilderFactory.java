package org.svip.sbom.factory.objects.SPDX23;

import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23FileBuilder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23PackageBuilder;
import org.svip.sbom.factory.interfaces.SBOMBuilderFactory;

/**
 * file: SPDX23SBOMBuilderFactory.java
 * Class for the SPDX 2.3 SBOM Factory
 *
 * @author Thomas Roman
 */

public class SPDX23SBOMBuilderFactory implements SBOMBuilderFactory {
    private SPDX23PackageBuilder packageBuilder;
    private SPDX23FileBuilder fileBuilder;

    @Override
    public SPDX23Builder createBuilder() {
        return new SPDX23Builder();
    }
}
