package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.svip.api.services.SBOMFileService;
import org.svip.repair.fix.Fix;

import java.util.List;
import java.util.Map;

/**
 * REST API Controller for managing SBOM repair operations
 *
 * @author Juan Francisco Patino
 **/
public class RepairController {

    /**
     * Spring-configured logger
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(SBOMController.class);

    private final SBOMFileService sbomService;

    /**
     * Create new Controller with services
     *
     * @param sbomService              Service for handling SBOM queries
     */
    public RepairController(SBOMFileService sbomService) {
        this.sbomService = sbomService;
    }


    ///
    /// POST
    ///

    /**
     * USAGE. Send POST request to /sboms/repair/statements with a URL parameter id to get repair statement.
     * @param id id of SBOM to repair, if needed
     * @return map of repair fixes as a repair statement
     */
    @PostMapping("/sboms/repair/statement")
    public ResponseEntity<?> repairStatement(Long id){ // todo just change to a list of fixes?

        Map<String, Map<String, List<Fix<?>>>> repairStatement;
        try{
            repairStatement = sbomService.getRepairStatement(id);
        }
        catch(JsonProcessingException e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(repairStatement == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(repairStatement, HttpStatus.OK);

    }


}
