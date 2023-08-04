package org.svip.api.services;

import org.springframework.stereotype.Service;
import org.svip.api.repository.QualityReportFileRepository;
import org.svip.api.repository.SBOMRepository;

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
}
