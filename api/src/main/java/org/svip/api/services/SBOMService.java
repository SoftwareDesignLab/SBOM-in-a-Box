package org.svip.api.services;

import org.springframework.stereotype.Service;
import org.svip.api.entities.SBOM;
import org.svip.api.repository.SBOMRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Business logic for accessing the SBOM table
 *
 * @author Derek Garcia
 **/
@Service
public class SBOMService {
    private final SBOMRepository sbomRepository;

    /**
     * Create new Service for a target repository
     *
     * @param sbomRepository SBOM repository to access
     */
    public SBOMService(SBOMRepository sbomRepository){
        this.sbomRepository = sbomRepository;
    }


    /**
     * Create a new sbom entry in the database
     *
     * @param sbom sbom to upload
     * @return uploaded sbom entry
     */
    public SBOM upload(SBOM sbom) throws Exception {
        try{
            return this.sbomRepository.save(sbom);
        } catch (Exception e){
            // todo custom exception instead of generic
            throw new Exception("Failed to upload to Database: " + e.getMessage());
        }
    }

    /**
     * Get all the IDs of the store SBOMs in the database
     *
     * @return list of sbom IDs
     */
    public Long[] getAllIDs(){
        List<Long> sbomIDs = new ArrayList<>();

        // Get all sboms and add their ID to the ID list
        this.sbomRepository.findAll().forEach( sbom -> sbomIDs.add(sbom.getId()) );

        // convert to long array
        return sbomIDs.toArray(new Long[0]);
    }
}
