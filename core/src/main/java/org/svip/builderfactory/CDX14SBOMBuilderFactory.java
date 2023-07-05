package org.svip.builderfactory;

import org.svip.builderfactory.interfaces.SBOMBuilderFactory;
import org.svip.builders.component.CDX14PackageBuilder;
import org.svip.sbom.builder.interfaces.schemas.CycloneDX14.CDX14SBOMBuilder;

/**
 * file: CDX14SBOMBuilderFactory.java
 * Class for the CDX 1.4 SBOM Factory
 *
 * @author Thomas Roman
 */

public class CDX14SBOMBuilderFactory implements SBOMBuilderFactory {
    private CDX14PackageBuilder packageBuilder;

    /** the below code will be replaced when the CDX14Builder is merged */
    @Override
    public CDX14SBOMBuilder createBuilder()
    {
        return null;
    }
    /**
     @Override
     public CDX14Builder createBuilder()
     {
     return new CDX14Builder();
     }
     * */
}
