package org.svip.sbomfactory.serializers.deserializer;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.SBOM;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CDX14JSONDeserializerTest extends DeserializerTest {
    public CDX14JSONDeserializerTest() {
        super(new CDX14JSONDeserializer());
    }

    @Test
    public void readFromStringTest() throws IOException {
        SBOM sbom = getDeserializer().readFromString(Files.readString(Path.of(CDX_14_JSON_SBOM)));

        // TODO more assertions
        assertNotNull(sbom);
        assertEquals("1", sbom.getVersion());
        assertEquals("1.4", sbom.getSpecVersion());
        assertEquals(17, sbom.getComponents().size());
        assertEquals("container", sbom.getRootComponent().getType());
    }
}
