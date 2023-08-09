package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.svip.api.entities.QualityReportFile;
import org.svip.api.entities.SBOMFile;
import org.svip.api.entities.VEXFile;
import org.svip.api.requests.UploadQRFileInput;
import org.svip.api.requests.UploadVEXFileInput;
import org.svip.api.services.SBOMFileService;
import org.svip.api.services.VEXFileService;
import org.svip.metrics.pipelines.QualityReport;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.Deserializer;
import org.svip.vex.VEXResult;
import org.svip.vex.database.NVDClient;
import org.svip.vex.database.OSVClient;
import org.svip.vex.database.interfaces.VulnerabilityDBClient;
import org.svip.vex.model.VEX;
import org.svip.vex.model.VEXType;
import org.svip.vex.vexstatement.VEXStatement;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * file: VEXController.java
 * REST API Controller for generating VEX
 *
 * @author Derek Garcia
 **/
@RestController
@RequestMapping("/svip")
public class VEXController {

    /**
     * Spring-configured logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(VEXController.class);


    private final SBOMFileService sbomFileService;
    private final VEXFileService vexFileService;


    /**
     * Create new Controller with services
     *
     * @param sbomFileService Service for handling SBOM queries
     * @param vexFileService Service for handling VEX queries
     */
    public VEXController(SBOMFileService sbomFileService,VEXFileService vexFileService){
        this.sbomFileService = sbomFileService;
        this.vexFileService = vexFileService;
    }

    /**
     * USAGE Send GET request to /vex to generate a VEX Document for an SBOM
     * The API will respond with an HTTP 200 a VEX object, and a hashmap of
     * and errors that occurred
     *
     * @param id     The id of the SBOM contents to retrieve.
     * @param format the format of teh VEX Document
     * @param client the api client to use (currently NVD or OSV)
     * @return A new VEXResult of the VEX document and any errors that occurred
     */
    @GetMapping("/sboms/vex")
    public ResponseEntity<String> vex(@RequestHeader(value = "apiKey", required = false) String apiKey,
                                         @RequestParam("id") Long id,
                                         @RequestParam("format") String format,
                                         @RequestParam("client") String client) {



try{
            org.svip.api.entities.SBOM sbomFile = this.sbomFileService.getSBOMFile(id);

            // No SBOM was found
            if(sbomFile == null){
                LOGGER.info("VEX /svip/sboms/vex?id=" + id + " - FILE NOT FOUND");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            // Get stored content
            // todo more than 1 vex stored? Ie 1 from nvd, could run for osv
            // todo POST / arg to force rerun vex??
            if(sbomFile.getVEXFile() != null)
                return new ResponseEntity<>(sbomFile.getVEXFile().getContent(), HttpStatus.OK);

            // No VEX stored, generate one
            VEXResult vexResult = this.vexFileService.generateVEX(sbomFile.toSBOMObject(), client, format, apiKey);

            // todo better way to get datasource
            VEXFile.Database datasource = (client.equalsIgnoreCase("osv") ? VEXFile.Database.OSV : VEXFile.Database.NVD);

            // Create vexFile and upload to db
            VEXFile vf = new UploadVEXFileInput(vexResult).toVEXFile(sbomFile, datasource);
            this.vexFileService.upload(vf);   // update sbom relation

            // Log
            LOGGER.info("VEX /svip/sboms/vex?id=" + id);

            // Return JSON result
            return new ResponseEntity<>(vf.getContent(), HttpStatus.OK);

        } catch (JsonProcessingException e ){
            // error with Deserialization
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // error with QA
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
