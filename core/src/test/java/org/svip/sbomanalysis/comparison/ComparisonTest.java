package org.svip.sbomanalysis.comparison;

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
    protected static final String CDX_14_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/cdx_json/sbom.test.json";

    protected static final String SPDX23_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/spdx_json/sbom_corrupted.test.json";

    protected static final String SPDX23_TAGVALUE_SBOM = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomfactory/serializers/sample_boms/spdx_tagvalue/sbom.test.spdx";
    @Test
    public void compareSBOMs() throws IOException {
        Debug.logBlockTitle("Diff Report");
        SPDX23SBOM spdx23json = (SPDX23SBOM) getSPDXJSONDeserializer().readFromString(Files.readString(Path.of(getSPDXJSONTestFilePath())));
        CDX14SBOM cdx14json = (CDX14SBOM) getCDXJSONDeserializer().readFromString(Files.readString(Path.of(getCDXJSONTestFilePath())));
        SPDX23SBOM spdx23tag = (SPDX23SBOM) getSPDXTagValueDeserializer().readFromString(Files.readString(Path.of(getSpdx23TagValueTestFilePath())));
        SBOM[] sboms = new SBOM[3];
        sboms[0] = spdx23json;
        sboms[1] = cdx14json;
        sboms[2] = spdx23tag;
        APIController apiController = new APIController();
        DiffReport diffReport = apiController.compare(0, sboms);
        // TODO figure out how to actually test the diff reports
        Debug.log(Debug.LOG_TYPE.SUMMARY, "Test Diff Report" + "\n" + diffReport);
        Debug.logBlock();
    }

    public Deserializer getSPDXJSONDeserializer() {
        return new SPDX23JSONDeserializer();
    }
    public String getSPDXJSONTestFilePath() {
        return SPDX23_JSON_SBOM;
    }
    public Deserializer getCDXJSONDeserializer() {
        return new CDX14JSONDeserializer();
    }
    public String getCDXJSONTestFilePath() {
        return CDX_14_JSON_SBOM;
    }
    public Deserializer getSPDXTagValueDeserializer() {
        return new SPDX23TagValueDeserializer();
    }
    public String getSpdx23TagValueTestFilePath() {
        return SPDX23_TAGVALUE_SBOM;
    }
}
