package org.svip.sbomanalysis.comparison;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbomanalysis.differ.APIController;
import org.svip.sbomanalysis.differ.DiffReport;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.serializers.deserializer.CDX14JSONDeserializer;
import org.svip.sbomfactory.serializers.deserializer.Deserializer;
import org.svip.sbomfactory.serializers.deserializer.SPDX23JSONDeserializer;
import org.svip.sbomfactory.serializers.deserializer.SPDX23TagValueDeserializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer.getDeserializer;
import static org.junit.jupiter.api.Assertions.assertEquals;



public class ComparisonTest {
    protected static final String SBOM_1 = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/Benchmark_SBOM_Megacollection/cdxgen.json";

    protected static final String SBOM_2 = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/Benchmark_SBOM_Megacollection/cyclonedxMavenPlugin.json";

    protected static final String SBOM_3 = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/Benchmark_SBOM_Megacollection/jbom-18172.json";

    protected static final String SBOM_4 = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/Benchmark_SBOM_Megacollection/jbom-Case1-1.0-SNAPSHOT-jar-with-dependencies.json";

    protected static final String SBOM_5 = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/Benchmark_SBOM_Megacollection/spdx-sbom-generator.spdx";

    protected static final String SBOM_6 = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/Benchmark_SBOM_Megacollection/syft.json";

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
        APIController apiController = new APIController();
        DiffReport diffReport = apiController.compare(0, sboms);
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
