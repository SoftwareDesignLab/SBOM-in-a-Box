package org.svip.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.svip.api.entities.SBOM;
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
     * @throws Exception If there was a problem merging.
     */
    public Long merge(Long[] ids) throws Exception {

        // prefix to error messages
        String urlMsg = "MERGE /svip/merge?id=";

        // ensure there are at least two SBOMs to potentially merge
        if (ids.length < 2)
            throw new Exception("Not enough SBOMs provided to merge (must be at least 2)");

        // collect and deserialize SBOMs
        ArrayList<org.svip.sbom.model.interfaces.generics.SBOM> sboms = new ArrayList<>();

        for (Long id : ids
        ) {

            org.svip.sbom.model.interfaces.generics.SBOM sbomObj;
            try {
                sbomObj = getSBOMFile(id).toSBOMObject();
            } catch (JsonProcessingException e) {
                throw new Exception("Error deserializing SBOM (id " + id + ": " + e.getMessage());
            }

            if (sbomObj == null)
                throw new Exception("Converted SBOM not found.");

            // convert to SVIPSBOM
            try {
                sbomObj = Conversion.convertSBOM(sbomObj, SerializerFactory.Schema.SVIP,
                        (sbomObj.getFormat().toLowerCase().contains("spdx")) ?
                                SerializerFactory.Schema.SPDX23 : SerializerFactory.Schema.CDX14);
            } catch (ConversionException e) {
                throw new Exception("Error converting to SVIP SBOM (id " + id + ": " + e.getMessage());
            }

            sboms.add(sbomObj);

        }

        // merge
        org.svip.sbom.model.interfaces.generics.SBOM merged;
        try {
            MergerController mergerController = new MergerController();
            merged = mergerController.mergeAll(sboms);
        } catch (MergerException e) {
            throw new Exception("Error merging SBOMs: " + e.getMessage());
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
            throw new Exception("Error deserializing merged SBOM: " + e.getMessage());
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
            throw new Exception("Error: " + e.getMessage());
        }
        this.sbomRepository.save(mergedSBOMFile);
        return mergedSBOMFile.getId();
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
     * Unzip a MultipartFile of project files
     *
     * @param multipartFile the MultipartFile to unzip
     * @return Map of a filename to its contents
     */
    public static Map<String, String> unZip(MultipartFile multipartFile) throws IOException {
        Map<String, String> fileMap = new HashMap<>(); // Map of name to contents to return

        // Convert multipart file to zip file
        File zip = File.createTempFile(UUID.randomUUID().toString(), "temp");
        FileOutputStream o = new FileOutputStream(zip);
        IOUtils.copy(multipartFile.getInputStream(), o);
        o.close();

        ZipFile zipFile = new ZipFile(zip);

        // Create stream for each entry in the zip file
        byte[] buffer = new byte[1024];
        Stream<? extends ZipEntry> entryStream = zipFile.stream();

        // Read each zip file entry
        entryStream.forEach(entry -> {
            try {
                // Get the input stream for the current zip entry
                InputStream is = zipFile.getInputStream(entry);

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

                    // If valid name and contents then add to map
                    if (!entry.getName().isEmpty() && !contentsBuilder.toString().isEmpty())
                        fileMap.put(entry.getName(), contentsBuilder.toString());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return fileMap;
    }
}

