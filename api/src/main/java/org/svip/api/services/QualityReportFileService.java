/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

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
        return qaPipeline.process(sbom);
    }
}
