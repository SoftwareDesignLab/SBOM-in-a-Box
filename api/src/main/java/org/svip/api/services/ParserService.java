package org.svip.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.svip.api.entities.SBOM;
import org.svip.api.entities.SBOMFile;
import org.svip.api.repository.SBOMRepository;
import org.svip.api.requests.UploadSBOMFileInput;
import org.svip.generation.parsers.utils.VirtualPath;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.serializer.Serializer;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ParserService {
    /**
     * Spring-configured logger
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(ParserService.class);
    private final SBOMRepository sbomRepository;

    /**
     * Create new Service for a target repository
     *
     * @param sbomRepository SBOM repository to access
     */
    public ParserService(SBOMRepository sbomRepository) {
        this.sbomRepository = sbomRepository;
    }

    /**
     * USAGE. Send GENERATE request to /generate an SBOM from source file(s)
     *
     * @param projectName of project to be converted to SBOM
     * @param zipFile     path to zip file
     * @param schema      to convert to
     * @param format      to convert to
     * @return generated SBOM
     */
    public Long parseSBOM (MultipartFile zipFile, String projectName, SerializerFactory.Schema schema, SerializerFactory.Format format) throws IOException {
        String urlMsg = "GENERATE /svip/generate?projectName=" + projectName;

        // Ensure schema has a valid serializer
        try {
            schema.getSerializer(format);
        } catch (IllegalArgumentException e) {
            LOGGER.error(urlMsg + ": " + e.getMessage());
            return null;
        }
        ArrayList<HashMap<SBOMFile, Integer>> unZipped;
        try {
            unZipped = (ArrayList<HashMap<SBOMFile, Integer>>)
                    this.unZip(Objects.requireNonNull(this.convertMultipartToZip(zipFile)));
        } catch (ZipException e) {
            LOGGER.error(urlMsg + ":" + e.getMessage());
            return null;
        }

        HashMap<VirtualPath, String> virtualPathStringHashMap = new HashMap<>();

        for (HashMap<SBOMFile, Integer> h : unZipped
        ) {
            SBOMFile f = (SBOMFile) h.keySet().toArray()[0];
            if (!f.hasNullProperties()) // project files that are empty should just be ignored
                virtualPathStringHashMap.put(new VirtualPath(f.getFileName()), f.getContents());
        }

        org.svip.generation.parsers.ParserController parserController = new org.svip.generation.parsers.ParserController(projectName, virtualPathStringHashMap);


        SVIPSBOM parsed;
        try {
            parserController.parseAll();
            parsed = parserController.buildSBOM(schema);
        } catch (Exception e) {
            String error = "Error parsing into SBOM: " + Arrays.toString(e.getStackTrace());
            LOGGER.error(urlMsg + " " + error);
            return null;
        }


        Serializer s;
        String contents;

        try {
            s = SerializerFactory.createSerializer(schema, format, true);
            contents = s.writeToString(parsed);
        } catch (IllegalArgumentException | JsonProcessingException e) {
            String error = "Error serializing parsed SBOM: " + Arrays.toString(e.getStackTrace());
            LOGGER.error(urlMsg + " " + error);
            return null;
        }


        SBOMFile result = new SBOMFile(projectName + ((format == SerializerFactory.Format.JSON)
                ? ".json" : ".spdx"), contents);
        Random rand = new Random();
        result.setId(this.generateNewId(rand.nextLong(), rand, sbomRepository));

        // convert result sbomfile to sbom
        UploadSBOMFileInput u = new UploadSBOMFileInput(result.getFileName(), result.getContents());

        // Save according to overwrite boolean
        SBOM converted = u.toSBOMFile();

        sbomRepository.save(converted);

        return converted.getId();
    }

    /**
     * Generates new ID given old one
     *
     * @param id                 old ID
     * @param rand               Random class
     * @param sbomRepository repository
     * @return new ID
     */
    public long generateNewId(long id, Random rand, SBOMRepository sbomRepository) {
        // assign new id and name
        int i = 0;
        try {
            while (sbomRepository.findById(id).isPresent()) {
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
