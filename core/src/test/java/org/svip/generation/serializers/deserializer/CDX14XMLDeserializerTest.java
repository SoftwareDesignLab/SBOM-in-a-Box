package org.svip.generation.serializers.deserializer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.serializers.deserializer.CDX14XMLDeserializer;
import org.svip.serializers.deserializer.Deserializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CDX14XMLDeserializerTest extends DeserializerTest {
    private final CDX14SBOM cdx14xml;

    public CDX14XMLDeserializerTest() throws IOException {
        cdx14xml = (CDX14SBOM) getDeserializer().readFromString(Files.readString(Path.of(CDX_14_XML_SBOM)));
    }

    // TODO, make a new SBOM in the style of the other test SBOMs. Swap back the timestamp, metadata, and other tests I changed for use with the temp SBOM

    @Override
    public Deserializer getDeserializer() {
        return new CDX14XMLDeserializer();
    }

    @Test
    public void formatTest() {
        assertEquals("CycloneDX", cdx14xml.getFormat());
    }

    @Disabled
    @Test
    public void specVersionTest() {
        assertEquals("1.4", cdx14xml.getSpecVersion());
    }

    @Test
    public void uidTest() {
        assertEquals("urn:uuid:f057a217-e332-4981-94dc-799d6a776f58", cdx14xml.getUID());
    }

    @Test
    public void versionTest() {
        assertEquals("1", cdx14xml.getVersion());
    }

    @Test
    public void metadataTest() {
        assertEquals("2023-02-21T08:50:33-05:00", cdx14xml.getCreationData().getCreationTime());
    }

    @Test
    public void metadataToolTest() {
        CreationTool tool = cdx14xml.getCreationData().getCreationTools().stream().findFirst().get();
        assertEquals("syft", tool.getName());
        assertEquals("anchore", tool.getVendor());
        assertEquals("0.69.1", tool.getVersion());
    }

    @Test
    public void rootComponentTest() {
        CDX14ComponentObject root = cdx14xml.getRootComponent();
        assertEquals("alpine:latest", root.getName());
        assertEquals("5339058ca5e06f8a", root.getUID());
        assertEquals("container", root.getType());
    }

    @Test
    public void componentTest() {
        assertEquals(17, cdx14xml.getComponents().size());
    }
}
