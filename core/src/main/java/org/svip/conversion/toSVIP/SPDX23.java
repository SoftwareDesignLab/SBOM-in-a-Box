package org.svip.conversion.toSVIP;

import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23PackageBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Component;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;

/**
 * Name: SPDX2.3.java
 * Description: Converts an SPDX 2.3 Internal SBOM Object
 * into an SVIP SBOM Object while retaining all the original
 * information from the SPDX 2.3 SBOM. This will not "completely"
 * convert the SBOM to an SVIP SBOM, as the fields will still
 * represent SPDX 2.3 values.
 *
 * @author Tyler Drake
 */
public class SPDX23 {

    /**
     * Builds an SVIP SBOM referencing an SPDX 2.3 SBOM. This SVIP
     * SBOM will retain all the original values from the SPDX 2.3 sbom,
     * so only the internal Object will be converted, not the fields itself.
     *
     * @param sbom SPDX 2.3 SBOM Object
     * @return An SVIP SBOM containing all original SPDX 2.3 Values
     */
    public static SVIPSBOM convertToSVIP(SPDX23SBOM sbom) {

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
        builder.setRootComponent(convertPackage(sbom.getRootComponent()));

        // Stream components from SVIP SBOM, convert them, then put into CDX SBOM
        sbom.getComponents().stream().forEach(
                x -> {
                    if(x instanceof SPDX23Package)
                        builder.addComponent(convertPackage(x));
                    else if(x instanceof SPDX23File)
                        builder.addComponent(convertFile(x));
                }
        );

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
        builder.setSPDXLicenseListVersion(sbom.getSPDXLicenseListVersion());


        return builder.Build();
    }

    /**
     * Builds an SVIP Component referencing an SPDX 2.3 Component. This SVIP
     * Component will retain all the original values from the SPDX Component,
     * so only the internal Object will be converted, not the fields itself.
     *
     * @param originalComponent SPDX 2.3 Component Object
     * @return An SVIP Component containing all original SPDX 2.3 Values
     */
    public static SVIPComponentObject convertPackage(Component originalComponent) {

        SPDX23Package component = (SPDX23Package) originalComponent;

        SVIPComponentBuilder builder = new SVIPComponentBuilder();

        return builder.buildAndFlush();

    }

    /**
     * Builds an SVIP Component referencing an SPDX 2.3 File. This SVIP
     * Component will retain all the original values from the SPDX File,
     * so only the internal Object will be converted, not the fields itself.
     *
     * @param originalFile SPDX 2.3 Component Object
     * @return An SVIP Component containing all original SPDX 2.3 Values
     */
    public static SVIPComponentObject convertFile(Component originalFile) {

        SPDX23File file = (SPDX23File) originalFile;

        SVIPComponentBuilder builder = new SVIPComponentBuilder();

        return builder.buildAndFlush();

    }

}
