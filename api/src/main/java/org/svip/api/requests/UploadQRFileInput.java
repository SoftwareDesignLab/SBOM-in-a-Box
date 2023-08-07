package org.svip.api.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.api.entities.QualityReportFile;
import org.svip.metrics.pipelines.QualityReport;

/**
 * Input request to create a new QA FIle
 *
 * @author Derek Garcia
 **/
public record UploadQRFileInput(QualityReport qa) {

    /**
     * Create a new Quality Report File Object
     * @return SBOM File
     * @throws JsonProcessingException Failed to parse SBOM and is invalid
     */
    public QualityReportFile toQualityReportFile() throws JsonProcessingException {
        QualityReportFile qaf = new QualityReportFile();

        // Configure object mapper to remove null and empty arrays
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        // Set attributes
        qaf.setName(qa.getUid()).setContent(mapper.writeValueAsString(qa));

        return qaf;
    }
}
