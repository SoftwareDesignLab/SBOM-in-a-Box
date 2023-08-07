package org.svip.api.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.svip.api.entities.QualityReportFile;
import org.svip.api.entities.SBOM;
import org.svip.api.entities.SBOMFile;
import org.svip.api.repository.SBOMRepository;
import org.svip.api.requests.UploadSBOMFileInput;
import org.svip.conversion.Conversion;
import org.svip.sbom.builder.SBOMBuilderException;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.Deserializer;
import org.svip.serializers.exceptions.DeserializerException;
import org.svip.serializers.exceptions.SerializerException;
import org.svip.serializers.serializer.Serializer;

import java.util.*;

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
    public SBOMFileService(SBOMRepository sbomRepository) {
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
        try {
            return this.sbomRepository.save(sbom);
        } catch (Exception e) {
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
     * Convert an SBOM to different Schema and/or FileType
     *
     * @param id        of the SBOM
     * @param schema    to convert to
     * @param format    to convert to
     * @param overwrite whether to overwrite original
     * @return ID of converted SBOM
     */
    public Long convert(Long id, SerializerFactory.Schema schema, SerializerFactory.Format format, Boolean overwrite)
            throws DeserializerException, JsonProcessingException, SerializerException, SBOMBuilderException {

        // deserialize into SBOM object
        org.svip.sbom.model.interfaces.generics.SBOM deserialized;
        try {
            deserialized = getSBOMObject(id);
        } catch (Exception e) {
            throw new DeserializerException("Deserialization Error: " + e.getMessage());
        }
        if (deserialized == null) throw new DeserializerException("Cannot retrieve SBOM with id " + id + " to deserialize");

        SBOM.Schema ogSchema = (deserialized instanceof SPDX23SBOM) ? SBOM.Schema.SPDX_23 : SBOM.Schema.CYCLONEDX_14;

        SerializerFactory.Schema originalSchema = (ogSchema == SBOM.Schema.SPDX_23) ? // original schema of SBOM
                SerializerFactory.Schema.SPDX23 : SerializerFactory.Schema.CDX14;

        // use core Conversion functionality
        org.svip.sbom.model.interfaces.generics.SBOM Converted =
                Conversion.convertSBOM(deserialized, SerializerFactory.Schema.SVIP, originalSchema);

        // serialize into desired format
        Serializer s = SerializerFactory.createSerializer(schema, format, true); // todo serializers don't adjust the format nor specversion
        s.setPrettyPrinting(true);
        String contents = s.writeToString((SVIPSBOM) Converted);
        SerializerFactory.Schema resolvedSchema = SerializerFactory.resolveSchema(contents);
        SerializerFactory.Format resolvedFormat = SerializerFactory.resolveFormat(contents);
        if (resolvedSchema != schema)
            throw new SerializerException("Serialized SBOM does not match schema=" + schema + " (" + resolvedSchema + ")");
        else if (resolvedFormat != format) {
            throw new SerializerException("Serialized SBOM does not match format=" + format + " (" + resolvedFormat + ")");
        }

        Random rand = new Random();
        String newName = ((deserialized.getName() == null) ? Math.abs(rand.nextInt()) : deserialized.getName()) + "." + schema;

        UploadSBOMFileInput u = new UploadSBOMFileInput(newName, contents);

        // Save according to overwrite boolean
        SBOM converted = u.toSBOMFile();

        if (overwrite) {
            update(id, converted);
            return id;
        }

        this.sbomRepository.save(converted);
        return converted.getId();

    }

    /**
     * Merge two or more SBOMs
     * todo remove old SBOMs from DB?
     *
     * @param ids list of IDs to merge
     * @return ID of merged SBOM
     */
    public Long merge(Long[] ids) {

      /*
        TODO MERGE LOGIC HERE

        */

        return null;
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
        org.svip.sbom.model.interfaces.generics.SBOM sbom = getSBOMObject(id);
        if (sbom == null)
            return null;

        // Configure object mapper to remove null and empty arrays
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        // Return JSON String
        return mapper.writeValueAsString(sbom);
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

        try {
            SBOM try_ = sbomFile.get();
        } catch (ClassCastException e) {

            Object tmp = this.sbomRepository.findById(id).get(); // todo remove after new unit tests are written
            SBOMFile oldSbomFile = (SBOMFile) tmp;
            sbomFile = Optional.of(getSbom(oldSbomFile));
            //sbomFile.get().id = oldSbomFile.getId(); // uncomment for (old) unit tests

        }

        return sbomFile.orElse(null);

    }

    /**
     * // todo temporary fix until new tests are written
     */
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
    public Long[] getAllIDs() {
        List<Long> sbomIDs = new ArrayList<>();

        // Get all sboms and add their ID to the ID list
        this.sbomRepository.findAll().forEach(sbom -> sbomIDs.add(sbom.getId()));

        // convert to long array
        return sbomIDs.toArray(new Long[0]);
    }


    /**
     * Delete a target SBOM File from the database
     *
     * @param id of the SBOM to delete
     * @return id of deleted SBOM on success
     */
    public Long deleteSBOMFile(Long id) {
        // Retrieve SBOM File and check that it exists
        SBOM sbomFile = getSBOMFile(id);
        if (sbomFile == null)
            return null;

        // Delete from repository
        this.sbomRepository.delete(sbomFile);

        // return confirmation id
        return id;
    }


    /**
     * Retrieve SBOM File from the database as an SBOM Object
     *
     * @param id of the SBOM to retrieve
     * @return deserialized SBOM Object
     * @throws JsonProcessingException SBOM failed to be deserialized
     */
    private org.svip.sbom.model.interfaces.generics.SBOM getSBOMObject(Long id) throws JsonProcessingException {
        // Retrieve SBOMFile and check that it exists
        SBOM sbomFile = getSBOMFile(id);
        if (sbomFile == null)
            return null;

        // Attempt to deserialize and return the object
        Deserializer d = SerializerFactory.createDeserializer(sbomFile.getContent());

        return d.readFromString(sbomFile.getContent());
    }


    /**
     * Update an entry in the database
     *
     * @param id    ID of SBOM File to update
     * @param patch Patch SBOM File with updated information
     * @return id of the updated SBOM
     */
    private Long update(Long id, SBOM patch) {
        // Retrieve SBOMFile and check that it exists
        SBOM sbomFile = getSBOMFile(id);
        if (sbomFile == null)
            return null;

        sbomFile.setName(patch.getName())
                .setContent(patch.getContent())
                .setSchema(patch.getSchema())
                .setFileType(patch.getFileType());

        this.sbomRepository.save(sbomFile);

        return sbomFile.getId();
    }


    /**
     * Generates new SBOMFile id
     */
    public static long generateSBOMFileId() {
        Random rand = new Random();
        long id = rand.nextLong();
        id += (rand.nextLong()) % ((id < 0) ? id : Long.MAX_VALUE);
        return Math.abs(id);
    }

}
