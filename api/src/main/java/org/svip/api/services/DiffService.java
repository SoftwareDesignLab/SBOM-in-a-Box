package org.svip.api.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.svip.api.entities.SBOM;
import org.svip.api.entities.diff.ComparisonFile;
import org.svip.api.entities.diff.ConflictFile;
import org.svip.api.repository.ComparisonFileRepository;
import org.svip.api.repository.ConflictFileRepository;
import org.svip.api.requests.diff.UploadComparisonFileInput;
import org.svip.compare.Comparison;
import org.svip.compare.conflicts.MismatchType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File: DiffFileService.java
 * Business logic for accessing the Diff Reports
 *
 * @author Derek Garcia
 **/
@Service
public class DiffService {

    // Utility to hold JSON Formatted Diff Report
    @JsonPropertyOrder({"target", "diffreport"})
    private record DiffReport(Long target, Map<Long, ComparisonJSON> diffReport){
    }

    // Utility to hold JSON Formatted comparison
    private record ComparisonJSON(Map<String, List<ConflictFile>> componentConflicts, List<String> missingComponents){
    }

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


    private ComparisonFile upload(ComparisonFile cf) throws Exception {
        try {
            this.comparisonFileRepository.save(cf);
            // upload conflicts
            for(ConflictFile c : cf.getConflicts())
                uploadConflictFile(c);

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


    public String generateDiffReportAsJSON(SBOMFileService sfs, long targetID, Long[] otherIDs) throws Exception {

        SBOM targetSBOMFile = sfs.getSBOMFile(targetID);

        // todo throw error
        if(targetSBOMFile == null)
            return null;

        org.svip.sbom.model.interfaces.generics.SBOM targetSBOM = targetSBOMFile.toSBOMObject();

        // create diff report
        Map<Long, ComparisonJSON> comparisons = new HashMap<>();
        // Compare against all other ids
        for (Long id : otherIDs) {

            // don't compare against self
            if (targetID == id)
                continue;

            org.svip.api.entities.SBOM otherSBOMFile = sfs.getSBOMFile(id);
            // skip if failed to parse
            if (otherSBOMFile == null)
                continue;

            ComparisonFile cf = this.comparisonFileRepository.findByTargetSBOMAndOtherSBOM(targetSBOMFile, otherSBOMFile);
            if(cf == null){
                Comparison comparison = new Comparison(targetSBOM, otherSBOMFile.toSBOMObject());
                upload(new UploadComparisonFileInput(comparison).toComparisonFile(targetSBOMFile, otherSBOMFile));
            }

            // Sort component conflicts
            Map<String, List<ConflictFile>> componentConflicts = new HashMap<>();
            List<String> missingComponents = new ArrayList<>();
            assert cf != null;
            for(ConflictFile c : cf.getConflicts()){
                // Add to missing if missing
                if(c.getMismatchType() == MismatchType.MISSING_COMPONENT){
                    missingComponents.add(c.getName());
                    continue;
                }
                // Add to conflicts otherwise
                componentConflicts.computeIfAbsent(c.getName(), k -> new ArrayList<>());
                componentConflicts.get(c.getName()).add(c);
            }

            comparisons.put(id, new ComparisonJSON(componentConflicts, missingComponents));
        }


        // Configure object mapper to remove null and empty arrays
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        return mapper.writeValueAsString(new DiffReport(targetID, comparisons));
    }
}
