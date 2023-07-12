package org.svip.sbomfactory.serializers.deserializer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.old.SBOM;
import org.svip.sbom.model.shared.metadata.Contact;
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
        assertEquals("1.4", sbom.getSpecVersion());
        // DataLicense
        assertEquals("CC0-1.0", sbom.getLicenses().stream().toList().get(0));
        // DocumentName
        assertEquals("Test SBOM", sbom.getName());
        // DocumentNamespace
        assertEquals("12345678", sbom.getUID());
        // LicenseListVersion
        assertEquals("0.0", sbom.getSPDXLicenseListVersion());
        // created
        assertEquals("TEST TIMESTAMP", sbom.getCreationData().getCreationTime());

        // creators
        assertEquals(1, sbom.getCreationData().getCreationTools().size());
        List<CreationTool> creationTools = sbom.getCreationData().getCreationTools().stream().toList();
        assertEquals("SVIP Serializer", creationTools.get(0).getName());
        assertEquals("1.0.0", creationTools.get(0).getVersion());
        List<Contact> authorsList = sbom.getCreationData().getAuthors().stream().toList();
        assertEquals(2, authorsList.size());
        for( int i = 0; i < authorsList.size(); i++) {
            if (authorsList.get(i).getName() == "Test Author") {
                assertEquals("author@publisher.xyz", authorsList.get(i).getEmail());
            }
            else if (authorsList.get(i).getName() == "Supplier"){
                assertEquals("supplier@svip.xyz", authorsList.get(i).getEmail());
            }
        }
        //assertEquals("This SBOM was created for serializer testing",
        //        sbom.getCreationData().getCreatorComment());
        //assertEquals("Test Document Comment", sbom.getDocumentComment());
    }
    @Test
    public void componentTest() throws IOException {
        SPDX23TagValueDeserializer spdx23Deserializer = new SPDX23TagValueDeserializer();
        SPDX23SBOM sbom = spdx23Deserializer.readFromString(Files.readString(Path.of(SPDX23_TAGVALUE_SBOM)));
        assertNotNull(sbom);
        assertEquals(3, sbom.getComponents().size());
        List<Component> components = sbom.getComponents().stream().toList();
        // check for duplicates
        int count = 0;
        for (int i = 0; i < components.size(); i++) {
            Component componentObject = components.get(i);
            if (Objects.equals(componentObject.getUID(), "uid1")) {
                assertEquals("COMPONENT 1", componentObject.getName());
                // TODO access the spdx23 specific data
                count += 1;
            }
        }
        assertEquals(1, count);
    }
}
