package org.svip.api.utils;

import org.svip.api.model.SBOMFile;
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
import org.svip.serializers.deserializer.Deserializer;
import org.svip.serializers.serializer.Serializer;

import java.util.Map;
import java.util.Set;

/**
 * Utility class for SBOM conversion functionality
 *
 * @author Juan Francisco Patino
 */
public class Converter {

    private static final SVIPSBOMBuilder builder = new SVIPSBOMBuilder();
    private static final SVIPComponentBuilder compBuilder = new SVIPComponentBuilder();

    /**
     * Convert an SBOM to a desired schema
     *
     * @param schema the desired schema
     * @param format the desired format
     * @return converted SBOMFile
     */
    public static SBOMFile convert(SBOMFile sbom, SerializerFactory.Schema schema, SerializerFactory.Format format)
            throws Exception {

        // deserialize into SBOM object
        Deserializer d;
        SBOM deserialized;

        try {
            d = SerializerFactory.createDeserializer(sbom.getContents());
            deserialized = d.readFromString(sbom.getContents());
        } catch (Exception e) {
            throw new Exception("Deserialization Error: " + e.getMessage());
        }

        if (deserialized == null) throw new Exception("Deserialization Error");

        // serialize into desired format
        Serializer s;
        String serialized = null;
        try {
            // serialize into requested schema

            s = SerializerFactory.createSerializer(schema, format, true);
            s.setPrettyPrinting(true);
            SerializerFactory.Schema originalSchema = SerializerFactory.resolveSchema(sbom.getContents());

            buildSBOM(builder, deserialized, schema, originalSchema);

            // schema specific adjustments
            switch (schema) {
                case SPDX23 -> {
                    builder.setSpecVersion("2.3");
                    SVIPSBOM built = builder.Build();
                    serialized = s.writeToString(built);
                }
                case CDX14 -> {
                    builder.setSpecVersion("1.4");
                    SVIPSBOM built = builder.Build();
                    serialized = s.writeToString(built);
                }
            }

        } catch (Exception e) {
            throw new Exception("Serialization Error: " + e.getMessage());
        }

        if (serialized == null) throw new Exception("Serialization Error");

        return new SBOMFile("SUCCESS", serialized);
    }

    /**
     * Helper function to build an SBOM object from an object of the SBOM interface
     * @param deserialized SBOM interface object
     * @param schema desired schema
     * @param originalSchema original schema
     */
    public static void buildSBOM(SVIPSBOMBuilder builder, SBOM deserialized, SerializerFactory.Schema schema, SerializerFactory.Schema originalSchema) {
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
