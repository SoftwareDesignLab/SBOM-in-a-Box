package org.svip.api.services;

import org.springframework.stereotype.Service;
import org.svip.api.entities.QualityReportFile;
import org.svip.api.repository.QualityReportFileRepository;
import org.svip.metrics.pipelines.QualityReport;
import org.svip.metrics.pipelines.interfaces.generics.QAPipeline;
import org.svip.metrics.pipelines.schemas.CycloneDX14.CDX14Pipeline;
import org.svip.metrics.pipelines.schemas.SPDX23.SPDX23Pipeline;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;

import java.util.Optional;

/**
 * file: QualityReportFileService.java
 * Business logic for accessing the Quality Report File table
 *
 * @author Derek Garcia
 **/
@Service
public class QualityReportFileService {

    private final QualityReportFileRepository qualityReportFileRepository;

    /**
     * Create new Service for a target repository
     *
     * @param qualityReportFileRepository QA repository to access
     */
    public QualityReportFileService(QualityReportFileRepository qualityReportFileRepository){
        this.qualityReportFileRepository = qualityReportFileRepository;
    }


    /**
     * Create a new quality report entry in the database
     *
     * @param qaf Quality Report to upload
     * @return uploaded Quality Report entry
     * @throws Exception Error uploading to the Database
     */
    public QualityReportFile upload(QualityReportFile qaf) throws Exception {
        try{
            // todo relation logic for sbom?
            return this.qualityReportFileRepository.save(qaf);
        } catch (Exception e){
            // todo custom exception instead of generic
            throw new Exception("Failed to upload to Database: " + e.getMessage());
        }
    }


    /**
     * Generate a Quality Report for a given SBOM
     *
     * @param sbom SBOM to run QA on
     * @return Quality Report
     * @throws Exception SBOM not supported for metrics
     */
    public QualityReport generateQualityReport(SBOM sbom) throws Exception {

        // todo QAPipeline factory to handle this? similar to serializerFactory?
        QAPipeline qaPipeline;
        // Determine what QA Pipeline to use based on
        if (sbom instanceof CDX14SBOM) {
            qaPipeline = new CDX14Pipeline();
        } else if (sbom instanceof SPDX23SBOM) {
            qaPipeline = new SPDX23Pipeline();
        } else {
            // todo custom exception for QAPipeline, not the service
            throw new Exception("Metrics not supported for " + sbom.getClass().getSimpleName() + " sboms");
        }

        // QA test SBOM and return result
        return qaPipeline.process(sbom.getUID(), sbom);
    }


    /**
     * Delete Quality Report from repo
     *
     * @param id of the QA to delete
     * @return ID of removed Quality Report File if it exists
     */
    public Long deleteQualityReportFile(Long id) {
        // Retrieve QA File and check that it exists
        Optional<QualityReportFile> qaf = this.qualityReportFileRepository.findById(id);
        // todo throw error?
        if (qaf.isEmpty())
            return null;

        this.qualityReportFileRepository.delete(qaf.get());
        // Else return file
        return qaf.get().getID();
    }
}
