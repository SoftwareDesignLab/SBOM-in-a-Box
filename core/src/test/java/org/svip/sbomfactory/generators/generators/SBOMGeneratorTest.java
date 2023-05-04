package org.svip.sbomfactory.generators.generators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.Component;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.ParserController;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
                    System.out.printf("generator.writeFile(\"%s\", GeneratorSchema.GeneratorFormat.%s)\n", OUT_PATH, format);
                    generator.writeFile(OUT_PATH.toString(), format);
                    expectedFilePaths.add(generator.generatePathToSBOM(OUT_PATH.toString(), format));
                }
            }
        }

        File outDir = new File(OUT_PATH.toString());
        List<File> files = Arrays.stream(outDir.listFiles()).toList();

        for(String filePath : expectedFilePaths) {
            assertTrue(files.contains(new File(filePath)));
            System.out.println(filePath + " generated as expected.");

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
    void buildBOMStoreTypeTest() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        for(SBOMGenerator generator : generators) {
            BOMStore bomStore = generator.buildBOMStore();
            System.out.println("BOMStore Type: " + bomStore.getClass().getName());
            System.out.println("Generator Schema Type: " + generator.getSchema().getBomStoreType().getName());
            assertEquals(bomStore.getClass(), generator.getSchema().getBomStoreType());
        }
    }

    @Test
    @DisplayName("Correct BOMStore Tool")
    void buildBOMStoreToolTest() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        SBOMGenerator generator = generators.get(0);
        BOMStore bomStore = generator.buildBOMStore();
        List<Tool> bomStoreTools = bomStore.getTools();
        assertTrue(bomStoreTools.contains(generator.getTool()));
    }

    @Test
    @DisplayName("BOMStore Contains ALL SBOM Components")
    void buildBOMStoreComponentTest() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        for(SBOMGenerator generator : generators) {
            BOMStore bomStore = generator.buildBOMStore();
            Set<String> bomStoreComponentNames = bomStore.getAllComponents().stream().map(ParserComponent::getName).collect(Collectors.toSet());
            for(Component sbomComponent : internalSBOM.getAllComponents()) {
                if(sbomComponent.getUUID().equals(internalSBOM.getHeadUUID())) continue;
                String expectedName = sbomComponent.getName();
                System.out.println("BOMStore expected to contain component " + expectedName);
                assertTrue(bomStoreComponentNames.contains(expectedName));
                System.out.printf("    BOMStore contains component %s\n", expectedName);
            }
        }
    }
}
