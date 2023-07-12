package org.svip.sbom.model.interfaces.schemas.SPDX23;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;

import java.util.List;

/**
 * File: SPDX23Schema.java
 * SPDX 2.3 specific fields
 * <p>
 * Source: <a href="https://spdx.github.io/spdx-spec/v2.3/">https://spdx.github.io/spdx-spec/v2.3/"</a>
 *
 * @author Derek Garcia
 */
public interface SPDX23Schema extends SBOM {

    /*todo
     + getSnippets(): Set<Snippet>
     + getAdditionalLicenseInformation(): Set<LicenseInfo>
     + getAnnotaionInformation(): Set<Annotation>
     */

    /**
     * @return Version of the SPDX License List used when the SPDX document was created
     */
    String getSPDXLicenseListVersion();

    /**
     * @param other
     * @return component conflicts
     */
    List<Conflict> compare(SPDX23SBOM other);
}
