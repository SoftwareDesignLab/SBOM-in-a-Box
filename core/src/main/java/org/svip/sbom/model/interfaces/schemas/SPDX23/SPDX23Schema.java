package org.svip.sbom.model.interfaces.schemas.SPDX23;
import org.svip.sbom.model.interfaces.generics.SBOM;

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
}
