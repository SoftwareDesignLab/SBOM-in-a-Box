package org.svip.api.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.svip.api.entities.SBOM;
import org.svip.api.entities.SBOMFile;
import org.svip.api.repository.SBOMRepository;
import org.svip.api.utils.Utils;
import org.svip.conversion.Conversion;
import org.svip.sbom.builder.SBOMBuilderException;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.Deserializer;
import org.svip.serializers.exceptions.DeserializerException;
import org.svip.serializers.exceptions.SerializerException;
import org.svip.serializers.serializer.Serializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Business logic for accessing the SBOM File table
 *
 * @author Derek Garcia
 **/
@Service
public class SBOMFileService {
    private final SBOMRepository sbomRepository;
    private static final SVIPSBOMBuilder builder = new SVIPSBOMBuilder();


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
     * Convert an SBOM to different Schema and/or FileType
     *
     * @param id        of the SBOM
     * @param schema    to convert to
     * @param format    to convert to
     * @param overwrite whether to overwrite original
     * @return ID of converted SBOM
     */
    public Long convert(Long id, SerializerFactory.Schema schema, SerializerFactory.Format format, Boolean overwrite)
            throws DeserializerException, SBOMBuilderException, JsonProcessingException {

        // Retrieve SBOMFile and check that it exists
        SBOM sbomFile = getSBOMFile(id);
        if (sbomFile == null)
            return null;

        SerializerFactory.Schema originalSchema = (sbomFile.getSchema() == SBOM.Schema.SPDX_23) ? // original schema of SBOM
                SerializerFactory.Schema.SPDX23 : SerializerFactory.Schema.CDX14;

        // deserialize into SBOM object
        Deserializer d;
        org.svip.sbom.model.interfaces.generics.SBOM deserialized;
        try {
            d = SerializerFactory.createDeserializer(sbomFile.getContent());
            deserialized = d.readFromString(sbomFile.getContent());
        } catch (Exception e) {
            throw new DeserializerException("Deserialization Error: " + e.getMessage());
        }
        if (deserialized == null) throw new DeserializerException("Deserialization Error: Deserializer is null");


        // use core Conversion functionality
        org.svip.sbom.model.interfaces.generics.SBOM Converted =
                Conversion.convertSBOM(deserialized, SerializerFactory.Schema.SVIP, originalSchema);


        // serialize into desired format
        Serializer s = SerializerFactory.createSerializer(schema, format, true);
        s.setPrettyPrinting(true);
        String contents = s.writeToString((SVIPSBOM) Converted);

        // Save according to overwrite boolean
        SBOM converted = new SBOM().
                setName(sbomFile.getName()).
                setContent(contents).
                setSchema((schema == SerializerFactory.Schema.SPDX23) ? // original schema of SBOM
                        SBOM.Schema.SPDX_23: SBOM.Schema.CYCLONEDX_14).
                setFileType((format == SerializerFactory.Format.TAGVALUE) ? // original schema of SBOM
                        SBOM.FileType.TAG_VALUE : SBOM.FileType.JSON);

        converted.id = Utils.generateSBOMFileId(); // todo temporary fix?

        if(SerializerFactory.resolveSchema(contents) != schema || SerializerFactory.resolveFormat(contents) != format)
            return null; // extra assertion outside of unit tests

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
}
