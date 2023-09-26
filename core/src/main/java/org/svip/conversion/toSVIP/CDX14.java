package org.svip.conversion.toSVIP;

import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;

/**
 * Name: CDX14.java
 * Description: Converts a CDX 1.4 Internal SBOM Object
 * into an SVIP SBOM Object while retaining all the original
 * information from the SPDX 2.3 SBOM. This will not "completely"
 * convert the SBOM to an SVIP SBOM, as the fields will still
 * represent CDX 1.4 values.
 *
 * @author Tyler Drake
 */
public class CDX14 {

    /**
     * Builds an SVIP SBOM referencing a CycloneDX 1.4 SBOM. This SVIP
     * SBOM will retain all the original values from the CycloneDX sbom,
     * so only the internal Object will be converted, not the fields itself.
     *
     * @param sbom CDX 1.4 SBOM Object
     * @return An SVIP SBOM containing all original CDX 1.4 Values
     */
    public static SVIPSBOM convertToSVIP(CDX14SBOM sbom) {

        // Create new builder
        SVIPSBOMBuilder builder = new SVIPSBOMBuilder();

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

        // Stream licenses into new SBOM
        sbom.getLicenses().stream().forEach(x -> builder.addLicense(x));

        // Creation Data
        builder.setCreationData(sbom.getCreationData());

        // Document Comment
        builder.setDocumentComment(sbom.getDocumentComment());

        // Document Comment
        builder.setDocumentComment(sbom.getDocumentComment());

        // Root Component
        builder.setRootComponent(convertComponent(sbom.getRootComponent()));

        // Stream components from SVIP SBOM, convert them, then put into CDX SBOM
        sbom.getComponents().stream().forEach(x -> builder.addComponent(convertComponent(x)));

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

        // Set SPDX License List Version
        builder.setSPDXLicenseListVersion("");


        return builder.Build();

    }

    /**
     * Builds an SVIP Component referencing a CycloneDX 1.4 Component. This SVIP
     * Component will retain all the original values from the CycloneDX Component,
     * so only the internal Object will be converted, not the fields itself.
     *
     * @param originalComponent CDX 1.4 Component Object
     * @return An SVIP Component containing all original CDX 1.4 Values
     */
    public static SVIPComponentObject convertComponent(Component originalComponent) {

        CDX14ComponentObject component = (CDX14ComponentObject) originalComponent;

        SVIPComponentBuilder builder = new SVIPComponentBuilder();

        return builder.buildAndFlush();

    }

}
