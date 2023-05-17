package org.svip.sbomfactory.generators.generators.generators;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import utils.SBOM.SBOM;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * File: GeneratorSchema.java
 * <p>
 * Enum to store a set of all supported schemas and their respective supported
 * file formats.
 * </p>
 * @author Dylan Mulligan
 */
public enum GeneratorSchema {
    // This mapping ensures that any schema has a set of valid formats to compare
    // against and/or choose from.
    CycloneDX(new LinkedHashSet<>(Arrays.asList(GeneratorFormat.JSON, GeneratorFormat.XML))),
    SPDX(new LinkedHashSet<>(Arrays.asList(GeneratorFormat.JSON,/* GeneratorFormat.SPDX,*/ GeneratorFormat.YAML, GeneratorFormat.XML)));

    // Internal HashSet to store valid formats
    private final LinkedHashSet<GeneratorFormat> validFormats;

    /**
     * Create a new supported schema type with the valid file formats that it
     * can be written to.
     *
     * @param validFormats a HashSet of valid file formats that the schema can
     *        be written to
     */
    GeneratorSchema(LinkedHashSet<GeneratorFormat> validFormats) {
        this.validFormats = validFormats;
    }

    /**
     * Enum to store a set of all supported file formats (for all schemas)
     */
    public enum GeneratorFormat {
        // Construct types with their respective file extensions
        JSON("json", new ObjectMapper(new JsonFactory())),
        XML("xml", new XmlMapper()),
        YAML("yml",  new ObjectMapper(new YAMLFactory()));

        // Store file extension
        private final String extension;

        private final ObjectMapper objectMapper;

        /**
         * Create a new supported format type with the respective file extension
         *
         * @param extension this format's respective file extension
         */
        GeneratorFormat(String extension, ObjectMapper objectMapper) {
            this.extension = extension;
            this.objectMapper = objectMapper;
        }

        /**
         * Get this format's respective file extension.
         *
         * @return this format's respective file extension
         */
        public String getExtension() { return extension; }

        public ObjectMapper getObjectMapper() {
            // Configure a new pretty printer that indents arrays
            DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
            prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

            // Enable object mapper indentation and set the pretty printer
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.setDefaultPrettyPrinter(prettyPrinter);
            return objectMapper;
        }
    }

    /**
     * Determines whether or not the provided format matches this schema.
     *
     * @param format the format to test
     * @return true if the format is supported, false otherwise
     */
    public boolean supportsFormat(GeneratorFormat format) { return this.validFormats.contains(format); }

    /**
     * Gets the first/default supported format for this schema.
     *
     * @return the first/default supported format for this schema
     */
    public GeneratorFormat getDefaultFormat() { return this.validFormats.toArray(new GeneratorFormat[0])[0]; }

    /**
     * Instantiates and returns a new generator based on this enum's value.
     * @param sbom an SBOM Object to be written to file
     * @return a new SBOMGenerator implementation based on this enum's value,
     *         defaults to CycloneDXGenerator
     */
    public SBOMGenerator newGenerator(SBOM sbom) {
        switch (this) {
            case CycloneDX -> { return new CycloneDXGenerator(sbom); }
            case SPDX -> { return new SPDXGenerator(sbom); }
            // Default to CycloneDX if outSchema is not valid
            default -> { return new CycloneDXGenerator(sbom); }
        }
    }

    public static GeneratorSchema valueOfArgument(String argument) {
        // Ensure argument is not null, throw an exception if it is
        assert argument != null;

        switch (argument.toUpperCase()) {
            case "CDX", "CYCLONEDX" -> { return CycloneDX; }
            case "SPDX" -> { return SPDX; }
            default -> throw new IllegalArgumentException();
        }
    }
}
