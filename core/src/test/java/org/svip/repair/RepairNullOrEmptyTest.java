package org.svip.repair;

import org.junit.jupiter.api.Test;
import org.svip.repair.fix.Fix;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.serializers.deserializer.SPDX23JSONDeserializer;
import org.svip.serializers.deserializer.SPDX23TagValueDeserializer;
import org.svip.utils.Debug;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RepairNullOrEmptyTest {
    private final String NULL_COPYRIGHT_SBOM = System.getProperty("user.dir") +
            "/src/test/resources/repair/null-copyright.spdx";

    private final String MICROSOFT_COPYRIGHT_FIX = "© Microsoft Corporation. All rights reserved.";

    private final String PACKAGE_NAME = "System.Text.Json";

    private final RepairController r = new RepairController();

    @Test
    public void NullCopyright() throws Exception {
        SPDX23TagValueDeserializer spdx23JSONDeserializer = new SPDX23TagValueDeserializer();
        SPDX23SBOM sbom = spdx23JSONDeserializer.readFromString(Files.readString(Path.of(NULL_COPYRIGHT_SBOM)));
        Map<String, Map<String, List<Fix<?>>>> statement = r.generateStatement(sbom, sbom.getUID());
        assertEquals(MICROSOFT_COPYRIGHT_FIX, statement.get(PACKAGE_NAME).get(PACKAGE_NAME).get(0).fixed());
    }
}
