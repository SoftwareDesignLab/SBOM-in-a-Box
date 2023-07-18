package org.svip.sbomgeneration.serializers.deserializer;

public abstract class DeserializerTest {

    protected static final String CDX_14_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/cdx_json/sbom.test.json";

    protected static final String SPDX23_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/spdx_json/sbom.test.json";

    protected static final String SPDX23_TAGVALUE_SBOM = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/spdx_tagvalue/sbom.test.spdx";

    // TODO in the future: no metadata, no components, empty sbom

    public abstract Deserializer getDeserializer();
}
