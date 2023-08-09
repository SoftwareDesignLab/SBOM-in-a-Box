package org.svip.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import org.svip.api.entities.QualityReportFile;
import org.svip.api.entities.SBOM;
import org.svip.api.entities.diff.ComparisonFile;
import org.svip.api.repository.ComparisonFileRepository;
import org.svip.api.requests.UploadComparisonFileInput;
import org.svip.compare.Comparison;
import org.svip.compare.DiffReport;

import java.util.Optional;

/**
 * File: ComparisonFileService.java
 * Business logic for accessing the Diff Reports
 *
 * @author Derek Garcia
 **/
@Service
public class ComparisonFileService {

    private final ComparisonFileRepository comparisonFileRepository;

    /**
     * Create new Service for a target repository
     *
     * @param comparisonFileRepository Diff report repository to access
     */
    public ComparisonFileService(ComparisonFileRepository comparisonFileRepository){
        this.comparisonFileRepository = comparisonFileRepository;
    }

    public ComparisonFile upload(ComparisonFile cf) throws Exception {
        try {
            // todo relation logic for sbom?
            return this.comparisonFileRepository.save(cf);
        } catch (Exception e) {
            // todo custom exception instead of generic
            throw new Exception("Failed to upload to Database: " + e.getMessage());
        }
    }

    public Long saveComparison(SBOMFileService sfs, ComparisonFile cf) throws Exception {
        // Upload cf
        upload(cf);

        // Save SBOMss
        sfs.upload(cf.getTargetSBOM());
        sfs.upload(cf.getOtherSBOM());

        return cf.getID();
    }


    public ComparisonFile getComparisonFile(SBOM targetID, SBOM otherID){
        // Retrieve Comparison File and check that it exists
        return this.comparisonFileRepository.findByTargetSBOMAndOtherSBOM(targetID, otherID);
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

            ComparisonFile cf = getComparisonFile(targetSBOMFile, otherSBOMFile);
            // todo make method?
            if (cf == null) {

                org.svip.sbom.model.interfaces.generics.SBOM otherSBOM = otherSBOMFile.toSBOMObject();

                Comparison comparison = new Comparison(targetSBOM, otherSBOM);
                cf = new UploadComparisonFileInput(comparison).toQualityReportFile(targetSBOMFile, otherSBOMFile);
                saveComparison(sfs, cf);
            }
            diffReport.addComparison(id.toString(), cf.toComparison());
        }
        return diffReport;
    }
}
