package org.svip.sbomfactory.generators.generators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.Component;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.ParserController;
import org.svip.sbomfactory.generators.generators.cyclonedx.CycloneDXStore;
import org.svip.sbomfactory.generators.generators.utils.GeneratorException;
import org.svip.sbomfactory.generators.generators.utils.GeneratorSchema;
import org.svip.sbomfactory.generators.parsers.Parser;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.generators.utils.Tool;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.svip.sbomfactory.generators.utils.Debug.log;

public class SBOMGeneratorTest {
    private static final Path TEST_PATH = Path.of("src/test/java/org/svip/sbomfactory/generators/TestData/Java");
    private static final Path OUT_PATH = Path.of("src/test/java/org/svip/sbomfactory/generators/SBOMOut");
    private SBOM internalSBOM;
    private List<SBOMGenerator> generators;

    /**
     * Constructor parses an SBOM
     */
    public SBOMGeneratorTest() {
        // TODO Most of this is copy-pasted from GeneratorsTestMain, maybe move SBOM/controller construction into a
        //  different method
        ParserController testController = new ParserController(TEST_PATH); // Create new controller to build an SBOM

        Debug.enableSummary();
        // Read all files from our test path in
        try (Stream<Path> stream = Files.walk(TEST_PATH)) {
            stream.forEach(filepath -> {
                try {
                    // Set pwd to formatted filepath if it is actually a directory
                    if (Files.isDirectory(filepath)) {
                        testController.setPWD(filepath);
                        testController.incrementDirCounter();
                    } else { // Otherwise, it is a file, try to parse
                        testController.parse(filepath);
                    }
                } catch (Exception e) {
                    log(Debug.LOG_TYPE.EXCEPTION, e);
                }
            });
        } catch (Exception e) {
            log(Debug.LOG_TYPE.EXCEPTION, e);
        }

        internalSBOM = testController.getSBOM(); // Construct SBOM

        generators = new ArrayList<>();
        // Construct generators with each schema
        for(GeneratorSchema schema : GeneratorSchema.values()) {
            generators.add(new SBOMGenerator(internalSBOM, schema));
        }
    }

    @Test
    @DisplayName("writeFile()")
    void writeFileTest() throws IOException {
        OUT_PATH.toFile().mkdir();

        List<String> expectedFilePaths = new ArrayList<>();

        for(SBOMGenerator generator : generators) {
            // Test all possible formats
            for(GeneratorSchema.GeneratorFormat format : GeneratorSchema.GeneratorFormat.values()) {
                if(generator.getSchema().supportsFormat(format)) {
                    System.out.printf("generator.writeFile(\"%s\", GeneratorSchema.GeneratorFormat.%s)\n",
                            OUT_PATH, format);
                    generator.writeFile(OUT_PATH.toString(), format);
                    expectedFilePaths.add(generator.generatePathToSBOM(OUT_PATH.toString(), format));
                }
            }
        }

        File outDir = new File(OUT_PATH.toString());
        List<File> files = Arrays.stream(outDir.listFiles()).toList();

        for(String filePath : expectedFilePaths) {
            assertTrue(files.contains(new File(filePath)));
            System.out.println(filePath + " file has been generated.");

            // TODO test each format here?
        }

        //remove all files in the variable outdir inclusively
        for (File file : files) {
            Files.delete(file.toPath());
        }
        System.out.println("Files.delete(Paths.get(" + OUT_PATH + ");");
        Files.delete(Paths.get(OUT_PATH.toUri()));
    }

    @Test
    @DisplayName("Correct BOMStore Type")
    void buildBOMStoreTypeTest() throws InvocationTargetException, InstantiationException, IllegalAccessException,
            NoSuchMethodException {

        for(SBOMGenerator generator : generators) {
            BOMStore bomStore = generator.buildBOMStore();
            System.out.println("BOMStore Type: " + bomStore.getClass().getName());
            System.out.println("Generator Schema Type: " + generator.getSchema().getBomStoreType().getName());
            assertEquals(generator.getSchema().getBomStoreType(), bomStore.getClass());
        }
    }

    @Test
    @DisplayName("Correct BOMStore Tool")
    void buildBOMStoreToolTest() throws InvocationTargetException, InstantiationException, IllegalAccessException,
            NoSuchMethodException {

        SBOMGenerator generator = generators.get(0);
        BOMStore bomStore = generator.buildBOMStore();
        List<Tool> bomStoreTools = bomStore.getTools();
        assertTrue(bomStoreTools.contains(generator.getTool()));
        System.out.println("BOMStore Tool List contains " + generator.getTool());
    }

    @Test
    @DisplayName("BOMStore Contains ALL SBOM Components")
    void buildBOMStoreComponentTest() throws InvocationTargetException, InstantiationException, IllegalAccessException,
            NoSuchMethodException {

        for(SBOMGenerator generator : generators) {
            BOMStore bomStore = generator.buildBOMStore();
            String bomStoreType = bomStore.getClass().getSimpleName();
            Set<String> bomStoreComponentNames = bomStore.getAllComponents().stream().map(ParserComponent::getName)
                    .collect(Collectors.toSet());
            for(Component sbomComponent : internalSBOM.getAllComponents()) {
                if(sbomComponent.getUUID().equals(internalSBOM.getHeadUUID())) continue;
                String expectedName = sbomComponent.getName();
                System.out.printf("%s expected to contain component %s\n", bomStoreType, generator.getSchema(),
                        expectedName);
                assertTrue(bomStoreComponentNames.contains(expectedName));
                System.out.printf("    %s contains component %s\n", bomStoreType, expectedName);
            }
        }
    }

//    @Test
//    @DisplayName("addComponent() Non-Recursive")
//    void addComponentNonRecursiveTest() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
//        ParserComponent headComponent = new ParserComponent("Test Head");
//        ParserComponent testComponent = new ParserComponent("Test");
//
//        for(SBOMGenerator generator : generators) {
//            Object[] parameters = {"serialNumber", 1, headComponent};
//            Class<?>[] parameterTypes = Arrays.stream(parameters).map(Object::getClass).toArray(Class<?>[]::new);
//            BOMStore bomStore = generator.getSchema().getBomStoreType().getDeclaredConstructor(parameterTypes).newInstance(parameters);
//
//            generator.addComponent(bomStore, testComponent, false);
//        }
//    }

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
        System.out.printf("Generator.generatePathToSBOM(\"%s\", %s)\n", testDir, format);
        assertEquals(testDir + pathSeparator + sbomName + "_" + generators.get(0).getSchema() + "." +
                format.getExtension(), sbomPath);
    }
}
