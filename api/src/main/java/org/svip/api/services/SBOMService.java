package org.svip.api.services;

import org.svip.api.repository.SBOMRepository;

/**
 * @author Derek Garcia
 **/

public class SBOMService {
    private final SBOMRepository sbomRepository;

    public SBOMService(SBOMRepository sbomRepository){
        this.sbomRepository = sbomRepository;
    }

    // todo business logic using the repository
}
