package org.svip.conversion;

import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;

/**
 * Name: CovertSPDX23.java
 * Description: Builds a SPDX 2.3 SBOM using
 * the information from an SVIPSBOM.
 *
 * @author tyler_drake
 */
public class ConvertSPDX23 implements Convert {

    @Override
    public SPDX23SBOM convert(SVIPSBOM sbom) {

        // Create a new builder
        SPDX23Builder builder = new SPDX23Builder();

        // Format
        builder.setFormat(sbom.getFormat());

        // Name
        builder.setName(sbom.getName());

        // UID
        builder.setUID(sbom.getUID());

        // Version
        builder.setVersion(sbom.getVersion());

        // Spec Version
        builder.setSpecVersion(sbom.getSpecVersion());

        // Stream Licenses into new SBOM
        sbom.getLicenses().stream().forEach(x -> builder.addLicense(x));

        // Creation Data
        builder.setCreationData(sbom.getCreationData());

        // Document Comment
        builder.setDocumentComment(sbom.getDocumentComment());

        // Root Component
        builder.setRootComponent(sbom.getRootComponent());

        // Components

        // Stream Relationship data into new SBOM
        sbom.getRelationships().keySet().forEach(
                x -> sbom.getRelationships().get(x).stream().forEach(
                        y -> builder.addRelationship(x, y)
                )
        );

        // Stream External References into new SBOM
        sbom.getExternalReferences().stream().forEach(
                x -> builder.addExternalReference(x)
        );

        // SPDX License List
        builder.setSPDXLicenseListVersion(sbom.getSPDXLicenseListVersion());

        return builder.buildSPDX23SBOM();

    }

}
