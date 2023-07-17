package org.svip.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.svip.api.model.SBOMFile;
import org.svip.api.utils.Utils;

import org.svip.sbom.model.old.SBOM;
import org.svip.sbomanalysis.old.DiffReport;
import org.svip.sbomanalysis.qualityattributes.oldpipeline.QAPipeline;
import org.svip.sbomanalysis.qualityattributes.oldpipeline.QualityReport;
import org.svip.sbomanalysis.qualityattributes.processors.*;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.translators.TranslatorController;
import org.svip.sbomfactory.translators.TranslatorException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * File: APIController.java
 * REST API Controller for SBOM Comparison and QA
 *
 * @author Juan Francisco Patino
 * @author Asa Horn
 * @author Justin Jantzi
 * @author Derek Garcia
 * @author Ian Dunn
 */
@RestController
@RequestMapping("/plugfest")
public class PlugFestApiController {

    /**
     * USAGE. Send POST request to /compare with a collection of SBOM Json objects and a selected target
     *
     * @param targetIndex index of the target SBOM
     * @param sboms collection of SBOMs to compare
     * @return Wrapped Comparison object or error message
     */
    @PostMapping("/compare")
    public ResponseEntity<?> compare(@RequestParam("targetIndex") Integer targetIndex, @RequestBody SBOMFile[] sboms) throws TranslatorException {
        // null/empty sboms check
        int nullCheck = Utils.sbomFileArrNullCheck(sboms);
        if(nullCheck > -1)
            return new ResponseEntity<>("Invalid SBOM at index " + nullCheck + ".",
                    HttpStatus.BAD_REQUEST);

        if (sboms.length < 2) return new ResponseEntity<>("SBOM array must contain at least 2 elements to compare.",
                HttpStatus.BAD_REQUEST);

        if (targetIndex < 0 || targetIndex > sboms.length - 1) return new ResponseEntity<>("Target Index out of " +
                "bounds (must be between 0 and " + (sboms.length - 1) + ", was " + targetIndex + ").", HttpStatus.BAD_REQUEST);

        // Attempt to load comparison queue
        List<SBOM> compareQueue = new ArrayList<>();
        for (SBOMFile sbom : sboms){
//            try {
                compareQueue.add(TranslatorController.translateContents(sbom.getContents(), sbom.getFileName()));
//            } catch (TranslatorException e){
//                return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR); // todo uncomment once translators are moved
//            }
        }
        // Get target from queue
        SBOM targetSBOM = compareQueue.get(targetIndex);

        // Run comparison
        DiffReport dr = new DiffReport(sboms[targetIndex].getFileName(), targetSBOM);

        // Compare against all sboms in the queue
        for(int i = 0; i < compareQueue.size(); i++){
            // skip target
            if(targetSBOM.equals(compareQueue.get(i)))
                continue;
            dr.compare(sboms[i].getFileName(), compareQueue.get(i));
        }


        //encode and send report
        return Utils.encodeResponse(dr);
    }

    /**
     * USAGE. Send POST request to /qa with a single sbom file
     * The API will respond with an HTTP 200 and a serialized report in the body.
     *
     * @param servletRequest
     * @param sbomFile JSON object of sbom details
     * @return - wrapped QualityReport object, null if failed
     */
    @PostMapping("/qa")
    public ResponseEntity<?> qa(HttpServletRequest servletRequest, @RequestBody SBOMFile sbomFile) throws TranslatorException {
        try {
            servletRequest.setCharacterEncoding("UTF-8");
        }
        catch (Exception e) {
            // This will not happen as we are hardcoding UTF-8
            Debug.log(Debug.LOG_TYPE.ERROR, "Failed to set encoding");
        }

        SBOM sbom;

//        try {
            sbom = TranslatorController.translateContents(sbomFile.getContents(), sbomFile.getFileName());
//        } catch (TranslatorException e) {
//            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR); // todo uncomment once translators are moved
//        }

        // todo get tests/processors from user that they want to run?
        Set<AttributeProcessor> processors = new HashSet<>();
        processors.add(new CompletenessProcessor());
        processors.add(new UniquenessProcessor());
        processors.add(new RegisteredProcessor());
        processors.add(new LicensingProcessor());   // Add origin specific processors

        // Add CDX processor if relevant
        if(sbom.getOriginFormat() == SBOM.Type.CYCLONE_DX)
            processors.add(new CDXMetricsProcessor());

        // Add SPDX Processor if relevant
        if(sbom.getOriginFormat() == SBOM.Type.SPDX)
            processors.add(new SPDXMetricsProcessor());

        //run the QA
        QualityReport report = QAPipeline.process(sbomFile.getFileName(), sbom, processors);

        //encode and send report
        return Utils.encodeResponse(report);
    }

    /**
     * Send post request to /parse and it will convert the file contents to an SBOM object, returns null if failed to parse
     *
     * @param sbomFile JSON object of sbom details
     * @return SBOM object, null if failed to parse
     */
    @PostMapping("/parse")
    public ResponseEntity<?> parse(@RequestBody SBOMFile sbomFile) throws TranslatorException {
        SBOM sbom;

//        try {
            sbom = TranslatorController.translateContents(sbomFile.getContents(), sbomFile.getFileName());
//        } catch (TranslatorException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // TODO better status code? // todo uncomment once translators are moved
//        }

        return Utils.encodeResponse(sbom);
    }
}

