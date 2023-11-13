package org.svip.repair;

import org.junit.jupiter.api.Test;
import org.svip.repair.fix.Fix;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.serializers.deserializer.CDX14JSONDeserializer;
import org.svip.serializers.deserializer.SPDX23JSONDeserializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

// something to run while developing the repair pipeline // todo delete comment
public class RepairTest {

    private final String CDX_14_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/resources/serializers/cdx_json/sbom.alpine.json";

    private final String SPDX23_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/resources/serializers/spdx_json/syft-0.80.0-source-spdx-json.json";
    private final RepairController r = new RepairController();
    private final SPDX23JSONDeserializer spdx23JSONDeserializer = new SPDX23JSONDeserializer();
    private final CDX14JSONDeserializer cdx14JSONDeserializer = new CDX14JSONDeserializer();

    @Test
    public void SPDXRepairTest() throws Exception {
        SPDX23SBOM sbom = spdx23JSONDeserializer.readFromString(Files.readString(Path.of(SPDX23_JSON_SBOM)));
        Map<String, Map<String, List<Fix<?>>>> statement = r.generateStatement(sbom, sbom.getUID());
    }

    @Test
    public void CDXRepairTest() throws Exception {
        CDX14SBOM sbom = cdx14JSONDeserializer.readFromString(Files.readString(Path.of(CDX_14_JSON_SBOM)));
        Map<String, Map<String, List<Fix<?>>>> statement = r.generateStatement(sbom, sbom.getUID());
    }

    @Test
    public void CDXNullRepairsTest() throws Exception {
        CDX14SBOM sbom = cdx14JSONDeserializer.readFromString(Files.readString(Path.of(CDX_14_JSON_SBOM)));
        CDX14SBOM repairedSBOM = (CDX14SBOM) r.repairSBOM(sbom, null);
        assertEquals(true, sbom.equals(repairedSBOM));
    }

    @Test
    public void CDXEmptyRepairsTest() throws Exception {
        CDX14SBOM sbom = cdx14JSONDeserializer.readFromString(Files.readString(Path.of(CDX_14_JSON_SBOM)));
        Map<String, Map<String, List<Fix<?>>>>  fixes = new HashMap<String, Map<String, List<Fix<?>>>>();
        CDX14SBOM repairedSBOM = (CDX14SBOM) r.repairSBOM(sbom, fixes);
        assertEquals(0, sbom.compare(repairedSBOM).size());
    }
}
