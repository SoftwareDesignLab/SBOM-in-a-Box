package org.svip.sbomfactory.serializers;

import org.svip.sbomfactory.serializers.deserializer.CDX14JSONDeserializer;
import org.svip.sbomfactory.serializers.deserializer.Deserializer;
import org.svip.sbomfactory.serializers.deserializer.SPDX23JSONDeserializer;
import org.svip.sbomfactory.serializers.deserializer.SPDX23TagValueDeserializer;
import org.svip.sbomfactory.serializers.serializer.*;

import java.util.HashMap;
import java.util.Map;

import static org.svip.sbomfactory.serializers.SerializerFactory.Format.JSON;
import static org.svip.sbomfactory.serializers.SerializerFactory.Format.TAGVALUE;
import static org.svip.sbomfactory.serializers.SerializerFactory.Schema.*;

/**
 * File: SerializerFactory.java
 *
 * This class is responsible for managing serializer and deserializer schema/format mappings to allow creating any
 * serializer provided a schema and format, as well as any deserializer by auto-detecting schema and format.
 *
 * @author Ian Dunn
 */
public class SerializerFactory {
    /**
     * Stores a list of schemas and their valid format/serializer mappings. Each schema has the methods getSerializer
     * () and getDeserializer() based on formats. If an invalid format is found, an IllegalArgumentException is thrown.
     */
    public enum Schema {
        /**
         * CycloneDX 1.4 supports JSON serialization/deserialization via CDX14JSONSerializer & CDX14JSONDeserializer.
         */
        CDX14("CycloneDX", "1.4", new HashMap<>() {{
            this.put(JSON, new CDX14JSONSerializer());
        }}, new HashMap<>() {{
            this.put(JSON, new CDX14JSONDeserializer());
        }}),

        /**
         * SPDX 2.3 supports JSON and tag-value serialization via SPDX23JSONSerializer & SPDX23TagValueSerializer.
         * SPDX 2.3 supports JSON and tag-value deserialization via SPDX23JSONDeserializer & SPDX23TagValueDeserializer.
         */
        SPDX23("SPDX", "2.3", new HashMap<>() {{
            this.put(JSON, new SPDX23JSONSerializer());
            this.put(TAGVALUE, new SPDX23TagValueSerializer());
        }}, new HashMap<>() {{
            this.put(JSON, new SPDX23JSONDeserializer());
            this.put(TAGVALUE, new SPDX23TagValueDeserializer());
        }}),

        SVIP("SVIP", "1.0-a", new HashMap<>() {{
            this.put(JSON, new SVIPSBOMJSONSerializer());
        }}, new HashMap<>() {{
            // No deserialization supported
        }});

        private final String name;

        private final String version;

        /**
         * Map of valid formats to serializers.
         */
        private final Map<Format, Serializer> serializerMap;

        /**
         * Map of valid formats to deserializers.
         */
        private final Map<Format, Deserializer> deserializerMap;

        /**
         * Default constructor for Schema.
         *
         * @param serializerMap A map of valid formats to serializers.
         * @param deserializerMap A map of valid formats to deserializers.
         */
        private Schema(String name, String version, Map<Format, Serializer> serializerMap,
                       Map<Format, Deserializer> deserializerMap) {
            this.name = name;
            this.version = version;
            this.serializerMap = serializerMap;
            this.deserializerMap = deserializerMap;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        /**
         * Gets the serializer corresponding to the current schema and format.
         *
         * @param format The format of the serializer to get.
         * @return An instance of the serializer.
         * @throws IllegalArgumentException If the format is invalid.
         */
        private Serializer getSerializer(Format format) throws IllegalArgumentException {
            if (!serializerMap.containsKey(format)) throw new IllegalArgumentException(String.format("Format \"%s\" " +
                    "incompatible with serializer schema \"%s\"", this, format));
            return serializerMap.get(format);
        }

        /**
         * Gets the deserializer corresponding to the current schema and format.
         *
         * @param format The format of the deserializer to get.
         * @return An instance of the deserializer.
         * @throws IllegalArgumentException If the format is invalid.
         */
        private Deserializer getDeserializer(Format format) throws IllegalArgumentException {
            if (!deserializerMap.containsKey(format)) throw new IllegalArgumentException(String.format("Format \"%s\"" +
                    " incompatible with deserializer schema \"%s\"", this, format));
            return deserializerMap.get(format);
        }
    }

    /**
     * Stores a list of all formats we support serializing/deserializing with across all schemas.
     */
    public enum Format {
        JSON,
        TAGVALUE
    }

    /**
     * Create a Deserializer for an SBOM file by auto-detecting its schema and format using its contents.
     *
     * @param fileContents The contents of the SBOM file to deserialize.
     * @return A Deserializer to deserialize the SBOM file.
     * @throws IllegalArgumentException If a schema and/or format cannot be determined.
     */
    public static Deserializer createDeserializer(String fileContents) throws IllegalArgumentException {
        // TODO figure out a 100% correct way of determining file schema and format, this was my quick and dirty soln

        // Defaults to CDX14JSONDeserializer
        Schema schema = CDX14;
        Format format = JSON;

        if (fileContents.contains("SPDX")) schema = SPDX23;
        else if (fileContents.contains("rootComponent")) schema = SVIP; // Field unique to SVIP SBOM

        if (fileContents.contains("DocumentName:")) format = TAGVALUE;

        // TODO what if we still have an incorrect deserializer?
        return schema.getDeserializer(format);
    }

    /**
     * Create a Serializer for an SBOM object provided the schema, format, and whether to pretty-print the SBOM file
     * or not.
     *
     * @param schema The schema of the SBOM file.
     * @param format The format of the SBOM file.
     * @param prettyPrint Whether to pretty-print the SBOM file.
     * @return A Serializer to serialize the SBOM file.
     * @throws IllegalArgumentException If the schema/format combination provided is invalid.
     */
    public static Serializer createSerializer(Schema schema, Format format, boolean prettyPrint) throws IllegalArgumentException {
        // Map schema + format to a serializer
        Serializer serializer = schema.getSerializer(format);

        // Set objectmapper to pretty-print if specified
        if (prettyPrint) serializer.setPrettyPrinting(true);

        return serializer;
    }
}