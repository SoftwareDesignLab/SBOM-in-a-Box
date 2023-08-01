package org.svip.sbomanalysis.differ;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbomanalysis.comparison.DiffReport;
import org.svip.sbomgeneration.serializers.deserializer.CDX14JSONDeserializer;
import org.svip.sbomgeneration.serializers.deserializer.Deserializer;
import org.svip.sbomgeneration.serializers.deserializer.SPDX23JSONDeserializer;
import org.svip.sbomgeneration.serializers.deserializer.SPDX23TagValueDeserializer;
import org.svip.utils.Debug;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiffReportTest {
    protected static final String SBOM_1 = System.getProperty("user.dir") +
            "/src/test/resources/differ/Test1_CDX.json";

    protected static final String SBOM_2 = System.getProperty("user.dir") +
            "/src/test/resources/differ/Test2_SPDX.json";

    @Test
    public void testDiffReport() throws IOException {
        CDX14SBOM sbom1 = (CDX14SBOM) getCDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_1)));
        SPDX23SBOM sbom2 = (SPDX23SBOM) getSPDXJSONDeserializer().readFromString(Files.readString(Path.of(SBOM_2)));
        SBOM[] sboms = new SBOM[2];
        sboms[0] = sbom1;
        sboms[1] = sbom2;
        DiffReport diffReport = new DiffReport(sbom1.getUID(), sbom1);
        diffReport.compare(sbom2.getUID(), sbom2);
        // TODO figure out how to actually test the diff reports
        Debug.logBlockTitle("Diff Report");
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(diffReport));
        assertEquals("{\"target\":\"Test\",\"diffReport\":{\"Test\":{\"componentConflicts\":{\"COMPONENT 2\":[],\"COMPONENT 1\":[],\"metadata\":[],\"COMPONENT 3\":[]},\"missingComponents\":[\"COMPONENT 4\"]}}}", objectMapper.writeValueAsString(diffReport));
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