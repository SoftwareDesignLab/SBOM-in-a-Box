package org.svip.manipulation.toSVIP;

import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23FileObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;

import java.util.Map;

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
public class SPDX23 implements ToSVIP {

    /**
     * Builds an SVIP SBOM referencing an SPDX 2.3 SBOM. This SVIP
     * SBOM will retain all the original values from the SPDX 2.3 sbom,
     * so only the internal Object will be converted, not the fields itself.
     *
     * @param spdx_sbom SPDX 2.3 SBOM Object
     * @return An SVIP SBOM containing all original SPDX 2.3 Values
     */
    @Override
    public SVIPSBOM convertToSVIP(SBOM spdx_sbom) {

        //
        SPDX23SBOM sbom = (SPDX23SBOM) spdx_sbom;


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
                    if (x instanceof SPDX23Package) builder.addComponent(convertPackage(x));
                    else if (x instanceof SPDX23File) builder.addComponent(convertFile(x));
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

        // Cast the component to an SPDX 2.3 Package
        SPDX23Package component = (SPDX23Package) originalComponent;

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

        // Comment
        builder.setComment(component.getComment());

        // Attribution Text
        builder.setAttributionText(component.getAttributionText());

        // File Notice - NOT AVAILABLE IN SPDX 2.3 PACKAGE
        builder.setFileNotice(null);

        // Download Location
        builder.setDownloadLocation(component.getDownloadLocation());

        // File Name
        builder.setFileName(component.getFileName());

        // Files Analyzed
        builder.setFilesAnalyzed(component.getFilesAnalyzed());

        // Verification Code
        builder.setVerificationCode(component.getVerificationCode());

        // Home Page
        builder.setHomePage(component.getHomePage());

        // Source Info
        builder.setSourceInfo(component.getSourceInfo());

        // Release Date
        builder.setReleaseDate(component.getReleaseDate());

        // Built Date
        builder.setBuildDate(component.getBuiltDate());

        // Valid Until Date
        builder.setValidUntilDate(component.getValidUntilDate());

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

        // Mime Type - NOT AVAILABLE IN SPDX 2.3 PACKAGE
        builder.setMimeType(null);

        // Publisher - NOT AVAILABLE IN SPDX 2.3 PACKAGE
        builder.setPublisher(null);

        // Scope - NOT AVAILABLE IN SPDX 2.3 PACKAGE
        builder.setScope(null);

        // Group - NOT AVAILABLE IN SPDX 2.3 PACKAGE
        builder.setGroup(null);

        // Properties - NOT AVAILABLE IN SPDX 2.3 PACKAGE
        // Do not add any properties

        // Build and Return Component
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

        // Cast the component to an SPDX 2.3 File
        SPDX23FileObject file = (SPDX23FileObject) originalFile;

        // Create new SVIP Component Builder
        SVIPComponentBuilder builder = new SVIPComponentBuilder();

        // Type
        builder.setType(file.getType());

        // UID
        builder.setUID(file.getUID());

        // Author
        builder.setAuthor(file.getAuthor());

        // Name
        builder.setName(file.getName());

        // Licenses
        builder.setLicenses(file.getLicenses());

        // Copyright
        builder.setCopyright(file.getCopyright());

        // Hashes
        Map<String, String> hashes = file.getHashes();
        if (hashes != null) hashes.keySet().forEach(x -> builder.addHash(x, hashes.get(x)));

        // Comment
        builder.setComment(file.getComment());

        // Attribution Text
        builder.setAttributionText(file.getAttributionText());

        // File Notice
        builder.setFileNotice(file.getFileNotice());

        // Download Location - NOT AVAILABLE IN SPDX 2.3 FILE
        builder.setDownloadLocation(null);

        // File Name - NOT AVAILABLE IN SPDX 2.3 FILE
        builder.setFileName(null);

        // Files Analyzed - NOT AVAILABLE IN SPDX 2.3 FILE
        builder.setFilesAnalyzed(null);

        // Verification Code - NOT AVAILABLE IN SPDX 2.3 FILE
        builder.setVerificationCode(null);

        // Home Page - NOT AVAILABLE IN SPDX 2.3 FILE
        builder.setHomePage(null);

        // Source Info - NOT AVAILABLE IN SPDX 2.3 FILE
        builder.setSourceInfo(null);

        // Release Date - NOT AVAILABLE IN SPDX 2.3 FILE
        builder.setReleaseDate(null);

        // Built Date - NOT AVAILABLE IN SPDX 2.3 FILE
        builder.setBuildDate(null);

        // Valid Until Date - NOT AVAILABLE IN SPDX 2.3 FILE
        builder.setValidUntilDate(null);

        // Supplier - NOT AVAILABLE IN SPDX 2.3 FILE
        builder.setSupplier(null);

        // Version - NOT AVAILABLE IN SPDX 2.3 FILE
        builder.setVersion(null);

        // Description - NOT AVAILABLE IN SPDX 2.3 FILE
        builder.setDescription(null);

        // CPEs - NOT AVAILABLE IN SPDX 2.3 FILE
        // Do not add CPEs

        // PURLs - NOT AVAILABLE IN SPDX 2.3 FILE
        // Do not add PURLs

        // External References - NOT AVAILABLE IN SPDX 2.3 FILE
        // Do not add External References

        // Mime Type - NOT AVAILABLE IN SPDX 2.3 FILE
        builder.setMimeType(null);

        // Publisher - NOT AVAILABLE IN SPDX 2.3 FILE
        builder.setPublisher(null);

        // Scope - NOT AVAILABLE IN SPDX 2.3 FILE
        builder.setScope(null);

        // Group - NOT AVAILABLE IN SPDX 2.3 FILE
        builder.setGroup(null);

        // Properties - NOT AVAILABLE IN SPDX 2.3 FILE
        // Do not add any properties

        // Build and Return Component
        return builder.buildAndFlush();

    }

}
