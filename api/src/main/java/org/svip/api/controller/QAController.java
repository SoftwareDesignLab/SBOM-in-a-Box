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
import org.svip.api.entities.SBOMFile;
import org.svip.api.services.QualityReportFileService;
import org.svip.api.services.SBOMFileService;
import org.svip.metrics.pipelines.QualityReport;
import org.svip.metrics.pipelines.interfaces.generics.QAPipeline;
import org.svip.metrics.pipelines.schemas.CycloneDX14.CDX14Pipeline;
import org.svip.metrics.pipelines.schemas.SPDX23.SPDX23Pipeline;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.Deserializer;

import java.io.IOException;
import java.util.Optional;

/**
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
    private static final Logger LOGGER = LoggerFactory.getLogger(SBOMController.class);

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
//    @GetMapping("/sboms/qa")
//    public ResponseEntity<String> qa(@RequestParam("id") long id) throws IOException {
//
//        SBOM sbom;
//        Deserializer d;
//        QAPipeline qaPipeline;
//
//        // Get the SBOM to be tested
//        Optional<SBOMFile> sbomFile = sbomFileRepository.findById(id);
//
//        // Check if it exists
//        if (sbomFile.isEmpty()) {
//            LOGGER.info("QA /svip/sboms/qa?id=" + id + " - FILE NOT FOUND");
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        // Deserialize SBOM into JSON Object
//        try {
//            d = SerializerFactory.createDeserializer(sbomFile.get().getContents());
//            sbom = d.readFromString(sbomFile.get().getContents());
//        } catch (JsonProcessingException e) {
//            return new ResponseEntity<>("Failed to deserialize SBOM content, may be an unsupported format", HttpStatus.INTERNAL_SERVER_ERROR);
//        } catch (Exception e) {
//            return new ResponseEntity<>("Deserialization Error", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        // Determine what QA Pipeline to use based on
//        if (sbom instanceof CDX14SBOM) {
//            qaPipeline = new CDX14Pipeline();
//        } else if (sbom instanceof SPDX23SBOM) {
//            qaPipeline = new SPDX23Pipeline();
//        } else {
//            return new ResponseEntity<>("Deserialization Error", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        // QA test SBOM
//        QualityReport qualityReport = qaPipeline.process(String.valueOf(id), sbom);
//
//        // Log
//        LOGGER.info("QA /svip/sboms/?id=" + id + " - TESTED: " + sbomFile.get().getFileName());
//
//        // Return Quality Report as JSON to Frontend
//        ObjectMapper mapper = new ObjectMapper();
//        return new ResponseEntity<>(mapper.writeValueAsString(qualityReport), HttpStatus.OK);
//    }

}
