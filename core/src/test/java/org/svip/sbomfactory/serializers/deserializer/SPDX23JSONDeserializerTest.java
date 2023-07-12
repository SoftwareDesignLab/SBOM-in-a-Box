package org.svip.sbomfactory.serializers.deserializer;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.metadata.CreationTool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SPDX23JSONDeserializerTest extends DeserializerTest {
    public SPDX23JSONDeserializerTest() {
        super(new SPDX23JSONDeserializer());
    }

    @Test
    public void readFromStringTest() throws IOException {
        SPDX23JSONDeserializer spdx23Deserializer = new SPDX23JSONDeserializer();
        SPDX23SBOM sbom = spdx23Deserializer.readFromString(Files.readString(Path.of(SPDX23_JSON_SBOM)));
        assertNotNull(sbom);
    }

    @Test
    public void metadataTest() throws IOException {
        SPDX23JSONDeserializer spdx23Deserializer = new SPDX23JSONDeserializer();
        SPDX23SBOM sbom = spdx23Deserializer.readFromString(Files.readString(Path.of(SPDX23_JSON_SBOM)));
        assertNotNull(sbom);

        // TODO more assertions
        assertNotNull(sbom);
        // spdxVersion
        assertEquals("SPDX-2.3", sbom.getSpecVersion());
        // dataLicense
        assertEquals("CC0-1.0", sbom.getLicenses().stream().toList().get(0));
        // name
        assertEquals(".", sbom.getName());
        // documentNamespace
        assertEquals("https://anchore.com/syft/dir/d6734fe2-792a-4890-8428-453b3ed70ce7", sbom.getUID());
        // created
        assertEquals("2023-05-10T21:15:01Z", sbom.getCreationData().getCreationTime());
        // creators
        assertEquals(1, sbom.getCreationData().getCreationTools().size());
        List<CreationTool> creationTools = sbom.getCreationData().getCreationTools().stream().toList();
        //assertEquals("Anchore, Inc", creationTools.get(0).getVendor());
        assertEquals("syft", creationTools.get(0).getName());
        assertEquals("0.80.0", creationTools.get(0).getVersion());
    }

    @Test
    public void componentTest() throws IOException {
        SPDX23JSONDeserializer spdx23Deserializer = new SPDX23JSONDeserializer();
        SPDX23SBOM sbom = spdx23Deserializer.readFromString(Files.readString(Path.of(SPDX23_JSON_SBOM)));
        assertNotNull(sbom);

        assertEquals(11, sbom.getComponents().size());
        List<Component> components = sbom.getComponents().stream().toList();
        // checking for duplicates:
        int count = 1;
        for (int i = 1; i < components.size(); i++) {
            if (components.get(i).getUID() == "SPDXRef-Package--rsc.io-sampler-94f1adb847e5062b") {
                // TODO implement author
                // name
                assertEquals("rsc.io/sampler", components.get(0).getName());
                // versionInfo TODO SPDX specific data is lost as the CDX SBOM stores incomplete component interfaces
                //assertEquals("v1.3.0", components.get(0).getVersion());
                count += 1;
            }
        }
        assertEquals(1, count);
    }
}
