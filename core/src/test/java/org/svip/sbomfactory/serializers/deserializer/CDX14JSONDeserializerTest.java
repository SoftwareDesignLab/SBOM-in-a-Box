package org.svip.sbomfactory.serializers.deserializer;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.ExternalReference;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CDX14JSONDeserializerTest extends DeserializerTest {
    private final CDX14SBOM cdx14json;

    public CDX14JSONDeserializerTest() throws IOException {
        cdx14json = (CDX14SBOM) getDeserializer().readFromString(Files.readString(Path.of(CDX_14_JSON_SBOM)));
    }

    @Override
    public Deserializer getDeserializer() {
        return new CDX14JSONDeserializer();
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

        assertEquals("This SBOM was generated using the SVIP serializer tooling.",
                cdx14json.getCreationData().getCreatorComment());
        assertTrue(cdx14json.getCreationData().getProperties().get("testProperty").contains("testValue"));

        assertTrue(cdx14json.getLicenses().contains("MIT"));
    }

    @Test
    public void metadataToolTest() {
        CreationTool tool = cdx14json.getCreationData().getCreationTools().stream().findFirst().get();
        assertEquals("SVIP Serializer", tool.getName());
        assertEquals("SVIP", tool.getVendor());
        assertEquals("1.0.0", tool.getVersion());
        assertEquals("hash", tool.getHashes().get("SHA256"));
    }

    @Test
    public void metadataAuthorTest() {
        Contact author = cdx14json.getCreationData().getAuthors().stream().findFirst().get();
        assertEquals("Test Author", author.getName());
        assertEquals("author@publisher.xyz", author.getEmail());
        assertEquals("123-456-7890", author.getPhone());
    }

    @Test
    public void metadataSupplierTest() {
        Organization supplier = cdx14json.getCreationData().getSupplier();
        assertEquals("Supplier", supplier.getName());
        assertEquals("svip.xyz", supplier.getUrl());
        assertEquals("Supplier", supplier.getContacts().stream().findFirst().get().getName());
        assertEquals("supplier@svip.xyz", supplier.getContacts().stream().findFirst().get().getEmail());
        assertEquals("123-456-7890", supplier.getContacts().stream().findFirst().get().getPhone());
    }

    @Test
    public void metadataManufactureTest() {
        Organization manufacture = cdx14json.getCreationData().getManufacture();
        assertEquals("Manufacturer", manufacture.getName());
        assertEquals("svip.xyz", manufacture.getUrl());
        assertEquals("SVIP", manufacture.getContacts().stream().findFirst().get().getName());
        assertEquals("manufacturer@svip.xyz", manufacture.getContacts().stream().findFirst().get().getEmail());
        assertEquals("123-456-7890", manufacture.getContacts().stream().findFirst().get().getPhone());

    }

    private void testComponent(CDX14ComponentObject component, int num) {
        assertEquals("COMPONENT " + num, component.getName());
        assertEquals("uid" + num, component.getUID());
        assertEquals("mimeType" + num, component.getMimeType());
        assertEquals("type" + num, component.getType());
        assertEquals("group" + num, component.getGroup());
        assertEquals("version" + num, component.getVersion());
        assertEquals("scope" + num, component.getScope());
        assertEquals("copyright" + num, component.getCopyright());
        assertTrue(component.getCPEs().contains("cpe" + num));
        assertTrue(component.getPURLs().contains("purl" + num));

        // Supplier
        Organization supplier = component.getSupplier();
        assertEquals("SVIP" + num, supplier.getName());
        assertEquals(num + ".svip.xyz", supplier.getUrl());
        assertEquals("SVIP", supplier.getContacts().stream().findFirst().get().getName());
        assertEquals("svip@svip.xyz", supplier.getContacts().stream().findFirst().get().getEmail());
        assertEquals("123-456-7890", supplier.getContacts().stream().findFirst().get().getPhone());

        assertEquals("author" + num, component.getAuthor());
        assertEquals("publisher" + num, component.getPublisher());
        assertEquals("Summary: summary" + num + " | Details: extendedDescription" + num,
                component.getDescription().getSummary());
        assertEquals("hash" + num, component.getHashes().get("SHA256"));

        assertTrue(component.getLicenses().getInfoFromFiles().containsAll(List.of("licenseFileText" + num,
                "declared" + num,
                "concluded" + num)));

        ExternalReference ref = component.getExternalReferences().stream().findFirst().get();
        assertEquals(num + ".svip.xyz", ref.getUrl());
        // Comment is irrelevant here
        assertEquals("testRef" + num, ref.getType());
        assertEquals("hash" + num, ref.getHashes().get("SHA256"));
        assertTrue(component.getProperties().get("property" + num).contains("value" + num));
    }

    @Test
    public void rootComponentTest() {
        testComponent(cdx14json.getRootComponent(), 0);
    }

    @Test
    public void componentTest() {
        assertEquals(4, cdx14json.getComponents().size());
        for (Component component : cdx14json.getComponents()) {
            testComponent((CDX14ComponentObject) component,
                    Integer.parseInt(component.getName().substring("COMPONENT ".length())));
        }
    }

    @Test
    public void externalReferencesTest() {
        ExternalReference ref = cdx14json.getExternalReferences().stream().findFirst().get();
        assertEquals("svip.xyz", ref.getUrl());
        // Comment is irrelevant here
        assertEquals("testRef", ref.getType());
        assertEquals("hash", ref.getHashes().get("SHA256"));
    }

    @Test
    public void dependenciesTest() {
        Relationship rel = cdx14json.getRelationships().get("uid1").stream().findFirst().get();
        assertEquals("uid3", rel.getOtherUID());
    }
}
