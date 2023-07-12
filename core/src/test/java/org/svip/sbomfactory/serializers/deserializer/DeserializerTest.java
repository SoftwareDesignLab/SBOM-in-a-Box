package org.svip.sbomfactory.serializers.deserializer;

import org.junit.jupiter.api.BeforeEach;

public class DeserializerTest {

    protected final String CDX_14_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/cdx_json/sbom.test.json";

    protected final String SPDX23_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/spdx_json/sbom.test.json";

    protected final String SPDX23_TAGVALUE_SBOM = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/spdx_tagvalue/sbom.test.spdx";

    // TODO in the future: no metadata, no components, empty sbom

    private final Deserializer deserializer;

    public DeserializerTest(Deserializer deserializer) {
        this.deserializer = deserializer;
    }

    @BeforeEach
    public void setup() {
        // All deserializer configuration goes in here
        // TODO remove if nothing here
    }

    public Deserializer getDeserializer() {
        return deserializer;
    }
}
