package org.svip.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.svip.api.model.SBOMFile;
import org.svip.api.repository.SBOMFileRepository;
import org.svip.api.utils.Utils;
import org.svip.serializers.SerializerFactory;

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
            + "/src/test/resources/sample_sboms/sbom.alpine-compare.2-3.spdx";
    private final static String pythonSBOM = System.getProperty("user.dir")
            + "/src/test/resources/sample_sboms/sbom.python.2-3.spdx";
    private final static String dockerSBOM = System.getProperty("user.dir")
            + "/src/test/resources/sample_sboms/sbom.docker.2-2.spdx";
    private final static String alpineSBOMCdx = System.getProperty("user.dir")
            + "/src/test/resources/sample_sboms/sbom.alpine.xml";
    private final static String jakeSBOM = System.getProperty("user.dir")
            + "/src/test/resources/sample_sboms/jake_source_cdx.xml";
    private final static String condaSBOM = System.getProperty("user.dir")
            + "/src/test/resources/sample_sboms/cyclonedx_source_cdx.xml";
    private final static String dotnetSBOM = System.getProperty("user.dir")
            + "/src/test/resources/sample_sboms/cyclonedxfordotnetprojects_source_cdx.json";
    private final static String goSBOM = System.getProperty("user.dir")
            + "/src/test/resources/sample_sboms/cdx-gomod-1.4.0-bin.json";
    private final static String gradleSBOM = System.getProperty("user.dir")
            + "/src/test/resources/sample_sboms/CDXGradlePlugin_deployed_cdx.json";
    private final static String syftSPDX23JSON = System.getProperty("user.dir")
            + "/src/test/resources/sample_sboms/syft-0.80.0-source-spdx-json.json";

    private final static String vulnerableSBOMNVD = System.getProperty("user.dir")
            + "//src/test/resources/sample_sboms/CDX_vulnerabilities_sbom.json";

    private final static String vulnerableSBOMOSV = System.getProperty("user.dir")
            + "/src/test/resources/sample_sboms/CDX_vulns_osv_sbom.json";

    protected final SerializerFactory.Schema[] schemas = {SerializerFactory.Schema.SPDX23, SerializerFactory.Schema.CDX14};
    protected final SerializerFactory.Format[] formats = {SerializerFactory.Format.TAGVALUE, SerializerFactory.Format.JSON};

    /*
    Sample projects for parsers
     */

    public static class SampleProject {
        private final String dir = System.getProperty("user.dir") + "/src/test/resources/sample_projects/";
        public String type;
        public String[] sourceFileNames;
        private boolean gotten = false;

        public SampleProject(String type, String[] sourceFileNames) {
            this.type = type;
            this.sourceFileNames = sourceFileNames;
        }

        public String[] getProjectFiles() {
            if (!gotten)
                for (int i = 0; i < sourceFileNames.length; i++)
                    sourceFileNames[i] = dir + type + "/" + sourceFileNames[i];
            gotten = true;

            return sourceFileNames;
        }
    }

    private static final SampleProject java = new SampleProject("Java",
            new String[]{"lib/Foo.java", "Bar.java", "build.gradle", "pom.xml"});

    private static final SampleProject foobarCSharp =
            new SampleProject("CSharp/Bar", new String[]{"foobar.cs", "sample.csproj"});

    private static final SampleProject nugetCSharp =
            new SampleProject("CSharp/Nuget", new String[]{"ErrLog.IO.nuspec", "Logger.cs",
                    "WithFrameworkAssembliesAndDependencies.nuspec", "nuspec.xsd"});
    private static final SampleProject empty =
            new SampleProject("Empty", new String[]{"empty.cs"});
    private static final SampleProject subProcess =
            new SampleProject("Subprocess", new String[]{"subprocess.py"});
    private static final SampleProject ruby =
            new SampleProject("Ruby", new String[]{"foo.rb"});
    private static final SampleProject JS =
            new SampleProject("JS", new String[]{"lib/bar.js", "lib/foo.js", "index.js"});

    @BeforeEach
    public void setup() throws IOException {
        // Init controller with mocked repository
        // Avoid starting with OSI to decrease test time
        controller = new SVIPApiController(repository, false);
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
        contentsArray.add(new String(Files.readAllBytes(Paths.get(vulnerableSBOMNVD))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(vulnerableSBOMOSV))));

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
        fileNamesArray.add(vulnerableSBOMNVD);
        fileNamesArray.add(vulnerableSBOMOSV);

        final Map<Long, SBOMFile> resultMap = new HashMap<>();
        for (int i = 0; i < contentsArray.size(); i++) {
            resultMap.put((long) i, new SBOMFile(fileNamesArray.get(i), contentsArray.get(i)));
            resultMap.get((long) i).setId(i); // Set ID for testing purposes
        }

        return resultMap;
    }

    public static Map<Map<Long, String>, SBOMFile[]> getTestProjectMap() throws IOException {

        ArrayList<SBOMFile[]> files = new ArrayList<>();
        ArrayList<String> projectNames = new ArrayList<>();

        // java
        SBOMFile[] sbomFiles = Utils.configureProjectTest(java.getProjectFiles());
        files.add(sbomFiles);
        projectNames.add(java.type);

        // foobar cs proj
        sbomFiles = Utils.configureProjectTest(foobarCSharp.getProjectFiles());
        files.add(sbomFiles);
        projectNames.add(foobarCSharp.type);

        // foobar cs proj
        sbomFiles = Utils.configureProjectTest(nugetCSharp.getProjectFiles());
        files.add(sbomFiles);
        projectNames.add(nugetCSharp.type);

        // empty cs proj
        sbomFiles = Utils.configureProjectTest(empty.getProjectFiles());
        files.add(sbomFiles);
        projectNames.add(empty.type);

        // python
        sbomFiles = Utils.configureProjectTest(subProcess.getProjectFiles());
        files.add(sbomFiles);
        projectNames.add(subProcess.type);

        // Ruby
        sbomFiles = Utils.configureProjectTest(ruby.getProjectFiles());
        files.add(sbomFiles);
        projectNames.add(ruby.type);

        // JS
        sbomFiles = Utils.configureProjectTest(JS.getProjectFiles());
        files.add(sbomFiles);
        projectNames.add(JS.type);

        final Map<Map<Long, String>, SBOMFile[]> resultMap = new HashMap<>();
        for (int i = 0; i < files.size(); i++) {

            HashMap<Long, String> idMap = new HashMap<>();
            long projectId = i * 10L; // project id
            idMap.put(projectId, projectNames.get(i));
            resultMap.put(idMap, files.get(i));

            int j = 0;
            for (SBOMFile s : resultMap.get(idMap)
            ) {
                s.setId(projectId + j); // Set ID for testing purposes
                j++;
            }
        }

        return resultMap;

    }

}
