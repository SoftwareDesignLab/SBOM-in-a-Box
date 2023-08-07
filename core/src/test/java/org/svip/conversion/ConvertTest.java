package org.svip.conversion;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;

import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.CDX14JSONDeserializer;
import org.svip.serializers.deserializer.Deserializer;
import org.svip.serializers.deserializer.SPDX23TagValueDeserializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Name: ConvertTest.java
 * Description: Test class for Convert functionality
 *
 * @author Juan Francisco Patino
 * @author Tyler Drake
 */
public class ConvertTest {

    /**
     * Test Constants
     */

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

    /** Helper Functions **/

    public static Deserializer getCDXJSONDeserializer() {
        return new CDX14JSONDeserializer();
    }

    public static Deserializer getSPDXTagValueDeserializer() {
        return new SPDX23TagValueDeserializer();
    }

    /** Setup **/
    @BeforeAll
    static void setupTestSboms() throws IOException {
        sboms[0] = getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_1)));
        sboms[1] = getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_2)));
        sboms[2] = getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_3)));
        sboms[3] = getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_4)));
        sboms[4] = getSPDXTagValueDeserializer().readFromString(Files.readString(Path.of(SBOM_5)));
        sboms[5] = getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_6)));
    }

    /** Tests **/

    @Test
    public void convertCDXSVIP() throws Exception {

        // Get the source SBOM
        SBOM original = sboms[0];

        // Create new conversion
        Conversion conversion = new Conversion();

        // Convert the SPDX SBOM to an SVIP SBOM using Conversion
        SBOM result = conversion.convertSBOM(original, SerializerFactory.Schema.SVIP, SerializerFactory.Schema.CDX14);

        // Check if result shows proper data
        assertNotNull(result);
        assertEquals("SVIP", result.getFormat());
        assertEquals(original.getName(), result.getName());
        assertEquals(original.getComponents().size(), result.getComponents().size());

    }

    @Test
    public void convertSPDXSVIP() throws Exception {

        // Get the source SBOM
        SBOM original = sboms[4];

        // Create new conversion
        Conversion conversion = new Conversion();

        // Convert the SPDX SBOM to an SVIP SBOM using Conversion
        SBOM result = conversion.convertSBOM(original, SerializerFactory.Schema.SVIP, SerializerFactory.Schema.SPDX23);

        // Check it result shows proper data
        assertEquals("SVIP", result.getFormat());
        assertEquals(original.getName(), result.getName());
        assertEquals(original.getComponents().size(), result.getComponents().size());

    }

    @Test
    public void convertCDXSPDX() throws Exception {

        // Get the source SBOM
        SBOM original = sboms[0];

        // Create new conversion
        Conversion conversion = new Conversion();

        // Convert the CDX SBOM to an SPDX SBOM using Conversion
        SBOM result = conversion.convertSBOM(original, SerializerFactory.Schema.SPDX23, SerializerFactory.Schema.CDX14);

        // Check if result shows proper data
        assertNotNull(result);
        assertEquals("SPDX", result.getFormat());
        assertEquals(original.getName(), result.getName());
        assertEquals(original.getComponents().size(), result.getComponents().size());

    }

    @Test
    public void convertSPDXCDX() throws Exception {

        // Set the source SBOM
        SBOM original = sboms[4];

        // Create new conversion
        Conversion conversion = new Conversion();

        // Convert the SPDX SBOM to a CycloneDX SBOM using Conversion
        SBOM result = conversion.convertSBOM(original, SerializerFactory.Schema.CDX14, SerializerFactory.Schema.SPDX23);

        // Check if result shows proper data
        assertNotNull(result);
        assertEquals("CycloneDX", result.getFormat());
        assertEquals(original.getName(), result.getName());
        assertEquals(original.getComponents().size(), result.getComponents().size());

    }

    @Test
    public void convertAllTest() throws Exception {

        // For every SBOM in the SBOM list
        for (SBOM sbom : sboms
        ) {
            // For every Schema in the Schema list
            for (SerializerFactory.Schema schema : schemas
            ) {

                // Get the original Schema for the SBOM
                SerializerFactory.Schema originalSchema = (sbom instanceof CDX14SBOM) ? SerializerFactory.Schema.CDX14 : SerializerFactory.Schema.SPDX23;

                if (originalSchema == schema)
                    continue;

                // Create new conversion
                Conversion conversion = new Conversion();

                // Convert the SBOM to a different SBOM schema using Conversion
                SBOM result = conversion.convertSBOM(sbom, schema, originalSchema);

                assertNotNull(result);

                assertEquals(sbom.getComponents().size(), result.getComponents().size());

            }
        }
    }
}
