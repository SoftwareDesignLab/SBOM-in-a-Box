package org.svip.api.utils;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.svip.api.controller.SVIPApiController;
import org.svip.api.model.SBOMFile;
import org.svip.api.repository.SBOMFileRepository;
import org.svip.sbomgeneration.serializers.SerializerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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

    /**
     * Generates new ID given old one
     *
     * @param id                 old ID
     * @param rand               Random class
     * @param sbomFileRepository repository
     * @return new ID
     */
    public static long generateNewId(long id, Random rand, SBOMFileRepository sbomFileRepository) {
        // assign new id and name
        int i = 0;
        try {
            while (sbomFileRepository.findById(id).isPresent()) {
                id += (Math.abs(rand.nextLong()) + id) % ((i < 100 && id < 0) ? id : Long.MAX_VALUE);
                i++;
            }
        } catch (NullPointerException e) {
            return id;
        }

        return id;
    }

    /**
     * Unzip a ZipFile of SBOMFiles
     *
     * @param z the zipped file
     * @return List of file contents paired with an integer representing its depth in the project directory
     */
    public static List<HashMap<SBOMFile, Integer>> unZip(ZipFile z) {

        ArrayList<HashMap<SBOMFile, Integer>> vpArray = new ArrayList<>();

        byte[] buffer = new byte[1024];
        Stream<? extends ZipEntry> entryStream = z.stream();


        entryStream.forEach(entry -> {//from  w ww .ja v a  2 s .c  o m
            try {
                // Get the input stream for the current zip entry
                InputStream is = z.getInputStream(entry);
                /* Read data for the entry using the is object */

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
     * Unzip a ZipFile of SBOMFiles
     *
     * @param path path of the zipped file
     * @return List of file contents paired with an integer representing its depth in the project directory
     */
    public static List<HashMap<SBOMFile, Integer>> unZip(String path) throws IOException {

        ArrayList<HashMap<SBOMFile, Integer>> vpArray = new ArrayList<>();

        byte[] buffer = new byte[1024];
        ZipInputStream zs = new ZipInputStream(new FileInputStream(path));
        ZipEntry zipEntry = zs.getNextEntry();

        int depth; // todo we may not actually need depth

        while (zipEntry != null) {

            depth = zipEntry.getName().split("[\\/]").length - 1;

            if (!zipEntry.isDirectory()) {
                // write file content
                StringBuilder contentsBuilder = new StringBuilder();
                int len;
                try {
                    while ((len = zs.read(buffer)) > 0) {
                        contentsBuilder.append(new String(buffer));
                    }
                } catch (EOFException e) {
                    zs.close();
                    LOGGER.error(e.getMessage());
                    break;
                }

                HashMap<SBOMFile, Integer> hashMap = new HashMap<>();
                hashMap.put(new SBOMFile(zipEntry.getName(), contentsBuilder.toString()), depth);
                vpArray.add(hashMap);
            }
            zipEntry = zs.getNextEntry();
        }

        zs.closeEntry();
        zs.close();

        return vpArray;

    }

    /**
     * Convert a MultiPart file to a ZipFile
     * @param file MultiPart file, a .zip file
     * @return Converted ZipFile object
     */
    public static ZipFile convertMultipartToZip(MultipartFile file) throws IOException {

        File zip = File.createTempFile(UUID.randomUUID().toString(), "temp");
        FileOutputStream o = new FileOutputStream(zip);
        IOUtils.copy(file.getInputStream(), o);
        o.close();

        try {
            return new ZipFile(zip);

        } catch (ZipException e) {
            e.printStackTrace();
            throw new ZipException();
        }

    }

}
