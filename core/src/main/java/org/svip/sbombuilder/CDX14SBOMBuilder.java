package org.svip.sbombuilder;

import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;

public interface CDX14SBOMBuilder {
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
