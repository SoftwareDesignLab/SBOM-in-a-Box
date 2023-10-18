package org.svip.generation.serializers.deserializer;

import org.svip.serializers.deserializer.Deserializer;
import org.svip.serializers.deserializer.SPDX23TagValueDeserializer;

import java.io.IOException;

/**
 * File: SPDX23TagValueDeserializerTest.java
 *
 * This class extends SPDX23JSONDeserializerTest because the data stored in the SPDX file formats should match up
 * exactly. Use this class as a template for expanding other test formats.
 *
 * @author Ian Dunn
 */
public class SPDX23TagValueDeserializerTest extends SPDX23JSONDeserializerTest {

    public SPDX23TagValueDeserializerTest() throws IOException {
        // Needed for inheritance
    }

    @Override
    public Deserializer getDeserializer() {
        return new SPDX23TagValueDeserializer();
    }

    @Override
    public String getTestFilePath() {
        return SPDX23_TAGVALUE_SBOM;
    }
}
