package org.svip.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;

import java.util.List;

public class Resolver {
    public static List<String> resolveJSONStringArray(String contentsArray) {
        if(contentsArray == null) return null;

        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(contentsArray, new TypeReference<>(){});
        } catch (JsonProcessingException ignored) {} // If we can't process it, return null

        return null;
    }

    public static GeneratorSchema resolveSchema(String schemaName, boolean defaultToCDX) {
        GeneratorSchema schema = defaultToCDX ? GeneratorSchema.CycloneDX : null;

        try {
            if(schemaName != null) schema = GeneratorSchema.valueOfArgument(schemaName);
        } catch (IllegalArgumentException ignored) {} // If we can't process it, return default value of schema

        return schema;
    }

    public static GeneratorSchema.GeneratorFormat resolveFormat(String formatName, boolean defaultToJSON) {
        GeneratorSchema.GeneratorFormat format = defaultToJSON ? GeneratorSchema.GeneratorFormat.JSON : null;

        try {
            if(formatName != null) format = GeneratorSchema.GeneratorFormat.valueOf(formatName);
        } catch (IllegalArgumentException ignored) {} // If we can't process it, return default value of schema

        return format;
    }
}
