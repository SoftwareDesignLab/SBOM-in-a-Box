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
import org.svip.sbomgeneration.serializers.SerializerFactory;
import org.svip.sbomgeneration.serializers.deserializer.Deserializer;
import org.svip.sbomgeneration.serializers.serializer.Serializer;

import java.util.Arrays;
import java.util.HashMap;
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
     * @return converted SBOMFile and error message, if any
     */
    public static HashMap<SBOMFile, String> convert(SBOMFile sbom, SerializerFactory.Schema schema,
                                                    SerializerFactory.Format format) {

        HashMap<SBOMFile, String> ret = new HashMap<>();

        // deserialize into SBOM object
        Deserializer d;
        org.svip.sbom.model.interfaces.generics.SBOM deserialized;

        try {
            d = SerializerFactory.createDeserializer(sbom.getContents());
            deserialized = d.readFromString(sbom.getContents());
        } catch (Exception e) {
            return Utils.internalSerializerError(ret,
                    ": " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()),
                    "DURING DESERIALIZATION");
        }
        if (deserialized == null)
            return Utils.internalSerializerError(ret, "", "DURING DESERIALIZATION");


        // serialize into desired format
        Serializer s;
        String serialized = null;
        try {
            // serialize into requested schema

            s = SerializerFactory.createSerializer(schema, format, true);
            s.setPrettyPrinting(true);
            SerializerFactory.Schema originalSchema = Utils.assumeSchemaFromOriginal(sbom.getContents());

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
            return Utils.internalSerializerError(ret,
                    ": " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()),
                    "DURING SERIALIZATION");
        }
        if (serialized == null) {
            return Utils.internalSerializerError(ret, "", "DURING SERIALIZATION");
        }

        ret.put(new SBOMFile("SUCCESS", serialized), "");
        return ret;

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
                builder.addSPDX23Component(compBuilder.buildAndFlush());
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
                case CDX14 -> {

                    CDX14ComponentObject cdx14ComponentObject = (CDX14ComponentObject) component;
                    compBuilder.setSupplier(cdx14ComponentObject.getSupplier());
                    compBuilder.setVersion(cdx14ComponentObject.getVersion());
                    compBuilder.setDescription(cdx14ComponentObject.getDescription());

                    if (cdx14ComponentObject.getCPEs() != null)
                        for (String cpe : cdx14ComponentObject.getCPEs())
                            compBuilder.addCPE(cpe);

                    if (cdx14ComponentObject.getPURLs() != null)
                        for (String purl : cdx14ComponentObject.getPURLs())
                            compBuilder.addPURL(purl);

                    if (cdx14ComponentObject.getExternalReferences() != null)
                        for (ExternalReference ext : cdx14ComponentObject.getExternalReferences())
                            compBuilder.addExternalReference(ext);

                    compBuilder.setMimeType(cdx14ComponentObject.getMimeType());
                    compBuilder.setPublisher(cdx14ComponentObject.getPublisher());
                    compBuilder.setScope(cdx14ComponentObject.getScope());
                    compBuilder.setGroup(cdx14ComponentObject.getGroup());

                    if (cdx14ComponentObject.getProperties() != null)
                        for (Map.Entry<String, Set<String>> prop : cdx14ComponentObject.getProperties().entrySet())
                            for (String value : prop.getValue())
                                compBuilder.addProperty(prop.getKey(), value);
                }
                case SPDX23 -> {

                    // is this a package or file object?
                    if (component instanceof SPDX23PackageObject spdx23PackageObject) {
                        compBuilder.setComment(spdx23PackageObject.getComment());
                        compBuilder.setAttributionText(spdx23PackageObject.getAttributionText());
                    } else if (component instanceof SPDX23FileObject spdx23FileObject) {
                        compBuilder.setComment(spdx23FileObject.getComment());
                        compBuilder.setAttributionText(spdx23FileObject.getAttributionText());
                        compBuilder.setFileNotice(spdx23FileObject.getFileNotice());
                    }

                }

            }

    }

}
