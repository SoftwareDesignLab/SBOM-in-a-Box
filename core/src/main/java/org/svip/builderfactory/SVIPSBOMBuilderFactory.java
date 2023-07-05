package org.svip.builderfactory;

import org.svip.builderfactory.interfaces.SBOMBuilderFactory;
import org.svip.builders.component.SVIPComponentBuilder;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;

/**
 * file: SVIPSBOMBuilderFactory.java
 * Class for the SVIP SBOM Factory
 *
 * @author Thomas Roman
 */

public class SVIPSBOMBuilderFactory implements SBOMBuilderFactory {
    private SVIPComponentBuilder componentBuilder;
    @Override
    public SVIPSBOMBuilder createBuilder()
    {
        return new SVIPSBOMBuilder();
    }
}
