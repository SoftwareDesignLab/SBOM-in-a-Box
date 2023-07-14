package org.svip.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.svip.api.model.SBOMFile;
import org.svip.api.repository.SBOMFileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class APITest {

    SVIPApiController controller;

    @Mock
    SBOMFileRepository repository;

    private final static String alpineSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/sbom.alpine-compare.2-3.spdx";
    private final static String pythonSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/sbom.python.2-3.spdx";
    private final static String dockerSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/sbom.docker.2-2.spdx";
    private final static String alpineSBOMCdx = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/sbom.alpine.xml";
    private final static String jakeSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/jake_source_cdx.xml";
    private final static String condaSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/cyclonedx_source_cdx.xml";
    private final static String dotnetSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/cyclonedxfordotnetprojects_source_cdx.json";
    private final static String goSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/cdx-gomod-1.4.0-bin.json";
    private final static String gradleSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/CDXGradlePlugin_deployed_cdx.json";
    private final static String syftSPDX23JSON = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/syft-0.80.0-source-spdx-json.json";
    @BeforeEach
    public void setup() {
        // Init controller with mocked repository
        controller = new SVIPApiController(repository);
    }

    public static Map<Long, SBOMFile> getTestFileMap() throws IOException {
        final List<String> contentsArray = new ArrayList<>();
        final List<String> fileNamesArray = new ArrayList<>();

        contentsArray.add(new String(Files.readAllBytes(Paths.get(alpineSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(pythonSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(dockerSBOM))));

        contentsArray.add(new String(Files.readAllBytes(Paths.get(alpineSBOMCdx))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(jakeSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(condaSBOM))));

        contentsArray.add(new String(Files.readAllBytes(Paths.get(dotnetSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(goSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(gradleSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(syftSPDX23JSON))));

        fileNamesArray.add(alpineSBOM);
        fileNamesArray.add(pythonSBOM);
        fileNamesArray.add(dockerSBOM);

        fileNamesArray.add(alpineSBOMCdx);
        fileNamesArray.add(jakeSBOM);
        fileNamesArray.add(condaSBOM);

        fileNamesArray.add(dotnetSBOM);
        fileNamesArray.add(goSBOM);
        fileNamesArray.add(gradleSBOM);
        fileNamesArray.add(syftSPDX23JSON);

        final Map<Long, SBOMFile> resultMap = new HashMap<>();
        for (int i = 0; i < contentsArray.size(); i++) {
            resultMap.put((long) i, new SBOMFile(fileNamesArray.get(i), contentsArray.get(i)));
            resultMap.get((long) i).setId(i); // Set ID for testing purposes
        }

        return resultMap;
    }
}
