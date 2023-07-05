package org.svip.sbom.builder.interfaces.schemas.CycloneDX14;

import org.svip.sbom.builder.interfaces.generics.SBOMBuilder;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;

/**
 * file: CDX14SBOMBuilder.java
 * Interface for Cyclone DX 1.4 SBOM Builder
 *
 * @author Thomas Roman
 */
public interface CDX14SBOMBuilder extends SBOMBuilder {
    /**
     * add a CDX 1.4 package to the CDX 1.4 SBOM builder
     * @param cdx14Package the CDX 1.4 package
     * @return a cdx 1.4 SBOM builder
     */
    CDX14SBOMBuilder addCDX14Package(CDX14Package cdx14Package);

    /**
     * @return a CDX 1.4 SBOM
     */
    CDX14SBOM buildCDX14SBOM();
}
