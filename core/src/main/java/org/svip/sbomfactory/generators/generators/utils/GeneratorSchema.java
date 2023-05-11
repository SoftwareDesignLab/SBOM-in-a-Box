package org.svip.sbomfactory.generators.generators.utils;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.svip.sbom.model.SBOMType;
import org.svip.sbomfactory.generators.generators.BOMStore;
import org.svip.sbomfactory.generators.generators.cyclonedx.CycloneDXSerializer;
import org.svip.sbomfactory.generators.generators.cyclonedx.CycloneDXStore;
import org.svip.sbomfactory.generators.generators.spdx.SPDXSerializer;
import org.svip.sbomfactory.generators.generators.spdx.SPDXStore;

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
    CycloneDX("1.4", SBOMType.CYCLONE_DX, CycloneDXStore.class,
            new LinkedHashSet<>(Arrays.asList(GeneratorFormat.JSON, GeneratorFormat.XML))),
    SPDX("2.3", SBOMType.SPDX, SPDXStore.class,
            new LinkedHashSet<>(Arrays.asList(GeneratorFormat.JSON,/* GeneratorFormat.SPDX,*/ GeneratorFormat.YAML, GeneratorFormat.XML)));

    private final String version;
    private final SBOMType internalType;

    private final Class<? extends BOMStore> bomStoreType;
    // Internal HashSet to store valid formats
    private final LinkedHashSet<GeneratorFormat> validFormats;

    /**
     * Create a new supported schema type with the valid file formats that it
     * can be written to.
     *
     * @param validFormats a HashSet of valid file formats that the schema can
     *        be written to
     */
    GeneratorSchema(String version, SBOMType internalType, Class<? extends BOMStore> bomStoreType,
                    LinkedHashSet<GeneratorFormat> validFormats) {
        this.version = version;
        this.internalType = internalType;
        this.bomStoreType = bomStoreType;
        this.validFormats = validFormats;
    }

    /**
     * Enum to store a set of all supported file formats (for all schemas)
     */
    public enum GeneratorFormat {
        // Construct types with their respective file extensions
        JSON("json", new ObjectMapper(new JsonFactory())),
        XML("xml", new XmlMapper(new XmlFactory())),
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

        public ObjectMapper getObjectMapper(GeneratorSchema schema) throws GeneratorException {
            // Configure a new pretty printer
            DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
            // Indent arrays - this will cause each array element to be printed on its own line when not an object
            // prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

            // Enable object indentation and set the pretty printer
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.setDefaultPrettyPrinter(prettyPrinter);

            // Add serializer module to object mapper
            SimpleModule module = new SimpleModule();
            switch(schema) {
                case CycloneDX -> module.addSerializer(CycloneDXStore.class, new CycloneDXSerializer());
                case SPDX -> module.addSerializer(SPDXStore.class, new SPDXSerializer());
                default -> throw new GeneratorException("No serializer registered in getObjectMapper() for this schema.");
            }
            objectMapper.registerModule(module);

            return objectMapper;
        }
    }

    public String getVersion() {
        return this.version;
    }

    public SBOMType getInternalType() {
        return internalType;
    }

    public Class<? extends BOMStore> getBomStoreType() {
        return bomStoreType;
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
