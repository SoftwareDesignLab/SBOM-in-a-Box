package org.svip.sbombuilder.interfaces;

import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbombuilder.CDX14Builder;
import org.svip.sbombuilder.SVIPSBOMBuilder;

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
