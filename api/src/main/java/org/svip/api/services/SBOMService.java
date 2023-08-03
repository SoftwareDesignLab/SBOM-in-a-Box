package org.svip.api.services;

import org.springframework.stereotype.Service;
import org.svip.api.repository.SBOMRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Derek Garcia
 **/
@Service
public class SBOMService {
    private final SBOMRepository sbomRepository;

    public SBOMService(SBOMRepository sbomRepository){
        this.sbomRepository = sbomRepository;
    }


    public Long[] getAllIDs(){
        List<Long> sbomIDs = new ArrayList<>();

        // Get all sboms and add their ID to the ID list
        this.sbomRepository.findAll().forEach( sbom -> sbomIDs.add(sbom.getId()) );

        // convert to long array
        return sbomIDs.toArray(new Long[0]);
    }
}
