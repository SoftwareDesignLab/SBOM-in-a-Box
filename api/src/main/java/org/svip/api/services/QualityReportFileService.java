package org.svip.api.services;

import org.springframework.stereotype.Service;
import org.svip.api.entities.QualityReportFile;
import org.svip.api.entities.SBOM;
import org.svip.api.repository.QualityReportFileRepository;

/**
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
     * @param qaf sbom to upload
     * @return uploaded sbom entry
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
}
