package org.svip.api.services;

import org.springframework.stereotype.Service;
import org.svip.api.entities.diff.ComparisonFile;
import org.svip.api.entities.diff.ConflictFile;
import org.svip.api.repository.ComparisonFileRepository;
import org.svip.api.repository.ConflictFileRepository;
import org.svip.api.requests.UploadComparisonFileInput;
import org.svip.compare.Comparison;
import org.svip.compare.DiffReport;

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
            // todo better way to handle this?
            // upload all conflict files
            for(ConflictFile conflictFile : cf.getConflicts())
                uploadConflictFile(conflictFile);

            return this.comparisonFileRepository.save(cf);
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

            ComparisonFile cf = this.comparisonFileRepository.findByTargetSBOMAndOtherSBOM(targetSBOMFile, otherSBOMFile);

            // todo make method?
            if (cf == null) {

                org.svip.sbom.model.interfaces.generics.SBOM otherSBOM = otherSBOMFile.toSBOMObject();

                Comparison comparison = new Comparison(targetSBOM, otherSBOM);
                cf = new UploadComparisonFileInput(comparison).toComparisonFile(targetSBOMFile, otherSBOMFile);
                uploadComparisonFile(cf);
            }
            diffReport.addComparison(id.toString(), cf.toComparison());
        }
        return diffReport;
    }
}
