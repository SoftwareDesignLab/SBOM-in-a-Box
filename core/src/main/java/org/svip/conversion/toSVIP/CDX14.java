package org.svip.conversion.toSVIP;

import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;

import java.util.Map;

/**
 * Name: CDX14.java
 * Description: Converts a CDX 1.4 Internal SBOM Object
 * into an SVIP SBOM Object while retaining all the original
 * information from the CycloneDX 2.3 . This will not "completely"
 * convert the SBOM to an SVIP SBOM, as the fields will still
 * represent CDX 1.4 values.
 *
 * @author Tyler Drake
 */
public class CDX14 implements ToSVIP {

    /**
     * Builds an SVIP SBOM referencing a CycloneDX 1.4 SBOM. This SVIP
     * SBOM will retain all the original values from the CycloneDX sbom,
     * so only the internal Object will be converted, not the fields itself.
     *
     * @param cdx_sbom CDX 1.4 SBOM Object
     * @return An SVIP SBOM containing all original CDX 1.4 Values
     */
    @Override
    public SVIPSBOM convertToSVIP(SBOM cdx_sbom) {

        // Cast SBOM
        CDX14SBOM sbom = (CDX14SBOM) cdx_sbom;

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
        if(sbom.getLicenses() != null)
            sbom.getLicenses().stream().forEach(x -> builder.addLicense(x));

        // Creation Data
        builder.setCreationData(sbom.getCreationData());

        // Document Comment
        builder.setDocumentComment(sbom.getDocumentComment());

        // Document Comment
        builder.setDocumentComment(sbom.getDocumentComment());

        // Root Component
        if(sbom.getRootComponent() != null)
            builder.setRootComponent(convertComponent(sbom.getRootComponent()));
        else
            builder.setRootComponent(null);

        // Stream components from SVIP SBOM, convert them, then put into CDX SBOM
        if(sbom.getComponents() != null)
            sbom.getComponents().stream().filter(x-> x != null).forEach(x -> builder.addComponent(convertComponent(x)));

        // Stream Relationship data into new SBOM
        if(sbom.getRelationships() != null) {
            sbom.getRelationships().keySet().forEach(
                    x -> sbom.getRelationships().get(x).stream().forEach(
                            y -> builder.addRelationship(x, y)
                    )
            );
        }

        // Stream External References into new SBOM
        if(sbom.getExternalReferences() != null) {
            sbom.getExternalReferences().stream().forEach(
                    x -> builder.addExternalReference(x)
            );
        }

        // Set SPDX License List Version
        builder.setSPDXLicenseListVersion(null);


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

        // Cast the component to a CycloneDX 1.4 Component
        CDX14ComponentObject component = (CDX14ComponentObject) originalComponent;

        // Create new SVIP Component Builder
        SVIPComponentBuilder builder = new SVIPComponentBuilder();

        // Type
        builder.setType(component.getType());

        // UID
        builder.setUID(component.getUID());

        // Author
        builder.setAuthor(component.getAuthor());

        // Name
        builder.setName(component.getName());

        // Licenses
        builder.setLicenses(component.getLicenses());

        // Copyright
        builder.setCopyright(component.getCopyright());

        // Hashes
        Map<String, String> hashes = component.getHashes();
        if (hashes != null) hashes.keySet().forEach(x -> builder.addHash(x, hashes.get(x)));

        // Comment - NOT AVAILABLE IN CDX 1.4
        builder.setComment(null);

        // Attribution Text - NOT AVAILABLE IN CDX 1.4
        builder.setAttributionText(null);

        // File Notice - NOT AVAILABLE IN CDX 1.4
        builder.setFileNotice(null);

        // Download Location - NOT AVAILABLE IN CDX 1.4
        builder.setDownloadLocation(null);

        // File Name - NOT AVAILABLE IN CDX 1.4
        builder.setFileName(null);

        // Files Analyzed - NOT AVAILABLE IN CDX 1.4
        builder.setFilesAnalyzed(null);

        // Verification Code - NOT AVAILABLE IN CDX 1.4
        builder.setVerificationCode(null);

        // Home Page - NOT AVAILABLE IN CDX 1.4
        builder.setHomePage(null);

        // Source Info - NOT AVAILABLE IN CDX 1.4
        builder.setSourceInfo(null);

        // Release Date - NOT AVAILABLE IN CDX 1.4
        builder.setReleaseDate(null);

        // Built Date - NOT AVAILABLE IN CDX 1.4
        builder.setBuildDate(null);

        // Valid Until Date - NOT AVAILABLE IN CDX 1.4
        builder.setValidUntilDate(null);

        // Supplier
        builder.setSupplier(component.getSupplier());

        // Version
        builder.setVersion(component.getVersion());

        // Description
        builder.setDescription(component.getDescription());

        // CPEs
        if(component.getCPEs() != null) component.getCPEs().forEach(x -> builder.addCPE(x));

        // PURLs
        if(component.getPURLs() != null) component.getPURLs().forEach(x -> builder.addPURL(x));

        // External References
        if(component.getExternalReferences() != null)
            component.getExternalReferences().forEach(x -> builder.addExternalReference(x));

        // Mime Type
        builder.setMimeType(component.getMimeType());

        // Publisher
        builder.setPublisher(component.getPublisher());

        // Scope
        builder.setScope(component.getScope());

        // Group
        builder.setGroup(component.getGroup());

        // Properties
        if(component.getProperties() != null) {
            component.getProperties().keySet().forEach(
                    x -> component.getProperties().get(x).stream().forEach(
                            y -> builder.addProperty(x, y)
                    )
            );
        }

        // Build the component and return it
        return builder.buildAndFlush();

    }

}
