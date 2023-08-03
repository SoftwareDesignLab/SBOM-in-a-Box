package org.svip.conversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.svip.compare.DiffReport;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.CDX14JSONDeserializer;
import org.svip.serializers.deserializer.Deserializer;
import org.svip.serializers.deserializer.SPDX23JSONDeserializer;
import org.svip.serializers.deserializer.SPDX23TagValueDeserializer;
import org.svip.utils.Debug;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
    protected static final SBOM[] sboms = new SBOM[6];

    @BeforeAll
    static void setupTestSboms() throws IOException {
        CDX14SBOM sbom1 = (CDX14SBOM) getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_1)));
        CDX14SBOM sbom2 = (CDX14SBOM) getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_2)));
        CDX14SBOM sbom3 = (CDX14SBOM) getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_3)));
        CDX14SBOM sbom4 = (CDX14SBOM) getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_4)));
        SPDX23SBOM sbom5 = (SPDX23SBOM) getSPDXTagValueDeserializer().readFromString(Files.readString(Path.of(SBOM_5)));
        CDX14SBOM sbom6 = (CDX14SBOM) getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_6)));
        sboms[0] = sbom1;
        sboms[1] = sbom2;
        sboms[2] = sbom3;
        sboms[3] = sbom4;
        sboms[4] = sbom5;
        sboms[5] = sbom6;
    }

    @Test
    public void convertCDXSVIP() throws Exception {

        assertNotNull(Conversion.convertSBOM(sboms[0], SerializerFactory.Schema.SVIP, SerializerFactory.Schema.CDX14));

    }

    @Test
    public void convertSPDXSVIP() throws Exception {

        assertNotNull(Conversion.convertSBOM(sboms[0], SerializerFactory.Schema.SVIP, SerializerFactory.Schema.SPDX23));

    }

    /*
        // todo
     */
    @Test
    public void convertSPDXCDX() throws Exception {


    }

    @Test
    public void convertCDXSPDX() throws Exception {


    }

    @Test
    public void convertTest(){
        for (SBOM s:  sboms
             ) {

            // todo for each schema, convert


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
