package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.svip.api.entities.SBOM;
import org.svip.api.entities.SBOMFile;
import org.svip.api.requests.UploadSBOMFileInput;
import org.svip.api.services.SBOMFileService;
import org.svip.sbom.builder.SBOMBuilderException;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.exceptions.DeserializerException;
import org.svip.serializers.exceptions.SerializerException;

import java.util.Optional;

/**
 * REST API Controller for managing SBOM and SBOM operations
 *
 * @author Derek Garcia
 **/
@RestController
@RequestMapping("/svip")
public class SBOMController {

    /**
     * Spring-configured logger
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(SBOMController.class);

    private final SBOMFileService sbomService;

    /**
     * Create new Controller with services
     *
     * @param sbomService Service for handling SBOM queries
     */
    public SBOMController(SBOMFileService sbomService) {
        this.sbomService = sbomService;
    }


    ///
    /// POST
    ///

    /**
     * USAGE. Send POST request to /sboms with one SBOM Input data.
     * <p>
     * The API will respond with an HTTP 200 and the ID used to identify the SBOM file.
     *
     * @param uploadSBOMInput Input required to create a new SBOM instance from a request
     * @return The ID of the new SBOM
     */
    @PostMapping("/sboms")
    public ResponseEntity<Long> upload(@RequestBody UploadSBOMFileInput uploadSBOMInput) {

        try {
            // Attempt to upload input
            SBOM sbom = uploadSBOMInput.toSBOMFile();
            // Attempt to deserialize
            // todo move this to service
            Deserializer d = SerializerFactory.createDeserializer(sbom.getContent());
            d.readFromString(sbom.getContent());

            // If reach here, SBOM is valid, set additional fields
            sbom.setSchema(d)
                .setFileType(d);

            // Upload File
            this.sbomService.upload(sbom);

            // Log
            LOGGER.info("POST /svip/sboms - Uploaded SBOM with ID " + sbom.getId() + ": " + sbom.getName());

            // Return ID
            return new ResponseEntity<>(sbom.getId(), HttpStatus.OK);

        } catch (IllegalArgumentException | JsonProcessingException e) {
            // Problem with parsing
            LOGGER.error("POST /svip/sboms - " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Problem with uploading
            LOGGER.error("POST /svip/sboms - " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Merge two or more SBOMs
     * todo remove old SBOMs from DB?
     *
     * @param ids list of IDs to merge
     * @return ID of merged SBOM
     */
    @PostMapping("/sboms/merge")
    public ResponseEntity<Long> merge(@RequestBody Long[] ids){

        Long mergeID = this.sbomService.merge(ids);
        // todo convert should probably throw errors instead of returning null if error occurs
        if (mergeID == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if(mergeID == -1)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else if(mergeID < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // Return final merged ID
        return new ResponseEntity<>(mergeID, HttpStatus.OK);
    }


    ///
    /// PUT
    ///


    /**
     * USAGE. Send PUT request to /sboms an existing SBOM on the backend to a desired schema and format
     *
     * @param id        of the SBOM
     * @param schema    to convert to
     * @param format    to convert to
     * @param overwrite whether to overwrite original
     * @return ID of converted SBOM
     */
    @PutMapping("/sboms")
    public ResponseEntity<?> convert(@RequestParam("id") Long id, @RequestParam("schema") SerializerFactory.Schema schema,
                                     @RequestParam("format") SerializerFactory.Format format,
                                     @RequestParam("overwrite") Boolean overwrite) {
        Long convertID;
        try {
            convertID = this.sbomService.convert(id, schema, format, overwrite);
        } catch (DeserializerException | SBOMBuilderException | SerializerException | JsonProcessingException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Return converted ID
        return new ResponseEntity<>(convertID, HttpStatus.OK);
    }


    ///
    /// GET
    ///

    /**
     * USAGE. Send GET request to /sboms with a URL parameter id to get the deserialized SBOM.
     * <p>
     * The API will respond with an HTTP 200 and the SBOM object json
     *
     * @param id The id of the SBOM contents to retrieve.
     * @return A deserialized SBOM Object in JSON form
     */
    @GetMapping("/sbom")
    public ResponseEntity<String> getSBOMObjectAsJSON(@RequestParam("id") Long id) {

        try {
            String sbom = this.sbomService.getSBOMObjectAsJSON(id);

            // No SBOM was found
            if (sbom == null)
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            // Else return the object
            return new ResponseEntity<>(sbom, HttpStatus.OK);

        } catch (JsonProcessingException e) {
            // error with Deserialization
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * USAGE. Send GET request to /sboms/content with a URL parameter id to get the contents of the SBOM with the specified ID.
     * <p>
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
            LOGGER.warn("GET /svip/sboms/content?id=" + id + " - FILE NOT FOUND");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Log
        LOGGER.info("GET /svip/sboms/content?id=" + id + " - File: " + sbomFile.getName());

        return new ResponseEntity<>(sbomFile, HttpStatus.OK);
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
    public ResponseEntity<org.svip.sbom.model.interfaces.generics.SBOM> getSBOMObject(@RequestParam("id") Long id){

        try{
            org.svip.sbom.model.interfaces.generics.SBOM sbom = this.sbomService.getSBOMObject(id);

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
    public ResponseEntity<Long[]> getAllIds() {

        // Get all ids
        Long[] ids = this.sbomService.getAllIDs();

        // Log
        LOGGER.info("GET /svip/sboms - Found " + ids.length + " sbom" + (ids.length == 0 ? "." : "s."));

        // report nothing if no SBOMs in the database
        if (ids.length == 0)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        // Else return the array of stored IDs
        return new ResponseEntity<>(ids, HttpStatus.OK);
    }


    ///
    /// DELETE
    ///

    /**
     * USAGE. Send DELETE request to /delete with a URL parameter id to get the contents of the SBOM with the specified
     * ID.
     * <p>
     * The API will respond with an HTTP 200 and the ID of the deleted SBOM file (if found).
     *
     * @param id The id of the SBOM contents to retrieve.
     * @return The ID of the deleted file.
     */
    @DeleteMapping("/sboms")
    public ResponseEntity<Long> delete(@RequestParam("id") Long id) {

        // Attempt to delete id
        if (this.sbomService.deleteSBOMFile(id) == null) {
            LOGGER.warn("DELETE /svip/sboms?id=" + id + " - FILE NOT FOUND");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Log
        LOGGER.info("DELETE /svip/sboms?id=" + id);

        // Return deleted ID as confirmation
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

}
