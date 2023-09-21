package org.svip.sbom.factory.objects.CycloneDX14;

import org.svip.sbom.builder.objects.schemas.CDX14.CDX14PackageBuilder;
import org.svip.sbom.factory.interfaces.ComponentBuilderFactory;


/**
 * file: CDX14PackageBuilderFactory.java
 * Class to build CycloneDX 1.4 specific packages
 *
 * @author Matthew Morrison
 */
public class CDX14PackageBuilderFactory implements ComponentBuilderFactory {

    /**
     * Create a new Builder
     *
     * @return a new CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder createBuilder() {
        return new CDX14PackageBuilder();
    }
}
