package org.svip.conversion;

import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23PackageBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;

import java.util.Map;

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
        if(sbom.getLicenses() != null) sbom.getLicenses().stream().forEach(x -> builder.addLicense(x));

        // Creation Data
        builder.setCreationData(sbom.getCreationData());

        // Document Comment
        builder.setDocumentComment(sbom.getDocumentComment());

        // Root Component
        builder.setRootComponent(sbom.getRootComponent());

        // Components

        // Stream Relationship data into new SBOM
        if (sbom.getRelationships() != null) sbom.getRelationships().keySet().forEach(
                x -> sbom.getRelationships().get(x).stream().forEach(
                        y -> builder.addRelationship(x, y)
                )
        );

        // Stream External References into new SBOM
        if (sbom.getExternalReferences() != null) sbom.getExternalReferences().stream().forEach(
                x -> builder.addExternalReference(x)
        );

        // SPDX License List
        builder.setSPDXLicenseListVersion(sbom.getSPDXLicenseListVersion());

        // Return new SPDX 2.3 SBOM
        return builder.buildSPDX23SBOM();

    }

    /**
     * Coverts an SVIP Component into a SPDX23PackageObject
     *
     * @param component the SVIPComponent to use for information
     * @return An SPDX23PackageObject containing the data from the SVIPComponent
     */
    private SPDX23PackageObject convertComponent(Component component) {

        // Cast component as an SVIPComponentObject
        SVIPComponentObject componentSVIP = (SVIPComponentObject) component;

        // New builder for the CycloneDX Component
        SPDX23PackageBuilder builder = new SPDX23PackageBuilder();

        // Type
        builder.setType(componentSVIP.getType());

        // UID
        builder.setUID(componentSVIP.getUID());

        // Author
        builder.setAuthor(componentSVIP.getAuthor());

        // Name
        builder.setName(componentSVIP.getName());

        // Licenses
        builder.setLicenses(componentSVIP.getLicenses());

        // Copyright
        builder.setCopyright(componentSVIP.getCopyright());

        // get hashes then stream them into new CDX component
        Map<String, String> hashesSVIP = componentSVIP.getHashes();
        if(hashesSVIP != null) hashesSVIP.keySet().forEach(x -> builder.addHash(x, hashesSVIP.get(x)));

        // Comment
        builder.setComment(componentSVIP.getComment());

        // Attribution Text
        builder.setAttributionText(componentSVIP.getAttributionText());

        // Download Location
        builder.setDownloadLocation(componentSVIP.getDownloadLocation());

        // File Name
        builder.setFileName(componentSVIP.getFileName());

        // Files Analyzed
        builder.setFilesAnalyzed(componentSVIP.getFilesAnalyzed());

        // Verification Code
        builder.setVerificationCode(componentSVIP.getVerificationCode());

        // Home Page
        builder.setHomePage(componentSVIP.getHomePage());

        // Source Info
        builder.setSourceInfo(componentSVIP.getSourceInfo());

        // Release Date
        builder.setReleaseDate(componentSVIP.getReleaseDate());

        // Build Date
        builder.setBuildDate(componentSVIP.getBuiltDate());

        // Valid Until Date
        builder.setValidUntilDate(componentSVIP.getValidUntilDate());

        // Supplier
        builder.setSupplier(componentSVIP.getSupplier());

        // Version
        builder.setVersion(componentSVIP.getVersion());

        // Description
        builder.setDescription(componentSVIP.getDescription());

        // Stream CPEs into new SPDX Component
        if(componentSVIP.getCPEs() != null) componentSVIP.getCPEs().stream().forEach(x -> builder.addCPE(x));

        // Stream PURLs into new SPDX Component
        if(componentSVIP.getPURLs() != null) componentSVIP.getPURLs().stream().forEach(x -> builder.addPURL(x));

        // Stream External References into new SPDX Component
        if(componentSVIP.getExternalReferences() != null) {
            componentSVIP.getExternalReferences().stream().forEach(x -> builder.addExternalReference(x));
        }

        // Return new SPDX 2.3 Component
        return builder.buildAndFlush();

    }

}
