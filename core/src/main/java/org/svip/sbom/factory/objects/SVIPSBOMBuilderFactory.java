package org.svip.sbom.factory.objects;

import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.factory.interfaces.SBOMBuilderFactory;

/**
 * file: SVIPSBOMBuilderFactory.java
 * Class for the SVIP SBOM Factory
 *
 * @author Thomas Roman
 */

public class SVIPSBOMBuilderFactory implements SBOMBuilderFactory {
    private SVIPComponentBuilder componentBuilder;

    @Override
    public SVIPSBOMBuilder createBuilder() {
        return new SVIPSBOMBuilder();
    }
}
