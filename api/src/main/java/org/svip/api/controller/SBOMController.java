package org.svip.api.controller;

import org.svip.api.services.SBOMService;

/**
 * @author Derek Garcia
 **/

public class SBOMController {

    private final SBOMService sbomService;

    public SBOMController(SBOMService sbomService){
        this.sbomService = sbomService;
    }

}
