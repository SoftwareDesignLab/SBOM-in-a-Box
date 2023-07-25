package org.svip.sbomanalysis.comparison;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbomgeneration.serializers.deserializer.CDX14JSONDeserializer;
import org.svip.sbomgeneration.serializers.deserializer.Deserializer;
import org.svip.sbomgeneration.serializers.deserializer.SPDX23JSONDeserializer;
import org.svip.sbomgeneration.serializers.deserializer.SPDX23TagValueDeserializer;
import org.svip.utils.Debug;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class ComparisonTest {
    private static final String TEST_DIR = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomgeneration/serializers/sample_boms/Benchmark_SBOM_Megacollection/";

    protected static final String SBOM_1 = TEST_DIR + "cdxgen.json";

    protected static final String SBOM_2 = TEST_DIR + "cyclonedxMavenPlugin.json";

    protected static final String SBOM_3 = TEST_DIR + "jbom-18172.json";

    protected static final String SBOM_4 = TEST_DIR + "jbom-Case1-1.0-SNAPSHOT-jar-with-dependencies.json";

    protected static final String SBOM_5 = TEST_DIR + "spdx-sbom-generator.spdx";

    protected static final String SBOM_6 = TEST_DIR + "syft.json";

    @Test
    public void compareSBOMs() throws IOException {
        CDX14SBOM sbom1 = (CDX14SBOM) getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_1)));
        CDX14SBOM sbom2 = (CDX14SBOM) getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_2)));
        CDX14SBOM sbom3 = (CDX14SBOM) getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_3)));
        CDX14SBOM sbom4 = (CDX14SBOM) getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_4)));
        SPDX23SBOM sbom5 = (SPDX23SBOM) getSPDXTagValueDeserializer().readFromString(Files.readString(Path.of(SBOM_5)));
        CDX14SBOM sbom6 = (CDX14SBOM) getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_6)));
        SBOM[] sboms = new SBOM[6];
        sboms[0] = sbom1;
        sboms[1] = sbom2;
        sboms[2] = sbom3;
        sboms[3] = sbom4;
        sboms[4] = sbom5;
        sboms[5] = sbom6;
        DiffReport diffReport = new DiffReport(sbom4.getUID(), sbom1);
        // TODO figure out how to actually test the diff reports
        Debug.logBlockTitle("Diff Report");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        String diffReportString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(diffReport);
        Debug.log(Debug.LOG_TYPE.SUMMARY, "Test Diff Report" + "\n" + diffReportString);
        Debug.logBlock();
}

    public Deserializer getSPDXJSONDeserializer() {
        return new SPDX23JSONDeserializer();
    }
    public Deserializer getCDXJSONDeserializer() {
        return new CDX14JSONDeserializer();
    }
    public Deserializer getSPDXTagValueDeserializer() {
        return new SPDX23TagValueDeserializer();
    }
}
