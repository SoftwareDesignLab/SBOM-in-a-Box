package org.svip.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.controller.SVIPApiController;
import org.svip.api.model.SBOMFile;
import org.svip.api.repository.SBOMFileRepository;
import org.svip.sbomgeneration.serializers.SerializerFactory;
import org.svip.utils.VirtualPath;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.svip.sbomgeneration.serializers.SerializerFactory.Format.TAGVALUE;

/**
 * A static class containing helpful utilities for API calls and testing responses.
 *
 * @author Ian Dunn
 * @author Juan Francisco Patino
 */
public class Utils {

    /**
     * Spring-configured logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SVIPApiController.class);

    /**
     * Utility method to encode a response into a {@code ResponseEntity} of the same generic type. If there is an error
     * encoding the response, an empty ResponseEntity with the {@code HttpStatus.INTERNAL_SERVER_ERROR} code will be
     * thrown.
     *
     * @param response The response to encode.
     * @return The encoded ResponseEntity. Null if the response is null or if there was an error encoding.
     */
    public static <T> ResponseEntity<T> encodeResponse(T response) {
        if (response == null) return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        try {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Returns a message detailing what went wrong during serialization/deserialization
     *
     * @param ret              HashMap value to return containing message
     * @param exceptionMessage message from caught exception, if any
     * @param internalMessage  message detailing what specifically happened in convert()
     * @return HashMap value to return containing message
     */
    static HashMap<SBOMFile, String> internalSerializerError(HashMap<SBOMFile, String> ret,
                                                             String exceptionMessage, String internalMessage) {
        ret.put(new SBOMFile("", ""), internalMessage +
                exceptionMessage);
        return ret;
    }

    /**
     * Reusable code used in API controller to check if SBOMFile is empty/not found
     * Also eliminates the need for isPresent() check for Optionals
     */
    public static ResponseEntity<Long> checkIfExists(long id, Optional<SBOMFile> sbomFile, String call) {
        if (sbomFile.isEmpty()) {
            LOGGER.info("DELETE /svip/" + call + "?id=" + id + " - FILE NOT FOUND. INVALID ID");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return null;
    }

    /**
     * Helper function for ConvertFromAPITest
     *
     * @return whether it is valid to proceed with this test
     */
    public static boolean convertTestController(String convertToSchema, String convertToFormat, Long id,
                                                SerializerFactory.Schema thisSchema, Map<Long, SBOMFile> testMap,
                                                SBOMFile original) {
        Long[] validTests = {0L, 2L, 6L, 7L};
        boolean contains = false;
        for (Long l : validTests
        ) {
            if (Objects.equals(id, l)) {
                contains = true;
                break;
            }
        }
        if (!contains)
            return true;

        // don't convert to the same schema+format
        if (thisSchema == SerializerFactory.Schema.SPDX23 && (convertToSchema.equals("SPDX23"))) {
            if ((convertToFormat).equalsIgnoreCase(assumeFormatFromDocument(original)))
                return true;
        }
        if (thisSchema == SerializerFactory.Schema.CDX14 && (convertToSchema.equals("CDX14")))
            return true;
        // tagvalue format unsupported for cdx14
        if (convertToSchema.equals("CDX14") && convertToFormat.equals("TAGVALUE"))
            return true;
        // we don't support xml deserialization right now
        return testMap.get(id).getContents().contains("xml");
    }

    /**
     * Helper function to assume format from raw SBOM document
     *
     * @param sbom SBOMFile to check
     * @return String representation of the format (JSON/TAGVALUE)
     */
    public static String assumeFormatFromDocument(SBOMFile sbom) {
        String originalFormat = "JSON";
        if (sbom.getContents().contains("DocumentName:") || sbom.getContents().contains("DocumentNamespace:"))
            originalFormat = TAGVALUE.name();
        return originalFormat;
    }

    /**
     * Helper function to assume schema from raw SBOM document
     *
     * @param contents SBOMFile to check
     * @return String representation of the schema
     */
    public static SerializerFactory.Schema assumeSchemaFromOriginal(String contents) {
        return (contents.toLowerCase().contains("spdxversion")) ?
                SerializerFactory.Schema.SPDX23 : SerializerFactory.Schema.CDX14;
    }

    /**
     * Helper function for APITest
     *
     * @param projectFiles project source files
     * @return array of SBOMFiles we can use to test
     */
    public static SBOMFile[] configureProjectTest(String[] projectFiles) throws IOException {
        SBOMFile[] sbomFiles = new SBOMFile[projectFiles.length];
        for (int i = 0; i < projectFiles.length; i++) {
            try {
                sbomFiles[i] = new SBOMFile(projectFiles[i], new String(Files.readAllBytes(Paths.get(projectFiles[i]))));
            } catch (InvalidPathException e) {
                i = -1;
            }
        }
        return sbomFiles;
    }


    public static long generateNewId(long id, Random rand, SBOMFileRepository sbomFileRepository) {
        // assign new id and name
        int i = 0;
        while(sbomFileRepository.existsById(id)){ // todo check if frontend are okay with this
            id += (rand.nextLong() + id) % ((i < 100) ? id : Long.MAX_VALUE);
            i++;
        }
        return id;
    }

    public static VirtualPath unZip(String path) throws IOException{

        byte[] buffer = new byte[1024];
        ZipInputStream zs = new ZipInputStream(new FileInputStream(path));
        ZipEntry zipEntry = zs.getNextEntry();

        int depth = 1;
        while (zipEntry != null){

            File newFile = newFile(new File(System.getProperty("user.dir")), zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
                depth++;
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                try{
                    while ((len = zs.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }catch(EOFException e){
                    fos.close();
                    zs.close();
                    LOGGER.error(e.getMessage());
                    break;
                }

                fos.close();
            }
            zipEntry = zs.getNextEntry();

        }

        zs.closeEntry();
        zs.close();

        return null;

    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

}
