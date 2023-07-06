package org.svip.sbomfactory.serializers.deserializer;

import org.junit.jupiter.api.BeforeEach;

public class DeserializerTest {
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
