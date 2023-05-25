package org.svip.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * File: APITestInputInitializer.java
 * Initialize test input for API unit tests
 *
 * @author Juan Francisco Patino
 */
public class APITestInputInitializer {

    /**
     * Example SBOMs to use for testing
     */

    // spdx .spdx
    private final static String alpineSBOM = "src/test/java/org/svip/api/sample_sboms/sbom.alpine-compare.2-3.spdx";
    private final static String pythonSBOM = "src/test/java/org/svip/api/sample_sboms/sbom.python.2-3.spdx";
    private final static String dockerSBOM = "src/test/java/org/svip/api/sample_sboms/sbom.docker.2-2.spdx";

    // cdx .xml
    private final static String alpineSBOMCdx = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/sbom.alpine.xml";
    private final static String jakeSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/jake_source_cdx.xml";
    private final static String condaSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/cyclonedx_source_cdx.xml";

    // cdx .json
    private final static String dotnetSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/cyclonedxfordotnetprojects_source_cdx.json";
    private final static String goSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/cdx-gomod-1.4.0-bin.json";
    private final static String gradleSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/CDXGradlePlugin_deployed_cdx.json";

    // class level variables
    private final static List<String> contentsArray = new ArrayList<>();
    private final static List<String> fileNamesArray = new ArrayList<>();
    private final static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Constructs a String array with three elements:
     * <ul>
     *     <li>A JSON array of SBOM contents as a string</li>
     *     <li>A JSON array of corresponding SBOM filenames as a string</li>
     *     <li>Total number of test input SBOMs</li>
     * </ul>
     *
     * @throws IOException If the SBOM merging is broken
     */
    public static String[] testInput() throws IOException {

        contentsArray.add(new String(Files.readAllBytes(Paths.get(alpineSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(pythonSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(dockerSBOM))));

        contentsArray.add(new String(Files.readAllBytes(Paths.get(alpineSBOMCdx))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(jakeSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(condaSBOM))));

        contentsArray.add(new String(Files.readAllBytes(Paths.get(dotnetSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(goSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(gradleSBOM))));
        String contentsString = objectMapper.writeValueAsString(contentsArray);

        fileNamesArray.add(alpineSBOM);
        fileNamesArray.add(pythonSBOM);
        fileNamesArray.add(dockerSBOM);

        fileNamesArray.add(alpineSBOMCdx);
        fileNamesArray.add(jakeSBOM);
        fileNamesArray.add(condaSBOM);

        fileNamesArray.add(dotnetSBOM);
        fileNamesArray.add(goSBOM);
        fileNamesArray.add(gradleSBOM);
        String fileNamesString = objectMapper.writeValueAsString(fileNamesArray);

        return new String[]{contentsString,fileNamesString, String.valueOf(fileNamesArray.size())};

    }

}