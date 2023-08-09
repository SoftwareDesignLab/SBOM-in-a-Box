package org.svip.api.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.svip.api.entities.QualityReportFile;
import org.svip.api.entities.SBOM;
import org.svip.api.repository.SBOMRepository;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.Deserializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * file: SBOMFileService.java
 * Business logic for accessing the SBOM File table
 *
 * @author Derek Garcia
 **/
@Service
public class SBOMFileService {
    private final SBOMRepository sbomRepository;

    /**
     * Create new Service for a target repository
     *
     * @param sbomRepository SBOM repository to access
     */
    public SBOMFileService(SBOMRepository sbomRepository){
        this.sbomRepository = sbomRepository;
    }


    /**
     * Create a new sbom entry in the database
     *
     * @param sbom sbom to upload
     * @return uploaded sbom entry
     * @throws Exception Error uploading to the Database
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
     * Set a qa association for a given SBOM
     *
     * @param id id of the SBOM File
     * @param qaf QA file associated with the SBOM
     * @return ID of qaf
     */
    public Long setQualityReport(Long id, QualityReportFile qaf){
        SBOM sbom = getSBOMFile(id);

        // todo better return than null?
        if(sbom == null)
            return null;

        // Set and update SBOM File
        sbom.setQualityReport(qaf);
        this.sbomRepository.save(sbom);

        return qaf.getID();
    }


    /**
     * Set a qa association for a given SBOM
     *
     * @param id id of the SBOM File
     * @param qaf QA file associated with the SBOM
     * @return ID of qaf
     */
    public Long setQualityReport(Long id, QualityReportFile qaf){
        SBOM sbom = getSBOMFile(id);

        // todo better return than null?
        if(sbom == null)
            return null;

        // Set and update SBOM File
        sbom.setQualityReport(qaf);
        this.sbomRepository.save(sbom);

        return qaf.getID();
    }


    /**
     * Set a qa association for a given SBOM
     *
     * @param id id of the SBOM File
     * @param qaf QA file associated with the SBOM
     * @return ID of qaf
     */
    public Long setQualityReport(Long id, QualityReportFile qaf){
        SBOM sbom = getSBOMFile(id);

        // todo better return than null?
        if(sbom == null)
            return null;

        // Set and update SBOM File
        sbom.setQualityReport(qaf);
        this.sbomRepository.save(sbom);

        return qaf.getID();
    }


    /**
     * Retrieve SBOM File from the database as an JSON String
     *
     * @param id of the SBOM to retrieve
     * @return deserialized SBOM Object
     * @throws JsonProcessingException SBOM failed to be deserialized
     */
    public String getSBOMObjectAsJSON(Long id) throws JsonProcessingException {
        // Retrieve SBOM Object and check that it exists
        SBOM sbom = getSBOMFile(id);
        if(sbom == null)
            return null;

        // Configure object mapper to remove null and empty arrays
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        // Return JSON String
        return mapper.writeValueAsString(sbom.toSBOMObject());
    }

    /**
     * Get SBOM file from database
     *
     * @param id of the SBOM to retrieve
     * @return SBOMFile if it exists
     */
    public SBOM getSBOMFile(Long id) {
        // Retrieve SBOM File and check that it exists
        Optional<SBOM> sbomFile = this.sbomRepository.findById(id);
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


    /**
     * Delete a target SBOM File from the database
     *
     * @param id of the SBOM to delete
     * @return id of deleted SBOM on success
     */
    public Long deleteSBOMFile(Long id){
        // Retrieve SBOM File and check that it exists
        SBOM sbomFile = getSBOMFile(id);
        if (sbomFile == null)
            return null;

        // Delete from repository
        this.sbomRepository.delete(sbomFile);

        // return confirmation id
        return id;
    }
}
