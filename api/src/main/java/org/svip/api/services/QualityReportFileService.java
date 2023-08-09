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
    private QualityReportFile upload(QualityReportFile qaf) throws Exception {
        try{
            // todo relation logic for sbom?
            return this.qualityReportFileRepository.save(qaf);
        } catch (Exception e){
            // todo custom exception instead of generic
            throw new Exception("Failed to upload to Database: " + e.getMessage());
        }
    }

    /**
     * Save a new Quality Report
     *
     * @param sfs SBOMFileService to use to update SBOM
     * @param sbomFile SBOM File qa was generated for
     * @param qaf QA file associated with the SBOM
     * @return ID of qaf
     */
    public Long saveQualityReport(SBOMFileService sfs, org.svip.api.entities.SBOM sbomFile, QualityReportFile qaf) throws Exception {

        // Upload qaf
        upload(qaf);

        // Set and update SBOM File
        sbomFile.setQualityReport(qaf);
        sfs.upload(sbomFile);

        return qaf.getID();
    }

    /**
     * Delete Quality Report from repo
     *
     * @param qaf QualityReport to delete
     * @return ID of removed Quality Report File if it exists
     */
    public Long deleteQualityReportFile(QualityReportFile qaf) {
        this.qualityReportFileRepository.delete(qaf);
        // Else return file
        return qaf.getID();
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
}
