package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.svip.api.services.DiffService;
import org.svip.api.services.SBOMFileService;

/**
 * File: DiffController.java
 * REST API Controller for generating Diff Reports
 *
 * @author Derek Garcia
 **/
@RestController
@RequestMapping("/svip")
public class DiffController {

    /**
     * Spring-configured logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiffController.class);

    private final SBOMFileService sbomFileService;
    private final DiffService diffService;

    /**
     * Create new Controller with services
     *
     * @param sbomService Service for handling SBOM queries
     * @param diffService Service for handling QA queries
     */
    public DiffController(SBOMFileService sbomService, DiffService diffService){
        this.sbomFileService = sbomService;
        this.diffService = diffService;
    }


    /**
     * USAGE. Compares two or more given SBOMs (split into filename and contents), with the first one used as the baseline, and returns a comparison report.
     *
     * @param targetIndex the index of the target SBOM
     * @param ids         the ids of the SBOM files
     * @return generated diff report
     * @throws JsonProcessingException Error processing the report
     */
    @PostMapping("/sboms/compare")
    public ResponseEntity<String> compare(@RequestParam("targetIndex") int targetIndex, @RequestBody Long[] ids) throws JsonProcessingException {

        try{

            String diffReport = this.diffService.generateDiffReportAsJSON(this.sbomFileService, ids[targetIndex], ids);
            return new ResponseEntity<>(diffReport, HttpStatus.OK);      // track status?

        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
