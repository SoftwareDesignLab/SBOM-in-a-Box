package org.svip.sbomfactory.generators.generators.spdx;

import org.cyclonedx.exception.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.generators.SBOMGenerator;
import org.svip.sbomfactory.generators.utils.generators.GeneratorException;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbomfactory.translators.TranslatorSPDX;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SPDXTagValueWriterTest {
    private final static Path OUT_PATH = Path.of("src/test/java/org/svip/sbomfactory/generators/SBOMOut/");
    TranslatorSPDX spdxTranslator;
    SBOM testSBOM;
    SBOMGenerator generator;

    protected SPDXTagValueWriterTest() {
        spdxTranslator = new TranslatorSPDX();
        testSBOM = new SBOM();
        // We need >1 component in this because .spdx does not include a head component
        testSBOM.addComponent(null, new ParserComponent("testcomponent"));
        testSBOM.addComponent(testSBOM.getHeadUUID(), new ParserComponent("testvisiblepackage"));
//        SBOMGeneratorTest.addTestComponentsToSBOM(testSBOM); // TODO this currently breaks translator
        generator = new SBOMGenerator(testSBOM, GeneratorSchema.SPDX);
    }

    @Test
    @DisplayName("SPDX Tag-Value Writer Output Test")
    void serializeTest() throws GeneratorException, IOException, ParseException, ParserConfigurationException {
        OUT_PATH.toFile().mkdir();

        Debug.log(Debug.LOG_TYPE.INFO, String.format("generator.writeFile(\"%s\", GeneratorSchema.GeneratorFormat.SPDX)",
                OUT_PATH.toAbsolutePath()));
        // TODO if this test works, ensure string output is the same as file output and we should be good
        generator.writeFile(OUT_PATH.toAbsolutePath().toString(), GeneratorSchema.GeneratorFormat.SPDX);

        String filePath = generator.generatePathToSBOM(OUT_PATH.toAbsolutePath().toString(),
                GeneratorSchema.GeneratorFormat.SPDX);
        Debug.log(Debug.LOG_TYPE.INFO, String.format("spdxTranslator.translate(\"%s\");", filePath));
        SBOM outSBOM = spdxTranslator.translate(filePath);

        assertEquals(generator.getInternalSBOM(), outSBOM); // Compare against internal, updated generator SBOM
        Debug.log(Debug.LOG_TYPE.SUMMARY, "SBOM from generator == Translated SBOM from file output");
    }
}
