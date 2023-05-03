package org.svip.api;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.svip.sbom.model.SBOM;
import org.svip.sbomanalysis.comparison.Comparison;
import org.svip.sbomanalysis.qualityattributes.QAPipeline;
import org.svip.sbomanalysis.qualityattributes.QualityReport;
import org.svip.sbomfactory.translators.TranslatorPlugFest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
* File: APIController.java
* REST API Controller for SBOM Comparison and QA
*
* @author Juan Francisco Patino, Asa Horn
*/
@RestController
@RequestMapping("/plugfest")
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
     * USAGE. Send POST request to /compare with a multipart/form-data body containing two+ SBOM files.
     * The first SBOM will be the baseline, and the rest will be compared to it.
     * The API will respond with an HTTP 200 and a serialized DiffReport object.
     *
     * @param boms List of files to compare
     * @return Wrapped Comparison object
     */
    @RequestMapping(value="compare", method=RequestMethod.POST)
    public ResponseEntity<Comparison> compare(@RequestBody List<MultipartFile> boms) throws IOException {
        // Convert the SBOMs to SBOM objects
        ArrayList<SBOM> sboms = new ArrayList<>();

        for (MultipartFile file: boms) {
            // Get contents of the file
            String contents = new String(file.getBytes(), StandardCharsets.UTF_8);

            sboms.add(TranslatorPlugFest.translateContents(contents, file.getOriginalFilename()));
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
     * USAGE. Send POST request to /qa with a single sbom file in plain text in the body.
     * The API will respond with an HTTP 200 and a serialized report in the body.
     *
     * @param bom - SBOM to run metrics on
     * @return - wrapped QualityReport object
     */
    @RequestMapping(value="qa", method=RequestMethod.POST)
    public ResponseEntity<QualityReport> qa(@RequestBody MultipartFile bom) {
        // Get file contents into a string
        String contents;
        try {
            contents = new String(bom.getBytes(), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        SBOM sbom = TranslatorPlugFest.translateContents(contents, bom.getOriginalFilename());

        //run the QA
        QualityReport report = pipeline.process(sbom);

        //encode and send report
        try {
            return new ResponseEntity<>(report, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /** TODO Docstring
     * USAGE. Send POST request to /generator with
     */
    @RequestMapping(value="generator", method=RequestMethod.POST)
    public ResponseEntity<QualityReport> generator() {
        return null;
    }
}
