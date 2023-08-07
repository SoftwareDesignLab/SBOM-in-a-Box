package org.svip.conversion;

import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23FileObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
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

    private static SVIPSBOMBuilder builder;
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
            default -> { return null; }
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

            try{

                // Create a new SVIP Builder and build a new SVIP from it
                builder = new SVIPSBOMBuilder();
                buildSBOM(sbom, SerializerFactory.Schema.SVIP, originalSchema);
                return builder.Build();

            } catch (Exception e){

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

        // todo deserialization happens in controller

        // Get the converter
        Convert converter = getConvert(desiredSchema);

        // Standardize SBOM to an SVIPSBOM
        SVIPSBOM svipsbom = toSVIP(sbom, originalSchema);

        // If no converter was found, return the SBOM as an SVIPSBOM
        return converter == null ? svipsbom : converter.convert(svipsbom);

    }


    /**
     * Helper function to build an SBOM object from an object of the SBOM interface
     * @param deserialized SBOM interface object
     * @param schema desired schema
     * @param originalSchema original schema
     */
    public static void buildSBOM(SBOM deserialized, SerializerFactory.Schema schema, SerializerFactory.Schema originalSchema) {
        builder.setFormat(String.valueOf(schema));
        builder.setName(deserialized.getName());
        builder.setUID(deserialized.getUID());
        builder.setVersion(deserialized.getVersion());

        if (deserialized.getLicenses() != null)
            for (String license : deserialized.getLicenses()) {
                if (license == null) continue;
                builder.addLicense(license);
            }

        builder.setCreationData(deserialized.getCreationData());
        builder.setDocumentComment(deserialized.getDocumentComment());

        buildSVIPComponentObject(deserialized.getRootComponent(), originalSchema);
        builder.setRootComponent(compBuilder.buildAndFlush());

        if (deserialized.getComponents() != null)
            for (Component c : deserialized.getComponents()
            ) {
                if (c == null)
                    continue;
                buildSVIPComponentObject(c, originalSchema);
                builder.addComponent(compBuilder.buildAndFlush());
            }

        if (deserialized.getRelationships() != null)
            for (Map.Entry<String, Set<Relationship>> entry : deserialized.getRelationships().entrySet())
                for (Relationship r : entry.getValue())
                    builder.addRelationship(entry.getKey(), r);

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
    public static void buildSVIPComponentObject(Component component,
                                                SerializerFactory.Schema originalSchema) {
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

        // schema specific
        if(originalSchema != null)
            switch (originalSchema) {
                case CDX14 ->
                        configurefromCDX14Object((CDX14ComponentObject) component);
                case SPDX23 ->
                        configureFromSPDX23Object(component);
            }
        else
            // if original schema is unspecified, try both
            try{
                configureFromSPDX23Object(component);
            }
            catch (ClassCastException | NullPointerException e){
                try{
                    configurefromCDX14Object((CDX14ComponentObject) component);
                }
                catch (ClassCastException | NullPointerException e1){}
            }

    }

    /**
     * Configure the SVIPComponentBuilder from an SPDX23 Component Object or File Object
     * @param component SPDX23 object
     */
    private static void configureFromSPDX23Object(Component component) {
        // is this a package or file object?
        if (component instanceof SPDX23PackageObject spdx23PackageObject) {
            compBuilder.setComment(spdx23PackageObject.getComment());
            compBuilder.setAttributionText(spdx23PackageObject.getAttributionText());
        } else if (component instanceof SPDX23FileObject spdx23FileObject) {
            compBuilder.setComment(spdx23FileObject.getComment());
            compBuilder.setAttributionText(spdx23FileObject.getAttributionText());
            compBuilder.setFileNotice(spdx23FileObject.getFileNotice());
        }
        else if(component instanceof CDX14ComponentObject)
            throw new ClassCastException();
    }

    /**
     * Configure the SVIPComponentBuilder from an CDX14 Component Object
     * @param component CDX14 component object
     */
    private static void configurefromCDX14Object(CDX14ComponentObject component) {
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
