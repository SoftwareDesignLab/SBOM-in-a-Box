package org.svip.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.svip.api.utils.Utils;
import org.svip.sbomfactory.osi.OSI;
import org.svip.sbomfactory.translators.TranslatorController;
import org.svip.sbomfactory.translators.TranslatorException;

import java.util.HashMap;
import java.util.Map;

/**
 * API Controller for handling requests to SVIP
 *
 * @author Derek Garcia
 * @author Kevin Laporte
 * @author Asa Horn
 * @author Justin Jantzi
 * @author Matt London
 * @author Ian Dunn
 * @author Juan Francisco Patino
 */
@RestController
@RequestMapping("/svip")
public class SVIPApiController {

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
     * Current working directory
     */
    private static String pwd = "/src/test/java/org/svip/api";

    private final Map<String, String> files;

    /** TODO OSI
     * buildOSI runs on startup to build the OSI container independent of the front-end.
     */
//    @PostConstruct
//    public void buildOSI() {
//        // TODO: For SVIP v3, refactor to move OSI building operations into another class
//        osiContainer = new OSI(osiBoundDir, dockerPath);
//    }

    public SVIPApiController() {
        headers = new HttpHeaders();
        headers.add("AccessControlAllowOrigin", "http://localhost:4200");

        files = new HashMap<>();
    }

    /** TODO OSI
     * To be called when the object is released by the garbage collector. DO NOT CALL MANUALLY
     */
//    @PreDestroy
//    public void close() {
//        // Close the osi container so that we delete the instance
//        osiContainer.close();
//    }

    /**
     * TODO Creates a Node Graph from the master SBOM
     * and returns a JSON String representation of the Node Graph.
     *
     * @return JSON String representation of the Node Graph
     */
//    @GetMapping("/sbom-node-graph")
//    public ResponseEntity<?> getNodeGraph(@RequestBody String filePath) {
//        // todo redo
//        return null;
//    }

    /**
     * USAGE. Send POST request to /upload with one SBOM file.
     *   The SBOM file is made up of 2 JSON key-value pairs in the request body: fileName and contents.
     *
     * The API will respond with an HTTP 200 and the uploaded filename used to identify the SBOM file.
     *
     * @param sbomFile 2 JSON key-value pairs in the request body: fileName and contents.
     * @return The uploaded filename used to identify the SBOM file.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestBody Utils.SBOMFile sbomFile) {
        // Validate
        if (sbomFile.hasNullProperties)
            return new ResponseEntity<>("SBOM filename and/or contents may not be empty", HttpStatus.BAD_REQUEST);

        try {
            TranslatorController.translateContents(sbomFile.contents, sbomFile.fileName);
        } catch (TranslatorException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // Upload
        files.put(sbomFile.fileName, sbomFile.contents);

        // Return ID
        return Utils.encodeResponse(sbomFile.fileName);
    }

    /**
     * USAGE. Send GET request to /view with a URL parameter filename to get the contents of.
     *
     * The API will respond with an HTTP 200 and the contents of the SBOM file.
     *
     * @param fileName The filename of the SBOM contents to retrieve.
     * @return The contents of the SBOM file.
     */
    @GetMapping("/view")
    public ResponseEntity<String> view(@RequestParam("fileName") String fileName) {
        // Get SBOM
        String sbomFile = files.get(fileName);

        // Return SBOM or invalid ID
        if (sbomFile == null)
            return new ResponseEntity<>("Invalid SBOM ID.", HttpStatus.BAD_REQUEST);

        return Utils.encodeResponse(sbomFile);
    }

    /**
     * USAGE. Send GET request to /viewFiles.
     * The API will respond with an HTTP 200 and a JSON array of all currently uploaded SBOM files.
     *
     * @return A JSON array of all currently uploaded SBOM files.
     */
    @GetMapping("/viewFiles")
    public ResponseEntity<String[]> viewFiles() {
        // Get file names
        String[] fileNames = files.keySet().toArray(new String[0]);

        // Return file names
        return Utils.encodeResponse(fileNames);
    }

