package org.svip.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.svip.api.utils.Utils;
import org.svip.sbom.model.SBOM;
import org.svip.sbomanalysis.comparison.Comparison;
import org.svip.sbomanalysis.comparison.Merger;
import org.svip.sbomanalysis.qualityattributes.QAPipeline;
import org.svip.sbomanalysis.qualityattributes.QualityReport;
import org.svip.sbomfactory.generators.ParserController;
import org.svip.sbomfactory.generators.generators.SBOMGenerator;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;
import org.svip.sbomfactory.generators.utils.virtualtree.VirtualPath;
import org.svip.sbomfactory.generators.utils.virtualtree.VirtualTree;
import org.svip.sbomfactory.osi.OSI;
import org.svip.sbomfactory.translators.TranslatorController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
     *  Hold a pipeline object for QAReports
     */
    private static QAPipeline pipeline = new QAPipeline();

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
     * Creates a Node Graph from the master SBOM
     * and returns a JSON String representation of the Node Graph.
     *
     * @return JSON String representation of the Node Graph
     */
    @GetMapping("/sbom-node-graph")
    public ResponseEntity<String> getNodeGraph(@RequestParam String filePath) {
        // todo redo
        return null;
    }


    // TODO Docstring, explain how
    @PostMapping("/generateSBOM")
    public ResponseEntity<String> generate(@RequestParam("fileContents") String contentsArray,
                                           @RequestParam("fileNames") String fileArray,
                                           @RequestParam("schemaName") String schemaName,
                                           @RequestParam("formatName") String formatName) {

        // VALIDATE/PARSE INPUT DATA
        // todo OSI
        final ObjectMapper objectMapper = new ObjectMapper();
        List<String> fileContents = null;
        List<String> filePaths = null;

        // Parse file contents/paths
        try {
            fileContents = objectMapper.readValue(contentsArray, new TypeReference<>(){});
            filePaths = objectMapper.readValue(fileArray, new TypeReference<>(){});
        } catch (JsonProcessingException | IllegalArgumentException e) {
            // TODO what does front-end want for errors?
            String output = (fileContents == null ? "Malformed fileContents List.\n" : "");
            if(filePaths == null) output += "Malformed fileNames List.";
            return new ResponseEntity<>(output, HttpStatus.BAD_REQUEST);
        }

        // Get schema/format from parameters, if not valid, default to CycloneDX/JSON
        GeneratorSchema schema = GeneratorSchema.CycloneDX;
        GeneratorSchema.GeneratorFormat format = schema.getDefaultFormat();

        // Parse schema/format
        try {
            if(schemaName != null) schema = GeneratorSchema.valueOfArgument(schemaName);
            if(formatName != null) format = GeneratorSchema.GeneratorFormat.valueOf(formatName);
            if(!schema.supportsFormat(format)) format = schema.getDefaultFormat();
        } catch (IllegalArgumentException ignored) {}

        // Ensure equal lengths of file contents & paths
        if(fileContents.size() != filePaths.size()) return new ResponseEntity<>("File contents & file names are " +
                "different lengths.", HttpStatus.BAD_REQUEST);


        // BUILD FILE TREE REPRESENTATION
        // TODO talk to front-end and figure out what the project name should be, currently SVIP. Common directory?
        VirtualTree fileTree = new VirtualTree(new VirtualPath("SVIP"));
        for (int i = 0; i < filePaths.size(); i++) {
            fileTree.addNode(
                    new VirtualPath(filePaths.get(i)),
                    fileContents.get(i));
        }

        // PARSE FILES INTO SBOM
        final ParserController controller = new ParserController(fileTree);
        controller.parseAll();

        // Generate SBOM to string and send
        try {
            return new ResponseEntity<>(controller.toFile(null, schema, format), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error sending output to SBOM.", HttpStatus.INTERNAL_SERVER_ERROR);
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
    @PostMapping("/compare")
    public ResponseEntity<Comparison> compare(@RequestParam("contents") String contentArray, @RequestParam("fileNames") String fileArray) throws IOException {

        ArrayList<SBOM> sboms = Utils.translateMultiple(contentArray, fileArray);

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
    @PostMapping("/qa")
    public ResponseEntity<QualityReport> qa(@RequestParam("contents") String contents, @RequestParam("fileName") String fileName) {

        SBOM sbom = TranslatorController.toSBOM(contents, fileName);

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
    @PostMapping("/parse")
    public ResponseEntity<SBOM> parse(@RequestParam("contents") String contents, @RequestParam("fileName") String fileName) {

        SBOM sbom = TranslatorController.toSBOM(contents, fileName);

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


    /**
     * Merge 2 SBOMs together, regardless of origin format
     *
     * @param fileContents JSON string array of the contents of all provided SBOMs
     * @param fileNames JSON string array of the filenames of all provided SBOMs
     * @param schema String value of expected output schema (SPDX/CycloneDX)
     * @param format String value of expected output format (JSON/XML/YAML)
     * @return merged result SBOM
     */
    @PostMapping("merge")
    public ResponseEntity<String> merge(@RequestParam("fileContents") String fileContents,
                                   @RequestParam("fileNames") String fileNames
            , @RequestParam("schema") String schema, @RequestParam("format") String format) throws IOException {

        ArrayList<SBOM> sboms = Utils.translateMultiple(fileContents, fileNames);

        if(sboms.size() < 2){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Merger merger = new Merger();
        SBOM result = merger.merge(sboms); // report to return

        Map<GeneratorSchema, GeneratorSchema.GeneratorFormat> m = Utils.configureSchema(schema, format); // get schema enumerations from call
        assert m != null;
        GeneratorSchema generatorSchema = (GeneratorSchema) m.keySet().toArray()[0];
        GeneratorSchema.GeneratorFormat generatorFormat = m.get(generatorSchema);

        if(generatorSchema == GeneratorSchema.SPDX) // spdx schema implies spdx format
            generatorFormat = GeneratorSchema.GeneratorFormat.SPDX;

        SBOMGenerator generator = new SBOMGenerator(result, generatorSchema);
        String resultString = generator.writeFileToString(generatorFormat, true);

        // encode and send result
        return new ResponseEntity<>(resultString, HttpStatus.OK);
    }


}
