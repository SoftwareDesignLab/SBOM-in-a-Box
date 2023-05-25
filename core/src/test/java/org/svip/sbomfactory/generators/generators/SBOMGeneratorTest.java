package org.svip.sbomfactory.generators.generators;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.Component;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.generators.cyclonedx.CycloneDXStore;
import org.svip.sbomfactory.generators.utils.generators.GeneratorException;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.generators.Tool;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class SBOMGeneratorTest {
    private static final Path OUT_PATH = Path.of("src/test/java/org/svip/sbomfactory/generators/SBOMOut");
    private final SBOM internalSBOM;
    private final List<SBOMGenerator> generators;

    /**
     * Constructor parses an SBOM
     */
    public SBOMGeneratorTest() {
//        Debug.enableSummary();

        internalSBOM = new SBOM();
        addTestComponentsToSBOM(internalSBOM);

        generators = new ArrayList<>();
        // Construct generators with each schema
        for(GeneratorSchema schema : GeneratorSchema.values()) {
            generators.add(new SBOMGenerator(internalSBOM, schema));
        }
    }

    @Test
    @DisplayName("writeFile()")
    void writeFileTest() throws IOException, GeneratorException {
        OUT_PATH.toFile().mkdir();

        List<String> expectedFilePaths = new ArrayList<>();

        for(SBOMGenerator generator : generators) {
            // Test all possible formats
            for(GeneratorSchema.GeneratorFormat format : GeneratorSchema.GeneratorFormat.values()) {
                if(generator.getSchema().supportsFormat(format)) {
                    Debug.log(Debug.LOG_TYPE.INFO,
                            String.format("generator.writeFile(\"%s\", GeneratorSchema.GeneratorFormat.%s)",
                                    OUT_PATH, format));

                    generator.writeFile(OUT_PATH.toString(), format);
                    expectedFilePaths.add(generator.generatePathToSBOM(OUT_PATH.toString(), format));
                }
            }
        }

        File outDir = new File(OUT_PATH.toString());
        List<File> files = Arrays.stream(outDir.listFiles()).toList();

        for(String filePath : expectedFilePaths) {
            assertTrue(files.contains(new File(filePath)));
            Debug.log(Debug.LOG_TYPE.SUMMARY, filePath + " file has been generated.");
            // File contents tested in serializer tests
        }

        // Remove all files in OUT_PATH inclusively
        for (File file : files) {
            Files.delete(file.toPath());
        }
        Debug.log(Debug.LOG_TYPE.INFO, "Files.delete(Paths.get(" + OUT_PATH + ");");
        Files.delete(Paths.get(OUT_PATH.toUri()));
    }

    @Test
    @DisplayName("writeFileToString() (NO PRETTY-PRINTING)")
    void writeFileToStringNoPrettyPrintingTest() throws GeneratorException, JsonProcessingException {
        for(SBOMGenerator generator : generators) {
            // Test all possible formats
            for(GeneratorSchema.GeneratorFormat format : GeneratorSchema.GeneratorFormat.values()) {
                if(generator.getSchema().supportsFormat(format)) {
                    Debug.log(Debug.LOG_TYPE.INFO,
                            "generator.writeFileToString(GeneratorSchema.GeneratorFormat." + format + ", false)");

                    String output = generator.writeFileToString(format, false);

                    // Assert not null and not empty string (correct output tested in serializer tests)
                    assertNotNull(output);
                    assertNotEquals(output, "");

                    // Currently the only formats that allow no pretty printing are JSON and XML
                    if(format == GeneratorSchema.GeneratorFormat.JSON || format == GeneratorSchema.GeneratorFormat.XML) {
                        assertFalse(output.contains("\n")); // If output contains a newline, it is pretty printed
                        Debug.log(Debug.LOG_TYPE.SUMMARY, format + " Output correctly asserted to be one-line");
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("writeFileToString() (PRETTY-PRINTING)")
    void writeFileToStringPrettyPrintingTest() throws GeneratorException, JsonProcessingException {
        for(SBOMGenerator generator : generators) {
            // Test all possible formats
            for(GeneratorSchema.GeneratorFormat format : GeneratorSchema.GeneratorFormat.values()) {
                if(generator.getSchema().supportsFormat(format)) {
                    Debug.log(Debug.LOG_TYPE.INFO,
                            "generator.writeFileToString(GeneratorSchema.GeneratorFormat." + format + ", true)");

                    String output = generator.writeFileToString(format, true);

                    // Assert not null and not empty string (correct output tested in serializer tests)
                    assertNotNull(output);
                    assertNotEquals(output, "");

                    assertTrue(output.contains("\n")); // If output contains a newline, it is pretty printed
                    Debug.log(Debug.LOG_TYPE.SUMMARY, format + " Output correctly asserted to be pretty-printed");
                }
            }
        }
    }

    @Test
    @DisplayName("Correct BOMStore Type")
    void buildBOMStoreTypeTest() throws GeneratorException {
        for(SBOMGenerator generator : generators) {
            BOMStore bomStore = generator.buildBOMStore();
            Debug.log(Debug.LOG_TYPE.SUMMARY, "BOMStore Type: " + bomStore.getClass().getName());
            Debug.log(Debug.LOG_TYPE.SUMMARY,
                    "Generator Schema Type: " + generator.getSchema().getBomStoreType().getName());

            assertEquals(generator.getSchema().getBomStoreType(), bomStore.getClass());
        }
    }

    @Test
    @DisplayName("Correct BOMStore Tool")
    void buildBOMStoreToolTest() throws GeneratorException {
        SBOMGenerator generator = generators.get(0);
        BOMStore bomStore = generator.buildBOMStore();
        List<Tool> bomStoreTools = bomStore.getTools();
        assertTrue(bomStoreTools.contains(generator.getTool()));
        Debug.log(Debug.LOG_TYPE.SUMMARY, "BOMStore Tool List contains " + generator.getTool());
    }

    @Test
    @DisplayName("BOMStore Contains ALL SBOM Components")
    void buildBOMStoreComponentTest() throws GeneratorException {
        for(SBOMGenerator generator : generators) { // TODO do we need to do for each type of generator?
            BOMStore bomStore = generator.buildBOMStore();
            String bomStoreType = bomStore.getClass().getSimpleName();
            Set<String> bomStoreComponentNames = bomStore.getAllComponents().stream().map(ParserComponent::getName)
                    .collect(Collectors.toSet());

            for(Component sbomComponent : internalSBOM.getAllComponents()) {
                if(sbomComponent.getUUID().equals(internalSBOM.getHeadUUID())) continue;
                String expectedName = sbomComponent.getName();
                Debug.log(Debug.LOG_TYPE.SUMMARY,
                        String.format("%s expected to contain component %s", bomStoreType, expectedName));
                assertTrue(bomStoreComponentNames.contains(expectedName));
                Debug.log(Debug.LOG_TYPE.SUMMARY,
                        String.format("    %s contains component %s", bomStoreType, expectedName));
            }
        }
    }

    @Test
    @DisplayName("addComponent() Non-Recursive")
    void addComponentNonRecursiveTest() {
        SBOM testSBOM = new SBOM();
        List<ParserComponent> testComponents = addTestComponentsToSBOM(testSBOM);

        BOMStore bomStore = new CycloneDXStore("serialNumber", 1, testComponents.get(0));
        SBOMGenerator generator = new SBOMGenerator(testSBOM, GeneratorSchema.CycloneDX);
        generator.addComponent(bomStore, testComponents.get(1), false);
        Debug.log(Debug.LOG_TYPE.INFO, "generator.addComponent(bomStore, testComponent, false);");
        assertEquals(1, bomStore.getAllComponents().size());
    }

    @Test
    @DisplayName("addComponent() Recursive")
    void addComponentRecursiveTest() {
        SBOM testSBOM = new SBOM();
        List<ParserComponent> testComponents = addTestComponentsToSBOM(testSBOM);

        BOMStore bomStore = new CycloneDXStore("serialNumber", 1, testComponents.get(0));
        SBOMGenerator generator = new SBOMGenerator(testSBOM, GeneratorSchema.CycloneDX);
        generator.addComponent(bomStore, testComponents.get(1), true);
        Debug.log(Debug.LOG_TYPE.INFO, "generator.addComponent(bomStore, testComponent, true);");
        assertEquals(3, bomStore.getAllComponents().size());
    }

    @Test
    @DisplayName("addChildren()")
    void addChildrenTest() {
        SBOM testSBOM = new SBOM();
        List<ParserComponent> testComponents = addTestComponentsToSBOM(testSBOM);

        BOMStore bomStore = new CycloneDXStore("serialNumber", 1, testComponents.get(0));
        SBOMGenerator generator = new SBOMGenerator(testSBOM, GeneratorSchema.CycloneDX);
        generator.addComponent(bomStore, testComponents.get(1), false);
        Debug.log(Debug.LOG_TYPE.INFO, "generator.addComponent(bomStore, testComponent, false);");
        assertEquals(1, bomStore.getAllComponents().size());

        generator.addChildren(bomStore, testComponents.get(1));
        Debug.log(Debug.LOG_TYPE.INFO, "generator.addChildren(bomStore, testComponent);");
        assertEquals(3, bomStore.getAllComponents().size());
    }

    @Test
    @DisplayName("addChildren() with Invalid Parent")
    void addChildrenInvalidParentTest() {
        SBOM testSBOM = new SBOM();
        List<ParserComponent> testComponents = addTestComponentsToSBOM(testSBOM);

        BOMStore bomStore = new CycloneDXStore("serialNumber", 1, testComponents.get(0));
        SBOMGenerator generator = new SBOMGenerator(testSBOM, GeneratorSchema.CycloneDX);

        generator.addChildren(bomStore, testComponents.get(1));
        Debug.log(Debug.LOG_TYPE.INFO, "generator.addChildren(bomStore, testComponent);");
        assertEquals(0, bomStore.getAllComponents().size());
    }

    @Test
    @DisplayName("generatePathToSBOM()")
    void generatePathToSBOMTest() {
        String sbomName = internalSBOM.getComponent(internalSBOM.getHeadUUID()).getName();
        GeneratorSchema.GeneratorFormat format = GeneratorSchema.GeneratorFormat.JSON;

        String pathSeparator = "/";
        final String os = System.getProperty("os.name").toLowerCase();
        if(os.contains("win")) pathSeparator = "\\";
        String testDir = String.join(pathSeparator, "src", "main", "java", "test");

        String sbomPath = generators.get(0).generatePathToSBOM(testDir, format);
        Debug.log(Debug.LOG_TYPE.INFO,
                String.format("Generator.generatePathToSBOM(\"%s\", %s)", testDir, format));
        assertEquals(testDir + pathSeparator + sbomName + "_" + generators.get(0).getSchema() + "." +
                format.getExtension(), sbomPath);
    }

    /**
     * Private helper method to add test components to any SBOM.
     *
     * @param sbom The SBOM instance to add the test components to.
     * @return A list of the components: {@code [Head_Component, Test_Component, Child_Component_0, Child_Component_1]}
     */
    public static List<ParserComponent> addTestComponentsToSBOM(SBOM sbom) {
        ParserComponent headComponent = new ParserComponent("Head_Component");
        ParserComponent testComponent = new ParserComponent("Test_Component");
        ParserComponent testChild0 = new ParserComponent("Child_Component_0");
        ParserComponent testChild1 = new ParserComponent("Child_Component_1");

        sbom.addComponent(null, headComponent);
        Debug.log(Debug.LOG_TYPE.INFO, "sbom.addComponent(null, headComponent);");
        sbom.addComponent(headComponent.getUUID(), testComponent);
        Debug.log(Debug.LOG_TYPE.INFO, "sbom.addComponent(headComponent.getUUID(), testComponent);");
        sbom.addComponent(testComponent.getUUID(), testChild0);
        Debug.log(Debug.LOG_TYPE.INFO, "sbom.addComponent(testComponent.getUUID(), testChild);");
        sbom.addComponent(testComponent.getUUID(), testChild1);
        Debug.log(Debug.LOG_TYPE.INFO, "sbom.addComponent(testComponent.getUUID(), testChild2);");

        return List.of(headComponent, testComponent, testChild0, testChild1);
    }
}
