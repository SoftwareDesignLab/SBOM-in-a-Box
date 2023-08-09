package org.svip.api.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.svip.api.entities.diff.ComparisonFile;
import org.svip.api.requests.UploadComparisonFileInput;
import org.svip.api.services.ComparisonFileService;
import org.svip.api.services.SBOMFileService;
import org.svip.compare.Comparison;
import org.svip.compare.DiffReport;
import org.svip.sbom.model.interfaces.generics.SBOM;

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
    private final ComparisonFileService comparisonFileService;

    /**
     * Create new Controller with services
     *
     * @param sbomService Service for handling SBOM queries
     * @param comparisonFileService Service for handling QA queries
     */
    public DiffController(SBOMFileService sbomService, ComparisonFileService comparisonFileService){
        this.sbomFileService = sbomService;
        this.comparisonFileService = comparisonFileService;
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

            // Get target
            long targetID = ids[targetIndex];
            org.svip.api.entities.SBOM targetSBOMFile = this.sbomFileService.getSBOMFile(targetID);

            if(targetSBOMFile == null)
                return null;

            SBOM targetSBOM = targetSBOMFile.toSBOMObject();

            // create diff report
            DiffReport diffReport = new DiffReport(targetSBOM.getUID(), targetSBOM);

             // Compare against all other ids
            for(Long id : ids){

                // don't compare against self
                if(targetID == id)
                    continue;

                org.svip.api.entities.SBOM otherSBOMFile = this.sbomFileService.getSBOMFile(id);
                // skip if failed to parse
                if(otherSBOMFile == null)
                    continue;

                ComparisonFile cf = this.comparisonFileService.getComparisonFile(targetSBOMFile, otherSBOMFile);
                // todo make method?
                if(cf == null){

                    SBOM otherSBOM = otherSBOMFile.toSBOMObject();

                    Comparison comparison = new Comparison(targetSBOM, otherSBOM);
                    cf = new UploadComparisonFileInput(comparison).toQualityReportFile(targetSBOMFile, otherSBOMFile);
                    this.comparisonFileService.upload(cf);
                }
                diffReport.addComparison(id.toString(), cf.toComparison());
            }


            // Configure object mapper to remove null and empty arrays
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

            return new ResponseEntity<>(mapper.writeValueAsString(diffReport), HttpStatus.OK);      // track status?


            // get target
//            long targetID = ids[targetIndex];
//            SBOM targetSBOM = this.sbomFileService.getSBOMObject(targetID);
//
//            // create diff report
//            DiffReport diffReport = new DiffReport(targetSBOM.getUID(), targetSBOM);
//
//            // Compare against all other ids
//            for(Long id : ids){
//                // don't compare against self
//                if(targetID == id)
//                    continue;
//                // todo handle bad sbom but not break?
//                diffReport.compare(id.toString(), this.sbomFileService.getSBOMObject(id));
//            }
//
//            // Configure object mapper to remove null and empty arrays
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
//
//            return new ResponseEntity<>(mapper.writeValueAsString(diffReport), HttpStatus.OK);


        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
