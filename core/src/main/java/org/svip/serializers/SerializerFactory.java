package org.svip.serializers;


import org.json.JSONObject;
import org.json.XML;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.serializers.deserializer.CDX14JSONDeserializer;
import org.svip.serializers.deserializer.Deserializer;
import org.svip.serializers.deserializer.SPDX23JSONDeserializer;
import org.svip.serializers.deserializer.SPDX23TagValueDeserializer;
import org.svip.serializers.serializer.*;
import org.xml.sax.InputSource;

import javax.xml.crypto.dsig.XMLObject;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.svip.serializers.SerializerFactory.Format.JSON;
import static org.svip.serializers.SerializerFactory.Format.TAGVALUE;
import static org.svip.serializers.SerializerFactory.Schema.*;

/**
 * File: SerializerFactory.java
 * <p>
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
            this.put(Format.XML, new CDX14XMLSerializer());
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
         * @param serializerMap   A map of valid formats to serializers.
         * @param deserializerMap A map of valid formats to deserializers.
         */
        Schema(String name, String version, Map<Format, Serializer> serializerMap,
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
        public Serializer getSerializer(Format format) throws IllegalArgumentException {
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
        public Deserializer getDeserializer(Format format) throws IllegalArgumentException {
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
        XML,
        TAGVALUE;

        /**
         * Checks if the given string is a valid JSON.
         *
         * @param fileContents The file contents to check if it's a valid JSON.
         * @return true if valid JSON.
         */
        public static boolean isValidJSON(String fileContents) {
            try {
                new JSONObject(fileContents);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        /**
         * Checks if the given string is a valid XML.
         *
         * @param fileContents The file contents to check if it's a valid XML.
         * @return true if valid XML.
         */
        public static boolean isValidXML(String fileContents) {
            try {
                SAXParserFactory.newInstance().newSAXParser().getXMLReader().parse(new InputSource(new StringReader(fileContents)));
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        /**
         * Checks if the given string is a valid tag-value.
         *
         * @param fileContents The file contents to check if it's a valid tag-value.
         * @return true if valid tag-value.
         */
        public static boolean isValidTagValue(String fileContents) {
            // Matches PascalCase and begin with capital letters (e.g. SPDXID, SPDXVersion)
            Pattern p = Pattern.compile("^[A-Z][a-z0-9]*(?:[A-Z][a-z0-9]*)*(?:[A-Z]?)$");

            // Check all tags are PascalCase (value is ignored; it can sometimes be empty)
            return fileContents.lines()
                    .filter(line -> !(line.contains("#") || line.contains("</text>") || line.equals(""))) // Exclude
                    .map(line -> line.split(": ")[0]) // Get the tag
                    .allMatch(line -> p.matcher(line).matches()); // Match tag to regex
        }
    }

    /**
     * TODO find an accurate way to determine schema
     * Resolves the SBOM schema of the contents of a file.
     *
     * @param fileContents The file contents to resolve the schema of.
     * @return The schema, or null if no schema could be resolved.
     */
    public static Schema resolveSchema(String fileContents) {
        if (fileContents.contains("bom-ref")) return CDX14;
        else if (fileContents.contains("SPDXID")) return SPDX23;
        else if (fileContents.contains("rootComponent")) return SVIP; // Field unique to SVIP SBOM
        else return null;
    }

    /**
     * Resolves the SBOM schema by determining the instance of an SBOM object.
     *
     * @param sbom The SBOM Object to resolve the schema of.
     * @return The schema, or null if no schema could be resolved.
     */
    public static Schema resolveSchemaByObject(SBOM sbom) {
        if (sbom instanceof CDX14SBOM) return CDX14;
        else if (sbom instanceof SPDX23SBOM) return SPDX23;
        else if (sbom instanceof SVIPSBOM) return SVIP;
        else return null;
    }

    /**
     * TODO add support for XML
     * Resolves the file format of the contents of a file.
     *
     * @param fileContents The file contents to resolve the format of.
     * @return The format, or null if no format could be resolved.
     */
    public static Format resolveFormat(String fileContents) {
        if (Format.isValidTagValue(fileContents))
            return TAGVALUE;
        else if (Format.isValidJSON(fileContents))
            return JSON;
        else return null;
    }

    /**
     * Create a Deserializer for an SBOM file by auto-detecting its schema and format using its contents.
     *
     * @param fileContents The contents of the SBOM file to deserialize.
     * @return A Deserializer to deserialize the SBOM file.
     * @throws IllegalArgumentException If a schema and/or format cannot be determined.
     */
    public static Deserializer createDeserializer(String fileContents) throws IllegalArgumentException {
        // Defaults to CDX14JSONDeserializer
        Schema schema = resolveSchema(fileContents);
        Format format = resolveFormat(fileContents);

        String errorMessage;
        if (schema == null)
            errorMessage = "Invalid SBOM schema.";
        else if (format == null)
            errorMessage = "Invalid file format.";
        else
            return schema.getDeserializer(format);

        throw new IllegalArgumentException(errorMessage);
    }

    /**
     * Create a Serializer for an SBOM object provided the schema, format, and whether to pretty-print the SBOM file
     * or not.
     *
     * @param schema      The schema of the SBOM file.
     * @param format      The format of the SBOM file.
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