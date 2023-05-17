package org.svip.sbomfactory.generators.generators.spdx;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.generators.generators.BOMStoreTestCore;
import org.svip.sbomfactory.generators.generators.utils.GeneratorException;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SPDXStoreTest extends BOMStoreTestCore<SPDXStore> {
    protected SPDXStoreTest() {
        super(new SPDXStore("testSerialNumber", 1, testComponents.get(0)));

        // Add test licenses and files analyzed to see how SPDXStore handles them
        testComponents.get(0).addLicense("MIT");
        testComponents.get(1).addLicense("Random External License");
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

//    @Test
//    @DisplayName("getComponents() from complete BOMStore")
//    void getComponentsTest() throws GeneratorException {
//        Debug.log(Debug.LOG_TYPE.INFO, "addComponent(testComponents.get(0))");
//        bomStore.addComponent(testComponents.get(0)); // Add head component
//        Debug.log(Debug.LOG_TYPE.INFO, "addComponent(testComponents.get(1))");
//        bomStore.addComponent(testComponents.get(1)); // Add parent component
//
//        assertEquals(2, bomStore.getComponents().size());
//        Debug.log(Debug.LOG_TYPE.SUMMARY, "Correctly contains 2 top-level components");
//
//        Debug.log(Debug.LOG_TYPE.INFO, "addChild(testComponents.get(0), testComponents.get(2))");
//        bomStore.addChild(testComponents.get(0), testComponents.get(2)); // Add child component
//
//        assertEquals(2, bomStore.getComponents().size());
//        Debug.log(Debug.LOG_TYPE.SUMMARY, "Correctly contains 2 top-level components");
//    }

//    @Test
//    @DisplayName("addComponent()")
//    void addComponentTest() {
//        Debug.log(Debug.LOG_TYPE.INFO, "addComponent(testComponents.get(0))");
//        bomStore.addComponent(testComponents.get(0)); // Add head component
//
//        assertTrue(bomStore.getAllComponents().contains(testComponents.get(0)));
//        assertEquals(1, bomStore.getAllComponents().size());
//        Debug.log(Debug.LOG_TYPE.SUMMARY, "Correctly contains testComponents.get(0)");
//    }
//
//    @Test
//    @DisplayName("addChild() with valid parent")
//    void addChildValidTest() throws GeneratorException {
//        Debug.log(Debug.LOG_TYPE.INFO, "addComponent(testComponents.get(0))");
//        bomStore.addComponent(testComponents.get(0)); // Add head component
//
//        Debug.log(Debug.LOG_TYPE.INFO, "addChild(testComponents.get(0), testComponents.get(1))");
//        bomStore.addChild(testComponents.get(0), testComponents.get(1)); // Add child component
//
//        assertTrue(bomStore.getAllComponents().contains(testComponents.get(1)));
//        Debug.log(Debug.LOG_TYPE.SUMMARY, "Correctly contains testComponents.get(1)");
//    }
//
//    @Test
//    @DisplayName("addChild() with invalid parent")
//    void addChildInvalidTest() {
//        assertEquals(0, bomStore.getAllComponents().size());
//        Debug.log(Debug.LOG_TYPE.SUMMARY, "Correctly contains 0 components");
//
//        Debug.log(Debug.LOG_TYPE.INFO, "addChild(testComponents.get(0), testComponents.get(1))");
//
//        assertThrows(GeneratorException.class, () -> {
//            bomStore.addChild(testComponents.get(0), testComponents.get(1)); // Add child component
//        });
//
//        Debug.log(Debug.LOG_TYPE.SUMMARY, "Correctly throws GeneratorException");
//    }
//
//    @Test
//    @DisplayName("getChildren() with children")
//    void getChildrenTest() throws GeneratorException {
//        Debug.log(Debug.LOG_TYPE.INFO, "addComponent(testComponents.get(0))");
//        bomStore.addComponent(testComponents.get(0)); // Add head component
//
//        Debug.log(Debug.LOG_TYPE.INFO, "addChild(testComponents.get(0), testComponents.get(1))");
//        bomStore.addChild(testComponents.get(0), testComponents.get(1)); // Add child component
//
//        Debug.log(Debug.LOG_TYPE.INFO, "addChild(testComponents.get(0), testComponents.get(2))");
//        bomStore.addChild(testComponents.get(0), testComponents.get(2)); // Add child component
//
//        Debug.log(Debug.LOG_TYPE.INFO, "getChildren(testComponents.get(0).getUUID())");
//        List<ParserComponent> children = bomStore.getChildren(testComponents.get(0).getUUID());
//
//        assertTrue(children.contains(testComponents.get(1)));
//        Debug.log(Debug.LOG_TYPE.SUMMARY, "Child list correctly contains testComponents.get(1)");
//
//        assertTrue(children.contains(testComponents.get(2)));
//        Debug.log(Debug.LOG_TYPE.SUMMARY, "Child list correctly contains testComponents.get(2)");
//    }
//
//    @Test
//    @DisplayName("getChildren() without children")
//    void getChildrenWithoutChildrenTest() {
//        Debug.log(Debug.LOG_TYPE.INFO, "addComponent(testComponents.get(0))");
//        bomStore.addComponent(testComponents.get(0)); // Add head component
//
//        Debug.log(Debug.LOG_TYPE.INFO, "getChildren(testComponents.get(0).getUUID())");
//        List<ParserComponent> children = bomStore.getChildren(testComponents.get(0).getUUID());
//
//        assertEquals(0, children.size());
//        Debug.log(Debug.LOG_TYPE.SUMMARY, "Child list is empty");
//    }
}

