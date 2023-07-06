package org.svip.sbomfactory.serializers.serializer;

import org.junit.jupiter.api.BeforeEach;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SerializerTest {
    private final Serializer serializer;

    private final SVIPSBOM testSBOM;

    public SerializerTest(Serializer serializer) {
        this.serializer = serializer;
        this.testSBOM = buildTestSBOM();
    }

    private SVIPSBOM buildTestSBOM() {
        // TODO replace with SBOM builders (not in dev branch at this time)
        SVIPComponentObject testComponent = new SVIPComponentObject(
                "library",
                "Test Component UID",
                "SVIP",
                "Test Component",
                new LicenseCollection(),
                "NONE",
                new HashMap<>(),
                new Organization("SVIP", "svip.xyz"),
                "1.0.0",
                new Description("Description"),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                "svip.xyz",
                "svip.java",
                true,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                null,
                "",
                "",
                "");

        return new SVIPSBOM(
                "CycloneDX",
                "Test SBOM",
                "Test UID",
                "1.0.0",
                "1.4",
                new HashSet<>(),
                new CreationData(),
                null,
                testComponent,
                Set.of(testComponent),
                null,
                new HashSet<>(),
                "NONE");
    }

    @BeforeEach
    public void setup() {
        serializer.setPrettyPrinting(true);
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public SVIPSBOM getTestSBOM() {
        return testSBOM;
    }
}
