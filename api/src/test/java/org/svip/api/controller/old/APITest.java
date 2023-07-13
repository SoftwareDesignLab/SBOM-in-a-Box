package org.svip.api.controller.old;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.utils.Debug;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A collection of constants and static methods used across multiple different API endpoint tests.
 *
 * @author Juan Francisco Patino
 * @author Ian Dunn
 */
public class APITest {
    /**
     * Example file contents to use for input validation
     */

    protected final static String TESTCONTENTSARRAY_LENGTH1 = "[\"Example File Contents\"]";
    protected final static String TESTFILEARRAY_LENGTH1 = "[\"TestFileName1.java\"]";
    protected final static String TESTCONTENTSARRAY_LENGTH2 = "[\"Example File Contents 1\", \"Example File Contents 2\"]";
    protected final static String TESTFILEARRAY_LENGTH2 = "[\"src/java/SBOM/sbom2/TestFileName1.java\", \"src/java/SBOM/TestFileName2.java\"]";
    protected final static String CDX_SCHEMA = "CycloneDX";
    protected final static String INVALID_SCHEMA = "Invalid Test Schema";
    protected final static String JSON_FORMAT = "JSON";
    protected final static String INVALID_FORMAT = "GIF";

    /**
     * Example SBOMs to use for testing
     */

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

    /**
     * All generic configuration goes in here
     */
    public APITest() {
        Debug.enableSummary();
    }

    public static Map<String, String> testFileMap() throws IOException {
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

        fileNamesArray.add(alpineSBOM);
        fileNamesArray.add(pythonSBOM);
        fileNamesArray.add(dockerSBOM);

        fileNamesArray.add(alpineSBOMCdx);
        fileNamesArray.add(jakeSBOM);
        fileNamesArray.add(condaSBOM);

        fileNamesArray.add(dotnetSBOM);
        fileNamesArray.add(goSBOM);
        fileNamesArray.add(gradleSBOM);

        final Map<String, String> resultMap = new HashMap<>();
        for (int i = 0; i < contentsArray.size(); i++) {
            resultMap.put(fileNamesArray.get(i), contentsArray.get(i));
        }

        return resultMap;
    }

    /**
     * Constructs a String array with three elements:
     * <ul>
     *     <li>A JSON array of SBOM contents as a string</li>
     *     <li>A JSON array of corresponding SBOM filenames as a string</li>
     *     <li>Total number of test input SBOMs</li>
     *
     *     <li>A {@code List<String>} of all testable schemas.</li>
     *     <li>A {@code List<String>} of all testable formats.</li>
     * </ul>
     * Each element index in the schema/format lists corresponds to a valid schema/format pair (i.e. schemas[i] ==
     * CycloneDX and formats[i] == JSON, etc.)
     *
     * @throws IOException If the SBOM merging is broken
     */
    public static String[] testInput() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final List<String> schemaArray = new ArrayList<>();
        final List<String> formatArray = new ArrayList<>();
        final Map<String, String> fileMap = testFileMap();

        String contentsString = objectMapper.writeValueAsString(fileMap.keySet());
        String fileNamesString = objectMapper.writeValueAsString(fileMap.values());

        for(int i = 0; i < 3; i++) {
            schemaArray.add(GeneratorSchema.SPDX.name());
            formatArray.add(GeneratorSchema.GeneratorFormat.SPDX.name());
        }
        for(int i = 0; i < 6; i++)
            schemaArray.add(GeneratorSchema.CycloneDX.name());
        for(int i = 0; i < 3; i++)
            formatArray.add(GeneratorSchema.GeneratorFormat.XML.name());
        for(int i = 0; i < 3; i++)
            formatArray.add(GeneratorSchema.GeneratorFormat.JSON.name());
        String schemaString = objectMapper.writeValueAsString(schemaArray);
        String formatString = objectMapper.writeValueAsString(formatArray);

        return new String[]{
                contentsString,
                fileNamesString,
                String.valueOf(fileMap.size()),
                schemaString,
                formatString
        };
    }
}
