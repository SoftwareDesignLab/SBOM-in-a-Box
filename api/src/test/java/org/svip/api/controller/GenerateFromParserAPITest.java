package org.svip.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;
import org.svip.api.utils.Utils;
import org.svip.sbomgeneration.serializers.SerializerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GenerateFromParserAPITest extends APITest {

    private final String[] schemas = {"CDX14", "SPDX23"};
    private final String[] formats = {"JSON", "TAGVALUE"};
    private static final Logger LOGGER = LoggerFactory.getLogger(SVIPApiController.class);
    private final Map<Map<Long, String>, SBOMFile[]> testMap;

    public GenerateFromParserAPITest() throws IOException{
        testMap = getTestProjectMap();
    }

//    /**
//     * Tests bad SBOMFiles
//     */
//    @Test
//    @DisplayName("Invalid format test")
//    public void sbomFilesNullPropertiesTest() {
//
//        SBOMFile[] noName = new SBOMFile[]{new SBOMFile("", "int i = 3;")};
//        SBOMFile[] noContents = new SBOMFile[]{new SBOMFile("name.java", "")};
//        Collection<SBOMFile[]> files = testMap.values();
//        SBOMFile[] empty = null;
//        for (SBOMFile[] file: files
//             ) {
//            for (SBOMFile s: file
//                 ) {
//                if(s.hasNullProperties()) {
//                    empty = file;
//                    break;
//                }
//            }
//        }
//
//        assertEquals(HttpStatus.BAD_REQUEST, controller.generateParsers(noName,
//                        "Java", SerializerFactory.Schema.SPDX23, SerializerFactory.Format.JSON).
//                getStatusCode());
//
//        assertEquals(HttpStatus.BAD_REQUEST, controller.generateParsers(noContents,
//                        "cs", SerializerFactory.Schema.SPDX23, SerializerFactory.Format.JSON).
//                getStatusCode());
//
//        assertEquals(HttpStatus.BAD_REQUEST, controller.generateParsers(empty,
//                        "empty", SerializerFactory.Schema.SPDX23, SerializerFactory.Format.JSON).
//                getStatusCode());
//
//    }
//
//    /**
//     * CDX does not support Tag Value format
//     */
//    @Test
//    @DisplayName("Convert to CDX tag value test")
//    public void CDXTagValueTest() {
//        Collection<SBOMFile[]> files = testMap.values();
//        SBOMFile[] sbomFiles = (SBOMFile[]) files.toArray()[0];
//        assertEquals(HttpStatus.BAD_REQUEST, controller.generateParsers(sbomFiles,
//                        "Java", SerializerFactory.Schema.CDX14, SerializerFactory.Format.TAGVALUE).
//                getStatusCode());
//    }
//
    /**
     *
     */
    @Test
    @DisplayName("")
    public void zipTest() throws IOException {

        ResponseEntity<Long> response = (ResponseEntity<Long>) controller.generateParsers(System.getProperty("user.dir")
                + "/src/test/java/org/svip/api/sample_projects/Java.zip", "Java",
                SerializerFactory.Schema.SPDX23, SerializerFactory.Format.TAGVALUE);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

    }
//
//    @Test
//    @DisplayName("Generate from parser test")
//    public void generateTest() {
//
//        Collection<SBOMFile[]> files = testMap.values();
//
//        int i = 0;
//        for (SBOMFile[] file : files) {
//
//            HashMap<Long, String> projKey = (HashMap<Long, String>) testMap.keySet().toArray()[i];
//            String projectName = (String) projKey.values().toArray()[0];
//
//            LOGGER.info("Parsing project: " + projectName);
//
//            for (String schema : schemas
//            ) {
//                for (String format : formats
//                ) {
//
//                    if (projectName == null)
//                        System.out.println("hi");
//
//                    if (schema.equals("CDX14") && format.equals("TAGVALUE") || projectName.equals("Empty"))
//                        continue;
//
//                    if ((projectName.equals("Java") || projectName.equals("CSharp/Nuget") || projectName.equals("SubProcess") // todo fix SPDXJSONSerializers
//                            || projectName.equals("JS") || projectName.equals("Ruby"))
//                            && schema.equals("SPDX23") && format.equals("JSON"))
//                        continue;
//
//                    LOGGER.info("PARSING TO: " + schema + " + " + format);
//
//                    ResponseEntity<?> response = controller.generateParsers(file, projectName,
//                            SerializerFactory.Schema.valueOf(schema), SerializerFactory.Format.valueOf(format));
//
//                    assertEquals(HttpStatus.OK, response.getStatusCode());
//                    assertNotNull(response.getBody());
//                }
//            }
//            i++;
//            LOGGER.info("\n-------------\n");
//        }
//    }
}
