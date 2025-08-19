/**
 * Copyright 2021 Rochester Institute of Technology (RIT). Developed with
 * government support under contract 70RCSA22C00000008 awarded by the United
 * States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.svip.generation.serializers;

import org.junit.jupiter.api.Test;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.CDX14JSONDeserializer;
import org.svip.serializers.deserializer.Deserializer;
import org.svip.serializers.deserializer.SPDX23JSONDeserializer;
import org.svip.serializers.deserializer.v2.SPDX23TagValueDeserializer;
import org.svip.serializers.serializer.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SerializerFactoryTest {

    public final String CDX14_JSON;
    public final String SPDX23_JSON;
    public final String SPDX23_TAGVALUE_ALPINE;
    public final String SPDX23_TAGVALUE_DOCKER;
    public final String SPDX23_TAGVALUE_PYTHON;

    public SerializerFactoryTest() throws IOException {
        String source = System.getProperty("user.dir") + "/src/test/resources/serializers";
        CDX14_JSON = new String(Files.readAllBytes(Paths.get(source + "/cdx_json/sbom.alpine.json")));
        SPDX23_JSON = new String(Files.readAllBytes(Paths.get(source + "/spdx_json/syft-0.80" +
                ".0-source-spdx-json" +
                ".json")));
        SPDX23_TAGVALUE_ALPINE =
                new String(Files.readAllBytes(Paths.get(source + "/spdx_tagvalue/sbom.alpine.2-3.spdx")));
        SPDX23_TAGVALUE_DOCKER =
                new String(Files.readAllBytes(Paths.get(source + "/spdx_tagvalue/sbom.docker.2-2.spdx")));
        SPDX23_TAGVALUE_PYTHON =
                new String(Files.readAllBytes(Paths.get(source + "/spdx_tagvalue/sbom.python.2-3.spdx")));
    }

    @Test
    public void CDX14JSONSerializerTest() {
        Serializer serializer = SerializerFactory.createSerializer(
                SerializerFactory.Schema.CDX14,
                SerializerFactory.Format.JSON,
                true);

        assertInstanceOf(CDX14JSONSerializer.class, serializer);
    }

    @Test
    public void SPDX23JSONSerializerTest() {
        Serializer serializer = SerializerFactory.createSerializer(
                SerializerFactory.Schema.SPDX23,
                SerializerFactory.Format.JSON,
                true);

        assertInstanceOf(SPDX23JSONSerializer.class, serializer);
    }

    @Test
    public void SPDX23TagValueSerializerTest() {
        Serializer serializer = SerializerFactory.createSerializer(
                SerializerFactory.Schema.SPDX23,
                SerializerFactory.Format.TAGVALUE,
                true);

        assertInstanceOf(SPDX23TagValueSerializer.class, serializer);
    }

    @Test
    public void SVIPSBOMJSONSerializerTest() {
        Serializer serializer = SerializerFactory.createSerializer(
                SerializerFactory.Schema.SVIP,
                SerializerFactory.Format.JSON,
                true);

        assertInstanceOf(SVIPSBOMJSONSerializer.class, serializer);
    }

    @Test
    public void CDX14JSONDeserializerTest() {
        Deserializer deserializer = SerializerFactory.createDeserializer(CDX14_JSON);

        assertInstanceOf(CDX14JSONDeserializer.class, deserializer);
    }

    @Test
    public void SPDX23JSONDeserializerTest() {
        Deserializer deserializer = SerializerFactory.createDeserializer(SPDX23_JSON);

        assertInstanceOf(SPDX23JSONDeserializer.class, deserializer);
    }

    @Test
    public void SPDX23TagValueDeserializerTest() {
        Deserializer deserializer = SerializerFactory.createDeserializer(SPDX23_TAGVALUE_ALPINE);
        assertInstanceOf(SPDX23TagValueDeserializer.class, deserializer);

        deserializer = SerializerFactory.createDeserializer(SPDX23_TAGVALUE_DOCKER);
        assertInstanceOf(SPDX23TagValueDeserializer.class, deserializer);

        deserializer = SerializerFactory.createDeserializer(SPDX23_TAGVALUE_PYTHON);

        assertInstanceOf(SPDX23TagValueDeserializer.class, deserializer);
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
