package org.svip.api;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.svip.api.utils.Utils;
import org.svip.sbom.model.SBOM;
import org.svip.sbomanalysis.comparison.Comparison;
import org.svip.sbomanalysis.qualityattributes.QAPipeline;
import org.svip.sbomanalysis.qualityattributes.QualityReport;
import org.svip.sbomfactory.translators.TranslatorController;
import org.svip.sbomfactory.translators.TranslatorException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * File: APIController.java
 * REST API Controller for SBOM Comparison and QA
 *
 * @author Juan Francisco Patino, Asa Horn, Justin Jantzi
 */
@RestController
@RequestMapping("plugfest")
public class PlugFestApiController {

    /**
     *  Hold a pipeline object for QAReports
     */
    private static QAPipeline pipeline;

    /**
     * default constructor. Makes a QAPipeline
     */
    public PlugFestApiController() {
        pipeline = new QAPipeline();
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
    public ResponseEntity<?> compare(@RequestParam("contents") String contentArray,
                                     @RequestParam("fileNames") String fileArray) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> contents = objectMapper.readValue(contentArray, new TypeReference<List<String>>(){});
        List<String> fileNames = objectMapper.readValue(fileArray, new TypeReference<List<String>>(){});

        // Convert the SBOMs to SBOM objects
        ArrayList<SBOM> sboms = new ArrayList<>();

        for (int i = 0; i < contents.size(); i++) {
            // Get contents of the file
            try {
                sboms.add(TranslatorController.translateContents(contents.get(i), fileNames.get(i)));
            } catch (TranslatorException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        if(sboms.size() < 2) return new ResponseEntity<>("Must provide 2 or more SBOMs.", HttpStatus.BAD_REQUEST);

        Comparison report = new Comparison(sboms); // report to return
        report.runComparison();

        //encode and send report
        return Utils.encodeResponse(report);
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
    public ResponseEntity<?> qa(@RequestParam("contents") String contents, @RequestParam("fileName") String fileName) {
        SBOM sbom;
        try {
            sbom = TranslatorController.translateContents(contents, fileName);
        } catch (TranslatorException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //run the QA
        QualityReport report = pipeline.process(sbom);

        //encode and send report
        return Utils.encodeResponse(report);
    }

    /**
     * Send post request to /parse and it will convert the file contents to an SBOM object, returns null if failed to parse
     *
     * @param contents File contents of the SBOM file to parse
     * @param fileName Name of the file that the SBOM contents came from
     * @return SBOM object, null if failed to parse
     */
    @PostMapping("parse")
    public ResponseEntity<?> parse(@RequestParam("contents") String contents,
                                   @RequestParam("fileName") String fileName) {
        SBOM sbom;
        try {
            sbom = TranslatorController.translateContents(contents, fileName);
        } catch (TranslatorException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return Utils.encodeResponse(sbom);
    }
}
