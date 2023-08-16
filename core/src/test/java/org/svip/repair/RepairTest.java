package org.svip.repair;

import org.junit.jupiter.api.Test;
import org.svip.repair.statements.RepairStatement;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.serializers.deserializer.SPDX23JSONDeserializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

// something to run while developing the repair pipeline // todo delete comment
public class RepairTest {

    private final String CDX_14_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/resources/serializers/cdx_json/sbom.alpine.json";

    private final String SPDX23_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/resources/serializers/spdx_json/syft-0.80.0-source-spdx-json.json";
    private final RepairController r = new RepairController();

    @Test
    public void repairTest() throws IOException {

        SPDX23JSONDeserializer spdx23JSONDeserializer = new SPDX23JSONDeserializer();
        SPDX23SBOM sbom = spdx23JSONDeserializer.readFromString(Files.readString(Path.of(SPDX23_JSON_SBOM)));
        Map<String, Map<String, String>> statement = r.generateStatement(sbom, sbom.getUID());


    }



}
