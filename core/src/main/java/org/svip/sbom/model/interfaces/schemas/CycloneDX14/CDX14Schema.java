package org.svip.sbom.model.interfaces.schemas.CycloneDX14;

import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.compare.conflicts.Conflict;

import java.util.List;

/**
 * File: CDX14Schema.java
 *  <p>
 * CycloneDX 1.4 specific fields
 * <p>
 * Source: <a href="https://cyclonedx.org/docs/1.4/json/">https://cyclonedx.org/docs/1.4/json/</a>
 *
 * @author Derek Garcia
 */
public interface CDX14Schema extends SBOM {

    /* todo
    + getServices(): Set<Service>
    + getCompositions(): Set<Composition>
    + getSignature(): Signature
     */

    /**
     * Compare a CycloneDX 1.4 SBOM against another CycloneDX 1.4 SBOM Metadata
     *
     * @param other other CycloneDX 1.4 SBOM
     * @return list of conflict
     */
    List<Conflict> compare(CDX14SBOM other);
}
