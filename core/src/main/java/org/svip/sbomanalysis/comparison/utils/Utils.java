package org.svip.sbomanalysis.comparison.utils;

import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.Map;
import java.util.Set;

/**
 * Utility class for comparison functionality
 *
 * @author Juan Francisco Patino
 */
public class Utils {

    public static void addLicenses(Component componentB, LicenseCollection mergedLicenses) {
        Set<String> declaredB = componentB.getLicenses().getDeclared();

        if (!declaredB.isEmpty()) {
            declaredB.forEach(
                    mergedLicenses::addDeclaredLicense
            );
        }

        Set<String> fileB = componentB.getLicenses().getInfoFromFiles();

        if (!fileB.isEmpty()) {
            fileB.forEach(
                    mergedLicenses::addLicenseInfoFromFile
            );
        }
    }

    /*
     * todo these are in the API Utils, I'm not sure how to reach it from here without breaking the gradle (I don't think you can)
     */

    /**
     * Build an SVIP Component object
     *
     * @param component original uncasted component
     */
    public static void buildSVIPComponentObject(Component component, SVIPComponentBuilder compBuilder) {
        if (component == null)
            return;
        compBuilder.setType(component.getType());
        compBuilder.setUID(component.getUID());
        compBuilder.setAuthor(component.getAuthor());
        compBuilder.setName(component.getName());
        compBuilder.setLicenses(component.getLicenses());
        compBuilder.setCopyright(component.getCopyright());

        if (component.getHashes() != null)
            for (Map.Entry<String, String> entry : component.getHashes().entrySet())
                compBuilder.addHash(entry.getKey(), entry.getValue());
        configurefromCDX14Object((CDX14ComponentObject) component, compBuilder);

    }

    /**
     * Configure the SVIPComponentBuilder from an CDX14 Component Object
     *
     * @param component CDX14 component object
     */
    private static void configurefromCDX14Object(CDX14ComponentObject component, SVIPComponentBuilder compBuilder) {
        compBuilder.setSupplier(component.getSupplier());
        compBuilder.setVersion(component.getVersion());
        compBuilder.setDescription(component.getDescription());

        if (component.getCPEs() != null)
            for (String cpe : component.getCPEs())
                compBuilder.addCPE(cpe);

        if (component.getPURLs() != null)
            for (String purl : component.getPURLs())
                compBuilder.addPURL(purl);

        if (component.getExternalReferences() != null)
            for (ExternalReference ext : component.getExternalReferences())
                compBuilder.addExternalReference(ext);

        compBuilder.setMimeType(component.getMimeType());
        compBuilder.setPublisher(component.getPublisher());
        compBuilder.setScope(component.getScope());
        compBuilder.setGroup(component.getGroup());

        if (component.getProperties() != null)
            for (Map.Entry<String, Set<String>> prop : component.getProperties().entrySet())
                for (String value : prop.getValue())
                    compBuilder.addProperty(prop.getKey(), value);
    }


}
