package org.svip.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.svip.api.services.SBOMService;

/**
 * REST API Controller for Managing SBOM and SBOM operations
 *
 * @author Derek Garcia
 **/
@RestController
@RequestMapping("/svip")
public class SBOMController {

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
     * USAGE. Send GET request to /sboms.
     * The API will respond with an HTTP 200 and a JSON array of all IDs of currently uploaded SBOM files.
     *
     * @return A JSON array of IDs of all currently uploaded SBOM files.
     */
    @GetMapping("/sboms")
    public ResponseEntity<Long[]> getAllIds(){

        // Get all ids
        Long[] ids = this.sbomService.getAllIDs();

        // report nothing if no SBOMs in the database
        if(ids.length == 0)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        // Else return the array of stored IDs
        return new ResponseEntity<>(ids, HttpStatus.OK);
    }

}
