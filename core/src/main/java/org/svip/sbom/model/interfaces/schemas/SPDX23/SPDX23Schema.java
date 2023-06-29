package org.svip.sbom.model.interfaces.generics.schemas;

import org.svip.sbom.model.interfaces.generics.SBOM;

/**
 * File: SPDX23Schema.java
 * SPDX 2.3 specific fields
 *
 * @author Derek Garcia
 */
public interface SPDX23Schema extends SBOM {

    /*todo
     + getSnippets(): Set<Snippet>
     + getAdditionalLicenseInformation(): Set<LicenseInfo>
     + getAnnotaionInformation(): Set<Annotation>
     */
    String getSPDXLicenseListVersion();
}