    /**
     * USAGE. Send POST request to /generateSBOM with one or more files. If a schema or format is not provided or is
     * invalid, it will default to CycloneDX JSON.
     * The API will respond with an HTTP 200 and an SBOM string in the given schema and format (if applicable).
     *
     * @param contentsArray JSON array of project file contents (the source files) as a string.
     * @param fileArray JSON array of corresponding project file names as a string.
     * @param schemaName The name of the schema to output to.
     * @param formatName The name of the format to output to.
     * @return A ResponseEntity with code HTTP 200 and the SBOM file in string format.
     */
//    @PostMapping("/generateSBOM")
//    public ResponseEntity<?> generate(@RequestBody String contentsArray,
//                                      @RequestBody String fileArray,
//                                      @RequestBody String schemaName,
//                                      @RequestBody String formatName) {
//
//        // VALIDATE/PARSE INPUT DATA
//        // todo OSI
//        Map<String, List<String>> contentsAndFiles = Utils.validateContentsAndNamesArrays(contentsArray, fileArray);
//        if(contentsAndFiles == null) return new ResponseEntity<>("Invalid contents or filenames array.",
//                HttpStatus.BAD_REQUEST);
//
//        // Get schema/format from parameters, if not valid, default to CycloneDX/JSON
//        GeneratorSchema schema = Resolver.resolveSchema(schemaName, true);
//        GeneratorSchema.GeneratorFormat format = Resolver.resolveFormat(formatName, true);
//        if(!schema.supportsFormat(format)) format = schema.getDefaultFormat();
//
//        // BUILD FILE TREE REPRESENTATION
//        // TODO talk to front-end and figure out what the project name should be, currently SVIP. Common directory?
//        VirtualTree fileTree = new VirtualTree(new VirtualPath("SVIP"));
//        for (int i = 0; i < contentsAndFiles.get("filePaths").size(); i++) {
//            fileTree.addNode(
//                    new VirtualPath(contentsAndFiles.get("filePaths").get(i)),
//                    contentsAndFiles.get("fileContents").get(i));
//        }
//
//        // PARSE FILES INTO SBOM
//        final ParserController controller = new ParserController(fileTree);
//        controller.parseAll();
//
//        // Generate SBOM to string and send
//        try {
//            return Utils.encodeResponse(controller.toFile(null, schema, format));
//        } catch (IOException e) {
//            return new ResponseEntity<>("Error generating SBOM.", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    /**
     * USAGE. Send POST request to /compare with two+ SBOM files.
     * The first SBOM will be the baseline, and the rest will be compared to it.
     * The API will respond with an HTTP 200 and a serialized DiffReport object.
     *
     * @param contentsArray Array of SBOM file contents (the actual cyclonedx/spdx files) as a JSON string
     * @param fileArray Array of file names as a JSON string
     * @return Wrapped Comparison object
     */
//    @PostMapping("/compare")
//    public ResponseEntity<?> compare(@RequestBody String contentsArray,
//                                              @RequestBody String fileArray) {
//        Map<String, List<String>> contentsAndFiles = Utils.validateContentsAndNamesArrays(contentsArray, fileArray);
//        if(contentsAndFiles == null) return new ResponseEntity<>("Invalid contents and/or files.",
//                HttpStatus.BAD_REQUEST);
//
//        List<SBOM> sboms;
//        try {
//            sboms = Utils.translateMultiple(contentsAndFiles.get("fileContents"), contentsAndFiles.get(
//                    "filePaths"));
//        } catch (TranslatorException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        if(sboms.size() < 2) return new ResponseEntity<>("Must provide 2 or more SBOMs.", HttpStatus.BAD_REQUEST);
//
//        Comparison report = new Comparison(sboms); // report to return
//        report.runComparison();
//
//        //encode and send report
//        return Utils.encodeResponse(report);
//    }

