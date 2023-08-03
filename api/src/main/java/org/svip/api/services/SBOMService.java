package org.svip.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.svip.api.entities.SBOM;
import org.svip.api.entities.SBOMFile;
import org.svip.api.repository.SBOMRepository;
import org.svip.api.utils.Utils;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.Deserializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public org.svip.sbom.model.interfaces.generics.SBOM getSBOMObject(Long id) throws JsonProcessingException {
        SBOM sbomFile = getSBOMFile(id);

        if(sbomFile == null)
            return null;

        Deserializer d = SerializerFactory.createDeserializer(sbomFile.getContent());
        return d.readFromString(sbomFile.getContent());
    }


    /**
     * Get SBOM file from database
     *
     * @param id ID of the SBOM to query
     * @return SBOMFile if it exists
     */
    public SBOM getSBOMFile(Long id) {
        // Get SBOM
        Optional<SBOM> sbomFile = this.sbomRepository.findById(id);

        // No SBOM with the given ID
        if (sbomFile.isEmpty())
            return null;

        // Else return file
        return sbomFile.get();
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
