package org.svip.api.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.svip.api.entities.SBOM;
import org.svip.api.entities.SBOMFile;
import org.svip.api.repository.SBOMRepository;
import org.svip.api.requests.UploadSBOMFileInput;
import org.svip.conversion.Conversion;
import org.svip.conversion.ConversionException;
import org.svip.merge.MergerController;
import org.svip.merge.MergerException;
import org.svip.sbom.builder.SBOMBuilderException;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.exceptions.DeserializerException;
import org.svip.serializers.exceptions.SerializerException;
import org.svip.serializers.serializer.Serializer;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.svip.api.controller.SBOMController.LOGGER;


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
     * Convert an SBOM to different Schema and/or FileType
     *
     * @param id        of the SBOM
     * @param schema    to convert to
     * @param format    to convert to
     * @param overwrite whether to overwrite original
     * @return ID of converted SBOM
     */
    public Long convert(Long id, SerializerFactory.Schema schema, SerializerFactory.Format format, Boolean overwrite)
            throws DeserializerException, JsonProcessingException, SerializerException, SBOMBuilderException, ConversionException {

        // deserialize into SBOM object
        org.svip.sbom.model.interfaces.generics.SBOM deserialized;
        try {
            deserialized = getSBOMFile(id).toSBOMObject();
        } catch (Exception e) {
            throw new DeserializerException("Deserialization Error: " + e.getMessage());
        }
        if (deserialized == null)
            throw new DeserializerException("Cannot retrieve SBOM with id " + id + " to deserialize");

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

        // prefix to error messages
        String urlMsg = "MERGE /svip/merge?id=";

        // ensure there are at least two SBOMs to potentially merge
        if (ids.length < 2)
            return -2L; // bad request

        // collect and deserialize SBOMs
        ArrayList<org.svip.sbom.model.interfaces.generics.SBOM> sboms = new ArrayList<>();

        for (Long id : ids
        ) {

            org.svip.sbom.model.interfaces.generics.SBOM sbomObj;
            try {
                sbomObj = getSBOMFile(id).toSBOMObject();
            } catch (JsonProcessingException e) {
                LOGGER.info(urlMsg + id + "DURING DESERIALIZATION: " +
                        e.getMessage());
                return null; // internal server error
            }

            if (sbomObj == null)
                return -1L; // not found // todo custom exception

            // convert to SVIPSBOM
            try {
                sbomObj = Conversion.convertSBOM(sbomObj, SerializerFactory.Schema.SVIP,
                        (sbomObj.getFormat().toLowerCase().contains("spdx")) ?
                                SerializerFactory.Schema.SPDX23 : SerializerFactory.Schema.CDX14);
            } catch (ConversionException e) {
                LOGGER.info(urlMsg + id + "DURING CONVERSION TO SVIP: " +
                        e.getMessage());
                return null; // internal server error
            }

            sboms.add(sbomObj);

        }

        // merge
        org.svip.sbom.model.interfaces.generics.SBOM merged;
        try {
            MergerController mergerController = new MergerController();
            merged = mergerController.mergeAll(sboms);
        } catch (MergerException e) {
            String error = "Error merging SBOMs: " + e.getMessage();
            LOGGER.error(urlMsg + " " + error);
            return null; // internal server error
        }

        SerializerFactory.Schema schema = SerializerFactory.Schema.SPDX23;

        // serialize merged SBOM
        Serializer s = SerializerFactory.createSerializer(schema, SerializerFactory.Format.TAGVALUE, // todo default to SPDX JSON for now?
                true);
        s.setPrettyPrinting(true);
        String contents;
        try {
            contents = s.writeToString((SVIPSBOM) merged);
        } catch (JsonProcessingException | ClassCastException e) {
            String error = "Error deserializing merged SBOM: " + e.getMessage();
            LOGGER.error(urlMsg + " " + error);
            return null; // internal server error
        }

        // save to db
        Random rand = new Random();
        String newName = ((merged.getName() == null || merged.getName().isEmpty()) ? Math.abs(rand.nextInt()) :
                merged.getName()) + "." + schema.getName();

        UploadSBOMFileInput u = new UploadSBOMFileInput(newName, contents);
        SBOM mergedSBOMFile;
        try {
            mergedSBOMFile = u.toSBOMFile();
        } catch (JsonProcessingException e) {
            String error = "Error: " + e.getMessage();
            LOGGER.error(urlMsg + " " + error);
            return null; // internal server error
        }
        this.sbomRepository.save(mergedSBOMFile);
        return mergedSBOMFile.getId();
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
        org.svip.sbom.model.interfaces.generics.SBOM sbom = getSBOMFile(id).toSBOMObject();
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
        return sbomFile.orElse(null);

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
     * @param sbomFile SBOM file to delete
     * @return id of deleted SBOM on success
     */
    public Long deleteSBOMFile(SBOM sbomFile) {

        // Delete from repository
        this.sbomRepository.delete(sbomFile);

        // return confirmation id
        return sbomFile.getId();
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

    //
    // ZIP FILE UTILITIES
    //

    /**
     * Unzip a ZipFile of SBOMFiles
     *
     * @param z the zipped file
     * @return List of file contents paired with an integer representing its depth in the project directory
     */
    public static ArrayList<HashMap<SBOMFile, Integer>> unZip(ZipFile z) {

        ArrayList<HashMap<SBOMFile, Integer>> vpArray = new ArrayList<>();

        byte[] buffer = new byte[1024];
        Stream<? extends ZipEntry> entryStream = z.stream();

        entryStream.forEach(entry -> {
            try {
                // Get the input stream for the current zip entry
                InputStream is = z.getInputStream(entry);
                int depth = entry.getName().split("[\\/]").length - 1; // todo we may not actually need depth

                if (!entry.isDirectory()) {
                    StringBuilder contentsBuilder = new StringBuilder();
                    int len;
                    try {
                        while ((len = is.read(buffer)) > 0) {
                            contentsBuilder.append(new String(buffer));
                        }
                    } catch (EOFException e) {
                        is.close();
                        LOGGER.error(e.getMessage());
                    }

                    HashMap<SBOMFile, Integer> hashMap = new HashMap<>();
                    hashMap.put(new SBOMFile(entry.getName(), contentsBuilder.toString()), depth);
                    vpArray.add(hashMap);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return vpArray;

    }

    /**
     * Convert a MultiPart file to a ZipFile
     *
     * @param file MultiPart file, a .zip file
     * @return Converted ZipFile object
     */
    public static ZipFile convertMultipartToZip(MultipartFile file) throws IOException {

        File zip = File.createTempFile(UUID.randomUUID().toString(), "temp");
        FileOutputStream o = new FileOutputStream(zip);
        IOUtils.copy(file.getInputStream(), o);
        o.close();

        return new ZipFile(zip);

    }
}

