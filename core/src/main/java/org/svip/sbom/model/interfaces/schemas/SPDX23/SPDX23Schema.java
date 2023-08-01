package org.svip.sbom.model.interfaces.schemas.SPDX23;

import org.svip.compare.conflicts.Conflict;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;

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
     * Compare a SPDX 2.3 SBOM against another SPDX 2.3 SBOM Metadata
     *
     * @param other other SPDX 2.3 SBOM
     * @return list of conflicts
     */
    List<Conflict> compare(SPDX23SBOM other);
}
