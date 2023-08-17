//package org.svip.api.controller;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.svip.api.entities.SBOM;
//import org.svip.api.entities.SBOMFile;
//import org.svip.api.model.MockMultipartFile;
//import org.svip.api.model.SBOMFile;
//import org.svip.serializers.SerializerFactory;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.junit.jupiter.api.Assumptions.assumeTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.svip.generation.osi.OSIClient.dockerCheck;
//
///**
// * File: GenerateFromOSIAPITest.java
// * <p>
// * Holds the unit tests responsible for testing the OSI API endpoint.
// *
// * @author Ian Dunn
// */
//public class GenerateFromOSIAPITest extends APITest {
//    private final String sampleProjectDirectory = System.getProperty("user.dir")
//            + "/src/test/resources/sample_projects/";
//
//    private static Map<Long, SBOMFile> fileMap;
//
//    @BeforeAll
//    static void setupFileMap(){
//        try{
//            fileMap = getTestFileMap();
//        } catch (Exception e){
//            fail(e);
//        }
//    }
//
//    @Override
//    @BeforeEach
//    public void setup() {
//        // Ensure controller was able to construct OSI
//        assumeTrue(osiController.isOSIEnabled());
//    }
//
//    /**
//     * Tests invalid projects
//     */
//    @Test
//    @DisplayName("Invalid Project Test")
//    public void invalidProjectTest() throws FileNotFoundException {
//
//        String[] zipFiles = {"sampleProjectEmpty", "sampleProjectNullProperties"};
//
//        assertEquals(HttpStatus.NO_CONTENT,
//                osiController.generateOSI(
//                        new MockMultipartFile(new File(sampleProjectDirectory + zipFiles[0] + ".zip")),
//                        "Empty Project Folder",
//                        SerializerFactory.Schema.SPDX23,
//                        SerializerFactory.Format.JSON,
//                        null).getStatusCode());
//
//        assertEquals(HttpStatus.NO_CONTENT,
//                osiController.generateOSI(
//                        new MockMultipartFile(new File(sampleProjectDirectory + zipFiles[1] + ".zip")),
//                        "Empty C file",
//                        SerializerFactory.Schema.SPDX23,
//                        SerializerFactory.Format.JSON,
//                        null).getStatusCode());
//    }
//
//    /**
//     * CDX does not support Tag Value format
//     */
//    @Test
//    @DisplayName("Convert to CDX tag value test")
//    public void CDXTagValueTest() throws IOException {
//        assertEquals(HttpStatus.BAD_REQUEST,
//                osiController.generateOSI(
//                        new MockMultipartFile(new File(sampleProjectDirectory + "Scala.zip")),
//                        "Java",
//                        SerializerFactory.Schema.CDX14,
//                        SerializerFactory.Format.TAGVALUE,
//                        null).getStatusCode());
//    }
//
//    /**
//     * File must be a zip file
//     */
//    @Test
//    @DisplayName("Incorrect file type test")
//    public void zipExceptionTest() throws IOException {
//        assertEquals(HttpStatus.BAD_REQUEST,
//                osiController.generateOSI(
//                        new MockMultipartFile(new File(sampleProjectDirectory + "Ruby/lib/bar.rb")),
//                        "Java",
//                        SerializerFactory.Schema.CDX14,
//                        SerializerFactory.Format.JSON,
//                        null).getStatusCode());
//    }
//
//    /**
//     * Main SBOM Generation test
//     */
//    @Test
//    @DisplayName("Generate from OSI test")
//    public void generateTest() throws IOException {
//        // Mock repository output (returns SBOMFile that it received)
//        when(repository.save(any(SBOM.class))).thenAnswer(i -> i.getArgument(0));
//
//        // TODO No C#, Perl, or Scala OSI tools
//        String[] zipFiles = {"Conan", "Conda_noEmptyFiles", "Rust_noEmptyFiles"};
//
//        for (String file : zipFiles) {
//            for (SerializerFactory.Schema schema : schemas) {
//
//                for (SerializerFactory.Format format : formats) {
//                    if (schema == SerializerFactory.Schema.CDX14 && format == SerializerFactory.Format.TAGVALUE)
//                        continue;
//
//                    ResponseEntity<Long> response = (ResponseEntity<Long>) osiController.generateOSI(
//                            new MockMultipartFile(new File(sampleProjectDirectory + file + ".zip")),
//                            file,
//                            schema,
//                            format,
//                            null);
//                    assertEquals(HttpStatus.OK, response.getStatusCode());
//                    assertNotNull(response.getBody());
//                }
//
//            }
//
//
//        }
//
//    }
//
//}
