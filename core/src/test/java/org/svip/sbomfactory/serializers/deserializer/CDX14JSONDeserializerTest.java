package org.svip.sbomfactory.serializers.deserializer;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CDX14JSONDeserializerTest extends DeserializerTest {
    public CDX14JSONDeserializerTest() {
        super(new CDX14JSONDeserializer());
    }

    @Test
    public void readFromStringTest() throws IOException {
        CDX14JSONDeserializer cdx14Deserializer = new CDX14JSONDeserializer();
        CDX14SBOM sbom = cdx14Deserializer.readFromString(Files.readString(Path.of(CDX_14_JSON_SBOM)));
    }

    @Test
    public void metadataTest() throws IOException {
        CDX14JSONDeserializer cdx14Deserializer = new CDX14JSONDeserializer();
        CDX14SBOM sbom = cdx14Deserializer.readFromString(Files.readString(Path.of(CDX_14_JSON_SBOM)));

        // TODO more assertions
        assertNotNull(sbom);
        assertEquals("1.4", sbom.getSpecVersion());
        assertEquals("urn:uuid:139f2560-6fb3-4b85-b5fc-fe48be29b72b", sbom.getUID());
        assertEquals("1", sbom.getVersion());
        assertEquals("2023-04-05T07:56:11-04:00", sbom.getCreationData().getCreationTime());
        assertEquals(1, sbom.getCreationData().getCreationTools().size());
        List<CreationTool> creationTools = sbom.getCreationData().getCreationTools().stream().toList();
        assertEquals("anchore", creationTools.get(0).getVendor());
        assertEquals("syft", creationTools.get(0).getName());
        assertEquals("0.69.1", creationTools.get(0).getVersion());
        assertEquals("c4a58a4c9826996e", sbom.getRootComponent().getUID());
        assertEquals("container", sbom.getRootComponent().getType());
        assertEquals("alpine:latest", sbom.getRootComponent().getName());
        assertEquals("sha256:b6ca290b6b4cdcca5b3db3ffa338ee0285c11744b4a6abaa9627746ee3291d8d", sbom.getRootComponent().getVersion());
    }

    @Test
    public void componentsTest() throws IOException {
        CDX14JSONDeserializer cdx14Deserializer = new CDX14JSONDeserializer();
        CDX14SBOM sbom = cdx14Deserializer.readFromString(Files.readString(Path.of(CDX_14_JSON_SBOM)));
        assertEquals(17, sbom.getComponents().size());
        List<Component> components = sbom.getComponents().stream().toList();
        // checking for duplicates:
        int count = 1;
        for (int i = 1; i < components.size(); i++) {
            if (components.get(i).getUID() == "pkg:apk/alpine/musl@1.2.3-r4?arch=x86_64&upstream=musl&distro=alpine-3.17.3&package-id=d9700f02cf26e8b8") {
                // TODO implement author
                assertEquals("library", components.get(i).getType());
                assertEquals("musl", components.get(i).getName());
                // TODO CDX specific data is lost as the CDX SBOM stores incomplete component interfaces
                // assertEquals("1.2.3-r4", components.get(i).getVersion());
                // assertEquals("the musl c library (libc) implementation", components.get(i).getDescription());
                // TODO licenses not working
                // LicenseCollection licenses = components.get(i).getLicenses();
                // assertEquals("MIT", licenses.getInfoFromFiles().stream().toList().get(i));
                // TODO cpe, purl, external ref, and properties are not available in generic component
                count += 1;
            }
        }
    }
}
