package org.svip.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.svip.sbom.model.SBOM;
import org.svip.sbomanalysis.comparison.Comparison;
import org.svip.sbomanalysis.comparison.Merger;
import org.svip.sbomanalysis.qualityattributes.QAPipeline;
import org.svip.sbomanalysis.qualityattributes.QualityReport;
import org.svip.sbomfactory.generators.ParserController;
import org.svip.sbomfactory.generators.generators.utils.GeneratorSchema;
import org.svip.sbomfactory.osi.OSI;
import org.svip.sbomfactory.translators.Translator;
import org.svip.sbomfactory.translators.TranslatorPlugFest;
import org.svip.sbomvex.VEXFactory;
import org.svip.visualizer.NodeFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * file: SVIP.java
 * <p>
 * Driver to launch SVIP and acts as Master Controller
 *
 * @author Derek Garcia
 * @author Kevin Laporte
 * @author Asa Horn
 * @author Justin Jantzi
 * @author Matt London
 * @author Ian Dunn
 */

@RestController
@RequestMapping("/svip")
public class SVIPApiController {
    /**
     *  Hold a pipeline object for QAReports
     */
    private static QAPipeline pipeline;

    /**
     * Http headers of Spring boot application
     */
    private HttpHeaders headers;

    /**
     * OSI docker container representation
     */
    private OSI osiContainer;

    /**
     * Default OSI Bound Directory location
     */
    private static String osiBoundDir = "src/main/java/com/svip/osi/core/bound_dir";

    /**
     * Default path to where dockerfile is located
     */
    private static String dockerPath = "/core/src/main/java/org/svip/sbomfactory/osi/Dockerfile";

    /**
     * buildOSI runs on startup to build the OSI container independent of the front-end.
     */
    @PostConstruct
    public void buildOSI() {
        // TODO: For SVIP v3, refactor to move OSI building operations into another class
        osiContainer = new OSI(osiBoundDir, dockerPath);
    }

    public SVIPApiController() {
        headers = new HttpHeaders();
        headers.add("AccessControlAllowOrigin", "http://localhost:4200");
    }

    /**
     * To be called when the object is released by the garbage collector. DO NOT CALL MANUALLY
     */
    @PreDestroy
    public void close() {
        // Close the osi container so that we delete the instance
        osiContainer.close();
    }

    /**
     * Generator a report for the given filePath
     *
     * @param filePath Path to target Directory
     * @return JSON String
     * @throws Exception Error when generating the report
     */
    public String generateReport(String filePath) throws Exception {
        // 1. osi container generates SBOMs
        if(osiContainer.generateSBOMs(filePath) != 0)
            throw new Exception("Failed to Generate SBOMs with OSI");

        // 2. call translators
        ArrayList<SBOM> SBOMs = new ArrayList<>(); // TODO: Remove redundant init
        try{
            // TODO: Add controller for file driving possibly?
//            SBOMs = Translator.parseSBOMs(osiBoundDir + "/sboms");
        } catch (Exception e){
            throw new Exception("Unable to Translate SBOMs");
        }

        // 3. Merge SBOMs
        Merger merger = new Merger();
        SBOM master = merger.merge(SBOMs);

        if(master == null)
            throw new Exception("Failed to merge SBOMs");

//        // 4. Apply Vex
//        if (NVIPUsername == null || NVIPPassword == null)
//            System.err.println("NVIP Login Credentials not set. Call setNVIPLogin() first. No VEX will be applied.");
//        else if (NVIPEndpoint == null)
//            System.err.println("NVIP Endpoint not set. Call setNVIPEndpoint() first. No VEX will be applied.");
//        else {
//            try {
//                factory.applyVex(master);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        // 5. Convert to D3 and return result
        return new NodeFactory().CreateNodeGraphJSON(master);
    }


