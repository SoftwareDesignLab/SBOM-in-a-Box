package org.svip.conversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.svip.compare.DiffReport;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.CDX14JSONDeserializer;
import org.svip.serializers.deserializer.Deserializer;
import org.svip.serializers.deserializer.SPDX23JSONDeserializer;
import org.svip.serializers.deserializer.SPDX23TagValueDeserializer;
import org.svip.utils.Debug;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConvertTest {

    private static final String TEST_DIR = System.getProperty("user.dir") +
            "/src/test/resources/serializers/Benchmark_SBOM_Megacollection/";

    protected static final String SBOM_1 = TEST_DIR + "cdxgen.json";

    protected static final String SBOM_2 = TEST_DIR + "cyclonedxMavenPlugin.json";

    protected static final String SBOM_3 = TEST_DIR + "jbom-18172.json";

    protected static final String SBOM_4 = TEST_DIR + "jbom-Case1-1.0-SNAPSHOT-jar-with-dependencies.json";

    protected static final String SBOM_5 = TEST_DIR + "spdx-sbom-generator.spdx";

    protected static final String SBOM_6 = TEST_DIR + "syft.json";


    protected final SerializerFactory.Schema[] schemas = {SerializerFactory.Schema.SVIP,
            SerializerFactory.Schema.SPDX23, SerializerFactory.Schema.CDX14};

    protected static final SBOM[] sboms = new SBOM[6];

    @BeforeAll
    static void setupTestSboms() throws IOException {
        sboms[0] = (CDX14SBOM) getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_1)));
        sboms[1] = (CDX14SBOM) getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_2)));
        sboms[2] = (CDX14SBOM) getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_3)));
        sboms[3] = (CDX14SBOM) getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_4)));
        sboms[4] = (SPDX23SBOM) getSPDXTagValueDeserializer().readFromString(Files.readString(Path.of(SBOM_5)));
        sboms[5] = (CDX14SBOM) getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_6)));
    }

    @Test
    public void convertCDXSVIP() throws Exception {

        SBOM original = sboms[0];

        SBOM result = Conversion.convertSBOM(original, SerializerFactory.Schema.SVIP, SerializerFactory.Schema.CDX14);

        assertNotNull(result);
        assertEquals("SVIP", result.getFormat());
        assertEquals(original.getName(), result.getName());
        assertEquals(original.getComponents().size(), result.getComponents().size());

    }

    @Test
    public void convertSPDXSVIP() throws Exception {

        SBOM original = sboms[4];

        SBOM result = Conversion.convertSBOM(original, SerializerFactory.Schema.SVIP, SerializerFactory.Schema.SPDX23);

        assertEquals("SVIP", result.getFormat());
        assertEquals(original.getName(), result.getName());
        assertEquals(original.getComponents().size(), result.getComponents().size());

    }

    @Test
    public void convertCDXSPDX() throws Exception {

        SBOM original = sboms[0];

        SBOM result = Conversion.convertSBOM(original, SerializerFactory.Schema.SPDX23, SerializerFactory.Schema.CDX14);

        assertNotNull(result);
        assertEquals("SPDX", result.getFormat());
        assertEquals(original.getName(), result.getName());
        assertEquals(original.getComponents().size(), result.getComponents().size());

    }

    @Test
    public void convertSPDXCDX() throws Exception {

        SBOM original = sboms[4];

        SBOM result = Conversion.convertSBOM(original, SerializerFactory.Schema.CDX14, SerializerFactory.Schema.SPDX23);

        assertNotNull(result);
        assertEquals("CycloneDX", result.getFormat());
        assertEquals(original.getName(), result.getName());
        assertEquals(original.getComponents().size(), result.getComponents().size());

    }

    @Test
    public void convertTest() throws Exception {
        for (SBOM sbom : sboms
        ) {
            for (SerializerFactory.Schema schema : schemas
            ) {

                SerializerFactory.Schema originalSchema = (sbom instanceof CDX14SBOM) ? SerializerFactory.Schema.CDX14 : SerializerFactory.Schema.SPDX23;

                if (originalSchema == schema)
                    continue;

                assertNotNull(Conversion.convertSBOM(sbom, schema, originalSchema));

            }
        }
    }

    public Deserializer getSPDXJSONDeserializer() {
        return new SPDX23JSONDeserializer();
    }

    public static Deserializer getCDXJSONDeserializer() {
        return new CDX14JSONDeserializer();
    }

    public static Deserializer getSPDXTagValueDeserializer() {
        return new SPDX23TagValueDeserializer();
    }

}
