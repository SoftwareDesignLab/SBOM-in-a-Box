package org.svip.sbom.builder.interfaces.schemas.CycloneDX14;

import org.svip.sbom.builder.interfaces.SBOMBuilder;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;

/**
 * file: CDX14SBOMBuilder.java
 * Interface for Cyclone DX 1.4 SBOM Builder
 *
 * @author Thomas Roman
 */
public interface CDX14SBOMBuilder extends SBOMBuilder {

    CDX14SBOMBuilder addCDX14Package(CDX14Package cdx14Package);

    /**
     * @return a CDX 1.4 SBOM
     */
    CDX14SBOM buildCDX14SBOM();
}
