package org.svip.sbomfactory.serializers.deserializer;

import org.junit.jupiter.api.BeforeEach;

public class DeserializerTest {

    protected final String CDX_14_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/cdx_json/sbom.alpine.json";

    protected final String SPDX23_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/syft-0.80.0-source-spdx-json.json";

    protected final String SPDX23_TAGVALUE_SBOM = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/sbom.test.spdx";

    protected final String SPDX23_TAGVALUE_SBOM_NOCOMPONENTS = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/sbom.nocomponents.2-3.spdx";

    protected final String SPDX23_TAGVALUE_SBOM_NOMETADATA = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/sbom.alpine.2-3.nometadata.spdx";

    // TODO: CDX 1.4 JSON empty, no components, no metadata
    // TODO: SPDX 2.3 JSON empty, no components, no metadata

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
