package org.svip.sbomfactory.serializers.deserializer;

import org.junit.jupiter.api.BeforeEach;

public class DeserializerTest {

    protected final String CDX_14_JSON_SBOM = System.getProperty("user.dir") + "/src/test/java/org/svip" +
            "/sbomfactory" +
            "/serializers/sample_boms/cdx_json/sbom.alpine.json";

    private final Deserializer deserializer;

    public DeserializerTest(Deserializer deserializer) {
        this.deserializer = deserializer;
    }

    @BeforeEach
    public void setup() {
        // All deserializer configuration goes in here
    }

    public Deserializer getDeserializer() {
        return deserializer;
    }
}
