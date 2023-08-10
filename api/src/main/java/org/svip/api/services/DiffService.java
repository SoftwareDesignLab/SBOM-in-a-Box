package org.svip.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import org.svip.api.entities.SBOM;
import org.svip.api.entities.diff.ComparisonFile;
import org.svip.api.entities.diff.ConflictFile;
import org.svip.api.repository.ComparisonFileRepository;
import org.svip.api.repository.ConflictFileRepository;
import org.svip.api.requests.UploadComparisonFileInput;
import org.svip.api.requests.UploadConflictFileInput;
import org.svip.compare.Comparison;
import org.svip.compare.DiffReport;
import org.svip.compare.conflicts.Conflict;

/**
 * File: DiffFileService.java
 * Business logic for accessing the Diff Reports
 *
 * @author Derek Garcia
 **/
@Service
public class DiffService {

    private final ComparisonFileRepository comparisonFileRepository;
    private final ConflictFileRepository conflictFileRepository;

    /**
     * Create new Service for a target repository
     *
     * @param comparisonFileRepository comparison repository to access
     * @param conflictFileRepository conflict reporsitory to access
     */
    public DiffService(ComparisonFileRepository comparisonFileRepository, ConflictFileRepository conflictFileRepository){
        this.comparisonFileRepository = comparisonFileRepository;
        this.conflictFileRepository = conflictFileRepository;
    }


    private ComparisonFile uploadComparisonFile(ComparisonFile cf) throws Exception {
        try {
            this.comparisonFileRepository.save(cf);
            // upload conflicts
            for(ConflictFile c : cf.getConflicts())
                uploadConflictFile(c);
            // todo missing?
            return cf;
        } catch (Exception e) {
            // todo custom exception instead of generic
            throw new Exception("Failed to upload to Database: " + e.getMessage());
        }
    }

    private ConflictFile uploadConflictFile(ConflictFile cf) throws Exception {
        try {
            return this.conflictFileRepository.save(cf);
        } catch (Exception e) {
            // todo custom exception instead of generic
            throw new Exception("Failed to upload to Database: " + e.getMessage());
        }
    }

    private ComparisonFile uploadNewComparison(SBOM targetSBOMFile, SBOM otherSBOMFile) throws Exception {

        Comparison comparison = new Comparison(targetSBOMFile.toSBOMObject(), otherSBOMFile.toSBOMObject());

        // upload new comparison
        ComparisonFile cf = new UploadComparisonFileInput(comparison).toComparisonFile(targetSBOMFile, otherSBOMFile);
        uploadComparisonFile(cf);

        return cf;
    }


    public DiffReport generateDiffReport(SBOMFileService sfs, long targetID, Long[] ids) throws Exception {

        // Get target
        org.svip.api.entities.SBOM targetSBOMFile = sfs.getSBOMFile(targetID);

        // todo throw error
        if (targetSBOMFile == null)
            return null;

        org.svip.sbom.model.interfaces.generics.SBOM targetSBOM = targetSBOMFile.toSBOMObject();

        // create diff report
        DiffReport diffReport = new DiffReport(targetSBOM.getUID(), targetSBOM);

        // Compare against all other ids
        for (Long id : ids) {

            // don't compare against self
            if (targetID == id)
                continue;

            org.svip.api.entities.SBOM otherSBOMFile = sfs.getSBOMFile(id);
            // skip if failed to parse
            if (otherSBOMFile == null)
                continue;

//            ComparisonFile cf = this.comparisonFileRepository.findByTargetSBOMAndOtherSBOM(targetSBOMFile, otherSBOMFile);
            ComparisonFile cf = uploadNewComparison(targetSBOMFile, otherSBOMFile);
            // todo make method?
//            if (cf == null) {
//
//            }
            diffReport.addComparison(id.toString(), cf.toComparison());
        }
        return diffReport;
    }
}
