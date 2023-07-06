package org.svip.sbomfactory.serializers.serializer;

import org.junit.jupiter.api.BeforeEach;

public class SerializerTest {
    private final Serializer serializer;

    public SerializerTest(Serializer serializer) {
        this.serializer = serializer;
    }

    @BeforeEach
    public void setup() {
        // All serializer configuration goes in here
    }

    public Serializer getSerializer() {
        return serializer;
    }
}
