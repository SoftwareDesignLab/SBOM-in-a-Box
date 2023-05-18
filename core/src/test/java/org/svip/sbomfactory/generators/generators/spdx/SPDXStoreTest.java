package org.svip.sbomfactory.generators.generators.spdx;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.generators.generators.BOMStoreTestCore;
import org.svip.sbomfactory.generators.generators.utils.GeneratorException;
import org.svip.sbomfactory.generators.generators.utils.License;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SPDXStoreTest extends BOMStoreTestCore<SPDXStore> {
    protected SPDXStoreTest() {
        super(new SPDXStore("testSerialNumber", 1, testComponents.get(0)));

        // Add test licenses and files analyzed to see how SPDXStore handles them
        testComponents.get(0).addLicense("Random External License");
        testComponents.get(0).addFile("Test/File/Path/source_file.xyz");
    }

    @Test
    @DisplayName("getAllComponents() from empty BOMStore")
    void getAllComponentsEmptyTest() {
        assertEquals(0, bomStore.getAllComponents().size());
    }

    @Test
    @DisplayName("getAllComponents()")
    void getAllComponentsTest() throws GeneratorException {
        Debug.log(Debug.LOG_TYPE.INFO, "addComponent(testComponents.get(0))");
        bomStore.addComponent(testComponents.get(0)); // Add head component

        Debug.log(Debug.LOG_TYPE.INFO, "addChild(testComponents.get(0), testComponents.get(1))");
        bomStore.addChild(testComponents.get(0), testComponents.get(1)); // Add child component

        assertEquals(2, bomStore.getAllComponents().size());
        Debug.log(Debug.LOG_TYPE.SUMMARY, "Correctly contains 2 components");
    }

    @Test
    @DisplayName("addComponent()")
    void addComponentTest() {
        Debug.log(Debug.LOG_TYPE.INFO, "addComponent(testComponents.get(0))");
        bomStore.addComponent(testComponents.get(0)); // Add head component

        assertTrue(bomStore.getAllComponents().contains(testComponents.get(0)));
        assertEquals(1, bomStore.getAllComponents().size());
        Debug.log(Debug.LOG_TYPE.SUMMARY, "Correctly contains testComponents.get(0)");

        ParserComponent component = bomStore.getAllComponents().stream().toList().get(0);

        String spdxId = component.getSPDXID();
        assertNotNull(spdxId);
        Debug.log(Debug.LOG_TYPE.SUMMARY, "SPDX Package has ID: " + spdxId);

        License external = bomStore.getExternalLicenses().stream().toList().get(0);
        assertEquals(1, bomStore.getExternalLicenses().size());
        Debug.log(Debug.LOG_TYPE.SUMMARY, "SPDX Package correctly contains resolved external license with ID: "
                + external.getSpdxLicense());

        Map.Entry<String, String> file = bomStore.getFiles().entrySet().stream().toList().get(0);
        assertEquals(1, bomStore.getFiles().keySet().size());
        Debug.log(Debug.LOG_TYPE.SUMMARY, String.format("SPDX Document correctly contains file: %s with ID: %s",
                file.getKey(), file.getValue()));

        assertTrue(bomStore.getDocumentDescribes().contains(spdxId));
        Debug.log(Debug.LOG_TYPE.SUMMARY, "SPDX Document Description field contains ID: " + spdxId);

        assertEquals(1, bomStore.getPackages().size());
        Debug.log(Debug.LOG_TYPE.SUMMARY, "SPDX Packages contains added component");
    }

    @Test
    @DisplayName("addChild() with valid parent")
    void addChildValidTest() throws GeneratorException {
        Debug.log(Debug.LOG_TYPE.INFO, "addComponent(testComponents.get(0))");
        bomStore.addComponent(testComponents.get(0)); // Add head component

        Debug.log(Debug.LOG_TYPE.INFO, "addChild(testComponents.get(0), testComponents.get(1))");
        bomStore.addChild(testComponents.get(0), testComponents.get(1)); // Add child component

        Relationship relationship = bomStore.getRelationships().get(0);

        assertEquals(relationship.getRelationshipType(), Relationship.RELATIONSHIP_TYPE.DEPENDS_ON);
        assertEquals(relationship.getElementId(), testComponents.get(0).getSPDXID());
        assertEquals(relationship.getRelatedElement(), testComponents.get(1).getSPDXID());
        Debug.log(Debug.LOG_TYPE.SUMMARY, "Relationship correctly generated: " + relationship);
    }

    @Test
    @DisplayName("addChild() with invalid parent")
    void addChildInvalidTest() throws GeneratorException {
        assertEquals(0, bomStore.getAllComponents().size());
        Debug.log(Debug.LOG_TYPE.SUMMARY, "Correctly contains 0 components");

        testComponents.get(0).setSPDXID(null);
        testComponents.get(1).setSPDXID(null);
        Debug.log(Debug.LOG_TYPE.SUMMARY, "Reset testComponents SPDX IDs to null");

        Debug.log(Debug.LOG_TYPE.INFO, "addChild(testComponents.get(0), testComponents.get(1))");

        assertThrows(GeneratorException.class, () -> {
            bomStore.addChild(testComponents.get(0), testComponents.get(1)); // Add child component
        });
        Debug.log(Debug.LOG_TYPE.SUMMARY, "Correctly throws GeneratorException");
    }
}

