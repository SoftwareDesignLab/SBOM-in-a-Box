package org.svip.generation.serializers;

import org.junit.jupiter.api.Test;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.CDX14JSONDeserializer;
import org.svip.serializers.deserializer.Deserializer;
import org.svip.serializers.deserializer.SPDX23JSONDeserializer;
import org.svip.serializers.deserializer.SPDX23TagValueDeserializer;
import org.svip.serializers.serializer.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SerializerFactoryTest {

    public final String CDX14_JSON;
    public final String SPDX23_JSON;
    public final String SPDX23_TAGVALUE;

    public SerializerFactoryTest() throws IOException {
        String source = System.getProperty("user.dir") + "/src/test/resources/serializers";
        CDX14_JSON = new String(Files.readAllBytes(Paths.get(source + "/cdx_json/sbom.alpine.json")));
        SPDX23_JSON = new String(Files.readAllBytes(Paths.get(source + "/spdx_json/syft-0.80" +
                ".0-source-spdx-json" +
                ".json")));
        SPDX23_TAGVALUE =
                new String(Files.readAllBytes(Paths.get(source + "/spdx_tagvalue/sbom.alpine.2-3.spdx")));
    }

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
        Deserializer deserializer = SerializerFactory.createDeserializer(CDX14_JSON);

        assertTrue(deserializer instanceof CDX14JSONDeserializer);
    }

    @Test
    public void SPDX23JSONDeserializerTest() {
        Deserializer deserializer = SerializerFactory.createDeserializer(SPDX23_JSON);

        assertTrue(deserializer instanceof SPDX23JSONDeserializer);
    }

    @Test
    public void SPDX23TagValueDeserializerTest() {
        Deserializer deserializer = SerializerFactory.createDeserializer(SPDX23_TAGVALUE);

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
