package org.svip.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import org.svip.api.repository.DiffReportFileRepository;
import org.svip.compare.DiffReport;
import org.svip.sbom.model.interfaces.generics.SBOM;

/**
 * File: DiffReportFileService.java
 * Business logic for accessing the Diff Report File table
 *
 * @author Derek Garcia
 **/
@Service
public class DiffReportFileService {

    private final DiffReportFileRepository diffReportFileRepository;

    /**
     * Create new Service for a target repository
     *
     * @param diffReportFileRepository Diff report repository to access
     */
    public DiffReportFileService(DiffReportFileRepository diffReportFileRepository){
        this.diffReportFileRepository = diffReportFileRepository;
    }


    public DiffReport compare(SBOM target, SBOM other) throws JsonProcessingException {
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
