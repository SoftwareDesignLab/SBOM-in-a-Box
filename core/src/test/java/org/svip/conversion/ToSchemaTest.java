/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
 */

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
public class ToSchemaTest {

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

    /**
     * Helper Functions
     **/

    public static Deserializer getCDXJSONDeserializer() {
        return new CDX14JSONDeserializer();
    }

    public static Deserializer getSPDXTagValueDeserializer() {
        return new SPDX23TagValueDeserializer();
    }

    /**
     * Setup
     **/
    @BeforeAll
    static void setupTestSboms() throws IOException {
        sboms[0] = getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_1)));
        sboms[1] = getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_2)));
        sboms[2] = getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_3)));
        sboms[3] = getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_4)));
        sboms[4] = getSPDXTagValueDeserializer().readFromString(Files.readString(Path.of(SBOM_5)));
        sboms[5] = getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_6)));
    }

    /**
     * Tests
     **/

    @Test
    public void convertCDXSVIP() throws ConversionException {

        // Get the source SBOM
        SBOM original = sboms[0];

        // Create new conversion
        Conversion conversion = new Conversion();

        // Convert the SPDX SBOM to an SVIP SBOM using Conversion
        SBOM result = conversion.convertFull(original, SerializerFactory.Schema.CDX14, SerializerFactory.Schema.SVIP);

        // Check if result shows proper data
        assertNotNull(result);
        assertEquals("SVIP", result.getFormat());
        assertEquals(original.getName(), result.getName());
        assertEquals("1.0-a", result.getSpecVersion());
        assertEquals(original.getComponents().size(), result.getComponents().size());

    }

    @Test
    public void convertSPDXSVIP() throws ConversionException {

        // Get the source SBOM
        SBOM original = sboms[4];

        // Create new conversion
        Conversion conversion = new Conversion();

        // Convert the SPDX SBOM to an SVIP SBOM using Conversion
        SBOM result = conversion.convertFull(original, SerializerFactory.Schema.SPDX23, SerializerFactory.Schema.SVIP);

        // Check it result shows proper data
        assertEquals("SVIP", result.getFormat());
        assertEquals(original.getName(), result.getName());
        assertEquals("1.0-a", result.getSpecVersion());
        assertEquals(original.getComponents().size(), result.getComponents().size());

    }

    @Test
    public void convertCDXSPDX() throws ConversionException {

        // Get the source SBOM
        SBOM original = sboms[0];

        // Create new conversion
        Conversion conversion = new Conversion();

        // Convert the CDX SBOM to an SPDX SBOM using Conversion
        SBOM result = conversion.convertFull(original, SerializerFactory.Schema.CDX14, SerializerFactory.Schema.SPDX23);

        // Check if result shows proper data
        assertNotNull(result);
        assertEquals("SPDX", result.getFormat());
        assertEquals(original.getName(), result.getName());
        assertEquals("2.3", result.getSpecVersion());
        assertEquals(original.getComponents().size(), result.getComponents().size());

    }

    @Test
    public void convertSPDXCDX() throws ConversionException {

        // Set the source SBOM
        SBOM original = sboms[4];

        // Create new conversion
        Conversion conversion = new Conversion();

        // Convert the SPDX SBOM to a CycloneDX SBOM using Conversion
        SBOM result = conversion.convertFull(original, SerializerFactory.Schema.SPDX23, SerializerFactory.Schema.CDX14);

        // Check if result shows proper data
        assertNotNull(result);
        assertEquals("CycloneDX", result.getFormat());
        assertEquals(original.getName(), result.getName());
        assertEquals(original.getComponents().size(), result.getComponents().size());

    }

    @Test
    public void convertAllTest() throws ConversionException {

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
                SBOM result = conversion.convertFull(sbom, originalSchema, schema);

                assertNotNull(result);

                assertEquals(sbom.getComponents().size(), result.getComponents().size());

            }
        }
    }
}
