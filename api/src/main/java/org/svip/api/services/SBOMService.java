package org.svip.api.services;

import org.springframework.stereotype.Service;
import org.svip.api.repository.SBOMRepository;

/**
 * @author Derek Garcia
 **/
@Service
public class SBOMService {
    private final SBOMRepository sbomRepository;

    public SBOMService(SBOMRepository sbomRepository){
        this.sbomRepository = sbomRepository;
    }

    // todo business logic using the repository
}
