package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.svip.api.entities.QualityReportFile;
import org.svip.api.entities.SBOM;
import org.svip.api.entities.SBOMFile;
import org.svip.api.requests.UploadQRFileInput;
import org.svip.api.services.QualityReportFileService;
import org.svip.api.services.SBOMFileService;
import org.svip.metrics.pipelines.QualityReport;
import org.svip.metrics.pipelines.interfaces.generics.QAPipeline;
import org.svip.metrics.pipelines.schemas.CycloneDX14.CDX14Pipeline;
import org.svip.metrics.pipelines.schemas.SPDX23.SPDX23Pipeline;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.Deserializer;

import java.io.IOException;
import java.util.Optional;

/**
 * file: QAController.java
 * REST API Controller for generating Quality Reports
 *
 * @author Derek Garcia
 **/
@RestController
@RequestMapping("/svip")
public class QAController {

    /**
     * Spring-configured logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(QAController.class);

    private final SBOMFileService sbomService;
    private final QualityReportFileService qualityReportFileService;

    /**
     * Create new Controller with services
     *
     * @param sbomService Service for handling SBOM queries
     * @param qualityReportFileService Service for handling QA queries
     */
    public QAController(SBOMFileService sbomService, QualityReportFileService qualityReportFileService){
        this.sbomService = sbomService;
        this.qualityReportFileService = qualityReportFileService;
    }


    /**
     * USAGE Send GET request to /qa with a URL parameter id to conduct a quality assessment on the SBOM with
     * the specified ID.
     * <p>
     * The API will respond with an HTTP 200 and a JSON string of the Quality Report (if SBOM was found).
     *
     * @param id The id of the SBOM contents to retrieve.
     * @return A JSON string of the Quality Report file.
     */
    @GetMapping("/sboms/qa")
    public ResponseEntity<String> qa(@RequestParam("id") Long id) {
        try{
            SBOM sbom = this.sbomService.getSBOMObject(id);

            // No SBOM was found
            if(sbom == null)
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            /*
            qa.generateQAFile(SBOM) -> qaFile (has all data and parent sbom id)
            qa.upload -> (SBOM, QA)?

             */
            return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

        } catch (JsonProcessingException e ){
            // error with Deserialization
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
        try{
            SBOM sbomFile = this.sbomService.getSBOMFile(id);

            // No SBOM was found
            if(sbomFile == null){
                LOGGER.info("QA /svip/sboms/qa?id=" + id + " - FILE NOT FOUND");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            // Get stored content
            // todo POST / arg to force rerun qa?
            if(sbomFile.getQualityReportFile() != null)
                return new ResponseEntity<>(sbomFile.getQualityReportFile().getContent(), HttpStatus.OK);

            // No QA stored, generate instead
            org.svip.sbom.model.interfaces.generics.SBOM sbomObject = this.sbomService.getSBOMObject(id);
            QualityReport qa = this.qualityReportFileService.generateQualityReport(sbomObject);

            // Create qaf and upload to db
            QualityReportFile qaf = new UploadQRFileInput(qa).toQualityReportFile(sbomFile);
            this.qualityReportFileService.upload(qaf);
            this.sbomService.setQualityReport(id, qaf);     // update sbom relation

            // Log
            LOGGER.info("QA /svip/sboms/?id=" + id);

            // Return JSON result
            return new ResponseEntity<>(qaf.getContent(), HttpStatus.OK);

        } catch (JsonProcessingException e ){
            // error with Deserialization
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // error with QA
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
