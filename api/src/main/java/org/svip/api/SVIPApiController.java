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
import org.svip.sbom.model.SBOM;
import org.svip.sbomanalysis.comparison.Comparison;
import org.svip.sbomanalysis.comparison.Merger;
import org.svip.sbomanalysis.qualityattributes.QAPipeline;
import org.svip.sbomanalysis.qualityattributes.QualityReport;
import org.svip.sbomfactory.generators.ParserController;
import org.svip.sbomfactory.generators.generators.SBOMGenerator;

import org.svip.sbomfactory.generators.generators.utils.GeneratorException;
import org.svip.sbomfactory.generators.generators.utils.GeneratorSchema;
import org.svip.sbomfactory.osi.OSI;
import org.svip.sbomfactory.translators.TranslatorCDXJSON;
import org.svip.sbomfactory.translators.TranslatorCDXXML;
import org.svip.sbomfactory.translators.TranslatorPlugFest;
import org.svip.sbomfactory.translators.TranslatorSPDX;


import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
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


    @PostMapping("/generateSBOM")
    public ResponseEntity<String> generate(@RequestParam("contents") String contentsArray,
                                           @RequestParam("fileNames") String fileArray,
                                           @RequestParam("schemaName") String schemaName,
                                           @RequestParam("formatName") String formatName) throws IOException {

        // todo OSI
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

        Map<GeneratorSchema, GeneratorSchema.GeneratorFormat> m = configureSchema(schemaName, formatName);
        GeneratorSchema schema = (GeneratorSchema) m.keySet().toArray()[0];
        GeneratorSchema.GeneratorFormat format = (GeneratorSchema.GeneratorFormat) m.entrySet().toArray()[0];

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
    @PostMapping("/compare")
    public ResponseEntity<Comparison> compare(@RequestParam("contents") String contentArray, @RequestParam("fileNames") String fileArray) throws IOException {

        ArrayList<SBOM> sboms = translateMultiple(contentArray, fileArray);

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
    @PostMapping("/parse")
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
    public ResponseEntity<SBOM> merge(@RequestParam("fileContents") String fileContents, @RequestParam("fileNames") String fileNames
            , @RequestParam("schema") String schema, @RequestParam("format") String format) throws IOException, GeneratorException, ParseException, ParserConfigurationException, ParseException, ParseException, ParseException, ParseException {

        ArrayList<SBOM> sboms = translateMultiple(fileContents, fileNames);

        if(sboms.size() < 2){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Merger merger = new Merger();
        SBOM result = merger.merge(sboms); // report to return

        Map<GeneratorSchema, GeneratorSchema.GeneratorFormat> m = configureSchema(schema, format); // get schema enumerations from call
        GeneratorSchema generatorSchema = (GeneratorSchema) m.keySet().toArray()[0];
        GeneratorSchema.GeneratorFormat generatorFormat = m.get(generatorSchema);

        if(generatorSchema == GeneratorSchema.SPDX) // spdx schema implies spdx format
            generatorFormat = GeneratorSchema.GeneratorFormat.SPDX;

        SBOMGenerator generator = new SBOMGenerator(result, generatorSchema);
        String contents = generator.writeFileToString(generatorFormat, true);

        try{
            switch (generatorSchema){
                case SPDX: { //spdx, json, xml, yaml

                  //  switch (generatorFormat){
                      //  case JSON:
                      //      result = new TranslatorCDXJSON().translate(path);
                     //     break;
                     //   case XML:
                     //       result = new TranslatorCDXXML().translate(path);
                    //          break; // todo once the other SPDX formats are done, uncomment
                     //   case YAML:
                     //       result = new TranslatorCDXXML().translate(path);
                    //     break;
                    //    case SPDX:
                    result = new TranslatorSPDX().translateContents(contents, ""); //.spdx
                    //break;
                  //  }

                    break;
                }
                default:{ //CDX
                    // json xml

                    switch (generatorFormat){
                        case JSON:
                            result = new TranslatorCDXJSON().translateContents(contents, "");
                            break;
                        case XML:
                            generator.writeFileToString(generatorFormat, false);
                            result = new TranslatorCDXXML().translateContents(contents, "");
                            break;
                    }

                }
            }
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // encode and send result
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    /**
     * Code shared by /compare and /merge used to configure optional parameters
     *
     * @param schema schema string value
     * @param format format string value
     */
    private static Map<GeneratorSchema, GeneratorSchema.GeneratorFormat> configureSchema(String schema, String format) {

        GeneratorSchema resultSchema;
        try { resultSchema = GeneratorSchema.valueOfArgument(schema.toUpperCase()); }
        catch (IllegalArgumentException i) { return null;}

        GeneratorSchema.GeneratorFormat resultFormat;
        try { resultFormat = GeneratorSchema.GeneratorFormat.valueOf(format.toUpperCase()); }
        catch (IllegalArgumentException i) { return null;}

        return Map.of(resultSchema, resultFormat);

    }

    /**
     * Code shared by /compare and /merge used to deserialize multiple SBOMs
     *
     * @param fileContents JSON string array of the contents of all provided SBOMs
     * @param fileNames JSON string array of the filenames of all provided SBOMs
     * @return list of SBOM objects
     * @throws JsonProcessingException
     */
    private static ArrayList<SBOM> translateMultiple(String fileContents, String fileNames) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> contents = objectMapper.readValue(fileContents, new TypeReference<List<String>>(){});
        List<String> fNames = objectMapper.readValue(fileNames, new TypeReference<List<String>>(){});

        // Convert the SBOMs to SBOM objects
        ArrayList<SBOM> sboms = new ArrayList<>();

        for (int i = 0; i < contents.size(); i++) {
            // Get contents of the file
            sboms.add(TranslatorPlugFest.translateContents(contents.get(i), fNames.get(i)));
        }
        return sboms;
    }


}
