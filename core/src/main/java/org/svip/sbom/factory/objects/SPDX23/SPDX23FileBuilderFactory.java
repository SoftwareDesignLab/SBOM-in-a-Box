package org.svip.sbom.factory.objects.SPDX23;


import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23FileBuilder;
import org.svip.sbom.factory.interfaces.ComponentBuilderFactory;

/**
 * file: SPDX23FileBuilderFactory.java
 * Class to build SPDX 2.3 file specifics
 *
 * @author Matthew Morrison
 */
public class SPDX23FileBuilderFactory implements ComponentBuilderFactory {

    /**
     * Create a new SPDX File Builder
     * @return a new SPDX23FileBuilder
     */
    @Override
    public SPDX23FileBuilder createBuilder() {
        return new SPDX23FileBuilder();
    }
}
