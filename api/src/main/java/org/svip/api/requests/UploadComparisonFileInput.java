package org.svip.api.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.api.entities.QualityReportFile;
import org.svip.api.entities.SBOM;
import org.svip.api.entities.diff.ComparisonFile;
import org.svip.compare.Comparison;
import org.svip.metrics.pipelines.QualityReport;

/**
 * file: UploadComparisonFileInput.java
 * Input request to create a new Comparison File
 *
 * @author Derek Garcia
 **/
public record UploadComparisonFileInput(Comparison qa) {

    /**
     * Create a new Comparison File Object
     *
     * @param targetSBOM Target SBOM for comparison
     * @return ComparisonFile
     */
    public ComparisonFile toQualityReportFile(SBOM targetSBOM) throws JsonProcessingException {
        ComparisonFile qf = new ComparisonFile();
        // todo requests to convert missing and conflicts in the Comparison
        qf.setSBOM(targetSBOM);
        return qf;
    }
}
