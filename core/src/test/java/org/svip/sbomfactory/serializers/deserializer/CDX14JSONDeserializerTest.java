package org.svip.sbomfactory.serializers.deserializer;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CDX14JSONDeserializerTest extends DeserializerTest {
    private final CDX14SBOM cdx14json;
    public CDX14JSONDeserializerTest() throws IOException {
        super(new CDX14JSONDeserializer());
        cdx14json = (CDX14SBOM) getDeserializer().readFromString(Files.readString(Path.of(CDX_14_JSON_SBOM)));
    }

    @Test
    public void formatTest() {
        assertEquals("CycloneDX", cdx14json.getFormat());
    }

    @Test
    public void specVersionTest() {
        assertEquals("1.4", cdx14json.getSpecVersion());
    }

    @Test
    public void uidTest() {
        assertEquals("12345678", cdx14json.getUID());
    }

    @Test
    public void versionTest() {
        assertEquals("1.0.0", cdx14json.getVersion());
    }

    @Test
    public void metadataTest() {
        assertEquals("TEST TIMESTAMP", cdx14json.getCreationData().getCreationTime());

        CreationTool tool = cdx14json.getCreationData().getCreationTools().stream().findFirst().get();
        assertEquals("SVIP", tool.getName());
        assertEquals("SVIP", tool.getVendor());
        assertEquals("SVIP", tool.getVersion());
        assertEquals("hash", tool.getHashes().get("SHA256"));

        Contact author = cdx14json.getCreationData().getAuthors().stream().findFirst().get();
        assertEquals("Test Author", author.getName());
        assertEquals("author@publisher.xyz", author.getEmail());
        assertEquals("123-456-7890", author.getPhone());

        // TODO
    }

    @Test
    private void testComponent(Component component) {
        assertEquals("1.4", cdx14json.getSpecVersion());

        // TODO
    }

    @Test
    public void externalReferencesTest() {
        assertEquals("12345678", cdx14json.getUID());

        // TODO
    }

    @Test
    public void dependenciesTest() {
        assertEquals("1.0.0", cdx14json.getVersion());

        // TODO
    }
}
