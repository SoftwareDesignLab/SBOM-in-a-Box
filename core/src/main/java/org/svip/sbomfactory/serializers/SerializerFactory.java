package org.svip.sbomfactory.serializers;

import org.svip.sbomfactory.serializers.deserializer.Deserializer;
import org.svip.sbomfactory.serializers.serializer.Serializer;

public class SerializerFactory {
    public enum Schema {
        CDX14,
        SPDX23
    }

    public enum Format {
        JSON,
        XML,
        YAML,
        TAGVALUE
    }

    public static Deserializer createDeserializer() throws IllegalArgumentException {
        return null;
    }

    public static Serializer createSerializer(Schema schema, Format format, boolean prettyPrint) throws IllegalArgumentException {
        return null;
    }
}