package org.svip.sbom.factory.objects.CycloneDX14;

import org.svip.sbom.builder.objects.schemas.CDX14.CDX14Builder;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14PackageBuilder;
import org.svip.sbom.factory.interfaces.SBOMBuilderFactory;

/**
 * file: CDX14SBOMBuilderFactory.java
 * Class for the CDX 1.4 SBOM Factory
 *
 * @author Thomas Roman
 */

public class CDX14SBOMBuilderFactory implements SBOMBuilderFactory {
    private CDX14PackageBuilder packageBuilder;

    @Override
    public CDX14Builder createBuilder() {
        return new CDX14Builder();
    }
}
