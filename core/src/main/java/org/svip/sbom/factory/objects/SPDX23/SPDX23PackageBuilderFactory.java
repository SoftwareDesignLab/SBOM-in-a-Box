package org.svip.sbom.factory.objects.SPDX23;

import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23PackageBuilder;
import org.svip.sbom.factory.interfaces.ComponentBuilderFactory;

/**
 * file: SPDX23PackageBuilderFactory.java
 * Class to build SPDX 2.3 package specifications
 *
 * @author Matthew Morrison
 */
public class SPDX23PackageBuilderFactory implements ComponentBuilderFactory {

    /**
     * Create a new Builder
     * @return a new SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder createBuilder() {
        return new SPDX23PackageBuilder();
    }
}
