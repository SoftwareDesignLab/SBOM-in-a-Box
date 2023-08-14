package org.svip.conversion;

import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23FileObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.serializers.SerializerFactory;

import java.util.Map;
import java.util.Set;

/**
 * Name: Conversion.java
 * Description: Converts an SBOM from one schema to another.
 *
 * @author Tyler Drake
 * @author Juan Francisco Patino
 */
public class Conversion {

    /**
     * New SVIP SBOM Builder
     */
    private static SVIPSBOMBuilder builder;

    /**
     * New SVIP Component Builder
     */
    private static final SVIPComponentBuilder compBuilder = new SVIPComponentBuilder();

    /**
     * Gets the appropriate converter based on the desired schema requested
     *
     * @param desiredSchema
     * @return (A Convert object)
     */
    private static Convert getConvert(SerializerFactory.Schema desiredSchema) {

        // Return appropriate Converter depending on the desired schema
        // If it is SVIPSBOM or not found, return null
        switch (desiredSchema) {
            case SPDX23 -> {
                return new ConvertSPDX23();
            }
            case CDX14 -> {
                return new ConvertCDX14();
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * A simple function for standardizing the sent SBOM as a SVIPSBOM.
     * Any other handling that may be needed shall be done here.
     *
     * @param sbom
     * @return SVIPSBOM
     */
    private static SVIPSBOM toSVIP(SBOM sbom, SerializerFactory.Schema originalSchema) throws Exception {

        try {

            // Return the cast SBOM
            return (SVIPSBOM) sbom;

        } catch (ClassCastException c) {

            try {

                // Create a new SVIP Builder and build a new SVIP from it
                builder = new SVIPSBOMBuilder();
                buildSBOM(sbom, SerializerFactory.Schema.SVIP, originalSchema);
                return builder.Build();

            } catch (Exception e) {

                // Throw exception if we couldn't convert the SBOM
                throw new Exception("Couldn't standardize SBOM to SVIPSBOM: " + e.getMessage());

            }
        }
    }

    /**
     * Main driver for directing SBOM conversion.
     *
     * @param sbom
     * @param desiredSchema
     * @return
     */
    public static SBOM convertSBOM(SBOM sbom, SerializerFactory.Schema desiredSchema,
                                   SerializerFactory.Schema originalSchema) throws Exception {

        // TODO: deserialization happens in controller

        // Get the converter
        Convert converter = getConvert(desiredSchema);

        // Standardize SBOM to an SVIPSBOM
        SVIPSBOM svipsbom = toSVIP(sbom, originalSchema);

        // If no converter was found, return the SBOM as an SVIPSBOM
        return converter == null ? svipsbom : converter.convert(svipsbom);

    }


    /**
     * Helper function to build an SBOM object from an object of the SBOM interface
     *
     * @param deserialized   SBOM interface object
     * @param schema         desired schema
     * @param originalSchema original schema
     */
    public static void buildSBOM(SBOM deserialized, SerializerFactory.Schema schema, SerializerFactory.Schema originalSchema) {

        // Set Format
        builder.setFormat(String.valueOf(schema));

        // Set Name
        builder.setName(deserialized.getName());

        // Set UID
        builder.setUID(deserialized.getUID());

        // Set Version
        builder.setVersion(deserialized.getVersion());

        // If Licenses exist, store them into the new SVIP SBOM
        if (deserialized.getLicenses() != null) {
            for (String license : deserialized.getLicenses()) {
                if (license != null) builder.addLicense(license);
            }
        }

        // Set CreationData
        builder.setCreationData(deserialized.getCreationData());

        // Set DocumentComment
        builder.setDocumentComment(deserialized.getDocumentComment());

        // Configure RootComponent
        buildSVIPComponentObject(deserialized.getRootComponent(), originalSchema);

        // Set RootComponent
        builder.setRootComponent(compBuilder.buildAndFlush());

        // If Components Exist
        if (deserialized.getComponents() != null)

            // For Each Component
            for (Component c : deserialized.getComponents()) {

                // If the component exists
                if (c != null) {

                    // Try to cast the Component to an SVIPComponent and add it to SVIP SBOM
                    // If it fails, Build a new SVIPComponent from the Component then add it
                    try {

                        SVIPComponentObject d = (SVIPComponentObject) c;
                        builder.addComponent(d);

                    } catch (Exception e) {

                        buildSVIPComponentObject(c, originalSchema);
                        builder.addComponent(compBuilder.buildAndFlush());

                    }

                }

            }

        // If Relationships exist, store them into the new SVIP SBOM
        if (deserialized.getRelationships() != null)
            for (Map.Entry<String, Set<Relationship>> entry : deserialized.getRelationships().entrySet())
                for (Relationship r : entry.getValue())
                    builder.addRelationship(entry.getKey(), r);

        // If ExternalReferences exist, store them into the new SVIP SBOM
        if (deserialized.getExternalReferences() != null)
            for (ExternalReference e : deserialized.getExternalReferences())
                builder.addExternalReference(e);

    }

    /**
     * Build an SVIP Component object
     *
     * @param component      original uncasted component
     * @param originalSchema the original Schema we are converting from
     */
    public static void buildSVIPComponentObject(Component component, SerializerFactory.Schema originalSchema) {

        // If the component doesn't exist, return
        if (component == null) return;

        // Set Type
        compBuilder.setType(component.getType());

        // Set UID
        compBuilder.setUID(component.getUID());

        // Set Author
        compBuilder.setAuthor(component.getAuthor());

        // Set Name
        compBuilder.setName(component.getName());

        // Set Licenses
        compBuilder.setLicenses(component.getLicenses());

        // Set Copyright
        compBuilder.setCopyright(component.getCopyright());

        // If Hashes exist, store them into new SVIP Component
        if (component.getHashes() != null)
            for (Map.Entry<String, String> entry : component.getHashes().entrySet())
                compBuilder.addHash(entry.getKey(), entry.getValue());

        // Check if the schema exists
        if (originalSchema != null) {

            // Find the appropriate configuration method for component based on schema
            switch (originalSchema) {

                // If Schema is CDX14, Configure from CDX14 Object
                case CDX14 -> configureFromCDX14Object((CDX14ComponentObject) component);

                // If Schema is SPDX23, Configure from SPDX23 Object
                case SPDX23 -> configureFromSPDX23Object(component);

            }

        } else {

            // if original schema is unspecified, try both
            try {

                // First, try SPDX23
                configureFromSPDX23Object(component);

            } catch (ClassCastException | NullPointerException e) {

                // If SPDX23 does not work
                try {

                    // Try CDX14
                    configureFromCDX14Object((CDX14ComponentObject) component);

                } catch (ClassCastException | NullPointerException e1) {

                    // Neither works, so throw ClassCastException
                    throw new ClassCastException("Couldn't configure the Root Component during Conversion.");

                }

            }

        }

    }

    /**
     * Configure the SVIPComponentBuilder from an SPDX23 Component Object or File Object
     *
     * @param component SPDX23 object
     */
    private static void configureFromSPDX23Object(Component component) {

        // If Component is an SPDX 2.3 Package, configure the package
        // If Component is an SPDX 2.3 File, configure the file
        // Otherwise, throw a ClassCastException error
        if (component instanceof SPDX23PackageObject spdx23PackageObject) {

            configureFromSPDX23Package(spdx23PackageObject);

        } else if (component instanceof SPDX23FileObject spdx23FileObject) {

            configureFromSPDX23File(spdx23FileObject);

        } else {

            throw new ClassCastException("Component cannot be configured to an SPDX Package or an SPFX File Object.");

        }

    }

    /**
     * Configure the SVIPComponentBuilder from an SPDX23 Package Object
     */
    private static void configureFromSPDX23Package(SPDX23PackageObject component) {

        // Set Comment
        compBuilder.setComment(component.getComment());

        // Set AttributionText
        compBuilder.setAttributionText(component.getAttributionText());

        // Set DownloadLocation
        compBuilder.setDownloadLocation(component.getDownloadLocation());

        // Set FileName
        compBuilder.setFileName(component.getFileName());

        // Set FilesAnalyzed
        compBuilder.setFilesAnalyzed(component.getFilesAnalyzed());

        // Set VerificationCode
        compBuilder.setVerificationCode(component.getVerificationCode());

        // Set HomePage
        compBuilder.setHomePage(component.getHomePage());

        // Set SourceInfo
        compBuilder.setSourceInfo(component.getSourceInfo());

        // Set ReleaseDate
        compBuilder.setReleaseDate(component.getReleaseDate());

        // Set BuiltDate
        compBuilder.setBuildDate(component.getBuiltDate());

        // Set ValidUntilDate
        compBuilder.setValidUntilDate(component.getValidUntilDate());

        // Set Supplier
        compBuilder.setSupplier(component.getSupplier());

        // Set Version
        compBuilder.setVersion(component.getVersion());

        // Set Description
        compBuilder.setDescription(component.getDescription());

        // Stream CPEs into new SVIP Component
        if(component.getCPEs() != null) component.getCPEs().forEach(compBuilder::addCPE);

        // Stream PURLs into new SVIP Component
        if(component.getPURLs() != null) component.getPURLs().forEach(compBuilder::addPURL);

        // Stream ExternalReferences into new SVIP Component
        if(component.getExternalReferences() != null)
            component.getExternalReferences().forEach(compBuilder::addExternalReference);

    }

    /**
     * Configure the SVIPComponentBuilder from an SPDX23 File Object
     */
    private static void configureFromSPDX23File(SPDX23FileObject component) {

        // Set Type
        compBuilder.setType(component.getType());

        // Set UID
        compBuilder.setUID(component.getUID());

        // Set Author
        compBuilder.setAuthor(component.getAuthor());

        // Set Name
        compBuilder.setName(component.getName());

        // Set Licenses
        compBuilder.setLicenses(component.getLicenses());

        // Set Copyright
        compBuilder.setCopyright(component.getCopyright());

        // Set Comment
        compBuilder.setComment(component.getComment());

        // Set AttributionText
        compBuilder.setAttributionText(component.getAttributionText());

        // Set FileNotice
        compBuilder.setFileNotice(component.getFileNotice());

    }

    /**
     * Configure the SVIPComponentBuilder from an CDX14 Component Object
     *
     * @param component CDX14 component object
     */
    private static void configureFromCDX14Object(CDX14ComponentObject component) {

        // Set Supplier
        compBuilder.setSupplier(component.getSupplier());

        // Set Version
        compBuilder.setVersion(component.getVersion());

        // Set Description
        compBuilder.setDescription(component.getDescription());

        // If CPEs exist, store them into new SVIP Component
        if (component.getCPEs() != null)
            for (String cpe : component.getCPEs())
                compBuilder.addCPE(cpe);

        // If PURLs exist, store them into new SVIP Component
        if (component.getPURLs() != null)
            for (String purl : component.getPURLs())
                compBuilder.addPURL(purl);

        // If ExternalReferences exist, store them into new SVIP Component
        if (component.getExternalReferences() != null)
            for (ExternalReference ext : component.getExternalReferences())
                compBuilder.addExternalReference(ext);

        // Set MimeType
        compBuilder.setMimeType(component.getMimeType());

        // Set Publisher
        compBuilder.setPublisher(component.getPublisher());

        // Set Scope
        compBuilder.setScope(component.getScope());

        // Set Group
        compBuilder.setGroup(component.getGroup());

        // If Properties exist, store them into new SVIP Component
        if (component.getProperties() != null)
            for (Map.Entry<String, Set<String>> prop : component.getProperties().entrySet())
                for (String value : prop.getValue())
                    compBuilder.addProperty(prop.getKey(), value);
    }

}
