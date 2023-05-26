package org.svip.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;

import java.util.List;

/**
 * A static class containing helper methods to resolve different API parameters in string format to their actual type.
 *
 * @author Ian Dunn
 */
public class Resolver {

    /**
     * Resolves a JSON array in string format to a {@code List<String>}.
     *
     * @param contentsArray The JSON array to resolve.
     * @return The resolved {@code List<String>}. If malformed, empty, or null, return null.
     */
    public static List<String> resolveJSONStringArray(String contentsArray) {
        if(contentsArray == null) return null;

        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(contentsArray, new TypeReference<>(){});
        } catch (JsonProcessingException ignored) {} // If we can't process it, return null

        return null;
    }

    /**
     * Resolves a schema name to a {@code GeneratorSchema} type.
     *
     * @param schemaName The schema name to resolve.
     * @param defaultToCDX Whether to default to CycloneDX with an invalid schema name.
     * @return The resolved {@code GeneratorSchema}. If invalid and {@code defaultToCDX} is null, return null.
     */
    public static GeneratorSchema resolveSchema(String schemaName, boolean defaultToCDX) {
        GeneratorSchema schema = defaultToCDX ? GeneratorSchema.CycloneDX : null;

        try {
            if(schemaName != null) schema = GeneratorSchema.valueOfArgument(schemaName);
        } catch (IllegalArgumentException ignored) {} // If we can't process it, return default value of schema

        return schema;
    }

    /**
     * Resolves a format name to a {@code GeneratorSchema.GeneratorFormat} type.
     *
     * @param formatName The format name to resolve.
     * @param defaultToJSON Whether to default to JSON with an invalid schema name.
     * @return The resolved {@code GeneratorFormat}. If invalid and {@code defaultToJSON} is null, return null.
     */
    public static GeneratorSchema.GeneratorFormat resolveFormat(String formatName, boolean defaultToJSON) {
        GeneratorSchema.GeneratorFormat format = defaultToJSON ? GeneratorSchema.GeneratorFormat.JSON : null;

        try {
            if(formatName != null) format = GeneratorSchema.GeneratorFormat.valueOf(formatName);
        } catch (IllegalArgumentException ignored) {} // If we can't process it, return default value of schema

        return format;
    }
}
