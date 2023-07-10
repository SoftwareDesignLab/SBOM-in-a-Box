package org.svip.sbomfactory.serializers.deserializer;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.SBOM;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SPDX23JSONDeserializerTest extends DeserializerTest {
    public SPDX23JSONDeserializerTest() {
        super(new SPDX23JSONDeserializer());
    }

    @Test
    public void readFromStringTest() throws IOException {
        SBOM sbom = getDeserializer().readFromString(Files.readString(Path.of(SPDX23_JSON_SBOM)));
        assertNotNull(sbom);

        // TODO more assertions
        assertEquals(11, sbom.getComponents().size()); // TODO ensure no duplicates added?
    }
}
