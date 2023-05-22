package org.svip.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.generators.utils.GeneratorException;
import org.svip.sbomfactory.generators.generators.utils.GeneratorSchema;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;


/**
 * File: MergeFromAPITest.java
 * Unit test for API regarding Merging SBOMs
 * <p>
 * Tests:<br>
 * - mergeTest: Test that the API can merge three SBOMs
 *
 * @author Juan Francisco Patino
 */
public class MergeFromAPITest {
    /**
     *  Example SBOMs to use for testing
     */
    private final String alpineSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/sbom.alpine-compare.2-3.spdx";
    private final String pythonSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/sbom.python.2-3.spdx";
    private final String dockerSBOM = System.getProperty("user.dir")
            + "/src/test/java/org/svip/api/sample_sboms/sbom.docker.2-2.spdx";

    /**
     * Controller to test
     */
    private SVIPApiController ctrl;

    /**
     * Test that the API can Merge three SBOMs
     * @throws IOException If the SBOM merging is broken
     */
    @Test
    public void mergeTest() throws IOException, GeneratorException, ParserConfigurationException, ParseException {
        List<String> contentsArray = new ArrayList<>();
        List<String> fileNamesArray = new ArrayList<>();

        contentsArray.add(new String(Files.readAllBytes(Paths.get(alpineSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(pythonSBOM))));
        contentsArray.add(new String(Files.readAllBytes(Paths.get(dockerSBOM))));

        ObjectMapper objectMapper = new ObjectMapper();

        String contentsString = objectMapper.writeValueAsString(contentsArray);

        fileNamesArray.add(alpineSBOM);
        fileNamesArray.add(pythonSBOM);
        fileNamesArray.add(dockerSBOM);

        String fileNamesString = objectMapper.writeValueAsString(fileNamesArray);


        for(GeneratorSchema schema : GeneratorSchema.values()) {
            // Test all possible formats
            for(GeneratorSchema.GeneratorFormat format : GeneratorSchema.GeneratorFormat.values()) {

                if(schema == GeneratorSchema.SPDX)
                    switch (format) {
                        case XML, JSON, YAML -> { // todo we don't support SPDX with these formats yet
                            continue;
                        }
                    }

                if(format == GeneratorSchema.GeneratorFormat.XML)
                    continue; //todo get rid, this is for debug

                if(schema.supportsFormat(format)) {
                    // Test logic per merge
                    System.out.println("-----------------\ntesting " + schema + " " + format);
                    ResponseEntity<SBOM> report = ctrl.merge(contentsString, fileNamesString, schema.toString().toUpperCase(), format.toString().toUpperCase());
                    assertNotEquals(null, report.getBody());
                    System.out.println("PASSED " + schema + " " + format + "!!\n-----------------\n");
                }
            }
        }

    }

    /**
     * SETUP: Start API before testing
     */
    @BeforeEach
    public void setup(){

        ctrl = new SVIPApiController();

    }

}
