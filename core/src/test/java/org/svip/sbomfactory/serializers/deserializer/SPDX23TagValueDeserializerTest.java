package org.svip.sbomfactory.serializers.deserializer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.old.SBOM;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbomfactory.translators.TranslatorException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class SPDX23TagValueDeserializerTest extends DeserializerTest {
    public SPDX23TagValueDeserializerTest() {
        super(new SPDX23TagValueDeserializer());
    }
    @Test
    public void readFromStringTest() throws IOException {
        SPDX23TagValueDeserializer spdx23Deserializer = new SPDX23TagValueDeserializer();
        SPDX23SBOM sbom = spdx23Deserializer.readFromString(Files.readString(Path.of(SPDX23_TAGVALUE_SBOM)));
        assertNotNull(sbom);
    }
    @Test
    public void metadataTest() throws IOException {
        SPDX23TagValueDeserializer spdx23Deserializer = new SPDX23TagValueDeserializer();
        SPDX23SBOM sbom = spdx23Deserializer.readFromString(Files.readString(Path.of(SPDX23_TAGVALUE_SBOM)));
        assertNotNull(sbom);

        // TODO more assertions
        assertNotNull(sbom);
        // SPDXVersion
        assertEquals("2.3", sbom.getSpecVersion());
        // DataLicense
        assertEquals("CC0-1.0", sbom.getLicenses().stream().toList().get(0));
        // DocumentName
        assertEquals("alpine:latest", sbom.getName());
        // DocumentNamespace
        assertEquals("https://anchore.com/syft/image/alpine-latest-b9fc484b-41c4-4589-b3ef-c57bba20078c", sbom.getUID());
        // LicenseListVersion
        assertEquals("3.19", sbom.getSPDXLicenseListVersion());
        // created
        assertEquals("2023-03-10T14:43:10Z", sbom.getCreationData().getCreationTime());

        // creators
        assertEquals(1, sbom.getCreationData().getCreationTools().size());
        List<CreationTool> creationTools = sbom.getCreationData().getCreationTools().stream().toList();
        assertEquals("syft", creationTools.get(0).getName());
        assertEquals("0.69.1", creationTools.get(0).getVersion());
    }
    @Test
    public void componentTest() throws IOException {
        SPDX23TagValueDeserializer spdx23Deserializer = new SPDX23TagValueDeserializer();
        SPDX23SBOM sbom = spdx23Deserializer.readFromString(Files.readString(Path.of(SPDX23_TAGVALUE_SBOM)));
        assertNotNull(sbom);

        assertEquals(94, sbom.getComponents().size());
        List<Component> components = sbom.getComponents().stream().toList();
        //assertEquals("SPDXRef-1071b0c6b4d98bb1", components.get(0).getUID());
        // TODO get a better sbom
        // check for duplicates
        int count = 0;
        for (int i = 0; i < components.size(); i++) {
            if (Objects.equals(components.get(i).getUID(), "SPDXRef-35ab393f27e0bc39")) {
                count += 1;
            }
        }
        assertEquals(1, count);
    }
}
