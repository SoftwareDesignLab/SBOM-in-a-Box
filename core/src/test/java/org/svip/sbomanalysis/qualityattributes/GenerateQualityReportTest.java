package org.svip.sbomanalysis.qualityattributes;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbomanalysis.qualityattributes.pipelines.QualityReport;
import org.svip.sbomanalysis.qualityattributes.pipelines.schemas.CycloneDX14.CDX14Pipeline;
import org.svip.sbomanalysis.qualityattributes.pipelines.schemas.SPDX23.SPDX23Pipeline;
import org.svip.sbomgeneration.serializers.deserializer.CDX14JSONDeserializer;
import org.svip.sbomgeneration.serializers.deserializer.SPDX23JSONDeserializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * file: GenerateQualityReportTest.java
 * Test class to test generating a quality report through the pipeline classes
 *
 * @author Matthew Morrison
 */
public class GenerateQualityReportTest {

    private final String CDX_14_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomgeneration/serializers/sample_boms/cdx_json/sbom.alpine.json";

    private final String SPDX23_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/java/org/svip/sbomgeneration/serializers/sample_boms/syft-0.80.0-source-spdx-json.json";

    @Test
    public void test_quality_report_cdx_pipeline_test() throws IOException {
        CDX14Pipeline cdx14Pipeline = new CDX14Pipeline();

        CDX14JSONDeserializer cdx14Deserializer = new CDX14JSONDeserializer();
        CDX14SBOM sbom = cdx14Deserializer.readFromString(Files.readString(Path.of(CDX_14_JSON_SBOM)));

        QualityReport qualityReport = cdx14Pipeline.process(sbom.getUID(), sbom);
    }

    @Test
    public void test_quality_report_spdx_json_pipeline_test() throws IOException {
        SPDX23Pipeline spdx23Pipeline = new SPDX23Pipeline();

        SPDX23JSONDeserializer spdx23JSONDeserializer = new SPDX23JSONDeserializer();
        SPDX23SBOM sbom = spdx23JSONDeserializer.readFromString(Files.readString(Path.of(CDX_14_JSON_SBOM)));

        QualityReport qualityReport = spdx23Pipeline.process(sbom.getUID(), sbom);
    }

}
