package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.svip.api.entities.SBOM;
import org.svip.api.requests.UploadSBOMInput;
import org.svip.api.services.SBOMService;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.Deserializer;

/**
 * REST API Controller for Managing SBOM and SBOM operations
 *
 * @author Derek Garcia
 **/
@RestController
@RequestMapping("/svip")
public class SBOMController {

    /**
     * Spring-configured logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SBOMController.class);

    private final SBOMService sbomService;

    /**
     * Create new Controller with services
     *
     * @param sbomService Service for handling SBOM queries
     */
    public SBOMController(SBOMService sbomService){
        this.sbomService = sbomService;
    }


    /**
     * USAGE. Send POST request to /sboms with one SBOM Input data.
     *
     * The API will respond with an HTTP 200 and the ID used to identify the SBOM file.
     *
     * @param uploadSBOMInput Input required to create a new SBOM instance from a request
     * @return The ID of the new SBOM
     */
    @PostMapping("/sboms")
    public ResponseEntity<Long> upload(@RequestBody UploadSBOMInput uploadSBOMInput) {
        SBOM sbom = uploadSBOMInput.toSBOM();

        // Validate SBOM with deserializers
        try {
            // Attempt to deserialize
            // todo move this to service
            Deserializer d = SerializerFactory.createDeserializer(sbom.getContent());
            d.readFromString(sbom.getContent());

            // If reach here, SBOM is valid, set additional fields
            sbom.setSchema(d)
                .setFileType(d);

            // Upload File
            this.sbomService.upload(sbom);

        } catch (IllegalArgumentException | JsonProcessingException e) {
            // Problem with parsing
            LOGGER.error("POST /svip/sboms - " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            // Problem with uploading
            LOGGER.error("POST /svip/sboms - " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Log
        LOGGER.info("POST /svip/sboms - Uploaded SBOM with ID " + sbom.getId() + ": " + sbom.getName());

        // Return ID
        return new ResponseEntity<>(sbom.getId(), HttpStatus.OK);
    }


    /**
     * USAGE. Send GET request to /sboms with a URL parameter id to get the deserialized SBOM.
     *
     * The API will respond with an HTTP 200 and the SBOM object json
     *
     * @param id The id of the SBOM contents to retrieve.
     * @return A deserialized SBOM Object
     */
    @GetMapping("/sbom")
    public ResponseEntity<String> getSBOMObjectAsJSON(@RequestParam("id") Long id){

        try{
            String sbom = this.sbomService.getSBOMObjectAsJSON(id);

            // No SBOM was found
            if(sbom == null)
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            // Else return the object
            return new ResponseEntity<>(sbom, HttpStatus.OK);

        } catch (JsonProcessingException e ){
            // error with Deserialization
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * USAGE. Send GET request to /sboms/content with a URL parameter id to get the contents of the SBOM with the specified ID.
     *
     * The API will respond with an HTTP 200 and the contents of the SBOM file.
     *
     * @param id The id of the SBOM contents to retrieve.
     * @return The contents of the SBOM file.
     */
    @GetMapping("/sboms/content")
    public ResponseEntity<SBOM> getContent(@RequestParam("id") Long id) {
        // todo rename endpoint? Returns more than just content
        // Get SBOM
        SBOM sbomFile = this.sbomService.getSBOMFile(id);

        // Return SBOM or invalid ID
        if (sbomFile == null) {
            LOGGER.info("GET /svip/sboms/content?id=" + id + " - FILE NOT FOUND");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Log
        LOGGER.info("GET /svip/sboms/content?id=" + id + " - File: " + sbomFile.getName());

        return new ResponseEntity<>(sbomFile, HttpStatus.OK);
    }


    /**
     * USAGE. Send GET request to /sboms.
     * The API will respond with an HTTP 200 and a JSON array of all IDs of currently uploaded SBOM files.
     *
     * @return A JSON array of IDs of all currently uploaded SBOM files.
     */
    @GetMapping("/sboms")
    public ResponseEntity<Long[]> getAllIds(){

        // Get all ids
        Long[] ids = this.sbomService.getAllIDs();

        // Log
        LOGGER.info("GET /svip/sboms - Found " + ids.length + " sbom" + (ids.length == 0 ? "." : "s."));

        // report nothing if no SBOMs in the database
        if(ids.length == 0)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        // Else return the array of stored IDs
        return new ResponseEntity<>(ids, HttpStatus.OK);
    }

}
