package org.svip.sbomfactory.generators.generators.cyclonedx;

import org.cyclonedx.exception.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.generators.SBOMGenerator;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbomfactory.generators.utils.generators.GeneratorException;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;
import org.svip.sbomfactory.translators.TranslatorCDXXML;
import org.svip.sbomfactory.translators.TranslatorException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CycloneDXXMLSerializerTest {
    private final static Path OUT_PATH = Path.of("src/test/java/org/svip/sbomfactory/generators/SBOMOut/");
    TranslatorCDXXML xmlTranslator;
    SBOM testSBOM;
    SBOMGenerator generator;

    protected CycloneDXXMLSerializerTest() {
        xmlTranslator = new TranslatorCDXXML();
        testSBOM = new SBOM();
        testSBOM.addComponent(null, new ParserComponent("testcomponent"));
//        SBOMGeneratorTest.addTestComponentsToSBOM(testSBOM); // TODO this currently breaks translator
        generator = new SBOMGenerator(testSBOM, GeneratorSchema.CycloneDX);
    }

    @Test
    @DisplayName("CDX XML Serializer Output Test")
    void serializeTest() throws GeneratorException, IOException, ParseException, ParserConfigurationException, TranslatorException {
        OUT_PATH.toFile().mkdir();

        Debug.log(Debug.LOG_TYPE.INFO, String.format("generator.writeFile(\"%s\", GeneratorSchema.GeneratorFormat.XML)",
                OUT_PATH.toAbsolutePath()));
        // TODO if this test works, ensure string output is the same as file output and we should be good
        generator.writeFile(OUT_PATH.toAbsolutePath().toString(), GeneratorSchema.GeneratorFormat.XML);

        String filePath = generator.generatePathToSBOM(OUT_PATH.toAbsolutePath().toString(),
                GeneratorSchema.GeneratorFormat.XML);
        Debug.log(Debug.LOG_TYPE.INFO, String.format("xmlTranslator.translate(\"%s\");", filePath));
        SBOM outSBOM = xmlTranslator.translate(filePath);

        assertEquals(generator.getInternalSBOM(), outSBOM); // Compare against internal, updated generator SBOM
        Debug.log(Debug.LOG_TYPE.SUMMARY, "SBOM from generator == Translated SBOM from file output");
    }
}
