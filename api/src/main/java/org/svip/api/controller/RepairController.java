package org.svip.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svip.api.services.SBOMFileService;

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


}
