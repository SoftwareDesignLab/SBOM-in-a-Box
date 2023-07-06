package org.svip.sbomfactory.serializers;

import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.serializers.deserializer.CDX14JSONDeserializer;
import org.svip.sbomfactory.serializers.deserializer.Deserializer;
import org.svip.sbomfactory.serializers.deserializer.SPDX23JSONDeserializer;
import org.svip.sbomfactory.serializers.deserializer.SPDX23TagValueDeserializer;
import org.svip.sbomfactory.serializers.serializer.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SerializerFactoryTest {
    @Test
    public void CDX14JSONSerializerTest() {
        Serializer serializer = SerializerFactory.createSerializer(
                SerializerFactory.Schema.CDX14,
                SerializerFactory.Format.JSON,
                true);

        assertTrue(serializer instanceof CDX14JSONSerializer);
    }

    @Test
    public void SPDX23JSONSerializerTest() {
        Serializer serializer = SerializerFactory.createSerializer(
                SerializerFactory.Schema.SPDX23,
                SerializerFactory.Format.JSON,
                true);

        assertTrue(serializer instanceof SPDX23JSONSerializer);
    }

    @Test
    public void SPDX23TagValueSerializerTest() {
        Serializer serializer = SerializerFactory.createSerializer(
                SerializerFactory.Schema.SPDX23,
                SerializerFactory.Format.TAGVALUE,
                true);

        assertTrue(serializer instanceof SPDX23TagValueSerializer);
    }

    @Test
    public void SVIPSBOMJSONSerializerTest() {
        Serializer serializer = SerializerFactory.createSerializer(
                SerializerFactory.Schema.SVIP,
                SerializerFactory.Format.JSON,
                true);

        assertTrue(serializer instanceof SVIPSBOMJSONSerializer);
    }

    @Test
    public void CDX14JSONDeserializerTest() {
        Deserializer deserializer = SerializerFactory.createDeserializer("");

        assertTrue(deserializer instanceof CDX14JSONDeserializer);
    }

    @Test
    public void SPDX23JSONDeserializerTest() {
        Deserializer deserializer = SerializerFactory.createDeserializer("");

        assertTrue(deserializer instanceof SPDX23JSONDeserializer);
    }

    @Test
    public void SPDX23TagValueDeserializerTest() {
        Deserializer deserializer = SerializerFactory.createDeserializer("");

        assertTrue(deserializer instanceof SPDX23TagValueDeserializer);
    }

    @Test
    public void InvalidSerializerArgumentTest() {
        assertThrows(IllegalArgumentException.class, () ->
                SerializerFactory.createSerializer(
                        SerializerFactory.Schema.CDX14,
                        SerializerFactory.Format.TAGVALUE,
                        true));
    }
}
