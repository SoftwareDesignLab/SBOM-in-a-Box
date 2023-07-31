package org.svip.sbomgeneration.serializers.deserializer;

public abstract class DeserializerTest {

    protected static final String TEST_DATA_PATH = "/src/test/resources/serializers/";
    protected static final String CDX_14_JSON_SBOM = System.getProperty("user.dir") +
            TEST_DATA_PATH + "cdx_json/sbom.test.json";

    protected static final String SPDX23_JSON_SBOM = System.getProperty("user.dir") +
            TEST_DATA_PATH + "spdx_json/sbom.test.json";

    protected static final String SPDX23_TAGVALUE_SBOM = System.getProperty("user.dir") +
            TEST_DATA_PATH + "spdx_tagvalue/sbom.test.spdx";

    // TODO in the future: no metadata, no components, empty sbom

    public abstract Deserializer getDeserializer();
}
