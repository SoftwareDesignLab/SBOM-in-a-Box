package org.svip.conversion;

import org.svip.sbom.builder.objects.schemas.CDX14.CDX14Builder;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14PackageBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;

import java.util.Map;

/**
 * Name: CovertCDX14.java
 * Description: Builds a CycloneDX 1.4 SBOM using
 * the information from an SVIPSBOM.
 *
 * @author tyler_drake
 */
public class ConvertCDX14 implements Convert {

    @Override
    public CDX14SBOM convert(SVIPSBOM sbom) {

        // Create a new builder
        CDX14Builder builder = new CDX14Builder();

        // Format
        builder.setFormat("CycloneDX");

        // Name
        builder.setName(sbom.getName());

        // UID
        builder.setUID(sbom.getUID());

        // Version
        builder.setVersion("1.4");

        // Spec Version
        builder.setSpecVersion(sbom.getSpecVersion());

        // Stream licenses into new SBOM
        sbom.getLicenses().stream().forEach(x -> builder.addLicense(x));

        // Creation Data
        builder.setCreationData(sbom.getCreationData());

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

        // Return the new CycloneDX 1.4 SBOM
        return builder.buildCDX14SBOM();
    }

    /**
     * Coverts an SVIP Component into a CDX14ComponentObject
     *
     * @param component the SVIPComponent to use for information
     * @return A CDX14ComponentObject containing the data from the SVIPComponent
     */
    private CDX14ComponentObject convertComponent(Component component) {

        // Cast component as an SVIPComponentObject
        SVIPComponentObject componentSVIP = (SVIPComponentObject) component;

        // New builder for the CycloneDX Component
        CDX14PackageBuilder builder = new CDX14PackageBuilder();

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
        hashesSVIP.keySet().forEach(x -> builder.addHash(x, hashesSVIP.get(x)));

        // Supplier
        builder.setSupplier(componentSVIP.getSupplier());

        // Version
        builder.setVersion(componentSVIP.getVersion());

        // Description
        builder.setDescription(componentSVIP.getDescription());

        // Stream CPEs into new CDX Component
        componentSVIP.getCPEs().forEach(x -> builder.addCPE(x));

        // Stream PURLs into new PURL Component
        componentSVIP.getPURLs().forEach(x -> builder.addPURL(x));

        // Stream External References into new Component
        componentSVIP.getExternalReferences().forEach(x -> builder.addExternalReference(x));

        // Mime Type
        builder.setMimeType(componentSVIP.getMimeType());

        // Publisher
        builder.setPublisher(componentSVIP.getPublisher());

        // Scope
        builder.setScope(componentSVIP.getScope());

        // Group
        builder.setGroup(componentSVIP.getGroup());

        // Stream Properties into new CDX Component
        componentSVIP.getProperties().keySet().forEach(
                x -> componentSVIP.getProperties().get(x).stream().forEach(
                        y -> builder.addProperty(x, y)
                )
        );

        // Return new CycloneDX 1.4 Component
        return builder.buildAndFlush();

    }

}
