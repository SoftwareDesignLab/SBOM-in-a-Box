package org.svip.sbomfactory.serializers.deserializer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.svip.sbom.model.old.SBOM;
import org.svip.sbomfactory.translators.TranslatorException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SPDX23TagValueDeserializerTest extends DeserializerTest {
    public SPDX23TagValueDeserializerTest() {
        super(new SPDX23TagValueDeserializer());
    }
    @Test
    public void readFromStringTest() throws IOException {
        org.svip.sbom.model.interfaces.generics.SBOM sbom = getDeserializer().readFromString(Files.readString(Path.of(SPDX23_TAGVALUE_SBOM)));
        assertNotNull(sbom);
        assertEquals("2.3", sbom.getSpecVersion());
        assertEquals(94, sbom.getComponents().size()); // TODO more assertions
    }
}
