package org.svip.sbomfactory.serializers.deserializer;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Component;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.ExternalReference;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SPDX23JSONDeserializerTest extends DeserializerTest {
    private final SPDX23SBOM spdx23json;
    public SPDX23JSONDeserializerTest() throws IOException {
        super(new SPDX23JSONDeserializer());
        spdx23json = (SPDX23SBOM) getDeserializer().readFromString(Files.readString(Path.of(SPDX23_JSON_SBOM)));
    }

    @Test
    public void formatTest() {
        assertEquals("SPDX", spdx23json.getFormat());
    }

    @Test
    public void specVersionTest() {
        assertEquals("2.3", spdx23json.getSpecVersion());
    }

    @Test
    public void nameTest() {
        assertEquals("Test SBOM", spdx23json.getName());
    }

    @Test
    public void uidTest() {
        assertEquals("12345678", spdx23json.getUID());
    }

    @Test
    public void documentCommentTest() {
        assertEquals("Test Document Comment", spdx23json.getDocumentComment());
    }

    @Test
    public void dataLicenseTest() {
        assertEquals(Set.of("CC0-1.0"), spdx23json.getLicenses());
    }

    @Test
    public void metadataTest() {
        assertEquals("TEST TIMESTAMP", spdx23json.getCreationData().getCreationTime());
        assertEquals("This SBOM was generated using the SVIP serializer tooling.",
                spdx23json.getCreationData().getCreatorComment());
        assertEquals("0.0", spdx23json.getSPDXLicenseListVersion());
    }

    @Test
    public void metadataToolTest() {
        CreationTool tool = spdx23json.getCreationData().getCreationTools().stream().findFirst().get();
        assertEquals("SVIP Serializer", tool.getName());
        assertEquals("1.0.0", tool.getVersion());
    }

    @Test
    public void metadataAuthorTest() {
        Contact author = spdx23json.getCreationData().getAuthors().stream().findFirst().get();
        assertEquals("Test Author", author.getName());
        assertEquals("author@publisher.xyz", author.getEmail());
    }

    @Test
    public void metadataSupplierTest() {
        Organization supplier = spdx23json.getCreationData().getSupplier();
        assertEquals("Supplier", supplier.getName());
        assertEquals("Supplier", supplier.getContacts().stream().findFirst().get().getName());
        assertEquals("supplier@svip.xyz", supplier.getContacts().stream().findFirst().get().getEmail());
    }

    private void testComponent(SPDX23PackageObject component, int num) {
        assertEquals("COMPONENT " + num, component.getName());
        assertEquals("uid" + num, component.getUID());
        assertEquals("type" + num, component.getType());
        assertEquals("comment" + num, component.getComment());
        assertEquals("version" + num, component.getVersion());
        assertEquals("fileName" + num, component.getFileName());
        assertTrue(component.getCPEs().contains("cpe" + num));
        assertTrue(component.getPURLs().contains("purl" + num));

        // Supplier
        Organization supplier = component.getSupplier();
        assertEquals("SVIP" + num, supplier.getName());
        assertEquals("svip@svip.xyz", supplier.getContacts().stream().findFirst().get().getEmail());

        assertEquals("author" + num, component.getAuthor());
        assertEquals("downloadLocation" + num, component.getDownloadLocation());
        assertEquals(true, component.getFilesAnalyzed());
        assertEquals("homePage" + num, component.getHomePage());
        assertEquals("sourceInfo" + num, component.getSourceInfo());

        assertTrue(component.getLicenses().getConcluded().contains("concluded" + num));
        assertTrue(component.getLicenses().getDeclared().contains("declared" + num));
        assertTrue(component.getLicenses().getInfoFromFiles().contains("licenseFileText" + num));
        assertEquals("comment" + num, component.getLicenses().getComment());

        assertEquals("copyright" + num, component.getCopyright());
        assertEquals("summary" + num, component.getDescription().getSummary());
        assertEquals("extendedDescription" + num, component.getDescription().getDescription());
        assertEquals("attributionText" + num, component.getAttributionText());
        assertEquals("buildDate" + num, component.getBuiltDate());
        assertEquals("releaseDate" + num, component.getReleaseDate());
        assertEquals("validUntilDate" + num, component.getValidUntilDate());
        assertEquals("verificationCode" + num, component.getVerificationCode());

        assertEquals("hash" + num, component.getHashes().get("SHA256"));

        ExternalReference ref = component.getExternalReferences().stream().findFirst().get();
        assertEquals(num + ".svip.xyz", ref.getUrl());
        assertEquals("testRef" + num, ref.getType());
        assertEquals("testCategory", ref.getCategory());
    }

    @Test
    public void rootComponentTest() {
        testComponent(spdx23json.getRootComponent(), 0);
    }

    @Test
    public void componentTest() {
        assertEquals(4, spdx23json.getComponents().size());
        for (Component component : spdx23json.getComponents()) {
            testComponent((SPDX23PackageObject) component,
                    Integer.parseInt(component.getName().substring("COMPONENT ".length())));
        }
    }

    @Test
    public void relationshipsText() {
        Relationship rel = spdx23json.getRelationships().get("uid1").stream().findFirst().get();
        assertEquals("uid3", rel.getOtherUID());
        assertEquals("DESCRIBES", rel.getRelationshipType());
        assertEquals("Test Relationship Comment", rel.getComment());
    }
}
