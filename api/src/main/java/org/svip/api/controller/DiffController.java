package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.svip.api.entities.SBOMFile;
import org.svip.api.services.DiffReportFileService;
import org.svip.api.services.SBOMFileService;
import org.svip.api.utils.Utils;
import org.svip.compare.DiffReport;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.Deserializer;

import java.util.Optional;

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
    private final DiffReportFileService diffReportFileService;

    /**
     * Create new Controller with services
     *
     * @param sbomService Service for handling SBOM queries
     * @param diffReportFileService Service for handling QA queries
     */
    public DiffController(SBOMFileService sbomService, DiffReportFileService diffReportFileService){
        this.sbomFileService = sbomService;
        this.diffReportFileService = diffReportFileService;
    }


    /**
     * USAGE. Compares two or more given SBOMs (split into filename and contents), with the first one used as the baseline, and returns a comparison report.
     *
     * @param targetIndex the index of the target SBOM
     * @param ids         the ids of the SBOM files
     * @return generated diff report
     * @throws JsonProcessingException
     */
    @PostMapping("/sboms/compare")
    public ResponseEntity<DiffReport> compare(@RequestParam("targetIndex") int targetIndex, @RequestBody Long[] ids) throws JsonProcessingException {
//        // Get Target SBOM
//        Optional<SBOMFile> sbomFile = sbomFileRepository.findById(ids[targetIndex]);
//        // Check if it exists
//        ResponseEntity<Long> NOT_FOUND = Utils.checkIfExists(ids[targetIndex], sbomFile, "/sboms/compare");
//        if (NOT_FOUND != null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        // create the Target SBOM object using the deserializer
//        Deserializer d = SerializerFactory.createDeserializer(sbomFile.get().getContents());
//        SBOM targetSBOM = d.readFromString(sbomFile.get().getContents());
//        // create diff report
//        DiffReport diffReport = new DiffReport(targetSBOM.getUID(), targetSBOM);
//        // comparison sboms
//        for (int i = 0; i < ids.length; i++) {
//            if (i == targetIndex) continue;
//            // Get SBOM
//            sbomFile = sbomFileRepository.findById(ids[i]);
//            // Check if it exists
//            NOT_FOUND = Utils.checkIfExists(ids[i], sbomFile, "/sboms/compare");
//            if (NOT_FOUND != null)
//                continue; // sbom not found, continue to next ID TODO check with front end what to do if 1 sbom is missing
//            // create an SBOM object using the deserializer
//            d = SerializerFactory.createDeserializer(sbomFile.get().getContents());
//            SBOM sbom = d.readFromString(sbomFile.get().getContents());
//            // add the comparison to diff report
//            diffReport.compare(sbom.getUID(), sbom);
//        }
//        return Utils.encodeResponse(diffReport);
        return null;
    }
}
