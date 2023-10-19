package org.svip.api.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.api.entities.QualityReportFile;
import org.svip.api.entities.SBOMFile;
import org.svip.metrics.pipelines.QualityReport;

/**
 * file: UploadQRFileInput.java
 * Input request to create a new QA FIle
 *
 * @author Derek Garcia
 **/
public record UploadQRFileInput(QualityReport qa) {

    /**
     * Create a new Quality Report File Object
     *
     * @param sbomFile SBOM file of the qa was run on
     * @return QualityReportFile
     * @throws JsonProcessingException Failed to parse qa and is invalid
     */
    public QualityReportFile toQualityReportFile(SBOMFile sbomFile) throws JsonProcessingException {
        QualityReportFile qaf = new QualityReportFile();

        // Configure object mapper to remove null and empty arrays
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        // Set attributes
        qaf.setName(qa.getUid())
           .setContent(mapper.writeValueAsString(qa))
           .setSBOMFile(sbomFile);      // adds relationship

        // add to sbom
        sbomFile.setQualityReport(qaf);

        return qaf;
    }
}