    /**
     * TODO USAGE. Send POST request to /qa with a single sbom file
     * The API will respond with an HTTP 200 and a serialized report in the body.
     *
     * @param contents - File content of the SBOM to run metrics on
     * @param fileName - Name of the SBOM file
     * @return - wrapped QualityReport object, null if failed
     */
//    @PostMapping("/qa")
//    public ResponseEntity<?> qa(@RequestBody String contents, @RequestBody String fileName) {
//        // TODO ensure contents & fileName not null
//        SBOM sbom;
//        try {
//            sbom = TranslatorController.translateContents(contents, fileName);
//        } catch (TranslatorException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        Set<AttributeProcessor> processors = new HashSet<>();
//        processors.add(new CompletenessProcessor());
//        processors.add(new UniquenessProcessor());
//        processors.add(new RegisteredProcessor());
//        processors.add(new LicensingProcessor());   // Add origin specific processors
//
//        //run the QA
//        QualityReport report = QAPipeline.process(sbom.getHeadUUID().toString(), sbom, processors);
//
//        //encode and send report
//        return Utils.encodeResponse(report);
//    }


    /**
     * Merge 2 SBOMs together, regardless of origin format
     *
     * @param contentsArray JSON string array of the contents of all provided SBOMs
     * @param fileArray JSON string array of the filenames of all provided SBOMs
     * @param schema String value of expected output schema (SPDX/CycloneDX)
     * @param format String value of expected output format (JSON/XML/YAML)
     * @return merged result SBOM
     */
//    @PostMapping("merge")
//    public ResponseEntity<String> merge(@RequestParam("fileContents") String contentsArray,
//                                   @RequestParam("fileNames") String fileArray
//            , @RequestParam("schema") String schema, @RequestParam("format") String format){
//
//        Map<String, List<String>> contentsAndFiles = Utils.validateContentsAndNamesArrays(contentsArray, fileArray);
//        if(contentsAndFiles == null) return new ResponseEntity<>("Invalid contents or filenames array.",
//                HttpStatus.BAD_REQUEST);
//
//        List<SBOM> sboms;
//        try {
//            sboms = Utils.translateMultiple(contentsAndFiles.get("fileContents"), contentsAndFiles.get(
//                    "filePaths"));
//        } catch (TranslatorException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        if(sboms.size() < 2) return new ResponseEntity<>("At least 2 SBOMs required to merge", HttpStatus.BAD_REQUEST);
//
//        GeneratorSchema generatorSchema = Resolver.resolveSchema(schema, false);
//        GeneratorSchema.GeneratorFormat generatorFormat = Resolver.resolveFormat(format, false);
//        if(generatorSchema == null || generatorFormat == null ||
//                !generatorSchema.supportsFormat(generatorFormat))
//            return new ResponseEntity<>("Invalid schema/format combination", HttpStatus.BAD_REQUEST);
//
//        Merger merger = new Merger();
//        SBOM result = merger.merge(sboms); // report to return
//
//        String resultString = Utils.generateSBOM(result, generatorSchema, generatorFormat);
//        return Utils.encodeResponse(resultString);
//    }

    /**
     * Send post request to /parse and it will convert the file contents to an SBOM object, returns null if failed to parse
     *
     * @param contents File contents of the SBOM file to parse
     * @param fileName Name of the file that the SBOM contents came from
     * @return SBOM object, null if failed to parse
     */
//    @PostMapping("/parse")
//    public ResponseEntity<?> parse(@RequestBody Utils.SBOMFile sbomFile) {
//        if (sbomFile.hasNullProperties)
//            return new ResponseEntity<>("SBOM filename and/or contents may not be empty", HttpStatus.BAD_REQUEST);
//
//        SBOM sbom;
//        try {
//            sbom = TranslatorController.translateContents(sbomFile.contents, sbomFile.fileName);
//        } catch (TranslatorException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        return Utils.encodeResponse(sbom);
//    }
}