    /**
     * Creates a Node Graph from the master SBOM
     * and returns a JSON String representation of the Node Graph.
     *
     * @return JSON String representation of the Node Graph
     */
    @GetMapping("/sbom-node-graph")
    public ResponseEntity<String> getNodeGraph(@RequestParam String filePath) {
        try {
            String data = generateReport(filePath);
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("generateSBOM")
    public ResponseEntity<String> generate(@RequestParam("contents") String contentsArray, @RequestParam("fileNames") String fileArray, @RequestParam("schemaName") String schemaName, @RequestParam("formatName") String formatName) throws IOException {

        final ObjectMapper objectMapper = new ObjectMapper();
        final List<String> fileContents = objectMapper.readValue(contentsArray, new TypeReference<>(){});
        final List<String> filePaths = objectMapper.readValue(fileArray, new TypeReference<>(){});

        final ParserController controller = new ParserController(null); // TODO: Get root directory and use it here

        for (int i = 0; i < filePaths.size(); i++) {
            final String path = filePaths.get(i);
            final String contents = fileContents.get(i);
            controller.setPWD(path);
            controller.parse(path, contents); // TODO: Fix
        }

//        // Parse the root directory with the controller
//        try (final Stream<String> stream = filePaths.stream()) {
//            stream.forEach(filepath -> {
//                try {
//                    // Set pwd to formatted filepath if it is actually a directory
//                    if (Files.isDirectory(filepath)) {
//                        controller.setPWD(filepath);
//                        controller.incrementDirCounter(); // TODO: Remove
//                    } else { // Otherwise, it is a file, try to parse
//                        controller.setPWD(filepath);
//                        controller.parse(filepath, fileContents); // TODO: Fix
//                    }
//                } catch (Exception e) {
////                    log(Debug.LOG_TYPE.EXCEPTION, e);
//                }
//            });
//        } catch (Exception e) {
////            log(Debug.LOG_TYPE.EXCEPTION, e);
//        }

        // Get schema from parameters, if not valid, default to CycloneDX
        GeneratorSchema schema = GeneratorSchema.CycloneDX;
        try { schema = GeneratorSchema.valueOfArgument(schemaName.toUpperCase()); }
        catch (IllegalArgumentException ignored) { }

        // Get format from parameters, if not valid, default to JSON
        GeneratorSchema.GeneratorFormat format = schema.getDefaultFormat();
        try { format = GeneratorSchema.GeneratorFormat.valueOf(formatName.toUpperCase()); }
        catch (IllegalArgumentException ignored) {
//            log(Debug.LOG_TYPE.WARN, String.format(
//                    "Invalid format type provided: '%s', defaulting to '%s'",
//                    optArgs.get("-f").toUpperCase(),
//                    format
//            ));
        }

        //encode and send report
        try {
            return new ResponseEntity<>(controller.toFile(null, schema, format), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * USAGE. Send POST request to /compare with two+ SBOM files.
     * The first SBOM will be the baseline, and the rest will be compared to it.
     * The API will respond with an HTTP 200 and a serialized DiffReport object.
     *
     * @param contentArray Array of SBOM file contents (the actual cyclonedx/spdx files) as a JSON string
     * @param fileArray Array of file names as a JSON string
     * @return Wrapped Comparison object
     */
    @PostMapping("compare")
    public ResponseEntity<Comparison> compare(@RequestParam("contents") String contentArray, @RequestParam("fileNames") String fileArray) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> contents = objectMapper.readValue(contentArray, new TypeReference<List<String>>(){});
        List<String> fileNames = objectMapper.readValue(fileArray, new TypeReference<List<String>>(){});

        // Convert the SBOMs to SBOM objects
        ArrayList<SBOM> sboms = new ArrayList<>();

        for (int i = 0; i < contents.size(); i++) {
            // Get contents of the file
            sboms.add(TranslatorPlugFest.translateContents(contents.get(i), fileNames.get(i)));
        }

        if(sboms.size() < 2){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Comparison report = new Comparison(sboms); // report to return
        report.runComparison();

        //encode and send report
        try {
            return new ResponseEntity<>(report, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * USAGE. Send POST request to /qa with a single sbom file
     * The API will respond with an HTTP 200 and a serialized report in the body.
     *
     * @param contents - File content of the SBOM to run metrics on
     * @param fileName - Name of the SBOM file
     * @return - wrapped QualityReport object, null if failed
     */
    @PostMapping("qa")
    public ResponseEntity<QualityReport> qa(@RequestParam("contents") String contents, @RequestParam("fileName") String fileName) {

        SBOM sbom = TranslatorPlugFest.translateContents(contents, fileName);

        // Check if the sbom is null
        if (sbom == null) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }

        //run the QA
        QualityReport report = pipeline.process(sbom);

        //encode and send report
        try {
            return new ResponseEntity<>(report, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Send post request to /parse and it will convert the file contents to an SBOM object, returns null if failed to parse
     *
     * @param contents File contents of the SBOM file to parse
     * @param fileName Name of the file that the SBOM contents came from
     * @return SBOM object, null if failed to parse
     */
    @PostMapping("parse")
    public ResponseEntity<SBOM> parse(@RequestParam("contents") String contents, @RequestParam("fileName") String fileName) {
        SBOM sbom = TranslatorPlugFest.translateContents(contents, fileName);

        try {
            // Explicitly return null if failed
            if (sbom == null) {
                return new ResponseEntity<>(null, HttpStatus.OK);
            }
            return new ResponseEntity<>(sbom, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
