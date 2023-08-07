package org.svip.api.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.svip.api.entities.QualityReportFile;
import org.svip.api.entities.SBOM;
import org.svip.api.entities.SBOMFile;
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
     * Get SBOM file from database
     *
     * @param id of the SBOM to retrieve
     * @return SBOMFile if it exists
     */
    public SBOM getSBOMFile(Long id) {
        // Retrieve SBOM File and check that it exists

        Optional<SBOM> sbomFile = this.sbomRepository.findById(id);

        try{
            SBOM try_ = sbomFile.get();
        }
        catch (ClassCastException e){ // TODO this is a temporary fix

            Object tmp = this.sbomRepository.findById(id).get();
            SBOMFile oldSbomFile = (SBOMFile) tmp;
            sbomFile = Optional.of(getSbom(oldSbomFile));
            sbomFile.get().id = oldSbomFile.getId();

        }

        return sbomFile.orElse(null);

    }

    private static SBOM getSbom(SBOMFile oldSbomFile) {
        SBOM sbom = new SBOM();

        sbom.setName(oldSbomFile.getFileName());
        sbom.setContent(oldSbomFile.getContents());

        SBOM.Schema schema = sbom.getName().endsWith(".spdx") && sbom.getContent().toLowerCase().contains("spdx") ?
                SBOM.Schema.SPDX_23 : SBOM.Schema.CYCLONEDX_14;

        sbom.setSchema(schema);

        SBOM.FileType fileType = schema == SBOM.Schema.CYCLONEDX_14 && !sbom.getContent().toLowerCase().contains("spdx") ?
                SBOM.FileType.JSON : SBOM.FileType.TAG_VALUE;

        sbom.setFileType(fileType);
        return sbom;
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
     * @param sbomFile SBOM file to delete
     * @return id of deleted SBOM on success
     */
    public Long deleteSBOMFile(SBOM sbomFile){

        // Delete from repository
        this.sbomRepository.delete(sbomFile);

        // return confirmation id
        return sbomFile.getId();
    }
}
